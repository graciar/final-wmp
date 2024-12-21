package com.example.afinal.enrollment;

public class Subject {
    private String code;
    private String name;
    private int credit;
    private String department;

    public Subject(String code, String name, int credit, String department) {
        this.code = code;
        this.name = name;
        this.credit = credit;
        this.department = department;
    }

    public Subject() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
