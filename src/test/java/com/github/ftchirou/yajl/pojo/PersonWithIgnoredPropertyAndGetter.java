package com.github.ftchirou.yajl.pojo;

import com.github.ftchirou.yajl.annotations.Json;
import com.github.ftchirou.yajl.annotations.JsonGetter;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.List;

public class PersonWithIgnoredPropertyAndGetter {
    private String firstName;

    private String lastName;

    @Json(ignore=true)
    private DateTime birthDate;

    private List<String> friends;

    public PersonWithIgnoredPropertyAndGetter() {
        this.friends = new ArrayList<>();
    }

    public PersonWithIgnoredPropertyAndGetter(String firstName, String lastName, DateTime birthDate, List<String> friends) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.friends = friends;
    }

    @JsonGetter(name="age")
    public int getAge() {
        return Years.yearsBetween(birthDate, DateTime.now()).getYears();
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
