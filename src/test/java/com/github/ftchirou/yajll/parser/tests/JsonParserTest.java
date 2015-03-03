package com.github.ftchirou.yajll.parser.tests;

import com.github.ftchirou.yajl.json.*;
import com.github.ftchirou.yajl.parser.JsonParser;
import com.github.ftchirou.yajl.parser.JsonProcessingException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.containsString;

@Ignore
public class JsonParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseEmptyObject() throws JsonProcessingException {
        String json = "{}";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));
        assertEquals(((JsonObject) node).size(), 0);
        assertEquals(node.toJsonString(), json);
    }

    @Test
    public void parseSimpleObjectWithStringValue() throws JsonProcessingException {
        String json = "{ \"key\" :  \"value\" }";

        JsonParser parser =  new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));
        assertEquals(((JsonObject) node).size(), 1);
        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleObjectWithStringValueWithSpaces() throws JsonProcessingException {
        String json = "{ \"key\" : \"value\"\r\n}";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "value");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleObjectWithIntValue() throws JsonProcessingException {
        String json = "{ \"key\": 102 }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonInt.class));
        assertEquals(object.get("key").intValue(), 1);


        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleQuoteInStringValue() throws JsonProcessingException {
        String json = "{ \"key\" : \"ab\'c\" }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "ab\'c");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseLowercaseUnicodeStringValue() throws JsonProcessingException {
        String json = "{ \"key\" : \"\u2000\u20ff\" }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "\u2000\u20FF");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseUppercaseUnicodeStringValue() throws JsonProcessingException {
        String json = "{ \"key\" : \"\u2000\u20FF\" }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "\u2000\u20FF");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseNonProtectedSlashInStringValue() throws JsonProcessingException {
        String json = "{ \"key\" : \"http://foo\" }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonString.class));
        assertEquals(object.get("key").stringValue(), "http://foo");

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseSimpleObjectWithDoubleValue() throws JsonProcessingException {
        String json = "{ \"pi\" : 3.14E-10 }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("pi"));
        assertTrue(object.get("pi").isDecimal());
        assertThat(object.get("pi"), instanceOf(JsonDecimal.class));

        assertEquals(object.get("pi").doubleValue(), 3.14E-10, 1e-5);
    }

    @Test
    public void parseSimpleObjectWithLowercaseDoubleValue() throws JsonProcessingException {
        String json = "{ \"pi\" : 3.14e-10 }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("pi"));
        assertThat(object.get("pi"), instanceOf(JsonDecimal.class));

        assertEquals(object.get("pi").doubleValue(), 3.14E-10, 1e-5);
    }

    @Test
    public void parseObjectWithLongValue() throws JsonProcessingException {
        String json = "{ \"key\" : 12345123456789 }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

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
    public void parseObjectWithBigIntegerValue() throws JsonProcessingException {
        String json = "{ \"key\" : 123456789123456789123456789 }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

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
    public void parseObjectWithDoublePrecisionFloatingPointValue() throws JsonProcessingException {
        String json = "{ \"pi\" : 3.14159265358979323846 }";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

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
    public void parseSimpleIntArray() throws JsonProcessingException {
        String json = "[11, 42, 74, 55, 63]";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

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
    public void parseArrayOfEmptyObjects() throws JsonProcessingException {
        String json = "[ { }, { }, []]";

        JsonParser parser = new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonArray.class));

        JsonArray array = (JsonArray) node;

        assertEquals(array.size(), 3);

        assertThat(array.get(0), instanceOf(JsonObject.class));
        assertThat(array.get(1), instanceOf(JsonObject.class));
        assertThat(array.get(2), instanceOf(JsonArray.class));

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithNullValue() throws JsonProcessingException {
        String json = "{ \"key\" : null }";

        JsonParser parser =  new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonNull.class));

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithBooleanTrueValue() throws JsonProcessingException {
        String json = "{ \"key\" : true }";

        JsonParser parser =  new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonBoolean.class));
        assertEquals(object.get("key").booleanValue(), true);

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void parseObjectWithBooleanFalseValue() throws JsonProcessingException {
        String json = "{ \"key\" : false }";

        JsonParser parser =  new JsonParser();
        JsonNode node = parser.parse(json);

        assertThat(node, instanceOf(JsonObject.class));

        JsonObject object = (JsonObject) node;

        assertEquals(object.size(), 1);
        assertTrue(object.containsKey("key"));
        assertThat(object.get("key"), instanceOf(JsonBoolean.class));
        assertEquals(object.get("key").booleanValue(), false);

        assertEquals(node.toJsonString(), json.replaceAll("\\s+", ""));
    }

    @Test
    public void throwExceptionIfStringValueIsUnclosed() throws JsonProcessingException {
        String json = "{ \"key\" : \"value }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("unclosed string literal"));

        JsonParser parser = new JsonParser();
        parser.parse(json);
    }

    @Test
    public void throwExceptionIfKeyStringIsUnclosed() throws JsonProcessingException {
        String json = "{ \"key : \"value\" }";

        thrown.expect(JsonProcessingException.class);

        JsonParser parser = new JsonParser();
        parser.parse(json);
    }

    @Test
    public void throwExceptionIfValueIsASingleQuotedString() throws JsonProcessingException {
        String json = "{ \"key\" : 'value' }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character \'"));

        JsonParser parser = new JsonParser();
        parser.parse(json);
    }

    @Test
    public void throwExceptionIfStringValueIsNotQuoted() throws JsonProcessingException {
        String json = "{ \"key\" : value }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character v"));

        JsonParser parser = new JsonParser();
        parser.parse(json);
    }

    @Test
    public void throwExceptionIfKeyIsNotDoubleQuoted() throws JsonProcessingException {
        String json = "{ key : \"value\" }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character k"));

        JsonParser parser = new JsonParser();
        parser.parse(json);
    }

    @Test
    public void throwExceptionIfKeyIsSingleQuoted() throws JsonProcessingException {
        String json = "{ 'key' : \"value\" }";

        thrown.expect(JsonProcessingException.class);
        thrown.expectMessage(containsString("invalid character '"));

        JsonParser parser = new JsonParser();
        parser.parse(json);
    }
}
