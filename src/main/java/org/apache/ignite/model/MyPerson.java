package org.apache.ignite.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;

import java.io.Serializable;

public class MyPerson implements Serializable {
    public Long getId() {
        return id;
    }

    /** MyPerson ID (indexed). */
    @QuerySqlField(index = true)
    private Long id;

    /** Organization ID (indexed). */
    @QuerySqlField(index = true)
    private Long orgId;

    /** First name (not-indexed). */
    @QuerySqlField
    private String firstName;

    /** Last name (not indexed). */
    @QuerySqlField
    private String lastName;

    public void setResume(String resume) {
        this.resume = resume;
    }

    /** Resume text (create LUCENE-based TEXT index for this field). */
    @QueryTextField
    private String resume;

    public MyPerson(Long id){
        this.id = id;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    /** Salary (indexed). */
    @QuerySqlField(index = true)
    private double salary;


}