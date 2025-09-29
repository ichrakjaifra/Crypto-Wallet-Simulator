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

        if (sortedTransactions.isEmpty()) {
            return 1;
        }

        int position = 1;
        for (Transaction tx : sortedTransactions) {
            if (tx.getFees() > transaction.getFees()) {
                position++;
            } else {
                break;
            }
        }

        return position;
    }

    public int calculateSimulatedPosition(Transaction simulatedTransaction, FeeLevel feeLevel) {
        List<Transaction> simulatedMempool = new ArrayList<>(mempool.getTransactions());

        Transaction tempTx = new Transaction();
        tempTx.setId(UUID.randomUUID());
        tempTx.setFees(calculateSimulatedFee(simulatedTransaction.getCryptoType(), feeLevel, simulatedTransaction.getAmount()));
        tempTx.setFeeLevel(feeLevel);

        simulatedMempool.add(tempTx);

        List<Transaction> sorted = simulatedMempool.stream()
                .sorted(Comparator.comparingDouble(Transaction::getFees).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getId().equals(tempTx.getId())) {
                return i + 1;
            }
        }

        return sorted.size();
    }

    private double calculateSimulatedFee(CryptoType cryptoType, FeeLevel feeLevel, double amount) {
        double baseFee = getBaseFee(cryptoType);
        double multiplier = feeLevel.getMultiplier();
        double amountFactor = 1 + (amount / 10000);

        return baseFee * multiplier * amountFactor;
    }

    private double getBaseFee(CryptoType cryptoType) {
        switch (cryptoType) {
            case BITCOIN: return 0.0001;
            case ETHEREUM: return 0.001;
            default: return 0.001;
        }
    }

    public long estimateTime(int position, FeeLevel feeLevel) {
        long baseTime = position * 10L;

        switch (feeLevel) {
            case RAPIDE:
                return baseTime / 3;
            case STANDARD:
                return baseTime / 2;
            case ECONOMIQUE:
            default:
                return baseTime;
        }
    }

    public long estimateTime(int position) {
        return estimateTime(position, FeeLevel.ECONOMIQUE);
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
        transaction.setAmount(1 + (random.nextDouble() * 9));
        transaction.setFees(0.1 + (random.nextDouble() * 4.9));
        transaction.setCreationDate(LocalDateTime.now().minusMinutes(random.nextInt(120)));
        transaction.setFeeLevel(FeeLevel.values()[random.nextInt(FeeLevel.values().length)]);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCryptoType(CryptoType.values()[random.nextInt(CryptoType.values().length)]);
        transaction.setWalletId(UUID.randomUUID());

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