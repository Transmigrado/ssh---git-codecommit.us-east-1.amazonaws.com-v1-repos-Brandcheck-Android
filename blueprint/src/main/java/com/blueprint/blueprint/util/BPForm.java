package com.blueprint.blueprint.util;

import android.content.Context;
import android.util.Log;

import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.builder.BPAppBuilder;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

/**
 * Creado Por jorgeacostaalvarado el 07-10-15.
 */
public class BPForm {

    private Context context;

    public BPForm (Context context){
        this.context = context;
    }

    public void add(BPCollection fields, BPUIBinding binding){



        for(int i = 0 ; i < fields.size(); i++){
            BPObject field = fields.get(i);

            Log.d("universe", field.toString());

            int id = BPAppBuilder.getUIID(field.getString("ui"));

            if (id == 0) {
                id = context.getApplicationContext().getResources().getIdentifier(field.getString("ui"), "id", context.getApplicationContext().getPackageName());
            }


            Log.d("universe",field.getString("ui") + " value : " + String.valueOf( binding.get(id).getView()) + " " + String.valueOf(id));
        }
    }

}
