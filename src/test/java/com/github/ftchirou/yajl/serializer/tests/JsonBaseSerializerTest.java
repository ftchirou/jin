package com.github.ftchirou.yajl.serializer.tests;

import com.github.ftchirou.yajl.annotations.JsonGetter;
import com.github.ftchirou.yajl.annotations.Json;
import com.github.ftchirou.yajl.annotations.JsonValue;
import com.github.ftchirou.yajl.serializer.JsonBaseSerializer;
import com.github.ftchirou.yajl.io.JsonWriter;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

@Ignore
public class JsonBaseSerializerTest {

    @Test
    public void serializePrimitives() throws IOException {
        assertEquals("\"hello\"", JsonSerializer.serialize("hello"));
        assertEquals("42", JsonSerializer.serialize(42));
        assertEquals("12", JsonSerializer.serialize(12L));
        assertEquals("true", JsonSerializer.serialize(true));
        assertEquals("false", JsonSerializer.serialize(false));
        assertEquals("142.5", JsonSerializer.serialize(142.5));
        assertEquals("134.8", JsonSerializer.serialize(134.8f));
        assertEquals("null", JsonSerializer.serialize(null));
    }

    @Test
    public void serializeSimpleArray() throws IOException {
        int[] array = new int[] { 12, 34, 87, 0, 33 };

        assertEquals("[12,34,87,0,33]", JsonSerializer.serialize(array));
    }

    @Test
    public void serializeSimpleCollection() throws IOException {
        Collection<String> collection = Arrays.asList("one", "two", "three", null, "five", null, "six");

        assertEquals("[\"one\",\"two\",\"three\",null,\"five\",null,\"six\"]", JsonSerializer.serialize(collection));
    }

    @Test
    public void serializeMixedCollection() throws IOException {
        Collection<Object> collection = Arrays.asList(42, Double.valueOf(3.4d), true, Arrays.asList("one", "two", "three"), null);

        assertEquals("[42,3.4,true,[\"one\",\"two\",\"three\"],null]", JsonSerializer.serialize(collection));
    }

    @Test
    public void serializeSimpleMap() throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "one");
        map.put("b", "two");
        map.put("c", null);

        assertEquals("{\"a\":\"one\",\"b\":\"two\",\"c\":null}", JsonSerializer.serialize(map));
    }

    @Test
    public void serializeSimplePOJO() throws IOException {
        POJO pojo = new POJO("John", "Doe", 25, POJO.Gender.MALE, Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":25,\"gender\":\"MALE\",\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}",
                JsonSerializer.serialize(pojo));
    }

    @Test
    public void serializePOJOWithAnnotations() throws IOException {
        POJOWithAnnotations pojo = new POJOWithAnnotations("John", "Doe", new DateTime(1989, 4, 8, 15, 15), POJOWithAnnotations.Gender.MALE, Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"first_name\":\"John\",\"last_name\":\"Doe\",\"birth_date\":\"1989-04-08T15:15:00.000Z\",\"gender\":\"male\",\"friends\":[\"Jane\",\"Jill\",\"Helen\"],\"age\":25}",
                JsonSerializer.serialize(pojo));
    }

    static class POJO {
        public enum Gender { MALE, FEMALE };

        private String firstName;

        private String lastName;

        private int age;

        private Gender gender;

        private List<String> friends;

        public POJO(String firstName, String lastName, int age, Gender gender, List<String> friends) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.gender = gender;
            this.friends = friends;
        }
    }

    static class POJOWithAnnotations {
        public enum Gender {
            @JsonValue("male")
            MALE,

            @JsonValue("female")
            FEMALE
        }

        @Json(propertyName="first_name")
        private String firstName;

        @Json(propertyName="last_name")
        private String lastName;

        @Json(propertyName="birth_date", serializeWith=DateTimeSerializer.class)
        private DateTime birthDate;

        private Gender gender;

        private List<String> friends;

        public POJOWithAnnotations(String firstName, String lastName, DateTime birthDate, Gender gender, List<String> friends) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.gender = gender;
            this.friends = friends;
        }

        @JsonGetter(name="age")
        public int getAge() {
            return Years.yearsBetween(birthDate, DateTime.now()).getYears();
        }
    }


    static class JsonSerializer {

        public static String serialize(Object object) throws IOException {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            JsonWriter jsonWriter = new JsonWriter(b);

            JsonBaseSerializer serializer = new JsonBaseSerializer();
            serializer.serialize(object, jsonWriter);

            jsonWriter.flush();

            String json = b.toString("UTF-8");

            jsonWriter.close();

            return json;
        }
    }
}
