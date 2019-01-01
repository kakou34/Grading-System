package sample.model;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class Course {
    private final IntegerProperty id;
    private final StringProperty courseName;
    private final IntegerProperty quota;

    public Course(int courseId, String courseName, int quota) {
        this.id = new SimpleIntegerProperty(courseId);
        this.courseName = new SimpleStringProperty(courseName);
        this.quota = new SimpleIntegerProperty(quota);
    }

    public int getId(){return id.get();}
    public String getCourseName() {return courseName.get();}
    public int getQuota(){return quota.get();}

    public StringProperty courseNameProperty() { return courseName; }
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty quotaProperty() { return quota; }

    public void setId(int id){this.id.set(id);}
    public void setCourseName (String str) {this.courseName.set(str);}
    public void setQuota (int quota){this.quota.set(quota);}
}
