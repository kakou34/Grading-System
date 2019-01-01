package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.Course;
import sample.util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static sample.LoginController.showAlert;

public class InstructorController {

    private Connection conn;
    @FXML
    public TableView<Course> tblCourses;
    @FXML
    public TableColumn<Object, Object> colId;
    @FXML
    public TableColumn<Object, Object> colCourseName;
    @FXML
    public TableColumn<Object, Object> colQuota;
    @FXML
    public TextField txtID;
    @FXML
    public TextField txtCourseName;
    @FXML
    public TextField txtQuota;
    @FXML
    public Button btnAdd;
    @FXML
    public TextField txtShowID;
    @FXML
    public Button btnShowCourse;
    @FXML
    public Button btnShowAll;

    private ObservableList<Course> data;

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        //Set cell value factory to tableview
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colQuota.setCellValueFactory(new PropertyValueFactory<>("quota"));
    }

    public void btnAddClick (ActionEvent actionEvent) throws SQLException, ClassNotFoundException{
        try {
            Integer id = Integer.valueOf(txtID.getText());
            String name = txtCourseName.getText();
            Integer quota = Integer.valueOf(txtQuota.getText());
            if (!txtCourseName.getText().isEmpty() &&
                    !txtQuota.getText().isEmpty() ) {
                conn = DBUtil.dbConnect();
                String query = "INSERT INTO course (ID, COURSE_NAME, QUOTA) VALUES ('" + id + "','" + name + "','" + quota + "')";
                Statement stmt = conn.createStatement();
                int result = stmt.executeUpdate(query);
                if (result == 1) {
                    showAlert("Sucessfully Added");
                    txtID.setText("");
                    txtCourseName.setText("");
                    txtQuota.setText("");
                    showAllCourses();
                    stmt.close();
                    conn.close();
                }
            } else {
                showAlert("Please make sure you filled all required information");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            showAlert("Course ID must be unique");
        } catch (NumberFormatException e) {
            showAlert("ID must be a non-zero number of at most 11 digits and Quota a number of at most 4 digits");
        }
    }

    public void btnShowClick(ActionEvent actionEvent) throws IOException {
        try {
            int id = Integer.parseInt(txtShowID.getText());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/course.fxml"));
            AnchorPane page = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Course Page");
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            stage.setScene(scene);
            CourseController controller = loader.getController();
            controller.setCourseID(id);
            stage.show();
        } catch (NumberFormatException e) {
            showAlert("Please write the course's ID");
        }
    }

    public void btnShowAll(ActionEvent actionEvent) {
        data = FXCollections.observableArrayList();
        try {
            conn = DBUtil.dbConnect();
            String query = "SELECT * FROM course";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.add(new Course(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        tblCourses.setItems(data);
    }

    private void showAllCourses() throws SQLException, ClassNotFoundException {
        data = FXCollections.observableArrayList();
        tblCourses.getItems().clear();
        conn = DBUtil.dbConnect();
        String query = "SELECT * FROM course";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            data.add(new Course(rs.getInt(1), rs.getString(2), rs.getInt(3)));
        }
            rs.close();
            stmt.close();
            DBUtil.dbDisconnect(conn);
            tblCourses.setItems(data);
    }

    public void tblColClick(MouseEvent mouseEvent) {
        if (!tblCourses.getSelectionModel().getSelectedIndices().isEmpty()) {
            txtShowID.setText(tblCourses.getSelectionModel().getSelectedItem().getId()+"");
        }
    }

}
