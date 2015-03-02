package com.github.ftchirou.yajl.deserializer.tests;

import com.github.ftchirou.yajl.deserializer.JsonBaseDeserializer;
import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.parser.JsonParsingException;
import com.github.ftchirou.yajl.type.TypeReference;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;

public class JsonBaseDeserializerTest {

    @Test
    public void deserializePrimitives() throws Exception {
        Object s = JsonDeserializer.deserialize("\"hello\"", String.class);
        assertThat(s, instanceOf(String.class));
        assertEquals("hello", s);

        Object integer = JsonDeserializer.deserialize("42", Integer.class);
        assertThat(integer, instanceOf(Integer.class));
        assertEquals(42, integer);

        Object boolTrue = JsonDeserializer.deserialize("true", Boolean.class);
        assertThat(boolTrue, instanceOf(Boolean.class));
        assertEquals(true, boolTrue);

        Object boolFalse = JsonDeserializer.deserialize("false", Boolean.class);
        assertThat(boolFalse, instanceOf(Boolean.class));
        assertEquals(false, boolFalse);

        Object dbl = JsonDeserializer.deserialize("142.5", Double.class);
        assertThat(dbl, instanceOf(Double.class));
        assertEquals(142.5, (double) dbl, 1e-5);

        Object flt = JsonDeserializer.deserialize("134.8", Float.class);
        assertThat(flt, instanceOf(Float.class));
        assertEquals(134.8f, (float) flt, 1e-5);

        Object nl = JsonDeserializer.deserialize("null", Object.class);
        assertEquals(nl, null);
    }

    @Test
    public void deserializeSimpleArray() throws IOException, JsonParsingException {
        Object array = JsonDeserializer.deserialize("[12,34,102,12,33]", Integer[].class);

        assertThat(array, instanceOf(Integer[].class));
        System.out.println(array);

    }

    @Test
    public void deserializeSimpleCollection() throws IOException, JsonParsingException {

        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        JsonReader reader = new JsonReader("[13,2,3,48,5]");


        List<Integer> integers = deserializer.deserialize(reader, new TypeReference<ArrayList<Integer>>() { });

        System.out.println(integers);
    }

    @Test
    public void deserializeSimpleMap() throws IOException, JsonParsingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        JsonReader reader = new JsonReader("{\"a\":1,\"b\":2,\"c\":3}");

        Map<String, Integer> map = deserializer.deserialize(reader, new TypeReference<LinkedHashMap<String, Integer>>() { });

        System.out.println(map);
    }

    static class JsonDeserializer {

        public static Object deserialize(String s, Class<?> cls) throws IOException, JsonParsingException {
            JsonReader reader = new JsonReader(s);

            JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

            return deserializer.deserialize(reader, cls);
        }
    }
}
