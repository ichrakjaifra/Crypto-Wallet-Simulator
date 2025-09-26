package main.java.com.crypto.services;

import main.java.com.crypto.models.Mempool;
import main.java.com.crypto.models.Transaction;
import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.enums.FeeLevel;
import main.java.com.crypto.enums.TransactionStatus;
import main.java.com.crypto.utils.FeeCalculator;
import main.java.com.crypto.utils.LoggerUtil;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MempoolService {
    private static MempoolService instance;
    private Mempool mempool;
    private Random random;

    private MempoolService() {
        this.mempool = Mempool.getInstance();
        this.random = new Random();
    }

    public static synchronized MempoolService getInstance() {
        if (instance == null) {
            instance = new MempoolService();
        }
        return instance;
    }

    public void addTransactionToMempool(Transaction transaction) {
        mempool.addTransaction(transaction);
        LoggerUtil.logInfo("Transaction ajoutée au mempool: " + transaction.getId());
    }

    public void removeTransactionFromMempool(UUID transactionId) {
        mempool.removeTransaction(transactionId);
        LoggerUtil.logInfo("Transaction retirée du mempool: " + transactionId);
    }

    public int calculatePosition(Transaction transaction) {
        List<Transaction> sortedTransactions = mempool.getSortedTransactions();

        for (int i = 0; i < sortedTransactions.size(); i++) {
            if (sortedTransactions.get(i).getId().equals(transaction.getId())) {
                return i + 1;
            }
        }

        // Si la transaction n'est pas trouvée, elle serait à la fin
        return sortedTransactions.size() + 1;
    }

    public long estimateTime(int position) {
        return position * 10L; // 10 minutes par position
    }

    public int getMempoolSize() {
        return mempool.getTransactionCount();
    }

    public List<Transaction> getMempoolTransactions() {
        return mempool.getTransactions();
    }

    public List<Transaction> getSortedMempoolTransactions() {
        return mempool.getSortedTransactions();
    }

    public void generateRandomTransactions(int count) {
        for (int i = 0; i < count; i++) {
            Transaction randomTx = createRandomTransaction();
            mempool.addTransaction(randomTx);
        }
        LoggerUtil.logInfo(count + " transactions aléatoires générées dans le mempool");
    }

    private Transaction createRandomTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setSourceAddress(generateRandomAddress());
        transaction.setDestinationAddress(generateRandomAddress());
        transaction.setAmount(1 + (random.nextDouble() * 9)); // Montant aléatoire entre 1 et 10
        transaction.setFees(0.1 + (random.nextDouble() * 4.9)); // Frais aléatoires entre 0.1 et 5
        transaction.setCreationDate(LocalDateTime.now().minusMinutes(random.nextInt(120)));
        transaction.setFeeLevel(FeeLevel.values()[random.nextInt(FeeLevel.values().length)]);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCryptoType(CryptoType.values()[random.nextInt(CryptoType.values().length)]);
        transaction.setWalletId(UUID.randomUUID()); // ID wallet fictif

        return transaction;
    }

    private String generateRandomAddress() {
        String chars = "0123456789abcdef";
        StringBuilder address = new StringBuilder("0x");
        for (int i = 0; i < 40; i++) {
            address.append(chars.charAt(random.nextInt(chars.length())));
        }
        return address.toString();
    }

    public void clearMempool() {
        mempool.clear();
        LoggerUtil.logInfo("Mempool vidé");
    }

    public Optional<Transaction> findTransactionInMempool(UUID transactionId) {
        return mempool.getTransactionById(transactionId);
    }
}
