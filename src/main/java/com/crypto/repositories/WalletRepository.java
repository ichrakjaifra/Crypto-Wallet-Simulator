package main.java.com.crypto.repositories;

import main.java.com.crypto.interfaces.IWallet;
import main.java.com.crypto.models.Wallet;
import main.java.com.crypto.models.BitcoinWallet;
import main.java.com.crypto.models.EthereumWallet;
import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.utils.LoggerUtil;
import java.sql.*;
import java.util.*;

public class WalletRepository implements IWallet {
    private static WalletRepository instance;
    private DatabaseConnection dbConnection;

    private WalletRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public static synchronized WalletRepository getInstance() {
        if (instance == null) {
            instance = new WalletRepository();
        }
        return instance;
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        String sql = "SELECT * FROM wallets WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la recherche du wallet par ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Wallet> findAll() {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT * FROM wallets ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                wallets.add(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la récupération de tous les wallets", e);
        }
        return wallets;
    }

    @Override
    public Wallet save(Wallet wallet) {
        if (findById(wallet.getId()).isPresent()) {
            return update(wallet);
        } else {
            return insert(wallet);
        }
    }

    private Wallet insert(Wallet wallet) {
        String sql = "INSERT INTO wallets (id, address, balance, crypto_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, wallet.getId());
            stmt.setString(2, wallet.getAddress());
            stmt.setDouble(3, wallet.getBalance());
            stmt.setString(4, wallet.getCryptoType().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LoggerUtil.logInfo("Wallet inséré avec succès: " + wallet.getId());
                return wallet;
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de l'insertion du wallet: " + wallet.getId(), e);
        }
        return null;
    }

    private Wallet update(Wallet wallet) {
        String sql = "UPDATE wallets SET address = ?, balance = ?, crypto_type = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, wallet.getAddress());
            stmt.setDouble(2, wallet.getBalance());
            stmt.setString(3, wallet.getCryptoType().name());
            stmt.setObject(4, wallet.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LoggerUtil.logInfo("Wallet mis à jour avec succès: " + wallet.getId());
                return wallet;
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la mise à jour du wallet: " + wallet.getId(), e);
        }
        return null;
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM wallets WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                LoggerUtil.logInfo("Wallet supprimé avec succès: " + id);
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la suppression du wallet: " + id, e);
        }
    }

    @Override
    public Optional<Wallet> findByAddress(String address) {
        String sql = "SELECT * FROM wallets WHERE address = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la recherche du wallet par adresse: " + address, e);
        }
        return Optional.empty();
    }

    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        CryptoType cryptoType = CryptoType.valueOf(rs.getString("crypto_type"));
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

        wallet.setId((UUID) rs.getObject("id"));
        wallet.setAddress(rs.getString("address"));
        wallet.setBalance(rs.getDouble("balance"));

        return wallet;
    }

    public void initializeDatabase() {
        createTablesIfNotExist();
    }

    private void createTablesIfNotExist() {
        String[] createTables = {
                "CREATE TABLE IF NOT EXISTS wallets (" +
                        "id UUID PRIMARY KEY, " +
                        "address VARCHAR(255) NOT NULL UNIQUE, " +
                        "balance DOUBLE PRECISION NOT NULL DEFAULT 0.0, " +
                        "crypto_type VARCHAR(20) NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

                "CREATE TABLE IF NOT EXISTS transactions (" +
                        "id UUID PRIMARY KEY, " +
                        "source_address VARCHAR(255) NOT NULL, " +
                        "destination_address VARCHAR(255) NOT NULL, " +
                        "amount DOUBLE PRECISION NOT NULL, " +
                        "fees DOUBLE PRECISION NOT NULL, " +
                        "creation_date TIMESTAMP NOT NULL, " +
                        "fee_level VARCHAR(20) NOT NULL, " +
                        "status VARCHAR(20) NOT NULL, " +
                        "crypto_type VARCHAR(20) NOT NULL, " +
                        "wallet_id UUID REFERENCES wallets(id) ON DELETE CASCADE, " +
                        "confirmed_at TIMESTAMP NULL)"
        };

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : createTables) {
                stmt.executeUpdate(sql);
            }
            LoggerUtil.logInfo("Tables de la base de données initialisées avec succès");

        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de l'initialisation de la base de données", e);
        }
    }
}
