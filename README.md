## Jin
#### Simple and fast JSON processing.

### What Jin is ?
Jin is a lightweight library for processing JSON in Java. It uses a streaming API for fast JSON processing and can be used in 2 modes

* **Databind** Serialize or deserialize Java objects directly to or from a JSON stream. The stream can be a string, a file, a response from an HTTP request, basically any valid Java InputStream, OutputStream.
* **In-memory tree representation** Construct a mutable in-memory tree representation from a JSON stream.
 
### Usage

#### Databind

##### Basic Serialization
###### Primitives, array, list and map serialization
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
      
      String list = Json.toJson(Arrays.asList("one", "two", "three", "four", "five")); 
      // => "[\"one\",\"two\",\"three\",\"four\",\"five\"]"
      
      String mixedArray = Json.toJson(Arrays.asList(42, Double.valueOf(3.4d), true, Arrays.asList("one", "two", "three"), null)); 
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
 
##### Basic Deserialization

###### Primitives deserialization

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

###### JSON Array deserialization

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

The constructor of the class ```CollectionType``` takes 2 arguments of type ```java.lang.reflect.Type```. The first is the type of the container (ArrayList, ArrayDeque, LinkedHashSet, ...) and the second is the type of each element.

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


