package com.example.afinal.enrollment;

import java.util.List;

public class EnrollmentHelperClass {
    private String userId;
    private List<String> enrolledSubjects;

    public EnrollmentHelperClass(String userId, List<String> enrolledSubjects) {
        this.userId = userId;
        this.enrolledSubjects = enrolledSubjects;
    }

    public EnrollmentHelperClass() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getEnrolledSubjects() {
        return enrolledSubjects;
    }

    public void setEnrolledSubjects(List<String> enrolledSubjects) {
        this.enrolledSubjects = enrolledSubjects;
    }
}
