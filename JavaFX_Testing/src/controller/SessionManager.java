package controller;

import model.Admin;
import model.Doctor;
import model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private Doctor currentDoctor;
    private Admin currentAdmin;

    // Private constructor to prevent instantiation
    private SessionManager() {}

    // Get the Singleton instance
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Set the currently logged-in user
    public void setCurrentUser(User user) {
        clearSession();
        this.currentUser = user;
    }

    // Set the currently logged-in doctor
    public void setCurrentDoctor(Doctor doctor) {
        clearSession();
        this.currentDoctor = doctor;
    }
    
    public void setCurrentAdmin(Admin currentAdmin) {
    	clearSession();
		this.currentAdmin = currentAdmin;
	}

    // Get the currently logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Get the currently logged-in doctor
    public Doctor getCurrentDoctor() {
        return currentDoctor;
    }
    
    public Admin getCurrentAdmin() {
		return currentAdmin;
	}

    // Clear the session (log out)
    public void clearSession() {
        currentUser = null;
        currentDoctor = null;
    }
}
