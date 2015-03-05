package jin.databind.tests;

import jin.databind.JsonBaseDeserializer;
import jin.io.JsonReader;
import jin.io.JsonProcessingException;
import jin.pojo.*;
import jin.type.CollectionType;
import jin.type.MapType;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

public class JsonBaseDeserializerTest {

    @Test
    public void deserializeZero() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("0");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(Integer.class));
        assertEquals(0, (int) object);

        reader.close();
    }

    @Test
    public void deserializeInt() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("42");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(Integer.class));
        assertEquals(42, (int) object);

        reader.close();
    }

    @Test
    public void deserializeString() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("\"hello\"");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(String.class));
        assertEquals("hello", object);

        reader.close();
    }

    @Test
    public void deserializeTrue() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("true");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(Boolean.class));
        assertEquals(true, object);

        reader.close();
    }

    @Test
    public void deserializeFalse() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("false");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(Boolean.class));
        assertEquals(false, object);

        reader.close();
    }


    @Test
    public void deserializeDouble() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("142.35");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(Double.class));
        assertEquals(142.35, (Double) object, 1e-5);

        reader.close();
    }

    @Test
    public void deserializeSimpleArray() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("[13, 76, 98, 90]");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(ArrayList.class));
        assertEquals(Arrays.asList(13, 76, 98, 90), object);

        reader.close();
    }

    @Test
    public void deserializeSimpleMap() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"one\" : 1, \"two\" : 2, \"three\" : 3}");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(HashMap.class));

        HashMap<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        assertEquals(map, object);

        reader.close();
    }

    @Test
    public void deserializeArrayOfArray() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("[[1, 2], [3, 4, 5], [6, 7]]");

        Object object = deserializer.deserialize(reader);

        assertThat(object, instanceOf(ArrayList.class));

        ArrayList list = (ArrayList) object;
        int size = list.size();

        for (int i = 0; i < size; ++i) {
            Object inner = list.get(0);

            assertThat(inner, instanceOf(ArrayList.class));

            ArrayList innerList = (ArrayList) inner;

            for (Object o: innerList) {
                assertThat(o, instanceOf(Integer.class));
            }
        }

        reader.close();
    }

    @Test
    public void deserializeArrayWithSimpleTypeInformation() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("[1.25, 2.4, 3, 98.8]");

        Object object = deserializer.deserialize(reader, ArrayList.class);

        assertThat(object, instanceOf(ArrayList.class));

        ArrayList list = (ArrayList) object;

        assertThat(list.get(0), instanceOf(Double.class));
        assertThat(list.get(1), instanceOf(Double.class));
        assertThat(list.get(2), instanceOf(Integer.class));
        assertThat(list.get(3), instanceOf(Double.class));

        reader.close();
    }

    @Test
    public void deserializeArrayWithFullTypeInformation() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("[1.25, 2.4, 3, 98.8]");

        Object object = deserializer.deserialize(reader, new CollectionType(ArrayList.class, Double.class));

        assertThat(object, instanceOf(ArrayList.class));

        ArrayList list = (ArrayList) object;

        for (Object o: list) {
            assertThat(o, instanceOf(Double.class));
        }

        assertEquals(Arrays.asList(1.25, 2.4, 3.0, 98.8), list);

        reader.close();
    }

    @Test
    public void deserializeArrayWithFullTypeInformation2() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("[1.25, 2.4, 3, 98.8]");

        Object object = deserializer.deserialize(reader, new CollectionType(LinkedHashSet.class, Double.class));

        assertThat(object, instanceOf(LinkedHashSet.class));

        Set set = (Set) object;

        for (Object o: set) {
            assertThat(o, instanceOf(Double.class));
        }

        Set<Double> s = new LinkedHashSet<>();
        s.add(1.25);
        s.add(2.4);
        s.add(3.0);
        s.add(98.8);

        assertEquals(s, set);

        reader.close();
    }

    @Test
    public void deserializeArrayOfArrayWithFullTypeInformation() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("[ [\"one\", \"two\"], [\"three\", \"four\"], [\"five\", \"six\"] ]");

        Object object = deserializer.deserialize(reader,
            new CollectionType(ArrayDeque.class,
                new CollectionType(LinkedHashSet.class, String.class)));

        assertThat(object, instanceOf(ArrayDeque.class));

        ArrayDeque deque = (ArrayDeque) object;

        while (!deque.isEmpty()) {
            Object inner = deque.pop();

            assertThat(inner, instanceOf(LinkedHashSet.class));

            Set set = (LinkedHashSet) inner;

            for (Object o: set) {
                assertThat(o, instanceOf(String.class));
            }
        }

        reader.close();
    }

    @Test
    public void deserializeMapWithFullTypeInformation() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{ \"a\" : [1, 9, 0, 8], \"b\" : [2, 3], \"c\" : [3, 4]}");
   
        Object object = deserializer.deserialize(reader,
            new MapType(HashMap.class, String.class,
                new CollectionType(ArrayList.class, Integer.class)));

        assertThat(object, instanceOf(HashMap.class));

        HashMap map = (HashMap) object;
        Set keys = map.keySet();

        for (Object key: keys) {
            Object entry = map.get(key);

            assertThat(entry, instanceOf(ArrayList.class));

            ArrayList list = (ArrayList) entry;

            for (Object o : list) {
                assertThat(o, instanceOf(Integer.class));
            }
        }

        reader.close();
    }

    @Test
    public void deserializeSimplePOJO() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}");

        Person person = deserializer.deserialize(reader, Person.class);

        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
        assertEquals(25, person.getAge());
        assertEquals(Arrays.asList("Jane", "Jill", "Helen"), person.getFriends());

        reader.close();
    }

    @Test
    public void deserializePOJOWithEnum() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"MALE\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}");

        PersonWithEnum person = deserializer.deserialize(reader, PersonWithEnum.class);

        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
        assertEquals(PersonWithEnum.Gender.MALE, person.getGender());
        assertEquals(25, person.getAge());
        assertEquals(Arrays.asList("Jane", "Jill", "Helen"), person.getFriends());

        reader.close();
    }

    @Test
    public void deserializePOJOWithDefinedValueForEnumConstant() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"male\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}");

        PersonWithDefinedValueForEnumConstant person = deserializer.deserialize(reader, PersonWithDefinedValueForEnumConstant.class);

        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
        assertEquals(PersonWithDefinedValueForEnumConstant.Gender.MALE, person.getGender());
        assertEquals(25, person.getAge());
        assertEquals(Arrays.asList("Jane", "Jill", "Helen"), person.getFriends());

        reader.close();
    }

    @Test
    public void deserializePOJOWithDefinedPropertyNames() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"first_name\":\"John\",\"last_name\":\"Doe\",\"AGE\":25,\"FRIENDS\":[\"Jane\",\"Jill\",\"Helen\"]}");

        PersonWithDefinedPropertyNames person = deserializer.deserialize(reader, PersonWithDefinedPropertyNames.class);

        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
        assertEquals(25, person.getAge());
        assertEquals(Arrays.asList("Jane", "Jill", "Helen"), person.getFriends());

        reader.close();
    }

    @Test
    public void deserializePOJOWithCustomDeserializer() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"1989-04-08T15:15:00.000Z\",\"friends\":[\"Jane\",\"Jill\",\"Helen\"]}");

        PersonWithCustomSerializerAndDeserializer person = deserializer.deserialize(reader, PersonWithCustomSerializerAndDeserializer.class);

        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
        assertEquals(1989, person.getBirthDate().getYear());
        assertEquals(4, person.getBirthDate().getMonthOfYear());
        assertEquals(8, person.getBirthDate().getDayOfMonth());
        assertEquals(15, person.getBirthDate().getHourOfDay());
        assertEquals(15, person.getBirthDate().getMinuteOfHour());
        assertEquals(Arrays.asList("Jane", "Jill", "Helen"), person.getFriends());

        reader.close();
    }

    @Test
    public void deserializePOJOWithInheritedFields() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":25,\"friends\":[\"Jane\",\"Jill\",\"Helen\"],\"employerName\":\"Pythagoras Afrique\"}");

        Employee employee = deserializer.deserialize(reader, Employee.class);

        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals(25, employee.getAge());
        assertEquals(Arrays.asList("Jane", "Jill", "Helen"), employee.getFriends());
        assertEquals("Pythagoras Afrique", employee.getEmployerName());

        reader.close();
    }

    @Test
    public void deserializePolymorphicPOJOWithClassTypeInfo() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        JsonReader reader;

        reader = new JsonReader("{\"@class\":\"jin.pojo.StringField\",\"value\":\"hello\"}");
        Field stringField = deserializer.deserialize(reader, Field.class);

        assertThat(stringField, instanceOf(StringField.class));
        assertEquals("hello", ((StringField) stringField).getValue());

        reader.close();

        reader = new JsonReader("{\"@class\":\"jin.pojo.NumericField\",\"value\":42}");
        Field numericField = deserializer.deserialize(reader, Field.class);

        assertThat(numericField, instanceOf(NumericField.class));
        assertEquals(42, ((NumericField) numericField).getValue());

        reader.close();

        reader = new JsonReader("{\"@class\":\"jin.pojo.BooleanField\",\"value\":true}");
        Field booleanField = deserializer.deserialize(reader, Field.class);

        assertThat(booleanField, instanceOf(BooleanField.class));
        assertEquals(true, ((BooleanField) booleanField).getValue());

        reader.close();
    }

    @Test
    public void deserializePolymorphicPOJOWithCustomTypeInfo() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();

        JsonReader reader;

        reader = new JsonReader("{\"type\":\"string\",\"value\":\"hello\"}");
        FieldWithCustomTypeInfo stringField = deserializer.deserialize(reader, FieldWithCustomTypeInfo.class);

        assertThat(stringField, instanceOf(StringField2.class));
        assertEquals("hello", ((StringField2) stringField).getValue());

        reader.close();

        reader = new JsonReader("{\"type\":\"numeric\",\"value\":42}");
        FieldWithCustomTypeInfo numericField = deserializer.deserialize(reader, FieldWithCustomTypeInfo.class);

        assertThat(numericField, instanceOf(NumericField2.class));
        assertEquals(42, ((NumericField2) numericField).getValue());

        reader.close();

        reader = new JsonReader("{\"type\":\"boolean\",\"value\":false}");
        FieldWithCustomTypeInfo booleanField = deserializer.deserialize(reader, FieldWithCustomTypeInfo.class);

        assertThat(booleanField, instanceOf(BooleanField2.class));
        assertEquals(false, ((BooleanField2) booleanField).getValue());

        reader.close();
    }

    @Test
    public void deserializePOJOWithArrayOfPolymorphicObjects() throws IOException, JsonProcessingException {
        JsonBaseDeserializer deserializer = new JsonBaseDeserializer();
        JsonReader reader = new JsonReader("{\"@class\":\"jin.pojo.ArrayField\",\"fields\":[{\"@class\":\"jin.pojo.StringField\",\"value\":\"hello\"},{\"@class\":\"jin.pojo.NumericField\",\"value\":42},{\"@class\":\"jin.pojo.BooleanField\",\"value\":true}]}");

        Field field = deserializer.deserialize(reader, Field.class);

        assertThat(field, instanceOf(ArrayField.class));

        ArrayField arrayField = (ArrayField) field;

        assertEquals(arrayField.getFields().size(), 3);

        assertThat(arrayField.getFields().get(0), instanceOf(StringField.class));
        assertEquals("hello", ((StringField) arrayField.getFields().get(0)).getValue());

        assertThat(arrayField.getFields().get(1), instanceOf(NumericField.class));
        assertEquals(42, ((NumericField) arrayField.getFields().get(1)).getValue());

        assertThat(arrayField.getFields().get(2), instanceOf(BooleanField.class));
        assertEquals(true, ((BooleanField) arrayField.getFields().get(2)).getValue());
    }
}
