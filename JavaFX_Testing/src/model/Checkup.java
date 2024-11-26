package model;

import java.sql.SQLException;

import PersistenceHandler.DBHandler;

public class Checkup {
	private int checkupid;
	private int appointmentId;
    private String username;
    private String animalName;
    private String checkupType;
    private String checkupStatus;
    private int hoursTaken;

	private double baseRate;
    private CheckupStrategy strategy;
    static final DBHandler sqlhandler = DBHandler.getInstance();

    // Constructor  
    public Checkup(int id ,int appointmentid, String username, String animalName, String checkupType, String checkupStatus) {
    	this.checkupid = id;
    	this.appointmentId = appointmentid;
        this.username = username;
        this.animalName = animalName;
        this.checkupType = checkupType;
        this.checkupStatus = checkupStatus;
    }
        
    public static boolean isCheckUpDone(Animal selectedPet) throws SQLException {
    	return sqlhandler.isCheckUpDone(selectedPet);
    }
    
    public static Boolean updateCheckup(int appointmentId, int hours, Boolean status, float amount) {
    	return sqlhandler.updateCheckup(appointmentId, hours, status, amount);
    }
    
    public void calculateBaseRate() {
        if (strategy != null) {
            this.baseRate = strategy.calculateBaseRate();
        }
    }

    // Getters and Setters
    public int getCheckupid() {
    	return checkupid;
    }
    
    public void setCheckupid(int checkupid) {
    	this.checkupid = checkupid;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getCheckupType() {
        return checkupType;
    }

    public void setCheckupType(String checkupType) {
        this.checkupType = checkupType;
    }

    public String getCheckupStatus() {
        return checkupStatus;
    }

    public void setCheckupStatus(String checkupStatus) {
        this.checkupStatus = checkupStatus;
    }

    public int getHoursTaken() {
        return hoursTaken;
    }

    public void setHoursTaken(int hoursTaken) {
        this.hoursTaken = hoursTaken;
    }

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	
	public double getBaseRate() {
        return baseRate;
    }

    public void setStrategy(CheckupStrategy strategy) {
        this.strategy = strategy;
    }
}
