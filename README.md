# Jin : Simple and fast JSON processing.

## What Jin is ?
Jin is a lightweight library for processing JSON in Java inspired by the [Jackson Project](https://github.com/FasterXML/jackson). It uses a streaming API for fast JSON processing and can be used for

* **Databinding**. Serialization or deserialization of Java objects directly to or from a stream of JSON tokens. The stream can be a string, a file, a response from an HTTP request, basically any valid Java InputStream or OutputStream.
* Building a mutable **in-memory tree representation** from a JSON tokens stream.

The streaming API can also be used to write or read JSON tokens from a stream.

## Building the library

* First clone the repository. In a terminal, type

    ```
    git clone https://github.com/ftchirou/jin.git
    ```
    
* Now, ```cd``` in the directory ```jin``` and type ```./gradlew build``` (or ```gradlew.bat build``` on Windows) to build the library. The jar file ```jin-1.0.jar``` will be generated in the directory ```build/libs/```.

* If you want to run the tests, type ```./gradlew test``` (or ```gradlew.bat build``` on Windows) instead.

## Usage

### Databind

#### Basic Serialization

##### Primitives, Array, Collection and Map serialization
Just pass the object or the primitive to be serialized to one of the static methods ```Json.toJson(...)```.
```java

import jin.Json;

public class Main {
  public static void main(String[] args) {
      String aString = Json.toJson("hello"); 
      // => "\"hello\""
      
      String aInt = Json.toJson(42); 
      // => "42"
      
      String aLong = Json.toJson(128L); 
      // => "128"
      
      String aFloat = Json.toJson(2.45f); 
      // => "2.45"
      
      String aBool = Json.toJson(true); 
      // => "true"
      
      String aNull = Json.toJson(null); 
      // => "null"
      
      String array = Json.toJson(new int[] {1, 2, 3, 4, 5}); 
      // => "[1,2,3,4,5]"
      
      String collection = Json.toJson(Arrays.asList("one", "two", "three", "four", "five")); 
      // => "[\"one\",\"two\",\"three\",\"four\",\"five\"]"
      
      String mixedCollection = Json.toJson(Arrays.asList(42, Double.valueOf(3.4d), true, Arrays.asList("one", "two", "three"), null)); 
      // => "[42,3.4,true,[\"one\",\"two\",\"three\"],null]"
          
      Map<String, Integer> map = new HashMap<>();
      map.put("one", 1);
      map.put("two", 2);
      map.put("three", 3);
      
      String aMap = Json.toJson(map); 
      // => "{\"one\":1,\"two\":2,\"three\":3}"
  }
}
```

To write to a file use ```Json.toJson(Object, java.io.File)``` or use ```Json.toJson(Object, java.io.OutputStream)``` to write to any output stream

##### POJO serialization
To serialize a Plain Old Java Object, again, pass it to one of the static methods ```Json.toJson(...)```.

```java
public class Person {
    public enum Gender { MALE, FEMALE }

    private String firstName;

    private String lastName;

    private int age;

    public Person() {
    }

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}
```

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("John", "Doe", Person.Gender.MALE, 25);
        
        String json = Json.toJson(person); 
        // => "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"MALE\",\"age\":25}"
    }
}
```

You can control how the object members are translated in JSON with annotations.

* To specify the JSON property name of a member, annotate it with ```@Json(property="<property_name>")```.
  
  Example

  ```java
  public class Person {
    ...
    
    @Json(property="first_name")
    private String firstName;
    
    ...
  }
  ```
  
  ```java
  Json.toJson(new Person("John", "Doe", Person.Gender.MALE, 25); 
  // => "{\"first_name\":\"John\",\"lastName\":\"Doe\",\"gender\":\"MALE\",\"age\":25}"
  ```

* To ignore a member, annotate it with ```@Json(ignore=true)```.

 Example

  ```java
  public class Person {
    ...
    
    @Json(ignore=true)
    private Gender gender;
    
    ...
  }
  ```
  
  ```java
  Json.toJson(new Person("John", "Doe", Person.Gender.MALE, 25); 
  // => "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":25}"
  ```

* By default, ```toString()``` is called on an enum constant to get the value to put in the JSON. To customize the JSON value corresponding to each enum constant, annotate each enum constant with ```@JsonValue("<value>")```.

 Example
 ```java
 public class Person {
   public enum Gender {
     @JsonValue("XY")
     MALE,
     
     @JsonValue("XX")
     FEMALE
   }
   
   ...
 }
 ```

 ```java
 Json.toJson(new Person("John", "Doe", Person.Gender.MALE, 25); 
 // => "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"XY\",\"age\":25}"
 ```
 
#### Basic Deserialization

##### Primitives deserialization

Use one of the static methods ```Json.fromJson(...)```.

Example

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        int aInt = Json.fromJson("42");
        double aDouble = Json.fromJson("4.31E-10");
        String aString = Json.fromJson("\"hello\"");
        boolean aBoolean = Json.fromJson("true");
   }
}
````

##### JSON Array deserialization

By default, JSON arrays are deserialized in ```ArrayList<Object>```.

Example

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        Collection integers = Json.fromJson("[13.7, 76, 98.12, 45.7]");
        
        assertThat(integers, intanceOf(ArrayList.class));
        // => true
        
        assertThat(integers.get(0), instanceOf(Double.class));
        // => true
        
        assertThat(integers.get(1), instanceOf(Integer.class));
        // => true !!!!
    }
}
```

If you want a JSON array to be deserialized in a collection type other than ```ArrayList```, pass your type as the second argument to ```Json.fromJson()```.

Example

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        Collection integers = Json.fromJson("[13.7, 76, 98.12, 45.7"], ArrayDeque.class);
        
        assertThat(integers, instanceOf(ArrayDeque.class);
        // => true
    }
}
```

For even more control on how a JSON array should be deserialized and how the elements of the array should also be deserialized, pass an object of type ```CollectionType``` as the second argument to ```Json.fromJson(...)```.

Example

```java
import jin.Json
import jin.type.CollectionType;

public class Main {
    public static void main(String[] args) {
        ArrayList<Double> list = Json.fromJson("[13.7, 76, 98.12, 45.7"], 
            new CollectionType(ArrayList.class, Double.class));
        
        System.out.println(list);
        // => [13.7, 76.0, 98.12, 45.7]
        
        assertThat(list.get(1), instanceOf(Double.class));
        // => true !
    }
}
```

The constructor of the class ```CollectionType``` takes 2 arguments of type ```java.lang.reflect.Type```. The first is the concrete type of the collection (ArrayList, ArrayDeque, LinkedHashSet, ...) and the second is the type of each element.

Now the fun part. ```CollectionType``` also implements ```java.lang.reflect.Type```. That means that we can have things like

```java
import jin.Json;
import jin.type.CollectionType;

