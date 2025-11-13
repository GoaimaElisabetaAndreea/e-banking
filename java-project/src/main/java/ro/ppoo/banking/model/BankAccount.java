package ro.ppoo.banking.model;

import ro.ppoo.banking.enums.AccountType;
import ro.ppoo.banking.enums.Currency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankAccount implements Serializable {
    private final String iban;
    private double balance;
    private final Currency currency;
    private final AccountType type;
    private final List<Transaction> transactions;
    private boolean blocked = false;

    public BankAccount(String iban, double balance, Currency currency, AccountType type) {
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
        this.type = type;
        this.transactions = new ArrayList<>();
    }

    public BankAccount(BankAccount other) {
        this.iban = other.iban;
        this.balance = other.balance;
        this.currency = other.currency;
        this.type = other.type;
        this.blocked = other.blocked;
        this.transactions = new ArrayList<>();
        if (other.transactions != null) {
            for (Transaction t : other.transactions) {
                this.transactions.add(new Transaction(t));
            }
        }
    }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public String getIban() { return iban; }
    public double getBalance() { return balance; }
    public Currency getCurrency() { return currency; }
    public AccountType getType() { return type; }
    public List<Transaction> getTransactions() { return transactions; }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        String status = blocked ? "[BLOCKED]" : "";
        return type + " " + status + " (" + currency + ") - " + iban + ": " + String.format("%.2f", balance);
    }
}