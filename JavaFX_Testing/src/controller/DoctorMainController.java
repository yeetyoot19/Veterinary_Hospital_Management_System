package controller;

//import controller.LoginController;

import model.Doctor;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class DoctorMainController {
	
	private Doctor doctor;
    public void setDoctor() {	this.doctor = SessionManager.getInstance().getCurrentDoctor();	}

    // FXML elements
    @FXML
    private Button appointmentsButton;
    @FXML
    private Button travelCertificatesButton;
    @FXML
    private Button checkupButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phonenumberLabel;
    @FXML
    private TextArea nameField;
    @FXML
    private TextArea emailField;
    @FXML
    private TextArea phonenumberField;

    // Constructor
    public DoctorMainController() { }

    // Initialize method to set up any initial configurations
    @FXML
    public void initialize() {
    	setDoctor();
        nameField.setText(doctor.getDoctorName());
        emailField.setText(doctor.getEmail());
        phonenumberField.setText(String.valueOf(doctor.getPhoneNumber()));
    }
    
    @FXML
    // Method for handling the Appointments button click
    void handleAppointmentsButton(MouseEvent event) {
        Main.changeScene("/view/DoctorAppointment.fxml");
    }

    @FXML
    // Method for handling the Travel Certificates button click
    void handleTravelCertificatesButton(MouseEvent event) {
    	Main.changeScene("/view/DoctorTravelCertificates.fxml");
    }

    @FXML
    // Method for handling the Checkup button click
    void handleCheckupButton(MouseEvent event) {
    	Main.changeScene("/view/DoctorCheckup.fxml");
    }
    @FXML
    void handlelogoutButton(MouseEvent event) {
    	Main.changeScene("/view/Main.fxml");
    }

}
