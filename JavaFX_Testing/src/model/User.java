package model;

import java.util.List;

import PersistenceHandler.DBHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;

//User.java - Model class
public class User implements Observer {

	 private String name;
	 private String email;
	 private String phoneNumber;
	 private String password;
	 private String username;
	 private static final DBHandler sqlhandler = DBHandler.getInstance();
	
	 // Constructor
	 public User(String name, String email, String phoneNumber, String password, String username) {
	     this.name = name;
	     this.email = email;
	     this.phoneNumber = phoneNumber;
	     this.password = password;
	     this.username = username;
	 }
	
	 public static Object verifyLogin(String username, String password) {
		return sqlhandler.verifyLogin(username, password);
	 }
	 
	 // Basically equivalent to Request Certificate.
	 public Certificate createCertificate(User user, Doctor doctor, Animal animal, Checkup checkup) {
		 Certificate C = sqlhandler.createCertificate(user, doctor, animal, checkup);		 
		 return C;
	 }
	 
    public int bookAppointment(User user, Doctor doctor, Animal animal, DoctorAppointment appointmentTime, String checkUpMode) {
    	return sqlhandler.bookAppointment(user, doctor, animal, appointmentTime, checkUpMode);
    }
    
    public List<String> getUserPets(int userId) {
    	return sqlhandler.getUserPets(userId);
    }
	 
	 @Override
	 public void update(String message) {
		 Platform.runLater(() -> showAlert("Notification", message));
	 }
	 
	 public void showAlert(String title, String message) {
		    System.out.println(title + ": " + message);
		    // Or you can use a JavaFX Alert dialog
		    Alert alert = new Alert(Alert.AlertType.INFORMATION);
		    alert.setTitle(title);
		    alert.setContentText(message);
		    alert.showAndWait();
		}
	 
	 // Getters and Setters
	 public String getName() {
	     return name;
	 }
	
	 public void setName(String name) {
	     this.name = name;
	 }
	
	 public String getEmail() {
	     return email;
	 }
	
	 public void setEmail(String email) {
	     this.email = email;
	 }
	
	 public String getPhoneNumber() {
	     return phoneNumber;
	 }
	
	 public void setPhoneNumber(String phoneNumber) {
	     this.phoneNumber = phoneNumber;
	 }
	
	 public String getPassword() {
	     return password;
	 }
	
	 public void setPassword(String password) {
	     this.password = password;
	 }
	
	 public String getUsername() {
	     return username;
	 }
	
	 public void setUsername(String username) {
	     this.username = username;
	 }
 
}
