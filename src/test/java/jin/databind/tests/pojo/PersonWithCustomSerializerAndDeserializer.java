package jin.databind.tests.pojo;

import jin.annotations.Json;
import jin.databind.tests.DateTimeDeserializer;
import jin.databind.tests.DateTimeSerializer;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class PersonWithCustomSerializerAndDeserializer {
    private String firstName;

    private String lastName;

    @Json(serializeWith=DateTimeSerializer.class, deserializeWith=DateTimeDeserializer.class)
    private DateTime birthDate;

    private List<String> friends;

    public PersonWithCustomSerializerAndDeserializer() {
        this.friends = new ArrayList<>();
    }

    public PersonWithCustomSerializerAndDeserializer(String firstName, String lastName, DateTime birthDate, List<String> friends) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
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

    public DateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(DateTime birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
