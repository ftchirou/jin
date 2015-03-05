package com.github.ftchirou.yajl.pojo;

import java.util.List;

public class Employee extends Person {

    private String employerName;

    public Employee() {
        super();
    }

    public Employee(String firstName, String lastName, int age, List<String> friends, String employerName) {
        super(firstName, lastName, age, friends);

        this.employerName = employerName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
}
