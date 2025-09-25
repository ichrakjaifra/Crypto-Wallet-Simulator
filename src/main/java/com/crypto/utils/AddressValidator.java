package main.java.com.crypto.utils;

import main.java.com.crypto.enums.CryptoType;
import java.util.regex.Pattern;
import java.util.Random;

public class AddressValidator {
    private static final Pattern BITCOIN_PATTERN = Pattern.compile("^(1|3|bc1)[a-zA-Z0-9]{25,39}$");
    private static final Pattern ETHEREUM_PATTERN = Pattern.compile("^0x[a-fA-F0-9]{40}$");
    private static final Random random = new Random();

    public static boolean validateAddress(CryptoType cryptoType, String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }

        switch (cryptoType) {
            case BITCOIN:
                return validateBitcoinAddress(address);
            case ETHEREUM:
                return validateEthereumAddress(address);
            default:
                return false;
        }
    }

    private static boolean validateBitcoinAddress(String address) {
        return BITCOIN_PATTERN.matcher(address).matches();
    }

    private static boolean validateEthereumAddress(String address) {
        return ETHEREUM_PATTERN.matcher(address).matches();
    }

    public static String generateBitcoinAddress() {
        // Simulation simple d'une adresse Bitcoin
        String[] prefixes = {"1", "3", "bc1"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String chars = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        StringBuilder address = new StringBuilder(prefix);

        int length = 25 + random.nextInt(15); // Longueur entre 25 et 39 caractères
        for (int i = prefix.length(); i < length; i++) {
            address.append(chars.charAt(random.nextInt(chars.length())));
        }

        return address.toString();
    }

    public static String generateEthereumAddress() {
        // Simulation simple d'une adresse Ethereum
        String chars = "0123456789abcdef";
        StringBuilder address = new StringBuilder("0x");

        for (int i = 0; i < 40; i++) {
            address.append(chars.charAt(random.nextInt(chars.length())));
        }

        return address.toString();
    }
}
