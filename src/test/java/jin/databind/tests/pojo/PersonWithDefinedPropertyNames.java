package jin.databind.tests.pojo;

import jin.annotations.Json;

import java.util.ArrayList;
import java.util.List;

public class PersonWithDefinedPropertyNames {

    @Json(property="first_name")
    private String firstName;

    @Json(property="last_name")
    private String lastName;

    @Json(property="AGE")
    private int age;

    @Json(property="FRIENDS")
    private List<String> friends;

    public PersonWithDefinedPropertyNames() {
        this.friends = new ArrayList<>();
    }

    public PersonWithDefinedPropertyNames(String firstName, String lastName, int age, List<String> friends) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.friends = friends;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
