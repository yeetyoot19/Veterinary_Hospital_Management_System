package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField; // Correct TextField import


import model.Doctor;
import model.ManualCheckupStrategy;
import model.TextCheckupStrategy;
import model.VideoCallCheckupStrategy;
import model.AnimalHistory;
import model.Checkup;

import java.util.List;

import PersistenceHandler.DBHandler;
import application.Main;

public class DoctorCheckupController {
		
	private Doctor doctor;

    @FXML
    private TableView<Checkup> appointmentTableView;
    
    @FXML
    private TableColumn<Checkup, String> appointmnetnumColumn;
    
    @FXML
    private TableColumn<Checkup, String> usernameColumn;

    @FXML
    private TableColumn<Checkup, String> animalNameColumn;

    @FXML
    private TableColumn<Checkup, String> checkupTypeColumn;

    @FXML
    private TableColumn<Checkup, String> checkupStatusColumn;
        
    @FXML
    private Button backButton;
    
    @FXML
    private TextField hourstakenField;

    @FXML
    private Label messageLabel;
    
    @FXML
    private TextArea animalHistoryField;
    
    public DoctorCheckupController() { 
    	this.doctor = SessionManager.getInstance().getCurrentDoctor();
    }

    public void initialize() {
        // Set the columns for appointment table
    	appointmnetnumColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        animalNameColumn.setCellValueFactory(new PropertyValueFactory<>("animalName"));
        checkupTypeColumn.setCellValueFactory(new PropertyValueFactory<>("checkupType"));
        checkupStatusColumn.setCellValueFactory(new PropertyValueFactory<>("checkupStatus"));

        appointmentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get the selected animal's id from the selected Checkup object
                int appointmentId = newValue.getAppointmentId();

                // Fetch and display the animal history in the TextArea
                String history = AnimalHistory.getAnimalHistory(appointmentId);
                animalHistoryField.setText(history); 
            }
        });

        // Load sample appointments and animal histories
        loadCheckups();
    }

    private void loadCheckups() {
    	// Fetch the checkups for the current doctor
        List<Checkup> checkups = Doctor.loadCheckupsForDoctor(doctor.getDoctorId());

        // Create an observable list from the fetched data
        ObservableList<Checkup> observableCheckups = FXCollections.observableArrayList(checkups);

        // Set the TableView data
        appointmentTableView.setItems(observableCheckups);
    }

    @FXML
    private void handleBackButton(MouseEvent event) {
    	Main.changeScene("/view/DoctorMain.fxml");
    }
    
    @FXML
    private void handleCheckupButton(MouseEvent event) {
    	Checkup selectedCheckup = appointmentTableView.getSelectionModel().getSelectedItem();
    	if (selectedCheckup != null) {
            // Set the appropriate strategy based on the checkup type
            switch (selectedCheckup.getCheckupType().toLowerCase()) {
                case "manual":
                    selectedCheckup.setStrategy(new ManualCheckupStrategy());
                    break;
                case "text your doctor":
                    selectedCheckup.setStrategy(new TextCheckupStrategy());
                    break;
                case "video call":
                    selectedCheckup.setStrategy(new VideoCallCheckupStrategy());
                    break;
            }
    	}
    	
    	String hoursTaken = hourstakenField.getText();
        if (hoursTaken == null || hoursTaken.trim().isEmpty()) {
        	showAlert("Input Error", "Hours taken field is empty!", Alert.AlertType.ERROR);
            return;
        }
        
        selectedCheckup.calculateBaseRate();
        float checkup_amount = (float) (selectedCheckup.getBaseRate() * Integer.parseInt(hoursTaken));
        Boolean isUpdated = Checkup.updateCheckup(selectedCheckup.getAppointmentId(), Integer.parseInt(hoursTaken), true, checkup_amount);
        if(isUpdated == true) {
        	loadCheckups();
        	hourstakenField.clear();
        	showAlert("Success", "Checkup has been Performed", Alert.AlertType.CONFIRMATION);
        } else {
        	hourstakenField.clear();
        	showAlert("Updated Error", "No field selected.", Alert.AlertType.ERROR);
        }
        
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Optional: Set a header if needed
        alert.setContentText(content);

        // Show the alert and wait for user response
        alert.showAndWait();
    }


}
