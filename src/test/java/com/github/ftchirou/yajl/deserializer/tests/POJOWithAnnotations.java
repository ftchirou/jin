package com.github.ftchirou.yajl.deserializer.tests;

import com.github.ftchirou.yajl.annotations.Json;
import com.github.ftchirou.yajl.annotations.JsonGetter;
import com.github.ftchirou.yajl.annotations.JsonValue;
import com.github.ftchirou.yajl.serializer.tests.DateTimeSerializer;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.List;

public class POJOWithAnnotations {
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

    @Json(propertyName="birth_date", serializeWith=DateTimeSerializer.class, deserializeWith=DateTimeDeserializer.class)
    private DateTime birthDate;

    private Gender gender;

    private List<String> friends;

    public POJOWithAnnotations() {
        this.friends = new ArrayList<>();
    }

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

    @Override
    public String toString() {
        return "[" + firstName + ", " + lastName + ", " + birthDate + ", " + gender + ", " + friends + "]";
    }
}
