package com.analytics.student.model;

/**
 * Abstract base class representing a person with identity attributes.
 */
public abstract class Person {

    protected String studentId;
    protected String name;

    public Person() {
    }

    public Person(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Abstract method to get a summary String representation of the person.
     *
     * @return Summary string
     */
    public abstract String getDisplaySummary();
}
