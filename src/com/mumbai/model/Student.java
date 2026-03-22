package com.mumbai.model;

public class Student {
    private int    studentId;
    private String name;
    private String rollNumber;
    private String email;
    private String phone;
    private String branch;
    private int    currentSem;
    private int    yearOfStudy;
    private double cgpa;
    private double totalPercentage;

    // Getters & Setters
    public int    getStudentId()                       { return studentId; }
    public void   setStudentId(int v)                  { this.studentId = v; }
    public String getName()                            { return name; }
    public void   setName(String v)                    { this.name = v; }
    public String getRollNumber()                      { return rollNumber; }
    public void   setRollNumber(String v)              { this.rollNumber = v; }
    public String getEmail()                           { return email; }
    public void   setEmail(String v)                   { this.email = v; }
    public String getPhone()                           { return phone; }
    public void   setPhone(String v)                   { this.phone = v; }
    public String getBranch()                          { return branch; }
    public void   setBranch(String v)                  { this.branch = v; }
    public int    getCurrentSem()                      { return currentSem; }
    public void   setCurrentSem(int v)                 { this.currentSem = v; }
    public int    getYearOfStudy()                     { return yearOfStudy; }
    public void   setYearOfStudy(int v)                { this.yearOfStudy = v; }
    public double getCgpa()                            { return cgpa; }
    public void   setCgpa(double v)                    { this.cgpa = v; }
    public double getTotalPercentage()                 { return totalPercentage; }
    public void   setTotalPercentage(double v)         { this.totalPercentage = v; }

    @Override public String toString() {
        return name + " [" + rollNumber + " | " + branch + " | Sem " + currentSem + "]";
    }
}
