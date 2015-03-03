package com.github.ftchirou.yajl.deserializer.tests;

import com.github.ftchirou.yajl.deserializer.JsonBaseDeserializer;
import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.parser.JsonProcessingException;
import com.github.ftchirou.yajl.type.CollectionType;
import com.github.ftchirou.yajl.type.MapType;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;

public class JsonBaseDeserializerTest {

    @Test
    public void deserializePrimitivesWithoutTypeInformation() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader;

        reader = new JsonReader("[1.25, 2.4, 0, 98.8]");
        ArrayDeque<Double> answer = deserializer.deserialize(reader, new CollectionType(ArrayDeque.class, Double.class));


        System.out.println(answer);

    }

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
    public void deserializeSimpleArray() throws IOException, JsonProcessingException {
        Object array = JsonDeserializer.deserialize("[12,34,102,12,33]", Integer[].class);

        assertThat(array, instanceOf(Integer[].class));
        System.out.println(array);

    }

    @Test
    public void deserializeSimpleList() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("[[1,2],[3,4,5]]");

        List<Integer> ints = deserializer.deserialize(reader, ArrayList.class);

        System.out.println(ints);
    }

    @Test
    public void deserializeSimpleMap() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"one\" : 1, \"two\" : 2, \"three\" : 3}");

        Map<String, Integer> map = deserializer.deserialize(reader, LinkedHashMap.class);

        System.out.println(map);
    }

    @Test
    public void deserializeSimpleCollection() throws IOException, JsonProcessingException {

        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        JsonReader reader = new JsonReader("[ [13,2], [3,48,5] ]");


        List<List<Integer>> integers = deserializer.deserialize(reader, new CollectionType(ArrayList.class,
                                                                            new CollectionType(ArrayList.class, Integer.class)));

        System.out.println(integers);
    }

    @Test
    public void deserializeTypedMap() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        JsonReader reader = new JsonReader("{\"a\":[1,9,0,8],\"b\":[2,3],\"c\":[3,4]}");

        Map<String, List<Integer>> map = deserializer.deserialize(reader, new MapType(LinkedHashMap.class, String.class, new CollectionType(ArrayList.class, Integer.class)));

        System.out.println(map);
    }

    @Test
    public void deserializePOJOWithAnnotations() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"first_name\":\"John\",\"last_name\":\"Doe\",\"birth_date\":\"1989-04-08T15:15:00.000Z\",\"gender\":\"male\",\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}");

        POJOWithAnnotations pojo = deserializer.deserialize(reader, POJOWithAnnotations.class);

        System.out.println(pojo);
    }

    static class JsonDeserializer {

        public static Object deserialize(String s, Class<?> cls) throws IOException, JsonProcessingException {
            JsonReader reader = new JsonReader(s);

            JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

            return deserializer.deserialize(reader, cls);
        }
    }
}
