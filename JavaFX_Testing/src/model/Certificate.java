package model;

import java.util.ArrayList;
import java.util.List;

import PersistenceHandler.DBHandler;

public class Certificate {
    private int certificateid;
    private int checkupId;
    private int userId;
    private int doctorId;
    private int animalId;
    private Boolean approval;
    
    private String checkupStatus;
    private String username;
    private String animalname;
    
    private List<Observer> observers = new ArrayList<>();
    static final DBHandler sqlhandler = DBHandler.getInstance();

    public Certificate(int certificateId, int checkupid, int userId, int doctorId, int animalId, Boolean approval) {
        this.certificateid = certificateId;
        this.checkupId = checkupid;
        this.userId = userId;
        this.doctorId = doctorId;
        this.animalId = animalId;
        this.approval = approval;
    }
    
    public Certificate(int certificateId, String username, String animalname, String checkupstatus) {
        this.certificateid = certificateId;
        this.username = username;
        this.animalname = animalname;
        this.checkupStatus = checkupstatus;
    }
    
    // Observer pattern.
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    
    // Getters and Setters.
    public int getCertificateId() {
        return certificateid;
    }

    public void setCertificateId(int certificateId) {
        this.certificateid = certificateId;
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

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public Boolean isApproval() {
        return approval;
    }

    public void setApproval(Boolean approval) {
        this.approval = approval;
    }

	public int getCheckupId() {
		return checkupId;
	}

	public void setCheckupId(int checkupId) {
		this.checkupId = checkupId;
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
	
	public String getCheckupStatus() {
	    return checkupStatus;
	}
}
