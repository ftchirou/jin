package com.github.ftchirou.yajl.parser;

public class JsonParsingException extends Exception {

    public JsonParsingException() {
        super();
    }

    public JsonParsingException(String message) {
        super(message);
    }

    public JsonParsingException(Exception innerException) {
        super(innerException);
    }
}
