package com.komunitystore.utils;

/**
 * Created by Tanguy on 06/05/2015.
 */
public class KSEvent {

    private Type _type;
    private Error _error;

    public enum Type {
        LOGIN
    }

    public enum Error {
        NO_ERROR
    }

    public KSEvent(Type type, Error error) {
        _type = type;
        _error = error;
    }

    public Type getType() {
        return _type;
    }

    public Error getError() {
        return _error;
    }
}
