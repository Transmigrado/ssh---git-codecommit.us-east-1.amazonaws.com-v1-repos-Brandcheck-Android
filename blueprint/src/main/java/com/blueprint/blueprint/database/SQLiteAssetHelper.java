package com.blueprint.blueprint.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SQLiteAssetHelper extends SQLiteOpenHelper {

	private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
	private static final String ASSET_DB_PATH = "databases";

	private final Context mContext;
	private final String mName;
	private final CursorFactory mFactory;
	private final int mNewVersion;

	private SQLiteDatabase mDatabase = null;
	private boolean mIsInitializing = false;

	private String mDatabasePath;
	private String mArchivePath;
	private String mUpgradePathFormat;

	private int mForcedUpgradeVersion = 0;


	public SQLiteAssetHelper(Context context, String name, String storageDirectory, CursorFactory factory, int version) {

		super(context, name, factory, version);




		if (version < 1) throw new IllegalArgumentException("version debe ser mayor a 1 " + version);
		if (name == null) throw new IllegalArgumentException("No puede ser nula");

		mContext = context;
		mName = name;
		mFactory = factory;
		mNewVersion = version;

		mArchivePath = ASSET_DB_PATH + "/" + name + ".zip";
		if (storageDirectory != null) mDatabasePath = storageDirectory;
        else {
			mDatabasePath = context.getApplicationInfo().dataDir + "/databases";
		}
		mUpgradePathFormat = ASSET_DB_PATH + "/" + name + "_upgrade_%s-%s.sql";
	}


	public SQLiteAssetHelper(Context context, String name, CursorFactory factory, int version) {

		this(context, name, null, factory, version);
	}


	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
			return mDatabase;  // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getWritableDatabase called recursively");
		}


		boolean success = false;
		SQLiteDatabase db = null;

		try {
			mIsInitializing = true;

			db = createOrOpenDatabase(false);

			int version = db.getVersion();

			if (version != 0 && version < mForcedUpgradeVersion) {
				db = createOrOpenDatabase(true);
				db.setVersion(mNewVersion);
				version = db.getVersion();
			}

			if (version != mNewVersion) {
				db.beginTransaction();
				try {
					if (version == 0) {
						onCreate(db);
					} else {
						if (version > mNewVersion) {
							Log.w(TAG, "Can't downgrade read-only database from version " +version + " to " + mNewVersion + ": " + db.getPath());
						}
						onUpgrade(db, version, mNewVersion);
					}
					db.setVersion(mNewVersion);
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}

			onOpen(db);
			success = true;
			return db;
		} finally {
			mIsInitializing = false;
			if (success) {
				if (mDatabase != null) {
					try {
                        mDatabase.close();
                    } catch (Exception e) { }
				}
				mDatabase = db;
			} else {

				if (db != null) db.close();
			}
		}

	}



	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		if (mDatabase != null && mDatabase.isOpen()) {
			return mDatabase;
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getReadableDatabase called recursively");
		}

		try {
			return getWritableDatabase();
		} catch (SQLiteException e) {
			if (mName == null) throw e;
			Log.e(TAG, "Couldn't open " + mName + " for writing (will try read-only):", e);
		}

		SQLiteDatabase db = null;
		try {
			mIsInitializing = true;
			String path = mContext.getDatabasePath(mName).getPath();
			db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY);
			if (db.getVersion() != mNewVersion) {
				throw new SQLiteException("Can't upgrade read-only database from version " +
						db.getVersion() + " to " + mNewVersion + ": " + path);
			}

			onOpen(db);
			Log.w(TAG, "Opened " + mName + " in read-only mode");
			mDatabase = db;
			return mDatabase;
		} finally {
			mIsInitializing = false;
			if (db != null && db != mDatabase) db.close();
		}
	}


	@Override
	public synchronized void close() {
		if (mIsInitializing) throw new IllegalStateException("Closed during initialization");

		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}

	@Override
	public final void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.w(TAG, "Upgrading database " + mName + " from version " + oldVersion + " to " + newVersion + "...");

		ArrayList<String> paths = new ArrayList<String>();
		getUpgradeFilePaths(oldVersion, newVersion-1, newVersion, paths);

		if (paths.isEmpty()) {
			Log.e(TAG, "no upgrade script path from " + oldVersion + " to " + newVersion);
			throw new SQLiteAssetException("no upgrade script path from " + oldVersion + " to " + newVersion);
		}

		Collections.sort(paths);
		for (String path : paths) {
			try {
				Log.w(TAG, "processing upgrade: " + path);
				InputStream is = mContext.getAssets().open(path);
				String sql = convertStreamToString(is);
				if (sql != null) {
					String[] cmds = sql.split(";");
					for (String cmd : cmds) {
						//Log.d(TAG, "cmd=" + cmd);
						if (cmd.trim().length() > 0) {
							db.execSQL(cmd);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.w(TAG, "Successfully upgraded database " + mName + " from version " + oldVersion + " to " + newVersion);

	}

	public void setForcedUpgradeVersion(int version) {
		mForcedUpgradeVersion = version;
	}

	private SQLiteDatabase createOrOpenDatabase(boolean force) throws SQLiteAssetException {
		SQLiteDatabase db = returnDatabase();
		if (db != null) {
			// database already exists
			if (force) {
				Log.w(TAG, "forcing database upgrade!");
				copyDatabaseFromAssets();
				db = returnDatabase();
			}
			return db;
		} else {
			// database does not exist, copy it from assets and return it
			copyDatabaseFromAssets();
			db = returnDatabase();
			return db;
		}
	}

	private SQLiteDatabase returnDatabase(){
		try {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(mDatabasePath + "/" + mName, mFactory, SQLiteDatabase.OPEN_READWRITE);
			Log.i(TAG, "successfully opened database " + mName);
			return db;
		} catch (SQLiteException e) {
			Log.w(TAG, "could not open database " + mName + " - " + e.getMessage());
			return null;
		}
	}

	private void copyDatabaseFromAssets() throws SQLiteAssetException {
		Log.w(TAG, "copying database from assets...");

		try {
			InputStream zipFileStream = mContext.getAssets().open(mArchivePath);
			File f = new File(mDatabasePath + "/");
			if (!f.exists()) { f.mkdir(); }

			ZipInputStream zis = getFileFromZip(zipFileStream);
			if (zis == null) {
				throw new SQLiteAssetException("Archive is missing a SQLite database file");
			}
			writeExtractedFileToDisk(zis, new FileOutputStream(mDatabasePath + "/" + mName));

			Log.w(TAG, "database copy complete");

		} catch (FileNotFoundException fe) {
			SQLiteAssetException se = new SQLiteAssetException("Missing " + mArchivePath + " file in assets or target folder not writable");
			se.setStackTrace(fe.getStackTrace());
			throw se;
		} catch (IOException e) {
			SQLiteAssetException se = new SQLiteAssetException("Unable to extract " + mArchivePath + " to data directory");
			se.setStackTrace(e.getStackTrace());
			throw se;
		}
	}

	private InputStream getUpgradeSQLStream(int oldVersion, int newVersion) {
		String path = String.format(mUpgradePathFormat, oldVersion, newVersion);
		try {
			InputStream is = mContext.getAssets().open(path);
			return is;
		} catch (IOException e) {
			Log.w(TAG, "missing database upgrade script: " + path);
			return null;
		}
	}

	private void getUpgradeFilePaths(int baseVersion, int start, int end, ArrayList<String> paths) {

		int a;
		int b;

		InputStream is = getUpgradeSQLStream(start, end);
		if (is != null) {
			String path = String.format(mUpgradePathFormat, start, end);
			paths.add(path);

			a = start - 1;
			b = start;
			is = null;
		} else {
			a = start - 1;
			b = end;
		}

		if (a < baseVersion) {
			return;
		} else {
			getUpgradeFilePaths(baseVersion, a, b, paths); // recursive call
		}

	}

	private void writeExtractedFileToDisk(ZipInputStream zin, OutputStream outs) throws IOException {
		byte[] buffer = new byte[1024];
		int length;
		while ((length = zin.read(buffer))>0){
			outs.write(buffer, 0, length);
		}
		outs.flush();
		outs.close();
		zin.close();
	}

	private ZipInputStream getFileFromZip(InputStream zipFileStream) throws FileNotFoundException, IOException {
		ZipInputStream zis = new ZipInputStream(zipFileStream);
		ZipEntry ze = null;
		while ((ze = zis.getNextEntry()) != null) {
			Log.w(TAG, "extracting file: '" + ze.getName() + "'...");
			return zis;
		}
		return null;
	}

	@SuppressWarnings("resource")
	private String convertStreamToString(InputStream is) {
	    return new Scanner(is).useDelimiter("\\A").next();
	}

}
