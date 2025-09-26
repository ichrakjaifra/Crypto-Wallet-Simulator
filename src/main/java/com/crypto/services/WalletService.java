package main.java.com.crypto.services;

import main.java.com.crypto.repositories.WalletRepository;
import main.java.com.crypto.models.Wallet;
import main.java.com.crypto.models.BitcoinWallet;
import main.java.com.crypto.models.EthereumWallet;
import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.utils.LoggerUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WalletService {
    private static WalletService instance;
    private WalletRepository walletRepository;

    private WalletService() {
        this.walletRepository = WalletRepository.getInstance();
    }

    public static synchronized WalletService getInstance() {
        if (instance == null) {
            instance = new WalletService();
        }
        return instance;
    }

    public Wallet createWallet(CryptoType cryptoType) {
        Wallet wallet;

        switch (cryptoType) {
            case BITCOIN:
                wallet = new BitcoinWallet();
                break;
            case ETHEREUM:
                wallet = new EthereumWallet();
                break;
            default:
                throw new IllegalArgumentException("Type de crypto non supporté: " + cryptoType);
        }

        Wallet savedWallet = walletRepository.save(wallet);
        if (savedWallet != null) {
            LoggerUtil.logInfo("Nouveau wallet créé: " + savedWallet.getId() + " - " + cryptoType);
        }

        return savedWallet;
    }

    public Optional<Wallet> getWalletById(UUID id) {
        return walletRepository.findById(id);
    }

    public Optional<Wallet> getWalletByAddress(String address) {
        return walletRepository.findByAddress(address);
    }

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public boolean updateWalletBalance(UUID walletId, double newBalance) {
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            wallet.setBalance(newBalance);
            Wallet updatedWallet = walletRepository.save(wallet);
            if (updatedWallet != null) {
                LoggerUtil.logInfo("Solde du wallet mis à jour: " + walletId + " - Nouveau solde: " + newBalance);
                return true;
            }
        }
        return false;
    }

    public boolean deleteWallet(UUID walletId) {
        walletRepository.deleteById(walletId);
        LoggerUtil.logInfo("Wallet supprimé: " + walletId);
        return true;
    }

    public boolean validateWallet(Wallet wallet) {
        if (wallet == null) {
            return false;
        }
        if (wallet.getAddress() == null || wallet.getAddress().trim().isEmpty()) {
            return false;
        }
        if (wallet.getBalance() < 0) {
            return false;
        }
        return true;
    }
}
