package model;

import java.time.LocalDateTime;

import PersistenceHandler.DBHandler;

public class Appointment {
	private int appointmentId;
    private int userId;
    private int doctorId;
    private int animalId;

    private String username;
    private String animalname;
    private LocalDateTime appointmentTime;
    private String typeOfCheckup;
    
    private static final DBHandler sqlhandler = DBHandler.getInstance();

    // Constructor
    public Appointment(int appointmentId, int userId, int doctorId, int animalId, LocalDateTime appointmentTime, String typeOfCheckup) {
        this.setAppointmentId(appointmentId);
        this.setUserId(userId);
        this.setDoctorId(doctorId);
        this.setAnimalId(animalId);
        this.appointmentTime = appointmentTime;
        this.typeOfCheckup = typeOfCheckup;
    }
    
    public Appointment(String userId, String animalId, LocalDateTime appointmentTime, String typeOfCheckup) {
        this.setUsername(userId);
        this.setAnimalname(animalId);
        this.appointmentTime = appointmentTime;
        this.typeOfCheckup = typeOfCheckup;
    }
    
    // Functionality functions.
    
    // Getters and setters
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getTypeOfCheckup() {
        return typeOfCheckup;
    }

    public void setTypeOfCheckup(String typeOfCheckup) {
        this.typeOfCheckup = typeOfCheckup;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAnimalname() {
		return animalname;
	}

	public void setAnimalname(String animalname) {
		this.animalname = animalname;
	}

	public int getAnimalId() {
		return animalId;
	}

	public void setAnimalId(int animalId) {
		this.animalId = animalId;
	}

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
}
