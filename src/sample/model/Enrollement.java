package sample.model;

import javafx.beans.property.*;

public class Enrollement {

    private final IntegerProperty courseId;
    private final IntegerProperty studentID;
    private final DoubleProperty grade;

    public Enrollement(int courseId, int studentID, double grade) {
        this.courseId = new SimpleIntegerProperty(courseId);
        this.studentID = new SimpleIntegerProperty(studentID);
        this.grade = new SimpleDoubleProperty(grade);
    }

    public int getCourseId(){return courseId.get();}
    public int getStudentId() {return studentID.get();}
    public double getGrade(){return grade.get();}

    public IntegerProperty StudentIdProperty() { return studentID; }
    public IntegerProperty courseIdProperty() { return courseId; }
    public DoubleProperty GradeProperty() { return grade; }

    public void setCourseId(int id){this.courseId.set(id);}
    public void setStudentID (int id) {this.studentID.set(id);}
    public void setGrade (double grade){this.grade.set(grade);}
}
