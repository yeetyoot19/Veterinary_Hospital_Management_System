package controller;

import application.Main;

import model.Admin;
import model.Clinic;
import model.Doctor;
import model.User;

import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

import PersistenceHandler.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button LoginButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField usernameField; // Your TextField for username

    @FXML
    private PasswordField passwordField; // Your PasswordField for password
    
    SessionManager sessionManager;

    @FXML
    void handleLoginButtonClick(MouseEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Object result = Clinic.verifyLogin(username, password);
        
        sessionManager = SessionManager.getInstance();

        if (result instanceof User) {
            sessionManager.setCurrentUser((User) result);
            Main.changeScene("/view/Menu.fxml");
        } else if (result instanceof Doctor) {
            sessionManager.setCurrentDoctor((Doctor) result);
            Main.changeScene("/view/DoctorMain.fxml");
        } else if (result instanceof Admin) {
            sessionManager.setCurrentAdmin((Admin) result); 
            Main.changeScene("/view/AdminMain.fxml"); 
        } else {
            showError("Invalid username or password.");
        }
    }

    @FXML
    void handleLogoutUser(ActionEvent event) {
        SessionManager.getInstance().clearSession();
        Main.changeScene("/view/Main.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handlebackButton(MouseEvent event) {
        Main.changeScene("/view/Main.fxml");
    }

    @FXML
    void initialize() {
        assert LoginButton != null : "fx:id=\"LoginButton\" was not injected: check your FXML file 'Login.fxml'.";
    }
}
