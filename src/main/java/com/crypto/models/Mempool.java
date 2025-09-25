package main.java.com.crypto.models;

import java.util.*;
import java.util.stream.Collectors;

public class Mempool {
    private static Mempool instance;
    private Map<UUID, Transaction> transactions;

    private Mempool() {
        this.transactions = new HashMap<>();
    }

    public static synchronized Mempool getInstance() {
        if (instance == null) {
            instance = new Mempool();
        }
        return instance;
    }

    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    public void removeTransaction(UUID transactionId) {
        transactions.remove(transactionId);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions.values());
    }

    public List<Transaction> getSortedTransactions() {
        return transactions.values().stream()
                .sorted(Comparator.comparingDouble(Transaction::getFees).reversed())
                .collect(Collectors.toList());
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public boolean containsTransaction(UUID transactionId) {
        return transactions.containsKey(transactionId);
    }

    public void clear() {
        transactions.clear();
    }

    public Optional<Transaction> getTransactionById(UUID transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }
}
