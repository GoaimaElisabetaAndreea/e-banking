package ro.ppoo.banking.model;

import ro.ppoo.banking.enums.TransactionType;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private LocalDate date;
    private double amount;
    private TransactionType type;
    private BankAccount source;
    private BankAccount destination;

    public Transaction(int id, LocalDate date, double amount, TransactionType type, BankAccount source, BankAccount destination) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.source = source;
        this.destination = destination;
    }

    public Transaction(Transaction other){
        this.id = other.id;
        this.date = other.date;
        this.amount = other.amount;
        this.type = other.type;
        this.source = new BankAccount(other.source);
        this.destination = new BankAccount(other.destination);
    }
}
