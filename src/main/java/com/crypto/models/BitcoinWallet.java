package main.java.com.crypto.models;

import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.utils.AddressValidator;

public class BitcoinWallet extends Wallet {
    public BitcoinWallet() {
        super(CryptoType.BITCOIN);
    }

    @Override
    protected String generateAddress() {
        return AddressValidator.generateBitcoinAddress();
    }
}