public class Main {
    public static void main(String[] args) {
        List<Set<Integer>> list = Json.fromJson("[ [1, 2], [3, 4], [5, 6] ]",
            new CollectionType(ArrayList.class,
                new CollectionType(LinkedHashSet.class, Integer.class)));
                
        System.out.println(list);
        // => [[1, 2], [3, 4], [5, 6]]
        
        assertThat(list, instanceOf(ArrayList.class));
        // => true
        
        assertThat(list.get(0), instanceOf(LinkedHashSet.class));
        // => true
    }
}
```

##### JSON Object deserialization

JSON objects are deserialized by default in ```LinkedHashMap<Object, Object>```.

Example

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        Map map = Json.fromJson("{\"one\" : 1, \"two\" : 2, \"three\" : 3}");
        
        assertThat(map, instanceOf(LinkedHashMap.class);
        // => true
        
        System.out.println(map);
        // => {one=1, two=2, three=3}
    }
}
```

Like JSON arrays, it is possible to specify another map type by passing the type as the second argument to ```Json.fromJson(...)```.

Example

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        Map map = Json.fromJson(Json.fromJson("{\"one\" : 1, \"two\" : 2, \"three\" : 3}", 
            Hashtable.class));
        
        assertThat(map, instanceOf(Hashtable.class);
        // => true
        
        System.out.println(map);
        // => {two=2, one=1, three=3}
    }
}
```

You can specify the map type, the key and value types by using a ```MapType```. Just like ```CollectionType```, ```MapType``` implements ```java.lang.reflect.Type``` and takes 3 arguments: the concrete type of the map, the type of the keys and the type of the values.

Example

```java
import jin.Json;
import jin.type.MapType;
import jin.type.CollectionType;

