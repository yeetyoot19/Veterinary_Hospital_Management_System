package controller;
import application.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button LoginButton;

    @FXML
    private Button RegisterButton;

    @FXML
    void handleLoginButtonClick(MouseEvent event) {
    	Main.changeScene("/view/Login.fxml");
    }

    @FXML
    void handleRegisterButtonClick(MouseEvent event) {
    	Main.changeScene("/view/Register.fxml");
    }

    @FXML
    void initialize() {
        assert LoginButton != null : "fx:id=\"LoginButton\" was not injected: check your FXML file 'Main.fxml'.";
        assert RegisterButton != null : "fx:id=\"RegisterButton\" was not injected: check your FXML file 'Main.fxml'.";

    }

}
