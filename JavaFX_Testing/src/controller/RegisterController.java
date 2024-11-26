package controller;
import application.Main;

import java.net.URL;
import java.util.ResourceBundle;

import PersistenceHandler.DBHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import model.Clinic;
import model.User;
public class RegisterController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button RegisterButton;

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;

    @FXML
    void handleRegisterButtonClick(MouseEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneField.getText();
        String password = passwordField.getText();
        String username = usernameField.getText();

        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || username.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (Clinic.isUsernameTaken(username)) {
            showError("Username is already taken. Please enter a different username.");
            return;
        }

        User newUser = new User(name, email, phoneNumber, password, username);

        boolean isRegistered = Clinic.registerUser(newUser);

        if (isRegistered) {
            SessionManager.getInstance().setCurrentUser(newUser);
            Main.changeScene("/view/Menu.fxml");
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        assert RegisterButton != null : "fx:id=\"RegisterButton\" was not injected: check your FXML file 'Register.fxml'.";
    }
}
	