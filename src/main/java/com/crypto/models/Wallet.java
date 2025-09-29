package main.java.com.crypto.models;

import main.java.com.crypto.enums.CryptoType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Wallet {
    protected UUID id;
    protected String address;
    protected double balance;
    protected CryptoType cryptoType;
    protected List<Transaction> transactions;

    public Wallet(CryptoType cryptoType) {
        this.id = UUID.randomUUID();
        this.cryptoType = cryptoType;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.address = generateAddress();
    }

    protected abstract String generateAddress();

    // Getters et setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public CryptoType getCryptoType() { return cryptoType; }
    public void setCryptoType(CryptoType cryptoType) { this.cryptoType = cryptoType; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public void addTransaction(Transaction transaction) {
        // Vérifier si la transaction n'existe pas déjà
        boolean exists = this.transactions.stream()
                .anyMatch(t -> t.getId().equals(transaction.getId()));

        if (!exists) {
            this.transactions.add(transaction);
        }
    }

    // Charger les transactions depuis la base de données
    public void loadTransactions(List<Transaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
    }

    @Override
    public String toString() {
        return String.format("Wallet{id=%s, address=%s, balance=%.6f %s, transactions=%d}",
                id, address, balance, cryptoType.getSymbol(), transactions.size());
    }
}