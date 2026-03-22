package com.mumbai.model;

public class SubjectMark {
    private int    markId;
    private int    studentId;
    private int    semester;
    private String subjectName;
    private String subjectCode;
    private double ia1Marks;
    private double ia2Marks;
    private double eseMarks;
    private double eseTotal;
    private double practicalMarks;
    private double practicalTotal;
    private double totalMarks;
    private double gradePoint;
    private String grade;
    private int    credits;

    // Getters & Setters
    public int    getMarkId()                          { return markId; }
    public void   setMarkId(int v)                     { this.markId = v; }
    public int    getStudentId()                       { return studentId; }
    public void   setStudentId(int v)                  { this.studentId = v; }
    public int    getSemester()                        { return semester; }
    public void   setSemester(int v)                   { this.semester = v; }
    public String getSubjectName()                     { return subjectName; }
    public void   setSubjectName(String v)             { this.subjectName = v; }
    public String getSubjectCode()                     { return subjectCode; }
    public void   setSubjectCode(String v)             { this.subjectCode = v; }
    public double getIa1Marks()                        { return ia1Marks; }
    public void   setIa1Marks(double v)                { this.ia1Marks = v; }
    public double getIa2Marks()                        { return ia2Marks; }
    public void   setIa2Marks(double v)                { this.ia2Marks = v; }
    public double getEseMarks()                        { return eseMarks; }
    public void   setEseMarks(double v)                { this.eseMarks = v; }
    public double getEseTotal()                        { return eseTotal; }
    public void   setEseTotal(double v)                { this.eseTotal = v; }
    public double getPracticalMarks()                  { return practicalMarks; }
    public void   setPracticalMarks(double v)          { this.practicalMarks = v; }
    public double getPracticalTotal()                  { return practicalTotal; }
    public void   setPracticalTotal(double v)          { this.practicalTotal = v; }
    public double getTotalMarks()                      { return totalMarks; }
    public void   setTotalMarks(double v)              { this.totalMarks = v; }
    public double getGradePoint()                      { return gradePoint; }
    public void   setGradePoint(double v)              { this.gradePoint = v; }
    public String getGrade()                           { return grade; }
    public void   setGrade(String v)                   { this.grade = v; }
    public int    getCredits()                         { return credits; }
    public void   setCredits(int v)                    { this.credits = v; }
}
