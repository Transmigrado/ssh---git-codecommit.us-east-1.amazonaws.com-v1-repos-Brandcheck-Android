package com.blueprint.blueprint.event;

import android.view.View;
import com.blueprint.blueprint.object.BPObject;

/**
 * Created by jorgeacostaalvarado on 04-08-15.
 */
public class BPEvent {


    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public enum Type{
        ON_CLICK,
        ON_ITEM_CLICK,
        ON_CHECK_CHANGE,
        ON_TEXT_CHANGE
    }

    public Type type;
    private BPObject data = null;
    private View view = null;
    private Object parent = null;
    private int objectId = -1;

    public BPEvent(Type type){
        this.type = type;
    }

    public void setData(BPObject data){
        this.data = data;
    }



    public BPObject getData(){
        return data;
    }

    public void setView(View view){
        this.view = view;
    }

    public View getView(){
        return view;
    }
    public interface BPOnEvent{
        void onEvent(BPEvent event);
    }

}
