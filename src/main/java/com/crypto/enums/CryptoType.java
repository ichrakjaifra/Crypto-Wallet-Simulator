package main.java.com.crypto.enums;

public enum CryptoType {
    BITCOIN("BTC"),
    ETHEREUM("ETH");

    private final String symbol;

    CryptoType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
