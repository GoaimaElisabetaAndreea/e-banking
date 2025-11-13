package ro.ppoo.banking.model;

import ro.ppoo.banking.enums.TransactionType;
import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    private final int id;
    private final LocalDate date;
    private final double amount;
    private final TransactionType type;
    private BankAccount source;
    private BankAccount destination;
    private final String senderName;
    private final String receiverName;
    private final String details;

    public Transaction(int id, LocalDate date, double amount, TransactionType type,
                       BankAccount source, BankAccount destination,
                       String senderName, String receiverName, String details) { // Parametru nou
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.details = details;
    }

    public Transaction(Transaction other) {
        this.id = other.id;
        this.date = other.date;
        this.amount = other.amount;
        this.type = other.type;
        this.senderName = other.senderName;
        this.receiverName = other.receiverName;
        this.details = other.details;

        if (other.source != null) {
            this.source = new BankAccount(other.source.getIban(), other.source.getBalance(), other.source.getCurrency(), other.source.getType());
        }
        if (other.destination != null) {
            this.destination = new BankAccount(other.destination.getIban(), other.destination.getBalance(), other.destination.getCurrency(), other.destination.getType());
        }
    }


    public String getDetails() { return details; }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getSenderName() { return senderName; }
    public String getReceiverName() { return receiverName; }

    public String getSourceIban() { return (source != null) ? source.getIban() : "System/Cash"; }
    public String getDestinationIban() { return (destination != null) ? destination.getIban() : "System/Cash"; }
}