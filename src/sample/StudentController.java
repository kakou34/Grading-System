package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import sample.model.Course;
import sample.model.Enrollement;
import sample.util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import static sample.LoginController.showAlert;

public class StudentController {
    @FXML
    public TableView<Course> tblCourse2;
    @FXML
    public TableColumn<Object, Object> colID;
    @FXML
    public TableColumn<Object, Object> colCourseName;
    @FXML
    public TableColumn<Object, Object> colQuota;
    @FXML
    public Label lblgrade;
    @FXML
    public Label lblaverage;
    @FXML
    private Connection conn;
    @FXML
    public TableView<Enrollement> tblCourse;
    @FXML
    public TableColumn<Object, Object> colCourseId;
    @FXML
    public TableColumn<Object, Object> colStudentId;
    @FXML
    public TableColumn<Object, Object> colGrade;
    @FXML
    public Label studentName;
    @FXML
    public TextField txtCourseID;
    @FXML
    public Button btnShowGrade;
    @FXML
    public TextField txtID;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnShowMine;
    @FXML
    public Button btnShowAll;

    private String user;
    private ObservableList<Enrollement> data;
    private ObservableList<Course> data2;

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        //Set cell value factory to tableviews
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colQuota.setCellValueFactory(new PropertyValueFactory<>("quota"));

    }

    public void tblColClick(MouseEvent mouseEvent) {
        if (!tblCourse2.getSelectionModel().getSelectedIndices().isEmpty()) {
            txtID.setText(tblCourse2.getSelectionModel().getSelectedItem().getId() + "");
        }

    }

    public void btnShowGradeClick(ActionEvent actionEvent) {
        try {
            double grade = 0;
            int studentId = getUserID();
            int courseId = Integer.parseInt(txtCourseID.getText());
            double average = getClassAverage(courseId);
            conn = DBUtil.dbConnect();
            String query = "SELECT `GRADE` FROM `enrollement` WHERE `StudentID` = "+ studentId + " AND `CourseID` = " + courseId;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(!rs.next()) {
                showAlert("You are not registered in this Course");
            } else {
                rs = stmt.executeQuery(query);
            while (rs.next()) {
                grade = rs.getDouble(1);
            }
            rs.close();
            stmt.close();
            conn.close();
            lblgrade.setText(grade+"");
            lblaverage.setText(new DecimalFormat("###.#").format(average));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Exception");
        } catch (NumberFormatException e){
            showAlert("Please enter a valid ID");
        }

    }

    public void btnEnroll(ActionEvent actionEvent) {
        try {
            int id = Integer.valueOf(txtID.getText());
            int userId = getUserID();
            if (isValid(id)) {
                conn = DBUtil.dbConnect();
                String query = "INSERT INTO enrollement (CourseID, StudentID) VALUES ('" + id + "','" + userId + "')";
                Statement stmt = conn.createStatement();
                int result = stmt.executeUpdate(query);
                if (result == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Sucessfully Added");
                    alert.showAndWait();
                    txtID.setText("");
                    stmt.close();
                    conn.close();
                }
            } else { showAlert("This Course's quota is full OR The course does not exist");}
        } catch (NumberFormatException e) {
            showAlert("Please make sure the CourseID is correct");
        } catch (java.sql.SQLIntegrityConstraintViolationException e){
            showAlert("You cannot enroll in the same course twice");
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    public void btnShowMine(ActionEvent actionEvent) {
        tblCourse.setVisible(true);
        tblCourse2.setVisible(false);
        data = FXCollections.observableArrayList();
        try {
            int id = getUserID();
            conn = DBUtil.dbConnect();
            String query = "SELECT * FROM `enrollement` WHERE `StudentID` = "+id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.add(new Enrollement(rs.getInt(1), rs.getInt(2), rs.getDouble(3)));
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Exception");
        }
        tblCourse.setItems(data);

    }

    public void btnShowAll(ActionEvent actionEvent) {
        tblCourse.setVisible(false);
        tblCourse2.setVisible(true);
        data2 = FXCollections.observableArrayList();
        try {
            conn = DBUtil.dbConnect();
            String query = "SELECT * FROM course";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data2.add(new Course(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        tblCourse2.setItems(data2);
    }

    public void setUser(String str) {
        if (str != null && !str.isEmpty()) {
            this.user = str;
        }
    }

    //helper method
    private boolean isValid(int courseID) throws SQLException,ClassNotFoundException {
        int quota = 0;
        int count = 0;
            conn = DBUtil.dbConnect();
            String query = "SELECT `QUOTA` FROM `course` WHERE `ID` = " + courseID;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                quota = rs.getInt(1);
            }

            String query2 = "SELECT * FROM `enrollement` WHERE `CourseID` = " + courseID;
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery(query2);
            while (rs2.next()) {
                count++;
            }
            rs.close();
            stmt.close();
            conn.close();

        return quota > count;
    }

    private int getUserID() throws SQLException, ClassNotFoundException {
        int id = 0;
            conn = DBUtil.dbConnect();
            String query = "SELECT ID FROM student WHERE USERNAME LIKE '" + user +"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt(1);
            }
        return id;
    }

    public double getClassAverage(int courseId) throws SQLException, ClassNotFoundException {
        double sum = 0;
        int count = 0;
        conn = DBUtil.dbConnect();
        String query = "SELECT `GRADE` FROM `enrollement` WHERE `CourseID` = " + courseId;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            sum += rs.getDouble(1);
            count++;
        }
        rs.close();
        stmt.close();
        conn.close();
        return (sum/count);
    }

}
