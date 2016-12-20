package io.ebinar.infolder.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;

import io.ebinar.infolder.R;


/**
 * Creado Por jorgeacostaalvarado el 15-10-15.
 */
public class AlertManager {

    private static SparseArrayCompat<Object> dialogs = new SparseArrayCompat<>();

    public static void loading(Context context){
        ProgressDialog dialog = ProgressDialog.show(context, "", context.getResources().getString(R.string.waiting), true);
        dialogs.append(dialogs.size(),dialog);
    }


    public static void messageWithCancel(Context context, String title, String msg){



        AlertDialog alertDialog = new AlertDialog.Builder(context,R.style.dialog)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

        dialogs.append(dialogs.size(),alertDialog);



    }


    public static void message(Context context, String title, String msg){

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

        dialogs.append(dialogs.size(),alertDialog);



    }

    public static void closeCurrentDialog(){
        Object dialog = dialogs.get(dialogs.size() - 1);

        if(dialog instanceof ProgressDialog){
            ((ProgressDialog) dialog).cancel();
            dialogs.delete(dialogs.size() - 1);
        }
    }
}