public class Main {
    public static void main(String[] args) {
        Map map = Json.fromJson("{ \"a\" : [1, 9, 0, 8], \"b\" : [2, 3], \"c\" : [3, 4]}",
            new MapType(HashMap.class, String.class,
                new CollectionType(ArrayList.class, Integer.class)));
                
        assertThat(map, instanceOf(HashMap.class));
        // => true
        
        System.out.println(map);
        // => {b=[2, 3], c=[3, 4], a=[1, 9, 0, 8]}
    }
}
```

##### POJO deserialization

To deserialize a JSON object into a POJO:

* Declare an empty constructor (if not already done) in the class of the POJO, so that Jin could create instances of it.
* Pass the type of the POJO as the second argument to ```Json.toJson(...)```.

Example

```java
public class Person {
    public enum Gender { MALE, FEMALE }

    private String firstName;

    private String lastName;

    private int age;

    public Person() {
    }

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
    
    ...
}
```

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        Person person = Json.fromJson("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"MALE\",\"age\":25}", Person.class);
        
        assertEquals("John", person.getFirstName());
        // => true
        
        assertEquals("Doe", person.getLastName());
        // => true
        
        assertEquals(Person.Gender.MALE, person.getGender());
        // => true
        
        assertEquals(25, person.getAge);
        // => true
    }
}
```

If the POJO was annotated with ```@Json``` and/or ```@JsonValue``` annotations, the deserialization process will take that into account.


#### Advanced serialization and deserialization

##### Custom serializers and deserializers

To override the behavior of the default serializer/deserializer for an object field:

* Extend the abstract class ```jin.databind.JsonSerializer<T>``` / ```jin.databind.JsonDeserializer<T>``` and implement the method ```void serialize(T, JsonWriter writer)``` / ```T deserialize(JsonReader reader)```.
* Annotate the field with ```@Json(serializeWith=MyCustomSerializer.class, deserializeWith=MyCustomDeserializer.class)```.

Example

```java
public class Person {
    public enum Gender { MALE, FEMALE }

    private String firstName;

    private String lastName;

    @Json(serializeWith=DateTimeSerializer.class, deserializeWith=DateTimeDeserializer.class)
    private org.joda.DateTime birthDate

    public Person() {
    }

    public Person(String firstName, String lastName, org.joda.DateTime birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }
    
    ...
}
```

```java
import jin.databind.JsonSerializer;
import jin.io.JsonWriter;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeSerializer extends JsonSerializer<DateTime> {

    public void serialize(DateTime dt, JsonWriter writer) throws IOException {
        writer.writeString(dt.toString());
    }
}
```

```java
import jin.databind.JsonDeserializer;
import jin.io.JsonReader;
import jin.io.JsonToken;
import jin.io.TokenType;
import jin.io.JsonProcessingException;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeDeserializer extends JsonDeserializer<DateTime> {

    public DateTime deserialize(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken token = reader.expect(TokenType.STRING);

        return DateTime.parse(token.getValue());
    }
}
```

```java
import jin.Json;
import org.joda.DateTime;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("John", "Doe", new DateTime(1989, 4, 8, 15, 15);
        
        String json = Json.toJson(person);
        // => "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"1989-04-08T15:15:00.000Z\"}"
    }
}
```

##### Polymorphic object serialization / deserialization

Suppose we have a class hierarchy like the following

```java
public abstract class Field {
}
```

```java
public class StringField extends Field {

    private String value;
    
    public StringField() {
    
    }
    
    public StringField(String value) {
        this.value = value;
    }
}
```

```java
public class NumericField extends Field {

    private int value;
    
    public NumericField() {
    
    }
    
    public NumericField(int value) {
        this.value = value;
    }
}
```

```java
public class BooleanField extends Field {

    private boolean value;
    
    public BooleanField() {
    
    }
    
    public BooleanField(boolean value) {
        this.value = value;
    }
}
```

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayField extends Field {

    private List<Field> fields;
    
    public ArrayField() {
        this.fields = new ArrayList<Field>();
    }
    
    public ArrayField(List<Field> fields) {
        this.fields = fields;
    }
}
```

Now, suppose we want to serialize an ```ArrayField``` containing a ```StringField```, a ```NumericField``` and a ```BooleanField```.

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        ArrayField arrayField = new ArrayField(Arrays.asList(
            new StringField("hello"),
            new NumericField(42),
            new BooleanField(true)
        ));
        
        String json = Json.toJson(arrayField);
        // => "{\"fields\":[{\"value\":\"hello\"},{\"value\":42},{\"value\":true}]}"
    }
}
```

