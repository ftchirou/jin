package com.github.ftchirou.yajl.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {

    private final Type type;

    public TypeReference() {
        Type superType = getClass().getGenericSuperclass();

        if (superType instanceof Class) {
            throw new RuntimeException("missing type parameter.");
        }

        this.type = ((ParameterizedType) superType).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    public T newInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> rawType;

        if (type instanceof Class<?>) {
            rawType = (Class<?>) type;
        } else {
            rawType = (Class<?>) ((ParameterizedType) type).getRawType();
        }

        Constructor<?> constructor = rawType.getConstructor();

        return (T) constructor.newInstance();
    }

    public Type getType() {
        return type;
    }

    public Class<?> getReferenceClass() {
        return type instanceof Class<?> ? (Class<?>) type : (Class<?>) ((ParameterizedType) type).getRawType();
    }
}
