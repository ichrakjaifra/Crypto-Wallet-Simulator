package main.java.com.crypto.interfaces;

import main.java.com.crypto.models.Wallet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IWallet {
    Optional<Wallet> findById(UUID id);
    List<Wallet> findAll();
    Wallet save(Wallet wallet);
    void deleteById(UUID id);
    Optional<Wallet> findByAddress(String address);
}
