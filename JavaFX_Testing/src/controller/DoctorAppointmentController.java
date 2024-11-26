package controller;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import PersistenceHandler.DBHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import model.Appointment;
import model.Doctor;
import application.Main;

public class DoctorAppointmentController {
		
	private Doctor doctor;

    // FXML elements
    @FXML
    private TableView<Appointment> appointmentTable; 

    @FXML
    private TableColumn<Appointment, String> usernameColumn;

    @FXML
    private TableColumn<Appointment, String> animalnameColumn;

    @FXML
    private TableColumn<Appointment, String> datetimeColumn;

    @FXML
    private TableColumn<Appointment, String> typeOfCheckupColumn;

    @FXML
    private TextField dateField;

    @FXML
    private TextField starttimeField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button backButton;

    @FXML
    private Button addButton;

    private ObservableList<Appointment> appointments;
    
    public DoctorAppointmentController() { 
    	this.doctor = SessionManager.getInstance().getCurrentDoctor();
    }

    @FXML
    public void initialize() {
    	usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        animalnameColumn.setCellValueFactory(new PropertyValueFactory<>("animalname"));
        datetimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        typeOfCheckupColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfCheckup"));

        // Populate the table with data
        loadAppointments();
        
    }
    
    private void loadAppointments() {
        List<Appointment> appointmentList = Doctor.fetchDoctorAppointments(doctor.getDoctorId());
        
        appointments = FXCollections.observableArrayList(appointmentList);
        appointmentTable.setItems(appointments);
    }


    @FXML
    void handleBackButton(MouseEvent event) {
        Main.changeScene("/view/DoctorMain.fxml"); // Change to DoctorMain.fxml scene
    }

    @FXML
    void handleAddButton(MouseEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        String startingTime = starttimeField.getText();
        LocalTime time = new TimeConversion().convertToTime(startingTime);

        // Input validation
        if (selectedDate == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date is required.");
            return;
        }
        if (startingTime.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Starting time is required.");
            return;
        }
        
        // Insert into the database.
        Boolean ifinserted = Doctor.insertDoctorAppointment(doctor.getDoctorId(), selectedDate, time);
        if (ifinserted) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Time slot added successfully!");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Error", "Time and Date cannot match already added date-time.");
        }

        datePicker.setValue(null);
        starttimeField.clear();
    }
    
    public class TimeConversion {
        public LocalTime convertToTime(String timeString) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return LocalTime.parse(timeString, formatter);
            } catch (Exception e) {
                System.out.println("Invalid time format");
                return null; // Return null or handle the error as needed
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
