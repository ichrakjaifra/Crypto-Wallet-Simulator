package main.java.com.crypto.utils;

import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.enums.FeeLevel;
import java.util.Random;

public class FeeCalculator {
    private static final double BTC_BASE_FEE = 0.0001; // en BTC
    private static final double ETH_BASE_FEE = 0.001; // en ETH
    private static final Random random = new Random();

    public static double calculateFee(CryptoType cryptoType, FeeLevel feeLevel, double amount) {
        double baseFee = getBaseFee(cryptoType);
        double multiplier = getMultiplier(feeLevel);
        double amountFactor = 1 + (amount / 10000); // Impact modéré du montant

        // Variation aléatoire pour simuler la réalité du réseau
        double randomFactor = 0.8 + (random.nextDouble() * 0.4); // Entre 0.8 et 1.2

        double calculatedFee = baseFee * multiplier * amountFactor * randomFactor;

        // Arrondir à 6 décimales
        return Math.round(calculatedFee * 1000000.0) / 1000000.0;
    }

    public static double estimateFee(CryptoType cryptoType, FeeLevel feeLevel, double amount) {
        double baseFee = getBaseFee(cryptoType);
        double multiplier = getMultiplier(feeLevel);
        double amountFactor = 1 + (amount / 10000);

        return baseFee * multiplier * amountFactor;
    }

    private static double getBaseFee(CryptoType cryptoType) {
        switch (cryptoType) {
            case BITCOIN:
                return BTC_BASE_FEE;
            case ETHEREUM:
                return ETH_BASE_FEE;
            default:
                return 0.001;
        }
    }

    private static double getMultiplier(FeeLevel feeLevel) {
        return feeLevel.getMultiplier();
    }

    // Méthodes spécifiques pour chaque type de crypto
    private static double calculateBitcoinFee(FeeLevel feeLevel, double amount) {
        // Simulation des frais Bitcoin basés sur la taille de la transaction
        double baseSize = 250; // Taille de base en bytes
        double feePerByte = getBitcoinFeePerByte(feeLevel);
        return (baseSize * feePerByte) / 100000000; // Conversion en BTC
    }

    private static double calculateEthereumFee(FeeLevel feeLevel, double amount) {
        // Simulation des frais Ethereum basés sur le gas
        double gasLimit = 21000; // Limite de gas standard
        double gasPrice = getEthereumGasPrice(feeLevel); // Prix du gas en Gwei
        return (gasLimit * gasPrice) / 1000000000; // Conversion en ETH
    }

    private static double getBitcoinFeePerByte(FeeLevel feeLevel) {
        switch (feeLevel) {
            case ECONOMIQUE: return 10; // satoshis per byte
            case STANDARD: return 20;
            case RAPIDE: return 40;
            default: return 20;
        }
    }

    private static double getEthereumGasPrice(FeeLevel feeLevel) {
        switch (feeLevel) {
            case ECONOMIQUE: return 30; // Gwei
            case STANDARD: return 50;
            case RAPIDE: return 100;
            default: return 50;
        }
    }
}
