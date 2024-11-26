package controller;

import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import application.Main;
import model.Referral;
import PersistenceHandler.DBHandler;

public class ReferralController {

    @FXML
    private Button backButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button referralButton;

    private final DBHandler sqlhandler;

    @FXML
    void handleReferral(MouseEvent event) {
        String name = nameTextField.getText();
        String email = emailTextField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please fill out all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
            return;
        }

        Referral referral = new Referral(name, email);
        boolean isInserted = sqlhandler.insertReferral(referral);

        if (isInserted) {
            showAlert(AlertType.INFORMATION, "Success", "Referral sent successfully!");
            nameTextField.clear();
            emailTextField.clear();
        } else {
            showAlert(AlertType.ERROR, "Database Error", "Unable to send referral. Email might already be in use.");
        }
    }

    @FXML
    void handlebackButton(MouseEvent event) {
        Main.changeScene("/view/Menu.fxml");
    }

    @FXML
    void initialize() {
        assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'Referral.fxml'.";
        assert emailTextField != null : "fx:id=\"emailTextField\" was not injected: check your FXML file 'Referral.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'Referral.fxml'.";
        assert referralButton != null : "fx:id=\"referralButton\" was not injected: check your FXML file 'Referral.fxml'.";
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    public ReferralController() {
        this.sqlhandler = DBHandler.getInstance(); // Access Singleton SQLHandler
    }


    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
