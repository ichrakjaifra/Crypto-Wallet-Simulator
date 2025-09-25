package main.java.com.crypto.models;

import main.java.com.crypto.enums.FeeLevel;
import main.java.com.crypto.enums.TransactionStatus;
import main.java.com.crypto.enums.CryptoType;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private String sourceAddress;
    private String destinationAddress;
    private double amount;
    private double fees;
    private LocalDateTime creationDate;
    private FeeLevel feeLevel;
    private TransactionStatus status;
    private CryptoType cryptoType;
    private UUID walletId;

    public Transaction() {
        this.id = UUID.randomUUID();
        this.creationDate = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    // Getters et setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSourceAddress() { return sourceAddress; }
    public void setSourceAddress(String sourceAddress) { this.sourceAddress = sourceAddress; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getFees() { return fees; }
    public void setFees(double fees) { this.fees = fees; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public FeeLevel getFeeLevel() { return feeLevel; }
    public void setFeeLevel(FeeLevel feeLevel) { this.feeLevel = feeLevel; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public CryptoType getCryptoType() { return cryptoType; }
    public void setCryptoType(CryptoType cryptoType) { this.cryptoType = cryptoType; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public double getTotalAmount() {
        return amount + fees;
    }

    @Override
    public String toString() {
        return String.format("Transaction{id=%s, amount=%.6f, fees=%.6f, status=%s, feeLevel=%s}",
                id, amount, fees, status, feeLevel);
    }
}
