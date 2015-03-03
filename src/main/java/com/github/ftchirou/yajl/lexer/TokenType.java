package com.github.ftchirou.yajl.lexer;

public enum TokenType {
    STRING,
    NUMBER,
    TRUE,
    FALSE,
    NULL,
    OBJECT_START,
    OBJECT_END,
    ARRAY_START,
    ARRAY_END,
    COMMA,
    COLON,
    END_OF_STREAM
}
