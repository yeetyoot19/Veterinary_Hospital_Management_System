package application;

import java.util.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
        	System.out.println(System.getProperty("java.class.path"));

        	Class.forName("com.mysql.cj.jdbc.Driver");

            Main.primaryStage = primaryStage; // Store the primary stage reference for scene switching
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void changeScene(Parent root) {
        Stage stage = (Stage) primaryStage.getScene().getWindow(); // Adjust if primaryStage is a static field
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}
