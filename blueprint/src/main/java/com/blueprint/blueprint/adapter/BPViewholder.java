package com.blueprint.blueprint.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Creado por Jorge Acosta Alvarado on 04-08-15.
 */
public class BPViewholder extends RecyclerView.ViewHolder {

    private int viewType = 0;

    public BPViewholder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;
        Log.d("veginning", "ADD EVENT " + String.valueOf(itemView));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("veginning", "OOOOOH");
            }
        });
    }
    public int getViewType(){
        return  viewType;
    }


}
