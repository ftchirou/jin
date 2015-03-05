package jin.databind.tests;

import jin.databind.JsonBaseSerializer;
import jin.io.JsonWriter;
import jin.pojo.*;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

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
        Person person = new Person("John", "Doe", 25, Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}", JsonSerializer.serialize(person));
    }

    @Test
    public void serializePOJOWithEnum() throws IOException {
        PersonWithEnum person = new PersonWithEnum("John", "Doe", PersonWithEnum.Gender.MALE, 25, Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"MALE\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}", JsonSerializer.serialize(person));
    }

    @Test
    public void serializePOJOWithDefinedValueForEnumConstant() throws IOException {
        PersonWithDefinedValueForEnumConstant person = new PersonWithDefinedValueForEnumConstant("John", "Doe", PersonWithDefinedValueForEnumConstant.Gender.MALE, 25, Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"male\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}", JsonSerializer.serialize(person));
    }

    @Test
    public void serializePOJOWithDefinedPropertyNames() throws IOException {
        PersonWithDefinedPropertyNames person = new PersonWithDefinedPropertyNames("John", "Doe", 25, Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"first_name\":\"John\",\"last_name\":\"Doe\",\"AGE\":25,\"FRIENDS\":[\"Jane\",\"Jill\",\"Helen\"]}", JsonSerializer.serialize(person));
    }

    @Test
    public void serializePOJOWithIgnoredPropertyAndGetter() throws IOException {
        PersonWithIgnoredPropertyAndGetter person = new PersonWithIgnoredPropertyAndGetter("John", "Doe", new DateTime(1989, 4, 8, 15, 15), Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"friends\":[\"Jane\",\"Jill\",\"Helen\"],\"age\":25}", JsonSerializer.serialize(person));
    }

    @Test
    public void serializePOJOWithCustomSerializer() throws IOException {
        PersonWithCustomSerializerAndDeserializer person = new PersonWithCustomSerializerAndDeserializer("John", "Doe", new DateTime(1989, 4, 8, 15, 15), Arrays.asList("Jane", "Jill", "Helen"));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"1989-04-08T15:15:00.000Z\",\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}", JsonSerializer.serialize(person));
    }

    @Test
    public void serializePOJOWithInheritedFields() throws IOException {
        Employee employee = new Employee("John", "Doe", 25, Arrays.asList("Jane", "Jill", "Helen"), "Pythagoras Afrique");

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"],\"employerName\":\"Pythagoras Afrique\"}", JsonSerializer.serialize(employee));
    }

    @Test
    public void serializePolymorphicPOJOWithClassTypeInfo() throws IOException {
        StringField stringField = new StringField("hello");
        NumericField numericField = new NumericField(42);
        BooleanField booleanField = new BooleanField(true);

        assertEquals("{\"@class\":\"jin.pojo.StringField\",\"value\":\"hello\"}", JsonSerializer.serialize(stringField));
        assertEquals("{\"@class\":\"jin.pojo.NumericField\",\"value\":42}", JsonSerializer.serialize(numericField));
        assertEquals("{\"@class\":\"jin.pojo.BooleanField\",\"value\":true}", JsonSerializer.serialize(booleanField));
    }

    @Test
    public void serializePolymorphicPOJOWithCustomTypeInfo() throws IOException {
        StringField2 stringField2 = new StringField2("hello");
        NumericField2 numericField2 = new NumericField2(42);
        BooleanField2 booleanField2 = new BooleanField2(false);

        assertEquals("{\"type\":\"string\",\"value\":\"hello\"}", JsonSerializer.serialize(stringField2));
        assertEquals("{\"type\":\"numeric\",\"value\":42}", JsonSerializer.serialize(numericField2));
        assertEquals("{\"type\":\"boolean\",\"value\":false}", JsonSerializer.serialize(booleanField2));
    }

    @Test
    public void serializePOJOWithArrayOfPolymorphicObjects() throws IOException {
        ArrayField arrayField = new ArrayField(Arrays.asList(
                new StringField("hello"),
                new NumericField(42),
                new BooleanField(true)
        ));

        assertEquals("{\"@class\":\"jin.pojo.ArrayField\",\"fields\":[{\"@class\":\"jin.pojo.StringField\",\"value\":\"hello\"},{\"@class\":\"jin.pojo.NumericField\",\"value\":42},{\"@class\":\"jin.pojo.BooleanField\",\"value\":true}]}", JsonSerializer.serialize(arrayField));
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
