package controller;

import java.util.List;
import java.util.Map;

import PersistenceHandler.DBHandler;
import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Certificate;
import model.Clinic;
import model.Doctor;

public class DoctorTravelCertificateController {
		
	private Doctor doctor;

    @FXML
    private TableView<Certificate> certificateTable;

    @FXML
    private TableColumn<Certificate, Integer> certificateIdColumn;

    @FXML
    private TableColumn<Certificate, String> usernameColumn;

    @FXML
    private TableColumn<Certificate, String> animalNameColumn;

    @FXML
    private TableColumn<Certificate, String> checkupStatusColumn;

    public DoctorTravelCertificateController() {
        this.doctor = SessionManager.getInstance().getCurrentDoctor();
    }

    @FXML
    public void initialize() {
        // Set cell value factories
        certificateIdColumn.setCellValueFactory(new PropertyValueFactory<>("certificateId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        animalNameColumn.setCellValueFactory(new PropertyValueFactory<>("animalname"));
        checkupStatusColumn.setCellValueFactory(new PropertyValueFactory<>("checkupStatus"));

        // Fetch data from SQL
        loadCertificates();
    }

    private void loadCertificates() {
    	ObservableList<Certificate> certificates = Clinic.getCertificatesForDoctor(doctor.getDoctorId());

        certificateTable.setItems(certificates);
    }
    
    @FXML
    public void handleBackButton(MouseEvent mouse) {
    	Main.changeScene("/view/DoctorMain.fxml");
    }
    
    @FXML
    public void handleApproveButton(MouseEvent mouse) {
        // Retrieve the selected certificate from the table
        Certificate selectedCertificate = certificateTable.getSelectionModel().getSelectedItem();

        if (selectedCertificate == null) {
            // No row selected, show an error or prompt the user
        	showAlert(Alert.AlertType.INFORMATION, "Error", "Select a certificate.");
            return;
        }
        
        if(selectedCertificate.getCheckupStatus().equals("Checkup Not Performed")) {
        	showAlert(Alert.AlertType.INFORMATION, "Error", "First perform Checkup.");
            return;
        }

        // Get the certificate ID of the selected row
        int certificateId = selectedCertificate.getCertificateId();

        // Call SQL function to update the approval status to true
        boolean success = Doctor.updateCertificateApproval(certificateId);

        if (success) {
            selectedCertificate.setApproval(true);
            loadCertificates(); 
            showAlert(Alert.AlertType.INFORMATION, "Success", "Certificate approved successfully.");
        } else {
        	showAlert(Alert.AlertType.INFORMATION, "Error", "Failed to approve the certificate.");
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
