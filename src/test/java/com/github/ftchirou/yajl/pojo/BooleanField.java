package com.github.ftchirou.yajl.pojo;

public class BooleanField extends Field {

    private boolean value;

    public BooleanField() {

    }

    public BooleanField(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
