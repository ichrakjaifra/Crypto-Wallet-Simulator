package main.java.com.crypto.services;

import main.java.com.crypto.repositories.TransactionRepository;
import main.java.com.crypto.models.Transaction;
import main.java.com.crypto.models.Wallet;
import main.java.com.crypto.enums.FeeLevel;
import main.java.com.crypto.enums.TransactionStatus;
import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.utils.FeeCalculator;
import main.java.com.crypto.utils.AddressValidator;
import main.java.com.crypto.utils.LoggerUtil;
import main.java.com.crypto.exceptions.InvalidAddressException;
import main.java.com.crypto.exceptions.InsufficientBalanceException;
import main.java.com.crypto.exceptions.InvalidAmountException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionService {
    private static TransactionService instance;
    private TransactionRepository transactionRepository;
    private MempoolService mempoolService;


    private TransactionService() {
        this.transactionRepository = TransactionRepository.getInstance();
        this.mempoolService = MempoolService.getInstance();
    }

    public static synchronized TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    public Transaction createTransaction(Wallet wallet, String destinationAddress,
                                         double amount, FeeLevel feeLevel) {
        if (!validateAddress(wallet.getCryptoType(), destinationAddress)) {
            throw new InvalidAddressException("Adresse destination invalide pour " + wallet.getCryptoType());
        }

        if (amount <= 0) {
            throw new InvalidAmountException("Le montant doit être positif");
        }

        double fees = FeeCalculator.calculateFee(wallet.getCryptoType(), feeLevel, amount);
        double totalAmount = amount + fees;

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setSourceAddress(wallet.getAddress());
        transaction.setDestinationAddress(destinationAddress);
        transaction.setAmount(amount);
        transaction.setFees(fees);
        transaction.setFeeLevel(feeLevel);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCryptoType(wallet.getCryptoType());
        transaction.setWalletId(wallet.getId());
        transaction.setCreationDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (savedTransaction != null) {
            mempoolService.addTransactionToMempool(savedTransaction);

            // Débit réel du solde
            double newBalance = wallet.getBalance() - totalAmount;
            wallet.setBalance(newBalance);

            // Mettre à jour le wallet dans la base
            WalletService walletService = WalletService.getInstance();
            walletService.updateWalletBalance(wallet.getId(), newBalance);

            LoggerUtil.logInfo(String.format(
                    "Transaction créée: %s - Wallet: %s - Montant: %.6f - Frais: %.6f - Nouveau solde: %.6f",
                    savedTransaction.getId(), wallet.getId(), amount, fees, newBalance
            ));
        }

        return savedTransaction;
    }

    public Transaction createSimulatedTransaction(Wallet wallet, String destinationAddress,
                                                  double amount, FeeLevel feeLevel) {
        if (!validateAddress(wallet.getCryptoType(), destinationAddress)) {
            throw new InvalidAddressException("Adresse destination invalide");
        }

        if (amount <= 0) {
            throw new InvalidAmountException("Le montant doit être positif");
        }

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setSourceAddress(wallet.getAddress());
        transaction.setDestinationAddress(destinationAddress);
        transaction.setAmount(amount);
        transaction.setFees(calculateDeterministicFee(wallet.getCryptoType(), feeLevel, amount));
        transaction.setFeeLevel(feeLevel);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCryptoType(wallet.getCryptoType());
        transaction.setWalletId(wallet.getId());

        return transaction;
    }

    private double calculateDeterministicFee(CryptoType cryptoType, FeeLevel feeLevel, double amount) {
        double baseFee;
        switch (cryptoType) {
            case BITCOIN:
                baseFee = 0.0001;
                break;
            case ETHEREUM:
                baseFee = 0.001;
                break;
            default:
                baseFee = 0.001;
        }

        double multiplier = feeLevel.getMultiplier();
        double amountFactor = 1 + (amount / 10000);

        double fee = baseFee * multiplier * amountFactor;
        return Math.round(fee * 1000000.0) / 1000000.0;
    }

    public boolean validateAddress(CryptoType cryptoType, String address) {
        return AddressValidator.validateAddress(cryptoType, address);
    }

    public Optional<Transaction> getTransactionById(UUID id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByWallet(Wallet wallet) {
        return transactionRepository.findByWalletId(wallet.getId());
    }

    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.PENDING);
    }

    public boolean updateTransactionStatus(UUID transactionId, TransactionStatus newStatus) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setStatus(newStatus);

            if (newStatus == TransactionStatus.CONFIRMED) {
                transaction.setCreationDate(LocalDateTime.now());
            }

            Transaction updatedTransaction = transactionRepository.save(transaction);

            if (updatedTransaction != null) {
                if (newStatus == TransactionStatus.CONFIRMED) {
                    mempoolService.removeTransactionFromMempool(transactionId);
                }

                LoggerUtil.logInfo("Statut transaction " + transactionId + " mis à jour: " + newStatus);
                return true;
            }
        }
        return false;
    }

    public boolean processTransaction(Transaction transaction) {
        try {
            Thread.sleep(100);
            return updateTransactionStatus(transaction.getId(), TransactionStatus.CONFIRMED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return updateTransactionStatus(transaction.getId(), TransactionStatus.REJECTED);
        }
    }
}