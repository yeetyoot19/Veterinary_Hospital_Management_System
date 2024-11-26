package model;

import java.sql.Timestamp;

public class DoctorAppointment {
    private int doctorId;
    private String doctorName;
    private Timestamp timeSlot;

    public DoctorAppointment(int doctorId, String doctorName, Timestamp timeSlot) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.timeSlot = timeSlot;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Timestamp getTimeSlot() {
        return timeSlot;
    }
}
