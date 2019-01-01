package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the application icon.
        primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("view/login.fxml"));
        Pane myPane = myLoader.load();
        LoginController controller = myLoader.getController();
        controller.setPrevStage(primaryStage);
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(myPane));
        primaryStage.show();
    }


}
