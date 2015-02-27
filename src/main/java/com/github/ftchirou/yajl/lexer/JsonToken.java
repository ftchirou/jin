package com.github.ftchirou.yajl.lexer;

public class JsonToken {

    private TokenType type;

    private String value;

    private int position;

    public JsonToken() {

    }

    public JsonToken(TokenType type) {
        this.type = type;
    }

    public JsonToken(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public JsonToken(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    @Override
    public String toString() {
        return "[" + type.toString() + ", <" + value + ">, " + position + "]";
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
