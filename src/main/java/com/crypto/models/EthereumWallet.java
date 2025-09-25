package main.java.com.crypto.models;

import main.java.com.crypto.enums.CryptoType;
import main.java.com.crypto.utils.AddressValidator;

public class EthereumWallet extends Wallet {
    public EthereumWallet() {
        super(CryptoType.ETHEREUM);
    }

    @Override
    protected String generateAddress() {
        return AddressValidator.generateEthereumAddress();
    }
}
