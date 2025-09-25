package main.java.com.crypto.enums;

public enum FeeLevel {
    ECONOMIQUE(1, "Économique", 1.0),
    STANDARD(2, "Standard", 2.0),
    RAPIDE(3, "Rapide", 4.0);

    private final int priority;
    private final String description;
    private final double multiplier;

    FeeLevel(int priority, String description, double multiplier) {
        this.priority = priority;
        this.description = description;
        this.multiplier = multiplier;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return description;
    }
}