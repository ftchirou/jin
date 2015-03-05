package com.github.ftchirou.yajl.pojo;

import java.util.ArrayList;
import java.util.List;

public class Person {

    private String firstName;

    private String lastName;

    private int age;

    private List<String> friends;

    public Person() {
        this.friends = new ArrayList<>();
    }

    public Person(String firstName, String lastName, int age, List<String> friends) {
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
