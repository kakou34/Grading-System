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
import sample.model.User;
import sample.util.DBUtil;

import java.io.IOException;
import java.sql.*;

public class MainController {

    @FXML
    public Button btnSearch;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnEdit;
    public TextField txtID;
    Connection conn;

    @FXML
    public ChoiceBox userType;
    @FXML
    private TableView<User> tblUsers;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colUserName;
    @FXML
    private TableColumn<User, String> colLastName;
    @FXML
    private TableColumn<User, String> colFirstName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colTelephone;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtUserName;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTelephone;
    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUpdateid;
    @FXML
    private TextField txtUpdateTelephone;

    @FXML
    private TextField txtUpdateEmail;


    private ObservableList<User> data;
    private Main mainApp;

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        //Set cell value factory to tableview
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    }


    public void btnShowAllUser(ActionEvent actionEvent) {
        String role = userType.getValue().toString();
        data = FXCollections.observableArrayList();

        try {

            conn = DBUtil.dbConnect();
            String query = "SELECT * FROM " + role;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);


            while (rs.next()) {
                data.add(new User(rs.getInt(1), rs.getString(6), rs.getString(7), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }


            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        tblUsers.setItems(data);


    }

    public void tblColClick(MouseEvent mouseEvent) {
        if (!tblUsers.getSelectionModel().getSelectedIndices().isEmpty()) {
            txtUpdateEmail.setText(tblUsers.getSelectionModel().getSelectedItem().getEmail().toString());
            txtUpdateTelephone.setText(tblUsers.getSelectionModel().getSelectedItem().getTelephone().toString());
            txtUpdateid.setText(String.valueOf(tblUsers.getSelectionModel().getSelectedItem().getId()));
        }
    }


    public void btnAddClick(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String role = userType.getValue().toString().trim();
        try {
            Integer id = Integer.valueOf(txtID.getText());
            String username = txtUserName.getText();
            String password = txtPassword.getText();
            String firstname = txtName.getText();
            String lastname = txtSurname.getText();
            String email = txtEmail.getText();
            String telephone = txtTelephone.getText();

            if (!txtUserName.getText().isEmpty()     &&
                    !txtPassword.getText().isEmpty() &&
                    !txtName.getText().isEmpty()     &&
                    !txtSurname.getText().isEmpty()  &&
                    !txtEmail.getText().isEmpty()    &&
                    !txtTelephone.getText().isEmpty()) {
                conn = DBUtil.dbConnect();
                String query = "INSERT INTO " + role + " (ID, USERNAME, PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE) VALUES ('" + id + "','" + username + "','" + password + "','" + firstname + "','" + lastname + "','" + email + "','" + telephone + "')";
                String query2 = "INSERT INTO user (ID, USERNAME) VALUES ('" + id + "','" + username + "')";
                Statement stmt = conn.createStatement();
                int result2 = stmt.executeUpdate(query2);
                int result = stmt.executeUpdate(query);
                if (result == 1 && result2==1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Sucessfully Added");
                    alert.showAndWait();
                    txtID.setText("");
                    txtUserName.setText("");
                    txtPassword.setText("");
                    txtName.setText("");
                    txtSurname.setText("");
                    txtEmail.setText("");
                    txtTelephone.setText("");
                    showAllUser();
                    stmt.close();
                    conn.close();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please make sure you filled all required information");
                alert.showAndWait();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("ID and Username must be unique");
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter a valid ID");
            alert.showAndWait();
        }
    }

    public void btnSearchClick(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        tblUsers.getItems().clear();
        String role = userType.getValue().toString();
        data = FXCollections.observableArrayList();
        try {
            Integer id = Integer.valueOf(txtUpdateid.getText());
            conn = DBUtil.dbConnect();
            String query = "SELECT * FROM " + role + " WHERE ID= " + txtUpdateid.getText();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.add(new User(rs.getInt(1), rs.getString(6), rs.getString(7), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
            rs.close();
            stmt.close();
            conn.close();
            tblUsers.setItems(data);
        } catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("please enter the correct user's ID");
            alert.showAndWait();
        }
    }


    public void btnUpdateClick(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String role = userType.getValue().toString();
        String email = txtUpdateEmail.getText();
        String telephone = txtUpdateTelephone.getText();

        if (!txtUpdateid.getText().isEmpty() && !email.isEmpty() && !telephone.isEmpty()) {
            conn = DBUtil.dbConnect();
            String query = "UPDATE " + role + " SET EMAIL='" + email + "' , TELEPHONE='" + telephone + "' WHERE ID= " + txtUpdateid.getText();
            Statement stmt = conn.createStatement();
            int result = stmt.executeUpdate(query);
            if (result == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("User updated");
                alert.showAndWait();
                showAllUser();

            }
            stmt.close();
            DBUtil.dbDisconnect(conn);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please make sure you filled all required information");
            alert.showAndWait();
        }

    }


    public void btnDeleteClick(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            String role = userType.getValue().toString();
            if (!txtUpdateid.getText().isEmpty()) {
                conn = DBUtil.dbConnect();
                String query0 = "DELETE FROM `enrollement` WHERE StudentID=" + txtUpdateid.getText();
                String query = "DELETE FROM " + role + " WHERE ID=" + txtUpdateid.getText();
                String query2 = "DELETE FROM user WHERE ID=" + txtUpdateid.getText();
                Statement stmt = conn.createStatement();
                int result0 = stmt.executeUpdate(query0);
                int result = stmt.executeUpdate(query);
                int result2 = stmt.executeUpdate(query2);
                if (result == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("User Deleted Sucessfully");
                    alert.showAndWait();
                    showAllUser();

                }
                stmt.close();
                DBUtil.dbDisconnect(conn);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("please enter the correct user's ID");
                alert.showAndWait();
            }
        } catch (SQLIntegrityConstraintViolationException e){

            System.out.println("SQL Constraint violation Exception");
        }
    }


    public void showAllUser() throws SQLException, ClassNotFoundException {
        data = FXCollections.observableArrayList();
        String role = userType.getValue().toString();
        tblUsers.getItems().clear();
        conn = DBUtil.dbConnect();
        String query = "SELECT * FROM " + role;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            data.add(new User(rs.getInt(1), rs.getString(6), rs.getString(7), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
        }
        rs.close();
        stmt.close();
        DBUtil.dbDisconnect(conn);
        tblUsers.setItems(data);
    }


    public void editUser(ActionEvent actionEvent) {
        if (!tblUsers.getSelectionModel().getSelectedIndices().isEmpty()) {
            User selectedPerson = tblUsers.getSelectionModel().getSelectedItem();
            showUserEditDialog(selectedPerson);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select user");
            alert.showAndWait();
        }
    }


    public void showUserEditDialog(User user) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/editUser.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage stage = new Stage();
            stage.setTitle("Edit Person");
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(page);
            stage.setScene(scene);

            // Set the user into the controller.
            EditUserController controller = loader.getController();
            controller.setRole(userType.getValue().toString().trim().toLowerCase());
            controller.setUser(user);

            stage.show();


        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
