package com.github.ftchirou.yajl.deserializer;

import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.parser.JsonParsingException;

import java.io.IOException;

public abstract class JsonDeserializer<T> {

    public abstract T deserialize(JsonReader reader) throws IOException, JsonParsingException;
}
