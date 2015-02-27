package com.github.ftchirou.yajl.lexer;

public class UnrecognizedTokenException extends Exception {

    public UnrecognizedTokenException() {
        super();
    }

    public UnrecognizedTokenException(String message) {
        super(message);
    }
}
