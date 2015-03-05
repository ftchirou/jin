package jin;

import jin.databind.JsonBaseDeserializer;
import jin.io.JsonReader;
import jin.io.JsonWriter;
import jin.io.JsonProcessingException;
import jin.databind.JsonBaseSerializer;
import jin.tree.JsonDeserializer;
import jin.tree.JsonNode;

import java.io.*;
import java.lang.reflect.Type;

public class Json {

    public static <T> T fromJson(String json) throws IOException, JsonProcessingException {
        return fromJson(json, null);
    }

    public static <T> T fromJson(File file) throws IOException, JsonProcessingException {
        return fromJson(file, null);
    }

    public static JsonNode readTree(String json) throws IOException, JsonProcessingException {
        return readTree(new StringReader(json));
    }

    public static JsonNode readTree(File file) throws IOException, JsonProcessingException {
        return readTree(new FileInputStream(file));
    }

    public static JsonNode readTree(InputStream is) throws IOException, JsonProcessingException {
        return readTree(new JsonReader(is));
    }

    public static JsonNode readTree(Reader reader) throws IOException, JsonProcessingException {
        return readTree(new JsonReader(reader));
    }

    public static <T> T fromJson(String json, Type type) throws IOException, JsonProcessingException {
        return fromJson(new StringReader(json), type);
    }

    public static <T> T fromJson(File file, Type type) throws IOException, JsonProcessingException {
        return fromJson(new FileInputStream(file), type);
    }

    public static <T> T fromJson(InputStream is, Type type) throws IOException, JsonProcessingException {
        return fromJson(new JsonReader(is), type);
    }

    public static <T> T fromJson(Reader reader, Type type) throws IOException, JsonProcessingException {
        return fromJson(new JsonReader(reader), type);
    }

    public static void toJson(Object object, File file) throws IOException {
        toJson(object, new FileOutputStream(file));
    }

    public static String toJson(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toJson(object, baos);

        return baos.toString("UTF-8");
    }

    public static <T> T fromJson(JsonReader reader, Type type) throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        T object;
        if (type == null) {
            object = deserializer.deserialize(reader);
        } else {
            object = deserializer.deserialize(reader, type);
        }

        reader.close();

        return object;
    }

    private static JsonNode readTree(JsonReader reader) throws IOException, JsonProcessingException {
        JsonDeserializer deserializer = new JsonDeserializer();

        return deserializer.deserialize(reader);
    }

    public static void toJson(Object object, OutputStream os) throws IOException {
        JsonBaseSerializer serializer = new JsonBaseSerializer();
        JsonWriter writer = new JsonWriter(os);

        serializer.serialize(object, writer);

        writer.flush();

        writer.close();
    }
}
