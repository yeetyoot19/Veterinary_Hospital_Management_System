package model;

// Text Checkup Strategy
public class TextCheckupStrategy implements CheckupStrategy {
    @Override
    public double calculateBaseRate() {
        return 30.0; // Example base rate for text checkup
    }
}