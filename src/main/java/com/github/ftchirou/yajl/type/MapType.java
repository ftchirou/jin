package com.github.ftchirou.yajl.type;

import java.lang.reflect.Type;

public class MapType extends TypeLiteral {

    private Type concreteType;

    private Type keyType;

    private Type valueType;

    public MapType(Type concreteType, Type keyType, Type valueType) {
        this.concreteType = concreteType;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public Type getConcreteType() {
        return concreteType;
    }

    public void setConcreteType(Type concreteType) {
        this.concreteType = concreteType;
    }

    public Type getKeyType() {
        return keyType;
    }

    public void setKeyType(Type keyType) {
        this.keyType = keyType;
    }

    public Type getValueType() {
        return valueType;
    }

    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }
}