So far, so good. Every field in ```arrayField``` has been correctly serialized. Now, suppose we want to deserialize back the string returned in an ```ArrayField```.

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        ArrayField arrayField = Json.fromJson(
            "{\"fields\":[{\"value\":\"hello\"},{\"value\":42},{\"value\":true}]}",
            ArrayField.class);
            
        // => JsonProcessingException: cannot instantiate object of type 'Field'.
    }
}
```

A ```JsonProcessingException``` is thrown with the message ```cannot instantiate object of type 'Field'.```. The problem occurs when Jin tries to deserialize the elements of ```List<Field> fields``` in ```ArrayField```. Indeed, ```Field``` is an abstract class and can not be instantiated, beside Jin has no way to know which of ```StringField```, ```NumericField``` or ```BooleanField``` correspond to each JSON Object in the JSON array.

We need to give Jin, type information about which concrete type it should deserialize into depending on the JSON object it encounters.

We can achieve this with the annotation ```@JsonTypeInfo```. Let's annotate our ```Field``` class like below

```java
import jin.annotations.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
public abstract class Field {
}
```
Now, let's execute our main method again

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        ArrayField arrayField = new ArrayField(Arrays.asList(
            new StringField("hello"),
            new NumericField(42),
            new BooleanField(true)
        ));
        
        String json = Json.toJson(arrayField);
        // => "{\"@class\":\"ArrayField\",\"fields\":[{\"@class\":\"StringField\",\"value\":\"hello\"},{\"@class\":\"NumericField\",\"value\":42},{\"@class\":\"BooleanField\",\"value\":true}]}"

    }
}
```

At the beginning of each object, Jin has included a property ```@class``` which holds the full class name of the POJO serialized. During the deserialization, Jin will use the value of the ```@class``` property to know in which concrete class to deserialize each JSON object.

Let's deserialize back.

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        ArrayField arrayField = Json.fromJson(
            "{\"@class\":\"ArrayField\",\"fields\":[{\"@class\":\"StringField\",\"value\":\"hello\"},{\"@class\":\"NumericField\",\"value\":42},{\"@class\":\"BooleanField\",\"value\":true}]}",
            ArrayField.class);
            
        assertThat(arrayField.getFields().get(0), instanceOf(StringField.class));
        // => true
        
        assertThat(arrayField.getFields().get(1), instanceOf(NumericField.class));
        // => true
        
        assertThat(arrayField.getFields().get(2), instanceOf(BooleanField.class));
        // => true
    }
}
```

We could have declared ```arrayField``` with the abstract type ```Field``` and it would have worked; because of ```"@class" : "ArrayField"``` at the beginning of the JSON, Jin knows that it should deserialize the JSON object into an ```ArrayField```.

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        Field arrayField = Json.fromJson(
            "{\"@class\":\"ArrayField\",\"fields\":[{\"@class\":\"StringField\",\"value\":\"hello\"},{\"@class\":\"NumericField\",\"value\":42},{\"@class\":\"BooleanField\",\"value\":true}]}",
            Field.class);
            
        assertThat(arrayField, instanceOf(ArrayField.class);
        // => true
    }
}
```

###### Type information customization

It is possible to customize the property which holds the type information in the JSON. We can customize its name and its value with the ```@JsonTypeInfo``` annotation.

Suppose, instead of ```@class``` and the full class name of the object, we want the property to be named ```type``` and its value to be ```string``` for a ```StringField```, ```numeric``` for ```NumericField```, ```boolean``` for a ```BooleanField``` and ```array``` for an ```ArrayField```.

```java
import jin.annotations.JsonType;
import jin.annotations.JsonTypeInfo;
import jin.annotations.JsonTypes;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, property="type")
@JsonTypes(
    {
        @JsonType(id="string", value=StringField.class),
        @JsonType(id="numeric", value=NumericField.class),
        @JsonType(id="boolean", value=BooleanField.class),
        @JsonType(id="array", value=ArrayField.class)
    }
)
public abstract class Field {
    protected String type;
}
```

The ```use``` property of ```@JsonTypeInfo``` is set to ```JsonTypeInfo.Id.CUSTOM``` to specify that we don't want the type info property to hold the full class name of the object and the type info property name is set to ```type```.

