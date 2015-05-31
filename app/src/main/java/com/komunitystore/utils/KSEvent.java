package com.komunitystore.utils;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class KSEvent {

    private Type _type;
    private Error _error;
    private Object _object;

    public enum Type {
        LOGIN, REGISTER, FOLLOW_DEALS, PROXIMITY_DEALS, MY_DEALS
    }

    public enum Error {
        NO_ERROR, ERROR
    }

    public KSEvent(Type type, Error error, Object object) {
        _type = type;
        _error = error;
        _object = object;
    }

    public Type getType() {
        return _type;
    }

    public Error getError() {
        return _error;
    }

    public Object getObject() {
        return _object;
    }
}
