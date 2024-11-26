package controller;

import application.Main;

import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.Animal;
import model.AnimalFactory;
import model.Appointment;
import model.Certificate;
import model.Checkup;
import model.Clinic;
import model.Doctor;
import model.DoctorAppointment;

public class AppointmentController {
    private ObservableList<String> animalTypes = FXCollections.observableArrayList("Dog", "Cat", "Bird");
    private ObservableList<String> checkUpTypes = FXCollections.observableArrayList("Manual", "Text", "Video Call");
    private ObservableList<String> genders = FXCollections.observableArrayList("Female", "Male");
    
    private boolean requiresCertificate = false;

    private final Map<String, ObservableList<String>> animalAttributes = Map.of(
        "Dog", FXCollections.observableArrayList("Bulldog", "Golden Retriever", "Poodle", "Beagle"),
        "Cat", FXCollections.observableArrayList("Black", "White", "Tabby", "Orange"),
        "Bird", FXCollections.observableArrayList("40-50cm", "60-70cm", "70-100cm", "100-120cm")

    );

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ComboBox<String> specificAttributeComboBox;

    @FXML
    private Label attributeLabel;

    @FXML
    private TextField petNameTextField;

    @FXML
    private TextField petAgeTextField;

    @FXML
    private ChoiceBox<String> genderChoiceBox;

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
    
    SessionManager sessionManager;

    public AppointmentController() {
    	sessionManager = SessionManager.getInstance();
    }

    @FXML
    private void initialize() {
        typeComboBox.setItems(animalTypes);
        genderChoiceBox.setItems(genders);
        checkUpModeButton.setItems(checkUpTypes);

        doctorNameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDoctorName()));
        appointmentTimeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTimeSlot().toLocalDateTime().toString()));

        ObservableList<DoctorAppointment> doctorAppointments = Doctor.fetchAvailableSlots();
        if (doctorAppointments != null && !doctorAppointments.isEmpty()) {
            timeSlotTable.setItems(doctorAppointments);
        } else {
            showError("No available doctor time slots at the moment.");
        }

        // Add listener to update specific attribute ComboBox and Label
        typeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                updateSpecificAttributes(newValue);
            }
        });
    }

    private void updateSpecificAttributes(String animalType) {
        attributeLabel.setText(switch (animalType) {
            case "Dog" -> "Breed";
            case "Cat" -> "FurColor";
            case "Bird" -> "Wingspan";
            default -> "Attribute";
        });

        ObservableList<String> options = animalAttributes.getOrDefault(animalType, FXCollections.observableArrayList());
        specificAttributeComboBox.setItems(options);
        specificAttributeComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    void handleBookAppointmentButton(MouseEvent event) {
        try {
            String petName = petNameTextField.getText();
            String petType = typeComboBox.getValue();
            String petGender = genderChoiceBox.getValue();
            String checkUpMode = checkUpModeButton.getValue();
            int petAge = Integer.parseInt(petAgeTextField.getText());
            String specificAttribute = specificAttributeComboBox.getValue();
            String allergies = allergiesBox.getText();  // Get allergies from the TextArea
            String medications = medicationBox.getText(); // Get medications from the TextArea

            if (petName.isEmpty() || petType == null || petGender == null || checkUpMode == null || specificAttribute == null) {
                showError("Please fill out all fields.");
                return;
            }
            
            //create animal
            Animal animal = AnimalFactory.createAnimal(petType, petName, petAge, petGender, specificAttribute, allergies, medications);
            // insert animal
            animal.setId(Clinic.saveOrFetchAnimal(animal,this.sessionManager.getCurrentUser()));          
            //get timeslot
            DoctorAppointment selectedSlot = timeSlotTable.getSelectionModel().getSelectedItem();
            if (selectedSlot == null) {
                showError("Please select a time slot from the table.");
                return;
            }
            Doctor doctor = Clinic.findDoctorById(selectedSlot.getDoctorId());
            //book 
            int success = this.sessionManager.getCurrentUser().bookAppointment(this.sessionManager.getCurrentUser(), doctor, animal, selectedSlot, checkUpMode);

            if (success != 0) {
                showSuccess("Appointment successfully booked!");

	            // Check if a certificate is required
	            if (this.requiresCertificate) {
	            	
	                Checkup checkup = Clinic.getCheckupByAppointmentId(success, this.sessionManager.getCurrentUser(), animal);
	                Certificate newCertificate = this.sessionManager.getCurrentUser().createCertificate(this.sessionManager.getCurrentUser(), doctor, animal, checkup);
	                if (newCertificate != null) {
	                    System.out.println("Certificate created with ID: " + newCertificate.getCertificateId());
	                } else {
	                    System.out.println("Failed to create certificate.");
	                }
	            }
                
            } else {
            	showError("Failed to book the appointment.");
            }

        } catch (NumberFormatException e) {
            showError("Invalid input: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Error: " + e.getMessage());
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
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
    
    public void setRequiresCertificate(boolean requiresCertificate) {
        this.requiresCertificate = requiresCertificate;
    }
}

