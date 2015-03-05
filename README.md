## Jin
#### Simple and fast JSON processing.

### What Jin is ?
Jin is a lightweight library for processing JSON in Java. It uses a streaming API for fast JSON processing and can be used in 2 modes

* **Databind** Serialize or deserialize Java objects directly to or from a JSON stream. The stream can be a string, a file, a response from an HTTP request, basically any valid Java InputStream, OutputStream.
* **In-memory tree representation** Construct a mutable in-memory tree representation from a JSON stream.
 
### Usage

#### Databind

##### Serialization
###### Basic serialization
Just pass the object or the primitive to be serialized to one of the static methods ```Json.toJson(...)```.
```java

import jin.Json;

public class Main {
  public static void main(String[] args) {
      String aString = Json.toJson("hello"); // => "\"hello\""
      String aInt = Json.toJson(42); // => "42"
      String aLong = Json.toJson(128L); // => "128"
      String aFloat = Json.toJson(2.45f); // => "2.45"
      String aBool = Json.toJson(true); // => "true"
      String aNull = Json.toJson(null); // => "null"
      String array = Json.toJson(new int[] {1, 2, 3, 4, 5}); // => "[1,2,3,4,5]"
      String list = Json.toJson(Arrays.asList("one", "two", "three", "four", "five")); // => "[\"one\",\"two\",\"three\",\"four\",\"five\"]"
      String mixedArray = Json.toJson(Arrays.asList(42, Double.valueOf(3.4d), true, Arrays.asList("one", "two", "three"), null)); // => "[42,3.4,true,[\"one\",\"two\",\"three\"],null]"
          
      Map<String, Integer> map = new HashMap<>();
      map.put("one", 1);
      map.put("two", 2);
      map.put("three", 3);
      
      String aMap = Json.toJson(map); // => "{\"one\":1,\"two\":2,\"three\":3}"
  }
}
```

To write to a file use ```Json.toJson(Object, java.io.File)``` or use ```Json.toJson(Object, java.io.OutputStream)``` to write to any output stream

###### POJO serialization
To serialize a Plain Old Java Object, again, pass it to one of the static methods ```Json.toJson(...)```.

````java
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
        
        String json = Json.toJson(person); // => "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"MALE\",\"age\":25}"
    }
}
```

You can control how the object members are translated in JSON with annotations.

* We can specify the JSON property name of a member by annotating the member with ```@Json(property="<property_name>")```.
  
  Example

  ```java
  public class Person {
    ...
    
    @Json(property="first_name")
    private String firstName;
    
    ...
  }
  ```
  
  Calling ```Json.toJson(new Person("John", "Doe", Person.Gender.MALE, 25)``` will now return ```"{\"first_name\":\"John\",\"lastName\":\"Doe\",\"gender\":\"MALE\",\"age\":25}"```.

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
  Json.toJson(new Person("John", "Doe", Person.Gender.MALE, 25); // => "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":25}"
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
Json.toJson(new Person("John", "Doe", Person.Gender.MALE, 25); // => "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"gender\":\"XY\",\"age\":25}"
```

##### Collection serialization

Example
```java
Person person = new Person("John", "Doe", 25);

Json.toJson(person, new File("person.json")); // Serialize the object person to the file person.json.
```
