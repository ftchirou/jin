package jin.tree.tests;

import jin.io.JsonReader;
import jin.io.JsonProcessingException;
import jin.tree.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.containsString;

public class JsonDeserializerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseEmptyObject() throws IOException, JsonProcessingException {
        String json = "{}";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));
        assertEquals(((JsonObject) node).size(), 0);
        assertEquals(node.toJsonString(), json);
    }

    @Test
    public void parseSimpleObjectWithStringValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" :  \"value\" }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));
        assertEquals(((JsonObject) node).size(), 1);
        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleObjectWithStringValueWithSpaces() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : \"value\"\r\n}";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "value");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleObjectWithIntValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\": 102 }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonInt.class));
        assertEquals(object.get("key").intValue(), 102);


        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleQuoteInStringValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : \"ab\'c\" }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "ab\'c");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseLowercaseUnicodeStringValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : \"\u2000\u20ff\" }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "\u2000\u20FF");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseUppercaseUnicodeStringValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : \"\u2000\u20FF\" }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "\u2000\u20FF");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseNonProtectedSlashInStringValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : \"http://foo\" }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "http://foo");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleObjectWithDoubleValue() throws IOException, JsonProcessingException {
        String json = "{ \"pi\" : 3.14E-10 }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("pi"));
        assertTrue(object.get("pi").isDecimal());
        assertThat(object.get("pi"), instanceOf(JsonDecimal.class));

        assertEquals(object.get("pi").doubleValue(), 3.14E-10, 1e-5);
    }

    @Test
    public void parseSimpleObjectWithLowercaseDoubleValue() throws IOException, JsonProcessingException {
        String json = "{ \"pi\" : 3.14e-10 }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("pi"));
        assertThat(object.get("pi"), instanceOf(JsonDecimal.class));

        assertEquals(object.get("pi").doubleValue(), 3.14E-10, 1e-5);
    }

    @Test
    public void parseObjectWithLongValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : 12345123456789 }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertTrue(object.get("key").isLong());
        assertThat(object.get("key"), instanceOf(JsonLong.class));

        assertEquals(object.get("key").longValue(), 12345123456789L);

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithBigIntegerValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : 123456789123456789123456789 }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonBigInt.class));
        assertTrue(object.get("key").isBigInt());
        assertEquals(object.get("key").bigIntValue(), new BigInteger("123456789123456789123456789"));

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithDoublePrecisionFloatingPointValue() throws IOException, JsonProcessingException {
        String json = "{ \"pi\" : 3.14159265358979323846 }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("pi"));
        assertTrue(object.get("pi").isDecimal());
        assertThat(object.get("pi"), instanceOf(JsonDecimal.class));

        assertEquals(object.get("pi").bigDecimalValue(), new BigDecimal("3.14159265358979323846"));

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleIntArray() throws IOException, JsonProcessingException {
        String json = "[11, 42, 74, 55, 63]";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonArray.class));

        JsonArray array = (JsonArray) node;

        assertEquals(array.size(), 5);

        for (JsonNode element: array) {
            assertThat(element, instanceOf(JsonInt.class));
        }

        assertEquals(array.get(0).intValue(), 11);
        assertEquals(array.get(1).intValue(), 42);
        assertEquals(array.get(2).intValue(), 74);
        assertEquals(array.get(3).intValue(), 55);
        assertEquals(array.get(4).intValue(), 63);

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseArrayOfEmptyObjects() throws IOException, JsonProcessingException {
        String json = "[ { }, { }, []]";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonArray.class));

        JsonArray array = (JsonArray) node;

        assertEquals(array.size(), 3);

        assertThat(array.get(0), instanceOf(JsonObject.class));
        assertThat(array.get(1), instanceOf(JsonObject.class));
        assertThat(array.get(2), instanceOf(JsonArray.class));

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithNullValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : null }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonNull.class));

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithBooleanTrueValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : true }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonBoolean.class));
        assertEquals(object.get("key").booleanValue(), true);

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithBooleanFalseValue() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : false }";

        JsonNode node = JsonDeserializer.deserialize(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonBoolean.class));
        assertEquals(object.get("key").booleanValue(), false);

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void throwExceptionIfStringValueIsUnclosed() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : \"value }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("unclosed string literal"));

        JsonDeserializer.deserialize(json);
    }

    @Test
    public void throwExceptionIfKeyStringIsUnclosed() throws IOException, JsonProcessingException {
        String json = "{ \"key : \"value\" }";

        thrown.expect(JsonProcessingException.class);

        JsonDeserializer.deserialize(json);
    }

    @Test
    public void throwExceptionIfValueIsASingleQuotedString() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : 'value' }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character \'"));

        JsonDeserializer.deserialize(json);
    }

    @Test
    public void throwExceptionIfStringValueIsNotQuoted() throws IOException, JsonProcessingException {
        String json = "{ \"key\" : value }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character v"));

        JsonDeserializer.deserialize(json);
    }

    @Test
    public void throwExceptionIfKeyIsNotDoubleQuoted() throws IOException, JsonProcessingException {
        String json = "{ key : \"value\" }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character k"));

        JsonDeserializer.deserialize(json);
    }

    @Test
    public void throwExceptionIfKeyIsSingleQuoted() throws IOException, JsonProcessingException {
        String json = "{ 'key' : \"value\" }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character '"));

        JsonDeserializer.deserialize(json);
    }

    static class JsonDeserializer {

        public static JsonNode deserialize(String json) throws IOException, JsonProcessingException {
            JsonReader reader = new JsonReader(json);
            jin.tree.JsonDeserializer deserializer = new jin.tree.JsonDeserializer();

            return deserializer.deserialize(reader);
        }
    }
}
