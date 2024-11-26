package controller;

import model.Admin;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AdminMainController {

    // FXML elements
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private Button doctorAddButton;
    
    private Admin admin;

    // Constructor
    public AdminMainController() {}

    // Initialize method for any setup tasks
    @FXML
    public void initialize() {
    	// Function to show Admin information on the Main Page.
    	retrieveAdminData();
    }
    
    private void retrieveAdminData() {
    	admin = Admin.getInstance();
    	
    	nameField.setText(admin.getName());
        emailField.setText(admin.getEmail());
    }

    // Event handler for the "DOCTOR ADD" button
    @FXML
    public void handleDoctorAddButton(MouseEvent event) {
        Main.changeScene("/view/AdminAddDoctor.fxml");
    }
}
