package main.java.com.crypto.repositories;

import main.java.com.crypto.interfaces.ITransaction;
import main.java.com.crypto.models.Transaction;
import main.java.com.crypto.enums.TransactionStatus;
import main.java.com.crypto.enums.FeeLevel;
import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.utils.LoggerUtil;
import java.sql.*;
import java.util.*;

public class TransactionRepository implements ITransaction {
    private static TransactionRepository instance;
    private DatabaseConnection dbConnection;

    private TransactionRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public static synchronized TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la recherche de la transaction par ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY creation_date DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la récupération de toutes les transactions", e);
        }
        return transactions;
    }

    @Override
    public Transaction save(Transaction transaction) {
        if (findById(transaction.getId()).isPresent()) {
            return update(transaction);
        } else {
            return insert(transaction);
        }
    }

    private Transaction insert(Transaction transaction) {
        String sql = "INSERT INTO transactions (id, source_address, destination_address, amount, fees, " +
                "creation_date, fee_level, status, crypto_type, wallet_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, transaction.getId());
            stmt.setString(2, transaction.getSourceAddress());
            stmt.setString(3, transaction.getDestinationAddress());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setDouble(5, transaction.getFees());
            stmt.setTimestamp(6, Timestamp.valueOf(transaction.getCreationDate()));
            stmt.setString(7, transaction.getFeeLevel().name());
            stmt.setString(8, transaction.getStatus().name());
            stmt.setString(9, transaction.getCryptoType().name());
            stmt.setObject(10, transaction.getWalletId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LoggerUtil.logInfo("Transaction insérée avec succès: " + transaction.getId());
                return transaction;
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de l'insertion de la transaction: " + transaction.getId(), e);
        }
        return null;
    }

    private Transaction update(Transaction transaction) {
        String sql = "UPDATE transactions SET source_address = ?, destination_address = ?, amount = ?, " +
                "fees = ?, creation_date = ?, fee_level = ?, status = ?, crypto_type = ?, wallet_id = ? " +
                "WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getSourceAddress());
            stmt.setString(2, transaction.getDestinationAddress());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setDouble(4, transaction.getFees());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getCreationDate()));
            stmt.setString(6, transaction.getFeeLevel().name());
            stmt.setString(7, transaction.getStatus().name());
            stmt.setString(8, transaction.getCryptoType().name());
            stmt.setObject(9, transaction.getWalletId());
            stmt.setObject(10, transaction.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LoggerUtil.logInfo("Transaction mise à jour avec succès: " + transaction.getId());
                return transaction;
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la mise à jour de la transaction: " + transaction.getId(), e);
        }
        return null;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                LoggerUtil.logInfo("Transaction supprimée avec succès: " + id);
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la suppression de la transaction: " + id, e);
        }
    }

    @Override
    public List<Transaction> findByWalletId(UUID walletId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE wallet_id = ? ORDER BY creation_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, walletId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la recherche des transactions par wallet ID: " + walletId, e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByStatus(String status) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE status = ? ORDER BY creation_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.toUpperCase());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la recherche des transactions par statut: " + status, e);
        }
        return transactions;
    }

    public List<Transaction> findByStatus(TransactionStatus status) {
        return findByStatus(status.name());
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();

        transaction.setId((UUID) rs.getObject("id"));
        transaction.setSourceAddress(rs.getString("source_address"));
        transaction.setDestinationAddress(rs.getString("destination_address"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setFees(rs.getDouble("fees"));
        transaction.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        transaction.setFeeLevel(FeeLevel.valueOf(rs.getString("fee_level")));
        transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        transaction.setCryptoType(CryptoType.valueOf(rs.getString("crypto_type")));
        transaction.setWalletId((UUID) rs.getObject("wallet_id"));

        return transaction;
    }
}
