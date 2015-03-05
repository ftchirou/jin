package com.github.ftchirou.yajl.pojo;

public class StringField2 extends FieldWithCustomTypeInfo {
    private String value;

    public StringField2() {
        this.type = "string";
    }

    public StringField2(String value) {
        this.type = "string";
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
