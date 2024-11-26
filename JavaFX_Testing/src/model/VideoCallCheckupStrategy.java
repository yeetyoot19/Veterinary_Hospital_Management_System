package model;

// Video Call Checkup Strategy
public class VideoCallCheckupStrategy implements CheckupStrategy {
    @Override
    public double calculateBaseRate() {
        return 80.0; // Example base rate for video call checkup
    }
}