package com.github.ftchirou.yajl.serializer;

import com.github.ftchirou.yajl.writer.JsonWriter;

import java.io.IOException;

public abstract class JsonSerializer<T> {

    public abstract void serialize(T t, JsonWriter writer) throws IOException;
}
