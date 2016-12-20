package com.blueprint.blueprint.object;

/**
 * Creado por Jorge Acosta Alvarado on 04-08-15.
 */
public class BPGeoObject {

    public double latitude;
    public double longitude;

    public BPGeoObject(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(BPGeoObject b){
        return BPGeoObject.distanceGeoPoints(this, b);
    }

    public String toString(){
        return "{'latitude':"+String.valueOf(latitude)+",'longitude':"+String.valueOf(longitude)+"}";
    }

    public static BPGeoObject bpObjectToGeopoint(BPObject object){
        return new BPGeoObject(object.getDouble("latitude"),object.getDouble("longitude"));
    }
    public static double distanceGeoPoints(BPGeoObject geoPointA, BPGeoObject geoPointB){
        double lat1 = geoPointA.latitude;
        double lng1 = geoPointA.longitude;

        double lat2 = geoPointB.latitude;
        double lng2 = geoPointB.longitude;

        double R = 6371.0f;
        double t1 = lat1 * Math.PI / 180.0f;
        double t2 = lat2 * Math.PI / 180.0f;
        double d1 = (lat2 - lat1) * Math.PI / 180.0f;
        double d2 = (lng2 - lng1) * Math.PI / 180.0f;

        double a = Math.sin(d1 / 2) * Math.sin(d1 / 2) + Math.cos(t1) * Math.cos(t2) *  Math.sin(d2 / 2) * Math.sin(d2 / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R*c;
    }


}
