package model;

import java.sql.SQLException;
import java.util.List;

import PersistenceHandler.DBHandler;
import javafx.collections.ObservableList;

public class Clinic {
	private static final DBHandler sqlhandler = DBHandler.getInstance();
	
	public static ObservableList<Certificate> getCertificatesForDoctor(int doctorId) {
		return sqlhandler.getCertificatesForDoctor(doctorId);
	}
	
    public static Object verifyLogin(String username, String password) {
    	return sqlhandler.verifyLogin(username, password);
    }
    
    public static boolean isUsernameTaken(String username) {
    	return sqlhandler.isUsernameTaken(username);
    }
    
    public static boolean registerUser(User user) {
    	return sqlhandler.registerUser(user);
    }
    
    public static Doctor findDoctorById(int doctorId) {
    	return sqlhandler.findDoctorById(doctorId);
    }
    
    public static int saveOrFetchAnimal(Animal animal, User user) {
		return sqlhandler.saveOrFetchAnimal(animal, user);	
    }

    
    public static Checkup getCheckupByAppointmentId(int appointmentId, User user, Animal animal) throws SQLException {
    	return sqlhandler.getCheckupByAppointmentId(appointmentId,user,animal);
    }
    
    public static boolean isCertificateApproved(Animal selectedPet) throws SQLException {
    	return sqlhandler.isCertificateApproved(selectedPet);
    }
    
    public static boolean updateAnimalAdoptionStatus(Animal animal, int adoptedStatus, User user) {
    	return sqlhandler.updateAnimalAdoptionStatus(animal, adoptedStatus, user);
    }
    public static ObservableList<Animal> fetchNonAdoptedAnimals() {
    	return sqlhandler.fetchNonAdoptedAnimals();
    }
    
    public static Animal getPetByName(String petName, User user) {
    	return sqlhandler.getPetByName(petName, user);
    }
    
    public static int fetchUserIdByUsername(String username) {
    	return sqlhandler.fetchUserIdByUsername(username);
    }
}
