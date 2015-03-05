package jin;

import jin.databind.JsonBaseDeserializer;
import jin.io.JsonReader;
import jin.io.JsonWriter;
import jin.io.JsonProcessingException;
import jin.databind.JsonBaseSerializer;
import jin.tree.JsonDeserializer;
import jin.tree.JsonNode;

import java.io.*;

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

    public static <T> T fromJson(String json, Class<T> cls) throws IOException, JsonProcessingException {
        return fromJson(new StringReader(json), cls);
    }

    public static <T> T fromJson(File file, Class<T> cls) throws IOException, JsonProcessingException {
        return fromJson(new FileInputStream(file), cls);
    }

    public static <T> T fromJson(InputStream is, Class<T> cls) throws IOException, JsonProcessingException {
        return fromJson(new JsonReader(is), cls);
    }

    public static <T> T fromJson(Reader reader, Class<T> cls) throws IOException, JsonProcessingException {
        return fromJson(new JsonReader(reader), cls);
    }

    public static void toJson(Object object, File file) throws IOException {
        toJson(object, new FileOutputStream(file));
    }

    public static String toJson(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toJson(object, baos);

        return baos.toString("UTF-8");
    }

    public static <T> T fromJson(JsonReader reader, Class<T> cls) throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        T object;
        if (cls == null) {
            object = deserializer.deserialize(reader);
        } else {
            object = deserializer.deserialize(reader, cls);
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
