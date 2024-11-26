package controller;

import PersistenceHandler.DBHandler;
import java.sql.SQLException;
import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

public class AdminAddDoctorController {
	
	private final DBHandler sqlhandler;

    // FXML elements
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField contactnumberField;
    
    @FXML
    private TextField loginnameField;
    
    @FXML
    private PasswordField passwordField;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    // Constructor
    public AdminAddDoctorController() { this.sqlhandler = DBHandler.getInstance(); }

    // Event handler for the "BACK" button
    @FXML
    public void handleBackButton(MouseEvent event) {
        Main.changeScene("/view/AdminMain.fxml");
    }
    
    // Event handler for the "ADD" button
    @FXML
    public void handleAddButton(MouseEvent event) {
    	// Retrieve the text from the fields
        String name = nameField.getText();
        String email = emailField.getText();
        String contact = contactnumberField.getText();
        String loginName = loginnameField.getText();
        String password = passwordField.getText();
        
        // Validate inputs
        if (!validateInputs(name, email, contact, loginName, password)) {
            return; // Don't proceed if inputs are invalid
        }

        // Insert into the database
        try {
			Boolean ifinserted = sqlhandler.insertDoctorData(name, email, contact, loginName, password);
			if(ifinserted == true) {
				showSuccess("Doctor Insertion", "Doctor has been added.");
			}
			else {
				showError("Input Validation Error", "All fields must be filled.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        nameField.clear();
        emailField.clear();
        contactnumberField.clear();
        loginnameField.clear();
        passwordField.clear();
    }
    
 // Method to validate the inputs
    private boolean validateInputs(String name, String email, String contact, String loginName, String password) {
        // Check for empty fields
        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || loginName.isEmpty() || password.isEmpty()) {
            showError("Input Validation Error", "All fields must be filled.");
            return false;
        }

        return true;
    }

    // Method to show an error alert
    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to show a success alert
    private void showSuccess(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
