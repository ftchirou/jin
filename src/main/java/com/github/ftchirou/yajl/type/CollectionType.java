package com.github.ftchirou.yajl.type;

import java.lang.reflect.Type;

public class CollectionType extends TypeLiteral {

    private Type concreteType;

    private Type elementType;

    public CollectionType(Type concreteType, Type elementType) {
        this.concreteType = concreteType;
        this.elementType = elementType;
    }


    public Type getConcreteType() {
        return concreteType;
    }

    public void setConcreteType(Type concreteType) {
        this.concreteType = concreteType;
    }

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type elementType) {
        this.elementType = elementType;
    }
}
