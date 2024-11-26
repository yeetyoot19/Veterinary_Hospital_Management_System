package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Animal;
import model.Certificate;
import model.Checkup;
import model.Clinic;
import PersistenceHandler.DBHandler; // Assuming a DatabaseHandler for DB operations

public class TravelCertificateController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text approvalLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button bookAppointmentButton;

    @FXML
    private ComboBox<String> choosePetComboBox; // Populates pet names or IDs owned by the user

    @FXML
    private Button generateCertButton;
    
    SessionManager sessionManager;
    
    private Animal selectedPet;

    public TravelCertificateController(){
    	sessionManager = SessionManager.getInstance();
    }
    
    @FXML
    void handleBookAppointmentButton(MouseEvent event) {
        // Check if user has selected a pet
        selectedPet = Clinic.getPetByName(choosePetComboBox.getValue(), sessionManager.getCurrentUser());
        if (selectedPet == null) {
            // No pet selected, redirect to appointment booking with pet details
            navigateToAppointmentPage(false); // New appointment flow
        } else {
            // Pet already selected, book an appointment without entering pet details
            navigateToAppointmentPage(true); // Existing pet flow
        }
    }

    @FXML
    void handleGenerateCertificate(MouseEvent event) {
        try {
            if (selectedPet == null) {
                approvalLabel.setText("No pet selected. Please choose a pet.");
                return;
            }

            // Check the `checkup_status` and certificate approval
            boolean isCheckupDone = Checkup.isCheckUpDone(selectedPet);
            boolean isCertificateApproved = Clinic.isCertificateApproved(selectedPet);

            if (isCheckupDone && isCertificateApproved) {
                // Generate certificate and show confirmation
            	String certificate = "Travel Certificate\n" +
                        "====================\n" +
                        "Pet Name: " + selectedPet.getName() + "\n" +
                        "Type: " + selectedPet.getType() + "\n" +
                        "Owner: " + SessionManager.getInstance().getCurrentUser().getName();
            	
                approvalLabel.setText("Certificate Generated!");
                showCertificatePopup(certificate);
            } else {
                approvalLabel.setText("Certificate Approval PENDING");
            }

        } catch (Exception e) {
            e.printStackTrace();
            approvalLabel.setText("Error while generating certificate.");
        }
    }

    @FXML
    void initialize() {
        assert approvalLabel != null : "fx:id=\"approvalLabel\" was not injected: check your FXML file 'TravelCertificate.fxml'.";
        assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'TravelCertificate.fxml'.";
        assert bookAppointmentButton != null : "fx:id=\"bookAppointmentButton\" was not injected: check your FXML file 'TravelCertificate.fxml'.";
        assert choosePetComboBox != null : "fx:id=\"choosePetComboBox\" was not injected: check your FXML file 'TravelCertificate.fxml'.";
        assert generateCertButton != null : "fx:id=\"generateCertButton\" was not injected: check your FXML file 'TravelCertificate.fxml'.";

        populatePetComboBox();
        generateCertButton.setDisable(true); // Initially disabled

        // Add a listener to the ComboBox for pet selection
        choosePetComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
            	 selectedPet = Clinic.getPetByName(newValue, sessionManager.getCurrentUser());
                checkPetStatusAndEnableButton(selectedPet);
            } else {
                generateCertButton.setDisable(true); // Disable if no pet is selected
            }
        });
    }
    
    private void checkPetStatusAndEnableButton(Animal animal) {
        try {
            boolean isCheckupDone = Checkup.isCheckUpDone(animal);
            boolean isCertificateApproved = Clinic.isCertificateApproved(animal);

            // Enable the button only if both conditions are true
            generateCertButton.setDisable(!(isCheckupDone && isCertificateApproved));
            if (!isCheckupDone) {
                approvalLabel.setText("Checkup not completed yet.");
            } else if (!isCertificateApproved) {
                approvalLabel.setText("Certificate not approved yet.");
            } else {
                approvalLabel.setText("Ready to generate certificate.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            approvalLabel.setText("Error checking pet status.");
        }
    }



    private void populatePetComboBox() {
        List<String> pets = this.sessionManager.getCurrentUser().getUserPets(Clinic.fetchUserIdByUsername(this.sessionManager.getCurrentUser().getUsername()));
        if (pets.isEmpty()) {
            choosePetComboBox.getItems().add("No pets available. Add a pet through appointment booking.");
        } else {
            choosePetComboBox.getItems().addAll(pets);
        }
    }


    private void navigateToAppointmentPage(boolean useExistingPetDetails) {
        if (useExistingPetDetails) {
        	selectedPet = Clinic.getPetByName(choosePetComboBox.getValue(), sessionManager.getCurrentUser());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CredAppointment.fxml"));
            Parent root = null;
			try {
				root = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}

            // Pass the Animal object to the controller
            CredAppointmentController controller = loader.getController();
            controller.setAnimal(selectedPet);

            Main.changeScene(root); // Assuming Main.changeScene accepts a Parent object
        } else {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Appointment.fxml"));
            Parent root = null;
			try {
				root = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}

            // Pass the Animal object to the controller
            AppointmentController controller = loader.getController();
            controller.setRequiresCertificate(true);;
            Main.changeScene(root);
        }
    }


    private void showCertificatePopup(String certificate) {
        // Check if certificate is not empty
        if (certificate == null || certificate.isEmpty()) {
            showError("No certificate available.");
            return;
        }

        // Create a new alert to display the certificate
        Alert certificateAlert = new Alert(Alert.AlertType.INFORMATION);
        certificateAlert.setTitle("Certificate Information");
        certificateAlert.setHeaderText("Appointment Certificate");
        certificateAlert.setContentText(certificate);

        // Show the certificate in a pop-up window
        certificateAlert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    void handleBackButton(MouseEvent event) {
        Main.changeScene("/view/Menu.fxml");
    }
}
