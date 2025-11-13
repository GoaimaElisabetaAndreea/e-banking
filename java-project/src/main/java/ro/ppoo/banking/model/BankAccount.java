package ro.ppoo.banking.model;

import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    private String iban;
    private double balance;
    private String currency;
    private Client client;
    private List<Transaction> transactions;

    public BankAccount(String iban, double balance, String currency, Client client, List<Transaction> transactions) {
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
        this.client = client;
        this.transactions = transactions;
    }

    public BankAccount(BankAccount other){
        this.iban = other.iban;
        this.balance = other.balance;
        this.currency = other.currency;
        this.client = other.client;
        this.transactions = new ArrayList<Transaction>();

        for(Transaction transaction : other.transactions){
            this.transactions.add(new Transaction(transaction));
        }
    }
}
