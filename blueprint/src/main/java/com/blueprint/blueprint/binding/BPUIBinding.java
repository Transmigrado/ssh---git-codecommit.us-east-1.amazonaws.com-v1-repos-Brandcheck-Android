package com.blueprint.blueprint.binding;

import android.util.SparseArray;

import com.blueprint.blueprint.event.BPEvent;
import com.blueprint.blueprint.object.BPObject;

/**
 * Creado por Jorge Acosta Alvarado on 04-08-15.
 */
public class BPUIBinding extends SparseArray<BPViewBinding> {

    private Object parent;
    private BPEvent.BPOnEvent callbackEvent;

    public BPUIBinding(){

    }

    public BPUIBinding(Object parent){
        this.parent = parent;
    }

    public void setParent(Object parent){
        this.parent = parent;

        for(int i = 0; i < size(); i++) {
            get(keyAt(i)).addParent(parent);
        }
    }

    public void draw(){
        for(int i = 0; i < size(); i++) {
            get(keyAt(i)).draw();
        }
    }


    public BPViewBinding add(int objectId)
    {
        return add(objectId,new BPObject());
    }

    public BPViewBinding add(int objectId, BPObject info){
        BPViewBinding bpViewBinding = new BPViewBinding(objectId, info);
        if(callbackEvent!=null){
            bpViewBinding.setCallbackEvent(callbackEvent);
        }
        this.append(objectId, bpViewBinding);
        bpViewBinding.addParent(parent);

        return bpViewBinding;
    }

    public void setData(BPObject data) {
        for(int i = 0; i < size(); i++) {
            get(keyAt(i)).setData(data);
        }
    }

    public void setCallbackEvent(BPEvent.BPOnEvent callbackEvent) {
        this.callbackEvent = callbackEvent;
        for(int i = 0; i < size(); i++) {
            get(keyAt(i)).setCallbackEvent(callbackEvent);
        }
    }
}
