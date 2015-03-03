package com.github.ftchirou.yajl.parser;

public class JsonProcessingException extends Exception {

    public JsonProcessingException() {
        super();
    }

    public JsonProcessingException(String message) {
        super(message);
    }

    public JsonProcessingException(Exception innerException) {
        super(innerException);
    }
}
