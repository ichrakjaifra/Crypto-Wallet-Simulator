package main.java.com.crypto.enums;

public enum TransactionStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    REJECTED("Rejetée");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
