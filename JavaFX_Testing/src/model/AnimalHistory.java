package model;

import PersistenceHandler.DBHandler;

public class AnimalHistory {
    private String allergies;
    private String medications;
    
    private static final DBHandler sqlhandler = DBHandler.getInstance();

    // Default constructor
    public AnimalHistory() {}
    
    // Constructor
    public AnimalHistory(String allergies, String medications) {
        this.allergies = allergies;
        this.medications = medications;
    }
    
    public static String getAnimalHistory(int appointmentId) {
    	return sqlhandler.getAnimalHistory(appointmentId);
    }
    
    // Getters and Setters
    public String getAllergies() {
		return allergies;
	}

    // Getters and Setter
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    // To String method for easy debugging
    @Override
    public String toString() {
        return "AnimalHistory{" +
                ", allergies='" + allergies + '\'' +
                ", medications='" + medications + '\'' +
                '}';
    }
}
