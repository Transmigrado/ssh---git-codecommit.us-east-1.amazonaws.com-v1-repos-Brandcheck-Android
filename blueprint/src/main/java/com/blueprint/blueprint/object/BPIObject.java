package com.blueprint.blueprint.object;

/**
 * Creado por Jorge Acosta Alvarado on 04-08-15.
 */
public interface BPIObject {
    String getString(String key);
    int getInt(String key);
    double getDouble(String key);
    boolean getBoolean(String key);
    BPGeoObject getGeoObject(String key);
    BPResourceArray getResourceArray(String key);
    BPObject getBPObject(String key);
    void setString(String key, String value);
    void setDouble(String key, double value);
    void setInt(String key, int value);
    void setBoolean(String key, boolean value);
    void setGeoObject(String key, BPGeoObject value);
    void setResourceArray(String key, BPResourceArray value);
    void setBPObject(String key, BPObject value);
    BPCollection getBPCollection(String key);
    long getLong(String key);
    void setLong(String key, long value);
}
