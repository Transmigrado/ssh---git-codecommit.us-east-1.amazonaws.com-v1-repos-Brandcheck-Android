package io.ebinar.infolder.event;

import com.blueprint.blueprint.object.BPCollection;

/**
 * Creado Por jorgeacostaalvarado el 12-11-16.
 */
public class MediaEvent {

    public BPCollection getCollection() {
        return collection;
    }

    public void setCollection(BPCollection collection) {
        this.collection = collection;
    }

    public enum Type {
        GET_MEDIA_COMPLETE,
        NONE,
        REFRESH_MEDIA
    }

    private Type type = Type.NONE;
    private int media = 0;

    private BPCollection collection;

    public MediaEvent(Type type, int media){
        this.type = type;
        this.media = media;
    }

    public Type getType(){
        return type;
    }

    public int getMedia(){
        return media;
    }
}

