package com.github.ftchirou.yajl;

import com.github.ftchirou.yajl.annotations.Json;
import com.github.ftchirou.yajl.annotations.JsonTypeInfo;
import com.github.ftchirou.yajl.serializer.JsonBaseSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
public class Person {

    protected String type;

    @Json(propertyName="first_name")
    private String firstName;

    @Json(propertyName="last_name")
    private String lastName;

    @Json(propertyName="Age")
    private int age;

    @Json(propertyName="skills_list")
    public List<String> skills;

    public HashMap<String, Integer> skillsValues;

    private boolean isSkilled;

//    public List<ArrayList<ArrayList<Integer>>> list;


    public Person() {
        this.type = "person";

        skills = new ArrayList<>();
        skillsValues = new HashMap<>();
//        list = new ArrayList<>();
    }

    public Person(String firstName, String lastName, int age) {
        this.type = "person";

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.skills = new ArrayList<>();
        this.skills.add("JavaScript");
        this.skills.add("Java");
        this.skills.add("C#");

        this.skillsValues = new LinkedHashMap<>();
        this.skillsValues.put("JavaScript", 98);
        this.skillsValues.put("Java", 99);
        this.skillsValues.put("C#", 87);

        this.isSkilled = true;
    }

    @Override
    public String toString() {
//        return "[" + firstName + ", " + lastName + ", " + age + ", " + skills + ", " + skillsValues + ", " + isSkilled + ", " + list + "]";
        return "[" + firstName + ", " + lastName + ", " + age + ", " + skills + ", " + skillsValues + ", " + isSkilled + "]";

    }
}
