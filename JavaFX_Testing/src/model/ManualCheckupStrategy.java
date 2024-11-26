package model;

// Manual Checkup Strategy
public class ManualCheckupStrategy implements CheckupStrategy {
    @Override
    public double calculateBaseRate() {
        return 50.0; // Example base rate for manual checkup
    }
}