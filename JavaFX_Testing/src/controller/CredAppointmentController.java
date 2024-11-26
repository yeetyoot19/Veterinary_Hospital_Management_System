package controller;

import application.Main;

import PersistenceHandler.DBHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.Animal;
import model.Appointment;
import model.Bird;
import model.Cat;
import model.Certificate;
import model.Checkup;
import model.Clinic;
import model.Doctor;
import model.DoctorAppointment;
import model.Dog;
import model.User;

public class CredAppointmentController {
	
	private ObservableList<String> checkUpTypes = FXCollections.observableArrayList("Manual", "Text", "Video Call");
	
	public static Animal animal;
    @FXML
    private TextField typeComboBox;

    @FXML
    private TextField specificAttributeComboBox;

    @FXML
    private Label attributeLabel;

    @FXML
    private TextField petNameTextField;

    @FXML
    private TextField petAgeTextField;

    @FXML
    private TextField genderChoiceBox;

    @FXML
    private ChoiceBox<String> checkUpModeButton;

    @FXML
    private TableView<DoctorAppointment> timeSlotTable;

    @FXML
    private TableColumn<DoctorAppointment, String> doctorNameColumn;

    @FXML
    private TableColumn<DoctorAppointment, String> appointmentTimeColumn;

    @FXML
    private Button bookAppointmentButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private TextArea allergiesBox;
    
    @FXML
    private TextArea medicationBox;


    private final DBHandler sqlHandler;
    
    SessionManager sessionManager;

    public CredAppointmentController() {
    	sessionManager = SessionManager.getInstance();
        this.sqlHandler = DBHandler.getInstance(); // Singleton DBHandler
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
        if (animal != null) {
        	loadPetDetails(sessionManager.getCurrentUser(), animal);
        } else {
        	showError("Animal details are missing.");
        }
    }

    @FXML
    private void initialize() {
        checkUpModeButton.setItems(checkUpTypes);

        // Set up the table columns
        doctorNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDoctorName()));
        appointmentTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimeSlot().toString()));

        // Fetch and set available slots
        ObservableList<DoctorAppointment> doctorAppointments = Doctor.fetchAvailableSlots();
        if (doctorAppointments != null && !doctorAppointments.isEmpty()) {
            timeSlotTable.setItems(doctorAppointments);
        } else {
            showError("No available doctor time slots at the moment.");
        }
    }


    @FXML
    void handleBookAppointmentButton(MouseEvent event) {
        try {
            String checkUpMode = checkUpModeButton.getValue();
            DoctorAppointment selectedSlot = timeSlotTable.getSelectionModel().getSelectedItem();
            if (selectedSlot == null) {
                showError("Please select a time slot from the table.");
                return;
            }
            Doctor doctor = Clinic.findDoctorById(selectedSlot.getDoctorId());

            int success = sessionManager.getCurrentUser().bookAppointment(this.sessionManager.getCurrentUser(), doctor, animal, selectedSlot, checkUpMode);
            Checkup checkup = Clinic.getCheckupByAppointmentId(success,this.sessionManager.getCurrentUser(),this.animal);

            if (success!=0) {
            	 showSuccess("Appointment successfully booked!");
            	 
            	Certificate newCertificate = this.sessionManager.getCurrentUser().createCertificate(this.sessionManager.getCurrentUser(), doctor, animal, checkup);
            	if (newCertificate != null) {
            	    System.out.println("Certificate created with ID: " + newCertificate.getCertificateId());
            	} else {
            	    System.out.println("Failed to create certificate.");
            	}
            } else {
                showError("Failed to book the appointment.");
            }

        } catch (NumberFormatException e) {
            showError("Invalid age input. Please enter a number.");
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void loadPetDetails(User user, Animal animal) {
        try {
            if (animal == null) {
                showError("Unable to fetch pet details. Please check the animal ID.");
                return;
            }

            String allergies = this.sqlHandler.getallergies(animal);
            String medication = this.sqlHandler.getmedication(animal);
            String attr = "";
            if (animal instanceof Dog) {
                attr = ((Dog) animal).getBreed();
            } else if (animal instanceof Cat) {
                attr = ((Cat) animal).getfurcolor();
            } else if (animal instanceof Bird) {
                attr = ((Bird) animal).getwingspan();
            }

            petNameTextField.setText(animal.getName());
            petAgeTextField.setText(String.valueOf(animal.getAge()));
            genderChoiceBox.setText(animal.getGender());
            typeComboBox.setText(animal.getType());
            specificAttributeComboBox.setText(attr);
            allergiesBox.setText(allergies);
            medicationBox.setText(medication);

            petNameTextField.setDisable(true);
            petAgeTextField.setDisable(true);
            genderChoiceBox.setDisable(true);
            typeComboBox.setDisable(true);
            specificAttributeComboBox.setDisable(true);
            allergiesBox.setEditable(false);
            medicationBox.setEditable(false);

        } catch (Exception e) {
            showError("An error occurred while loading pet details: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    void handleBackButton(MouseEvent event) {
        Main.changeScene("/view/Menu.fxml");
    }
}

