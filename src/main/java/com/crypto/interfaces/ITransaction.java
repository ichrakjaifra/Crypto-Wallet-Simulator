package main.java.com.crypto.interfaces;

import main.java.com.crypto.models.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITransaction {
    Optional<Transaction> findById(UUID id);
    List<Transaction> findAll();
    Transaction save(Transaction transaction);
    void deleteById(UUID id);
    List<Transaction> findByWalletId(UUID walletId);
    List<Transaction> findByStatus(String status);
}
