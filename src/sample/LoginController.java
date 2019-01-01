package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    PreparedStatement pst = null;
    ResultSet resultSet = null;
    Stage prevStage;

    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ChoiceBox userRole;

    public void setPrevStage(Stage stage) {
        this.prevStage = stage;
    }

    public void login(ActionEvent actionEvent) {
        String userRoleChoice = userRole.getValue().toString();
        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        try {
            Connection conn = DBUtil.dbConnect();

            String query = null;
            switch (userRoleChoice) {
                case "Admin": query = "SELECT * FROM admin WHERE USERNAME=? AND PASSWORD=?"; break;
                case "Instructor": query = "SELECT * FROM instructor WHERE USERNAME=? AND PASSWORD=?"; break;
                case "Student": query = "SELECT * FROM student WHERE USERNAME=? AND PASSWORD=?"; break;
            }
            pst = conn.prepareStatement(query);
            pst.setString(1, userName);
            pst.setString(2, password);
            resultSet = pst.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login Successful");

                switch (userRoleChoice){
                    case "Admin": showMainView(); break;
                    case "Instructor": showInstructorView(); break;
                    case "Student": showStudentView(userName); break;
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Form Error!");
                alert.setHeaderText(null);
                alert.setContentText("Username or password wrong");
                alert.show();
            }
            pst.close();
            resultSet.close();
            DBUtil.dbDisconnect(conn);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void showMainView() {
        try {
            Pane myPane = FXMLLoader.load(getClass().getResource("view/main.fxml"));
            Scene scene = new Scene(myPane);
            Stage stage = new Stage();
            stage.setTitle("Main Page");
            stage.setScene(scene);
            prevStage.close();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void showStudentView( String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Student.fxml"));
            Pane myPane = loader.load();
            StudentController controller = loader.<StudentController>getController();
            controller.setUser(username);
            Scene scene = new Scene(myPane);
            Stage stage = new Stage();
            stage.setTitle("Student Page");
            stage.setScene(scene);
            prevStage.close();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void showInstructorView() {
        try {
            Pane myPane = FXMLLoader.load(getClass().getResource("view/Instructor.fxml"));
            Scene scene = new Scene(myPane);
            Stage stage = new Stage();
            stage.setTitle("Instructor Form");
            stage.setScene(scene);
            prevStage.close();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public static void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
