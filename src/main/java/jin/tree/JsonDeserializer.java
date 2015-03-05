package jin.tree;

import jin.io.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonDeserializer {

    public JsonDeserializer() {
    }

    public JsonNode deserialize(JsonReader reader) throws IOException, JsonProcessingException {
        reader.readToken();

        return deserializerValue(reader);
    }

    public JsonNode deserializeObject(JsonReader reader) throws IOException, JsonProcessingException {
        reader.expect(TokenType.OBJECT_START);

        if (reader.accept(TokenType.OBJECT_END)) {
            reader.expect(TokenType.OBJECT_END);

            return new JsonObject();
        }

        HashMap<String, JsonNode> members = deserializeObjectMembers(reader);

        reader.expect(TokenType.OBJECT_END);

        JsonObject object = new JsonObject();
        object.addAll(members);

        return object;

    }

    public JsonNode deserializeArray(JsonReader reader) throws IOException, JsonProcessingException {
        reader.expect(TokenType.ARRAY_START);

        JsonArray array = new JsonArray();

        while (!reader.accept(TokenType.ARRAY_END)) {
            array.add(deserializerValue(reader));

            if (reader.accept(TokenType.ARRAY_END)) {
                break;
            }

            reader.expect(TokenType.COMMA);
        }

        reader.expect(TokenType.ARRAY_END);

        return array;
    }

    private HashMap<String, JsonNode> deserializeObjectMembers(JsonReader reader) throws IOException, JsonProcessingException {
        LinkedHashMap<String, JsonNode> members = new LinkedHashMap<>();

        JsonPair pair = deserializeField(reader);

        members.put(pair.getKey(), pair.getValue());

        if (!reader.accept(TokenType.OBJECT_END)) {
            reader.expect(TokenType.COMMA);

            members.putAll(deserializeObjectMembers(reader));
        }

        return members;
    }

    private JsonPair deserializeField(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken keyToken = reader.expect(TokenType.STRING);

        reader.expect(TokenType.COLON);

        return new JsonPair(keyToken.getValue(), deserializerValue(reader));
    }

    private JsonNode deserializerValue(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken token = reader.currentToken();
        JsonNode node;

        switch (token.getType()) {
            case OBJECT_START:
                node = deserializeObject(reader);
                break;

            case ARRAY_START:
                node = deserializeArray(reader);
                break;

            case STRING:
                node = new JsonString(token.getValue());
                reader.readToken();
                break;

            case TRUE:
                node = new JsonBoolean(true);
                reader.readToken();
                break;

            case FALSE:
                node = new JsonBoolean(false);
                reader.readToken();
                break;

            case NUMBER:
                String value = token.getValue();
                if (value.contains(".") || value.contains("e") || value.contains("E")) {
                    node = deserializeDecimalValue(value);

                } else {
                    node = deserializeIntValue(value);
                }
                reader.readToken();
                break;

            case NULL:
            default:
                node = new JsonNull();
                reader.readToken();
                break;
        }

        return node;
    }

    private JsonNode deserializeDecimalValue(String value) {
       return new JsonDecimal(new BigDecimal(value));
    }

    private JsonNode deserializeIntValue(String value) {
        try {
            return new JsonInt(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            try {
                return new JsonLong(Long.parseLong(value));
            } catch (NumberFormatException nfe) {
                return new JsonBigInt(new BigInteger(value));
            }
        }
    }

    static class JsonPair {

        String key;

        JsonNode value;

        JsonPair(String key, JsonNode value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public JsonNode getValue() {
            return value;
        }
    }
}
