package model;

import java.sql.SQLException;

import PersistenceHandler.DBHandler;

public class Admin {
    private String name;
    private String email;
    private String password;
    
    private static Admin instance;
    
    static final DBHandler sqlhandler = DBHandler.getInstance();

    // Constructor
    private Admin() { 
    	this.name = "Johann Walt";
    	this.email = "johann.walt@admin.com";
    	this.password = "adminpassword";
    }
    
    public static Admin getInstance() {
        if (instance == null) {
            // Lazy initialization: Create the instance only when needed
            instance = new Admin();
        }
        return instance;
    }
    
    // Admin - Add doctor.
    public static Boolean admin_addsdoctor(String name, String email, String contact, String loginName, String password) throws SQLException {
    	return sqlhandler.insertDoctorData(name, email, contact, loginName, password);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    
}

