package com.blueprint.blueprint.database;

import android.database.sqlite.SQLiteException;

public class SQLiteAssetException extends SQLiteException {

	private static final long serialVersionUID = 1L;

	public SQLiteAssetException() {

    }

    public SQLiteAssetException(String error) {
        super(error);
    }
}