The following annotation ```@JsonTypes``` specify for each possible value of the type info property, which concrete subclass of ```Field``` it corresponds to.

We have added a member ```protected String type``` to ```Field```. Each subclass needs to set the value of this member accordingly.

```java
public class StringField extends Field {

    private String value;

    public StringField() {
        this.type = "string";
    }

    public StringField(String value) {
        this.type = "string";
        this.value = value;
    }
}
```

```java
public class NumericField extends Field {

    private int value;

    public NumericField() {
        this.type = "numeric";
    }

    public NumericField(int value) {
        this.type = "numeric";
        this.value = value;
    }
}
```

```java
public class BooleanField extends Field {

    private boolean value;

    public BooleanField() {
        this.type = "boolean";

    }

    public BooleanField(boolean value) {
        this.type = "boolean";
        this.value = value;
    }
}
```

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayField extends Field {

    private List<Field> fields;

    public ArrayField() {
        this.type = "array";
        this.fields = new ArrayList<Field>();
    }

    public ArrayField(List<Field> fields) {
        this.type = "array";
        this.fields = fields;
    }
}
```

If we serialize now, we will have

```java
import jin.Json;

public class Main {
    public static void main(String[] args) {
        ArrayField arrayField = new ArrayField(Arrays.asList(
            new StringField("hello"),
            new NumericField(42),
            new BooleanField(true)
        ));
        
        String json = Json.toJson(arrayField);
        // => "{\"type\":\"array\",\"fields\":[{\"type\":\"string\",\"value\":\"hello\"},{\"type\":\"numeric\",\"value\":42},{\"type\":\"boolean\",\"value\":true}]}"

    }
}
```

And the deserialization will just work as usual.

### In-memory JSON tree

```JsonNode``` is the abstract class which represents a JSON token in memory. Its concrete subclasses are

* ```JsonObject``` for JSON objects.
* ```JsonArray``` for JSON arrays.
* ```JsonInt``` for integers.
* ```JsonLong``` for longs.
* ```JsonDecimal``` for decimal values.
* ```JsonBigInt``` for large numbers.
* ```JsonBoolean``` for boolean values.
* ```JsonNull``` for ```null```.

You can use the methods ```isObject()```, ```isArray()```, ```isString()```, ```isBoolean()```, ... on a ```JsonNode``` to know its concrete type and the methods ```stringValue()```, ```booleanValue()```, ```intValue()```, ```doubleValue()```, ... on a ```JsonNode``` to get the embedded Java value.

To build a ```JsonNode``` from a stream of JSON tokens, use the static methods ```Json.readTree(...)```.

Example

```java
import jin.tree.JsonNode

public class Main {
    public static void main(String[] args) {
        JsonNode node = Json.readTree("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":25}");
        
        assertThat(node, instanceOf(JsonObject.class));
        // => true
        
        JsonObject object = (JsonObject) node;
        
        bool isString = object.get("firstname").isString();
        // => true
        
        String firstname = object.get("firstname").stringValue();
        // => "John"
        
        bool isInt = object.get("age").isInt();
        // => true
        
        int age = object.get("age").intValue();
        // => 25
    }
}
```

You can also build a ```JsonNode``` manually by instantiating the classes ```Json...``` and calling their appropriate methods.

Example

```java
import jin.tree.JsonObject;

public class Main {
    public static void main(String[] args) {
    
        JsonObject object = new JsonObject();
        object.add("firstname", "John")
              .add("lastname", "Doe")
              .add("age", 25);
              
        System.out.println(object.toJsonString());
        // => {"firstname":"John","lastname":"Doe","age":25}
    }
}
```

This is basically all one needs to know in order to use Jin. For further documentation, take a look

* in the file ```src/main/java/jin/Json.java``` for all the to/from JSON conversion options.
* at the methods in the files ```src/main/java/jin/io/JsonReader.java``` and ```src/main/java/jin/io/JsonWriter.java``` to know how to use the streaming API directly.
* in the package ```jin.tree``` for the different types of ```JsonNode``` and how to manipulate them.

## Requirements

* JDK 1.7 or later.

## Contributions

Filling an issue if you find a bug will be mucho appreciated. Pull-requests are welcome.

## License
This library is distributed under the MIT license found in the LICENSE.md file.
