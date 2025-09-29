
import main.java.com.crypto.services.WalletService;
import main.java.com.crypto.services.TransactionService;
import main.java.com.crypto.services.MempoolService;
import main.java.com.crypto.repositories.WalletRepository;
import main.java.com.crypto.models.Wallet;
import main.java.com.crypto.models.Transaction;
import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.enums.FeeLevel;
import main.java.com.crypto.enums.TransactionStatus;
import main.java.com.crypto.utils.LoggerUtil;
import main.java.com.crypto.exceptions.InvalidAddressException;
import main.java.com.crypto.exceptions.InsufficientBalanceException;
import main.java.com.crypto.exceptions.InvalidAmountException;

import java.util.Scanner;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main {

    private static WalletService walletService;
    private static TransactionService transactionService;
    private static MempoolService mempoolService;
    private static Scanner scanner = new Scanner(System.in);
    private static Wallet currentWallet = null;

    public static void main(String[] args) {
        try {

            walletService = WalletService.getInstance();
            transactionService = TransactionService.getInstance();
            mempoolService = MempoolService.getInstance();

            initializeDatabase();

            System.out.println("=== CRYPTO WALLET SIMULATOR ===");
            System.out.println("Application initialisée avec succès!");

            boolean running = true;

            while (running) {
                displayMainMenu();
                int choice = getIntInput("Choisissez une option: ");

                switch (choice) {
                    case 1:
                        createWallet();
                        break;
                    case 2:
                        selectWallet();
                        break;
                    case 3:
                        if (currentWallet != null) {
                            creditWallet();
                        } else {
                            System.out.println("Veuillez d'abord créer ou sélectionner un wallet.");
                        }
                        break;
                    case 4:
                        if (currentWallet != null) {
                            createTransaction();
                        } else {
                            System.out.println("Veuillez d'abord créer ou sélectionner un wallet.");
                        }
                        break;
                    case 5:
                        if (currentWallet != null) {
                            checkMempoolPosition();
                        } else {
                            System.out.println("Veuillez d'abord créer ou sélectionner un wallet.");
                        }
                        break;
                    case 6:
                        if (currentWallet != null) {
                            compareFeeLevels();
                        } else {
                            System.out.println("Veuillez d'abord créer ou sélectionner un wallet.");
                        }
                        break;
                    case 7:
                        if (currentWallet != null) {
                            viewMempool();
                        } else {
                            System.out.println("Veuillez d'abord créer ou sélectionner un wallet.");
                        }
                        break;
                    case 8:
                        if (currentWallet != null) {
                            viewWalletInfo();
                        } else {
                            System.out.println("Veuillez d'abord créer ou sélectionner un wallet.");
                        }
                        break;
                    case 9:
                        running = false;
                        System.out.println("Merci d'avoir utilisé Crypto Wallet Simulator!");
                        break;
                    default:
                        System.out.println("Option invalide. Veuillez réessayer.");
                }
            }

        } catch (Exception e) {
            LoggerUtil.logError("Erreur critique dans l'application: " + e.getMessage(), e);
            System.out.println("Une erreur critique est survenue. Voir les logs pour plus de détails.");
        } finally {
            scanner.close();
            LoggerUtil.logInfo("Application fermée");
        }
    }

    private static void initializeDatabase() {
        try {
            WalletRepository walletRepo = WalletRepository.getInstance();
            walletRepo.initializeDatabase();
            LoggerUtil.logInfo("Base de données initialisée avec succès");
        } catch (Exception e) {
            LoggerUtil.logError("Erreur lors de l'initialisation de la base de données", e);
            throw new RuntimeException("Impossible d'initialiser la base de données", e);
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== CRYPTO WALLET SIMULATOR ===");
        System.out.println("1. Créer un wallet crypto");
        System.out.println("2. Sélectionner un wallet");

        if (currentWallet != null) {
            System.out.println("3. Créditer le wallet (TEST)");
            System.out.println("4. Créer une transaction");
            System.out.println("5. Voir ma position dans le mempool");
            System.out.println("6. Comparer les 3 niveaux de frais");
            System.out.println("7. Consulter l'état actuel du mempool");
            System.out.println("8. Informations du wallet");
            System.out.println("9. Quitter");
        } else {
            System.out.println("9. Quitter");
        }
        System.out.println("===============================");

        if (currentWallet != null) {
            System.out.printf("Wallet actuel: %s (%s) - Solde: %.6f %s%n",
                    currentWallet.getAddress(),
                    currentWallet.getCryptoType(),
                    currentWallet.getBalance(),
                    currentWallet.getCryptoType().getSymbol());
        }
    }

    private static void createWallet() {
        System.out.println("\n=== CRÉATION D'UN WALLET ===");
        System.out.println("Choisissez le type de crypto:");
        System.out.println("1. Bitcoin (BTC)");
        System.out.println("2. Ethereum (ETH)");

        int choice = getIntInput("Votre choix: ");
        if (choice < 1 || choice > 2) {
            System.out.println("Choix invalide.");
            return;
        }

        CryptoType cryptoType = (choice == 1) ? CryptoType.BITCOIN : CryptoType.ETHEREUM;

        try {
            Wallet wallet = walletService.createWallet(cryptoType);
            currentWallet = wallet;

            System.out.println("Wallet créé avec succès!");
            System.out.println("ID: " + wallet.getId());
            System.out.println("Adresse: " + wallet.getAddress());
            System.out.println("Type: " + wallet.getCryptoType());
            System.out.println("Solde initial: 0.0 " + wallet.getCryptoType().getSymbol());

        } catch (Exception e) {
            System.out.println("Erreur lors de la création du wallet: " + e.getMessage());
            LoggerUtil.logError("Erreur création wallet: " + e.getMessage(), e);
        }
    }

    private static void creditWallet() {
        if (currentWallet == null) {
            System.out.println("Veuillez d'abord sélectionner un wallet.");
            return;
        }

        System.out.println("\n=== CRÉDITER LE WALLET (TEST) ===");
        System.out.printf("Wallet: %s%n", currentWallet.getAddress());
        System.out.printf("Solde actuel: %.6f %s%n",
                currentWallet.getBalance(),
                currentWallet.getCryptoType().getSymbol());

        double amount = getDoubleInput("Montant à créditer: ");

        if (amount <= 0) {
            System.out.println("Le montant doit être positif.");
            return;
        }

        boolean success = walletService.creditWallet(currentWallet.getId(), amount);

        if (success) {
            System.out.println("Wallet crédité avec succès!");
            // Recharger le wallet depuis la base
            currentWallet = walletService.getWalletById(currentWallet.getId()).orElse(currentWallet);
            System.out.printf("Nouveau solde: %.6f %s%n",
                    currentWallet.getBalance(),
                    currentWallet.getCryptoType().getSymbol());
        } else {
            System.out.println("Erreur lors du crédit.");
        }
    }

    private static void selectWallet() {
        System.out.println("\n=== SÉLECTION D'UN WALLET ===");
        List<Wallet> wallets = walletService.getAllWallets();

        if (wallets.isEmpty()) {
            System.out.println("Aucun wallet disponible. Veuillez d'abord créer un wallet.");
            return;
        }

        System.out.println("Wallets disponibles:");
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet = wallets.get(i);
            // Charger les transactions pour chaque wallet
            List<Transaction> transactions = transactionService.getTransactionsByWallet(wallet);
            wallet.loadTransactions(transactions);

            System.out.printf("%d. %s (%s) - Solde: %.6f %s - Transactions: %d%n",
                    i + 1,
                    wallet.getAddress(),
                    wallet.getCryptoType(),
                    wallet.getBalance(),
                    wallet.getCryptoType().getSymbol(),
                    transactions.size());
        }

        int choice = getIntInput("Choisissez un wallet: ");
        if (choice < 1 || choice > wallets.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        currentWallet = wallets.get(choice - 1);
        // Charger les transactions du wallet sélectionné
        List<Transaction> transactions = transactionService.getTransactionsByWallet(currentWallet);
        currentWallet.loadTransactions(transactions);

        System.out.println("Wallet sélectionné: " + currentWallet.getAddress());
        System.out.println("Transactions: " + currentWallet.getTransactions().size());
    }

    private static void createTransaction() {
        if (currentWallet == null) {
            System.out.println("Veuillez d'abord sélectionner un wallet.");
            return;
        }

        System.out.println("\n=== CRÉATION D'UNE TRANSACTION ===");
        System.out.println("Wallet source: " + currentWallet.getAddress());
        System.out.printf("Solde disponible: %.6f %s%n",
                currentWallet.getBalance(), currentWallet.getCryptoType().getSymbol());
        System.out.println("Transactions existantes: " + currentWallet.getTransactions().size());

        String destinationAddress = getStringInput("Adresse destination: ");

        double amount = getDoubleInput("Montant à envoyer: ");
        if (amount <= 0) {
            System.out.println("Le montant doit être positif.");
            return;
        }

        System.out.println("Choisissez le niveau de frais:");
        System.out.println("1. Économique (lent)");
        System.out.println("2. Standard (moyen)");
        System.out.println("3. Rapide (rapide)");

        int feeChoice = getIntInput("Votre choix: ");
        if (feeChoice < 1 || feeChoice > 3) {
            System.out.println("Choix invalide.");
            return;
        }

        FeeLevel feeLevel = FeeLevel.ECONOMIQUE;
        switch (feeChoice) {
            case 1: feeLevel = FeeLevel.ECONOMIQUE; break;
            case 2: feeLevel = FeeLevel.STANDARD; break;
            case 3: feeLevel = FeeLevel.RAPIDE; break;
        }

        try {
            // Sauvegarder l'état avant
            double ancienSolde = currentWallet.getBalance();
            int ancienNbTransactions = currentWallet.getTransactions().size();

            Transaction transaction = transactionService.createTransaction(
                    currentWallet,
                    destinationAddress,
                    amount,
                    feeLevel
            );

            System.out.println("\nTransaction créée avec succès!");
            System.out.println("ID: " + transaction.getId());
            System.out.println("Montant: " + transaction.getAmount() + " " + currentWallet.getCryptoType().getSymbol());
            System.out.println("Frais: " + transaction.getFees() + " " + currentWallet.getCryptoType().getSymbol());
            System.out.println("Total débité: " + (transaction.getAmount() + transaction.getFees()) + " " + currentWallet.getCryptoType().getSymbol());
            System.out.println("Niveau de frais: " + transaction.getFeeLevel());
            System.out.println("Statut: " + transaction.getStatus());

            int position = mempoolService.calculatePosition(transaction);
            long estimatedTime = mempoolService.estimateTime(position, feeLevel);
            System.out.println("Position dans le mempool: " + position + " / " + mempoolService.getMempoolSize());
            System.out.println("Temps d'attente estimé: " + estimatedTime + " minutes");


            Optional<Wallet> walletMaj = walletService.getWalletById(currentWallet.getId());
            if (walletMaj.isPresent()) {
                currentWallet = walletMaj.get();
                List<Transaction> transactions = transactionService.getTransactionsByWallet(currentWallet);
                currentWallet.loadTransactions(transactions);


                System.out.println("\n=== MISE À JOUR DU WALLET ===");
                System.out.printf("Solde avant: %.6f %s%n", ancienSolde, currentWallet.getCryptoType().getSymbol());
                System.out.printf("Solde après: %.6f %s%n", currentWallet.getBalance(), currentWallet.getCryptoType().getSymbol());
                System.out.println("Transactions avant: " + ancienNbTransactions);
                System.out.println("Transactions après: " + currentWallet.getTransactions().size());
            }

        } catch (InvalidAddressException e) {
            System.out.println("Erreur d'adresse: " + e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println("Solde insuffisant: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Erreur de montant: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la transaction: " + e.getMessage());
            LoggerUtil.logError("Erreur création transaction: " + e.getMessage(), e);
        }
    }

    private static void checkMempoolPosition() {
        if (currentWallet == null) {
            System.out.println("Veuillez d'abord sélectionner un wallet.");
            return;
        }

        // Recharger les transactions
        List<Transaction> transactions = transactionService.getTransactionsByWallet(currentWallet);
        currentWallet.loadTransactions(transactions);

        List<Transaction> pendingTransactions = currentWallet.getTransactions().stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING)
                .collect(Collectors.toList());

        if (pendingTransactions.isEmpty()) {
            System.out.println("Aucune transaction en attente pour ce wallet.");
            return;
        }

        System.out.println("\n=== POSITION DANS LE MEMPOOL ===");
        System.out.println("Transactions en attente trouvées: " + pendingTransactions.size());

        for (int i = 0; i < pendingTransactions.size(); i++) {
            Transaction tx = pendingTransactions.get(i);
            int position = mempoolService.calculatePosition(tx);
            long estimatedTime = mempoolService.estimateTime(position, tx.getFeeLevel());
            int totalTransactions = mempoolService.getMempoolSize();

            System.out.printf("%d. Transaction: %s%n", i + 1, tx.getId().toString().substring(0, 8) + "...");
            System.out.printf("   Montant: %.6f %s%n", tx.getAmount(), currentWallet.getCryptoType().getSymbol());
            System.out.printf("   Frais: %.6f %s%n", tx.getFees(), currentWallet.getCryptoType().getSymbol());
            System.out.printf("   Position: %d / %d%n", position, totalTransactions);
            System.out.printf("   Temps estimé: %d minutes%n", estimatedTime);
            System.out.println("   --------------------");
        }
    }

    private static void compareFeeLevels() {
        if (currentWallet == null) {
            System.out.println("Veuillez d'abord sélectionner un wallet.");
            return;
        }

        System.out.println("\n=== COMPARAISON DES FRAIS ===");
        String destinationAddress = getStringInput("Adresse destination (test): ");
        double amount = getDoubleInput("Montant à envoyer: ");

        if (amount <= 0) {
            System.out.println("Le montant doit être positif.");
            return;
        }

        mempoolService.generateRandomTransactions(20);

        System.out.println("\nComparaison des niveaux de frais pour " + amount + " " + currentWallet.getCryptoType().getSymbol());
        System.out.println("┌──────────────┬────────────┬──────────┬──────────────┬──────────────┐");
        System.out.println("│ Niveau       │ Frais       │ Position │ Temps estimé │ Coût total   │");
        System.out.println("├──────────────┼────────────┼──────────┼──────────────┼──────────────┤");

        for (FeeLevel level : FeeLevel.values()) {
            try {
                Transaction tempTx = transactionService.createSimulatedTransaction(
                        currentWallet,
                        destinationAddress,
                        amount,
                        level
                );

                int position = mempoolService.calculateSimulatedPosition(tempTx, level);
                long estimatedTime = mempoolService.estimateTime(position, level);
                double totalCost = tempTx.getAmount() + tempTx.getFees();

                System.out.printf("│ %-12s │ %-10.6f │ %-8d │ %-12d │ %-12.6f │%n",
                        level, tempTx.getFees(), position, estimatedTime, totalCost);

            } catch (Exception e) {
                System.out.printf("│ %-12s │ %-10s │ %-8s │ %-12s │ %-12s │%n",
                        level, "ERREUR", "-", "-", "-");
            }
        }

        System.out.println("└──────────────┴────────────┴──────────┴──────────────┴──────────────┘");

        mempoolService.clearMempool();
    }

    private static void viewMempool() {
        System.out.println("\n=== ÉTAT DU MEMPOOL ===");

        mempoolService.generateRandomTransactions(15);

        List<Transaction> mempoolTransactions = mempoolService.getSortedMempoolTransactions();
        int totalTransactions = mempoolTransactions.size();

        System.out.println("Transactions en attente: " + totalTransactions);
        System.out.println("┌──────────────────────────────────┬────────────┬────────────┬──────────┐");
        System.out.println("│ Transaction                      │ Frais       │ Priorité   │ Position │");
        System.out.println("├──────────────────────────────────┼────────────┼────────────┼──────────┤");

        for (int i = 0; i < Math.min(10, mempoolTransactions.size()); i++) {
            Transaction tx = mempoolTransactions.get(i);
            String displayId = tx.getId().toString().substring(0, 8) + "...";
            String prefix = "   ";
            String suffix = "";

            if (currentWallet != null) {
                List<Transaction> walletTransactions = transactionService.getTransactionsByWallet(currentWallet);
                boolean isUserTransaction = walletTransactions.stream()
                        .anyMatch(t -> t.getId().equals(tx.getId()));

                if (isUserTransaction) {
                    prefix = ">>> ";
                    suffix = " (VOUS)";
                }
            }

            System.out.printf("│ %-32s │ %-10.6f │ %-10s │ %-8d │%n",
                    prefix + displayId + suffix, tx.getFees(), tx.getFeeLevel(), i + 1);
        }

        if (mempoolTransactions.size() > 10) {
            System.out.printf("│ ... %-26d transactions supplémentaires ... │%n", mempoolTransactions.size() - 10);
        }

        System.out.println("└──────────────────────────────────┴────────────┴────────────┴──────────┘");
    }

    private static void viewWalletInfo() {
        if (currentWallet == null) {
            System.out.println("Veuillez d'abord sélectionner un wallet.");
            return;
        }

        // ⚠️ FORCER LE RECHARGEMENT
        Optional<Wallet> walletOpt = walletService.getWalletById(currentWallet.getId());
        if (walletOpt.isPresent()) {
            currentWallet = walletOpt.get();
            List<Transaction> transactions = transactionService.getTransactionsByWallet(currentWallet);
            currentWallet.loadTransactions(transactions);
        }

        System.out.println("\n=== INFORMATIONS DU WALLET ===");
        System.out.println("ID: " + currentWallet.getId());
        System.out.println("Adresse: " + currentWallet.getAddress());
        System.out.println("Type: " + currentWallet.getCryptoType());
        System.out.printf("Solde: %.6f %s%n", currentWallet.getBalance(), currentWallet.getCryptoType().getSymbol());

        List<Transaction> transactions = currentWallet.getTransactions();
        System.out.println("Nombre total de transactions: " + transactions.size());

        if (!transactions.isEmpty()) {
            System.out.println("\nDernières transactions:");
            System.out.println("┌──────────────────────────────────┬────────────┬────────────┬────────────┬──────────┐");
            System.out.println("│ ID                               │ Montant     │ Frais      │ Statut     │ Date     │");
            System.out.println("├──────────────────────────────────┼────────────┼────────────┼────────────┼──────────┤");

            transactions.stream()
                    .sorted((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()))
                    .limit(5)
                    .forEach(tx -> {
                        String shortId = tx.getId().toString().substring(0, 8) + "...";
                        String date = tx.getCreationDate().toLocalDate().toString();
                        System.out.printf("│ %-32s │ %-10.6f │ %-10.6f │ %-10s │ %-8s │%n",
                                shortId, tx.getAmount(), tx.getFees(), tx.getStatus(), date);
                    });

            System.out.println("└──────────────────────────────────┴────────────┴────────────┴────────────┴──────────┘");
        } else {
            System.out.println("\nAucune transaction pour ce wallet.");
        }
    }

    private static int getIntInput(String message) {
        System.out.print(message);
        while (!scanner.hasNextInt()) {
            System.out.println("Veuillez entrer un nombre valide.");
            scanner.next();
            System.out.print(message);
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    private static double getDoubleInput(String message) {
        System.out.print(message);
        while (!scanner.hasNextDouble()) {
            System.out.println("Veuillez entrer un nombre valide.");
            scanner.next();
            System.out.print(message);
        }
        double input = scanner.nextDouble();
        scanner.nextLine();
        return input;
    }

    private static String getStringInput(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}