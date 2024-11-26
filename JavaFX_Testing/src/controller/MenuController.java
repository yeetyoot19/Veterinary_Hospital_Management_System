package controller;
import application.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

public class MenuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem LogoutButton;

    @FXML
    private Button PharmacyButton;

    @FXML
    private Button adoptButton;

    @FXML
    private Button appointmentButton;
    
    @FXML
    private Button travelCertificateButton;

    @FXML
    private Button referralButton;

    @FXML
    void handleAdoption(MouseEvent event) {
    	Main.changeScene("/view/Adoption.fxml");
    }

    @FXML
    void handleAppointmentUser(MouseEvent event) {
    	Main.changeScene("/view/Appointment.fxml");
    }

    @FXML
    void handleCertificate(MouseEvent event) {
    	Main.changeScene("/view/TravelCertificate.fxml");
    }

    @FXML
    void handleFriend(MouseEvent event) {
    	Main.changeScene("/view/Referral.fxml");
    }

    @FXML
    void handleLogoutUser(ActionEvent event) {
    	Main.changeScene("/view/Main.fxml");
    }

    @FXML
    void handlePharmacy(MouseEvent event) {

    }

    @FXML
    void initialize() {
        assert LogoutButton != null : "fx:id=\"LogoutButton\" was not injected: check your FXML file 'Menu.fxml'.";
        assert PharmacyButton != null : "fx:id=\"PharmacyButton\" was not injected: check your FXML file 'Menu.fxml'.";
        assert adoptButton != null : "fx:id=\"adoptButton\" was not injected: check your FXML file 'Menu.fxml'.";
        assert appointmentButton != null : "fx:id=\"appointmentButton\" was not injected: check your FXML file 'Menu.fxml'.";
        assert referralButton != null : "fx:id=\"referralButton\" was not injected: check your FXML file 'Menu.fxml'.";

    }

}
