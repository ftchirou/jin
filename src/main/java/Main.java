import jin.Json;

import java.io.File;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Person person = new Person("John", "Doe", 25, Arrays.asList("Jane", "Jill", "Helen"));

        try {
            Json.toJson(person, new File("person.json"));

            Person p = Json.fromJson(new File("person.json"), Person.class);
            System.out.println(p);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
