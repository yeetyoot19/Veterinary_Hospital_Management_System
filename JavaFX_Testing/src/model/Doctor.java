package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import PersistenceHandler.DBHandler;
import javafx.collections.ObservableList;

public class Doctor {
    private int doctorId;
    private String name;
    private String email;
    private int phoneNumber;
    private String loginname;
    private String password;
      
    private static final DBHandler sqlhandler = DBHandler.getInstance();

    // Default constructor
    public Doctor() {  }

    // Parameterized constructor
    public Doctor(int doctorId, String doctorName, String email, int phoneNumber, String username, String password) {
        this.doctorId = doctorId;
        this.name = doctorName;
        
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.loginname = username;
        this.password = password;
    }

    public static List<Appointment> fetchDoctorAppointments(int doctorid) {
    	return sqlhandler.fetchDoctorAppointments(doctorid);
    }
    
    public static Boolean insertDoctorAppointment(int doctorid, LocalDate date, LocalTime time) {
    	return sqlhandler.insertDoctorAppointment(doctorid, date, time);
    }
    
    public static List<Checkup> loadCheckupsForDoctor(int doctorid) {
    	return sqlhandler.loadCheckupsForDoctor(doctorid);
    }
    
    public static ObservableList<DoctorAppointment> fetchAvailableSlots() {
    	return sqlhandler.fetchAvailableSlots();
    }
    
    public static Boolean updateCertificateApproval(int certificateId) {
    	return sqlhandler.updateCertificateApproval(certificateId);    
    }
       
    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return name;
    }

    public void setDoctorName(String doctorName) {
        this.name = doctorName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // toString method for debugging and display
    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", doctorName='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber=" + phoneNumber +
                '}';
    }
}
