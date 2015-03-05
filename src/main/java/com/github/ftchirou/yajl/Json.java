package com.github.ftchirou.yajl;

import com.github.ftchirou.yajl.deserializer.JsonBaseDeserializer;
import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.io.JsonWriter;
import com.github.ftchirou.yajl.parser.JsonProcessingException;
import com.github.ftchirou.yajl.serializer.JsonBaseSerializer;

import java.io.*;

public class Json {

    public static <T> T fromJson(String json) throws IOException, JsonProcessingException {
        return fromJson(json, null);
    }


    public static <T> T fromJson(File file) throws IOException, JsonProcessingException {
        return fromJson(file, null);
    }

    public static String toJson(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toJson(object, baos);

        return baos.toString("UTF-8");
    }

    public static void toJson(Object object, File file) throws IOException {
        toJson(object, new FileOutputStream(file));
    }

    public static <T> T fromJson(String json, Class<T> cls) throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader(json);

        T object;

        if (cls == null) {
            object = deserializer.deserialize(reader);
        } else {
            object = deserializer.deserialize(reader, cls);
        }

        reader.close();

        return object;
    }

    public static <T> T fromJson(File file, Class<T> cls) throws IOException, JsonProcessingException {
        return fromJson(new FileInputStream(file), cls);
    }

    public static <T> T fromJson(InputStream is, Class<T> cls) throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader(is);

        T object;

        if (cls == null) {
            object = deserializer.deserialize(reader);
        } else {
            object = deserializer.deserialize(reader, cls);
        }

        reader.close();

        return object;
    }

    public static void toJson(Object object, OutputStream os) throws IOException {
        JsonBaseSerializer serializer = new JsonBaseSerializer();
        JsonWriter writer = new JsonWriter(os);

        serializer.serialize(object, writer);

        writer.flush();

        writer.close();
    }
}
