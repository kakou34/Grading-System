package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.model.Enrollement;
import sample.util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static sample.LoginController.showAlert;

public class CourseController {
    @FXML
    public TextField txtID;
    @FXML
    public TextField txtUpdateGrade;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnShowAll;
    Connection conn;
    @FXML
    public TableColumn<Object, Object> colCourseId;
    @FXML
    public TableColumn<Object, Object> colStudentId;
    @FXML
    public TableColumn<Object, Object> colGrade;
    @FXML
    public TextField txtStudentID;
    @FXML
    public TextField txtGrade;
    @FXML
    public Button btnAdd;
    @FXML
    public TableView<Enrollement> tblGrades;
    private int courseID;
    private ObservableList<Enrollement> data;

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        //Set cell value factory to tableview
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public void btnAddClick(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try{
            int student = Integer.valueOf(txtStudentID.getText());
            double grade = Double.valueOf(txtGrade.getText());

            if (!txtStudentID.getText().isEmpty() &&
                    !txtGrade.getText().isEmpty() ) {
                conn = DBUtil.dbConnect();
                String query = "UPDATE enrollement SET GRADE='" + grade + "' WHERE CourseID= '" + courseID +"' AND StudentID =" + student;
                Statement stmt = conn.createStatement();
                int result = stmt.executeUpdate(query);
                if (result == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Sucessfully Added");
                    alert.showAndWait();
                    txtGrade.setText("");
                    txtStudentID.setText("");
                    showAll();
                    stmt.close();
                    conn.close();
                } else showAlert("No student found");
            } else {
                showAlert("Please make sure you filled all required information");
            }
        } catch (Exception e) {
            showAlert("Please make sure you filled valid data");
        }
    }

    public void btnUpdate(ActionEvent actionEvent) throws SQLException, ClassNotFoundException  {
        try{
            Integer id = Integer.valueOf(txtID.getText());
            double grade = Double.valueOf(txtUpdateGrade.getText());

            if (!txtUpdateGrade.getText().isEmpty() && !txtID.getText().isEmpty()) {
                conn = DBUtil.dbConnect();
                String query = "UPDATE enrollement SET GRADE='" + grade + "' WHERE CourseID= '" + courseID +"' AND StudentID =" + id;
                Statement stmt = conn.createStatement();
                int result = stmt.executeUpdate(query);
                if (result == 1) {
                    showAlert("User updated");
                    showAll();
                }
                stmt.close();
                DBUtil.dbDisconnect(conn);
            } else {
               showAlert("Please make sure you filled all required information");
            }
        } catch (NumberFormatException e){
            showAlert("Please make sure you gave valid data");
        }

    }

    public void btnShowAll(ActionEvent actionEvent) {
        data = FXCollections.observableArrayList();
        try {
            conn = DBUtil.dbConnect();
            String query = "SELECT * FROM enrollement WHERE CourseID=" + courseID;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.add(new Enrollement(rs.getInt(1), rs.getInt(2), rs.getDouble(3)));
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        tblGrades.setItems(data);
    }

    public void tblColClick(){
        if (!tblGrades.getSelectionModel().getSelectedIndices().isEmpty()) {
            txtID.setText(tblGrades.getSelectionModel().getSelectedItem().getStudentId()+"");
            txtUpdateGrade.setText(tblGrades.getSelectionModel().getSelectedItem().getGrade()+"");
        }
    }

    public void showAll(){
        data = FXCollections.observableArrayList();
        try {
            conn = DBUtil.dbConnect();
            String query = "SELECT * FROM enrollement WHERE CourseID=" + courseID;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.add(new Enrollement(rs.getInt(1), rs.getInt(2), rs.getDouble(3)));
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        tblGrades.setItems(data);
    }
}
