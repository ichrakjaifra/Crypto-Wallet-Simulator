-- Script de création de la base de données PostgreSQL

CREATE DATABASE crypto_wallet;

\c crypto_wallet;

CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    address VARCHAR(255) NOT NULL UNIQUE,
    balance DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    crypto_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    source_address VARCHAR(255) NOT NULL,
    destination_address VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    fees DOUBLE PRECISION NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    fee_level VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    crypto_type VARCHAR(20) NOT NULL,
    wallet_id UUID REFERENCES wallets(id) ON DELETE CASCADE,
    confirmed_at TIMESTAMP NULL
);

-- Index pour améliorer les performances
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_creation_date ON transactions(creation_date);
CREATE INDEX idx_wallets_address ON wallets(address);