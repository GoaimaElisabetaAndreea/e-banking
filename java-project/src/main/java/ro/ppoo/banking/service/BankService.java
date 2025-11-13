package ro.ppoo.banking.service;

import ro.ppoo.banking.enums.AccountType;
import ro.ppoo.banking.enums.Currency;
import ro.ppoo.banking.enums.TransactionType;
import ro.ppoo.banking.model.BankAccount;
import ro.ppoo.banking.model.Client;
import ro.ppoo.banking.model.Transaction;
import ro.ppoo.banking.repository.ClientRepository;

import java.time.LocalDate;
import java.util.Random;

public class BankService {

    private final ClientRepository clientRepository;
    private final CurrencyService currencyService;

    public BankService(ClientRepository clientRepository, CurrencyService currencyService) {
        this.clientRepository = clientRepository;
        this.currencyService = currencyService;
    }
    public void createAccount(Client client, Currency currency, AccountType type) {
        String newIban = generateRandomIBAN();
        double initialBalance = 0.0;

        BankAccount newAccount = new BankAccount(newIban, initialBalance, currency, type);
        client.getAccounts().add(newAccount);
        clientRepository.update(client);
    }

    public void transferMoney(String fromIban, String toIban, double amount, String details) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");

        Client sourceClient = findClientByAccountIban(fromIban);
        Client destClient = findClientByAccountIban(toIban);

        if (sourceClient == null || destClient == null) {
            throw new IllegalArgumentException("One of the accounts could not be found.");
        }

        boolean isSameClient = sourceClient.getCNP().equals(destClient.getCNP());

        BankAccount sourceAccount = getAccountFromClient(sourceClient, fromIban);
        BankAccount destAccount;

        if (isSameClient) {
            destAccount = getAccountFromClient(sourceClient, toIban);
        } else {
            destAccount = getAccountFromClient(destClient, toIban);
        }

        if (sourceAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        double convertedAmount = currencyService.convert(
                amount,
                sourceAccount.getCurrency(),
                destAccount.getCurrency()
        );

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destAccount.setBalance(destAccount.getBalance() + convertedAmount);

        String sourceName = sourceClient.getFirstname() + " " + sourceClient.getLastname();
        String destName = destClient.getFirstname() + " " + destClient.getLastname();

        Transaction tOut = new Transaction(
                new java.util.Random().nextInt(1000000),
                LocalDate.now(),
                amount,
                TransactionType.TRANSFER_SENT,
                sourceAccount, destAccount,
                sourceName, destName,
                details
        );
        sourceAccount.getTransactions().add(tOut);

        Transaction tIn = new Transaction(
                new java.util.Random().nextInt(1000000),
                LocalDate.now(),
                convertedAmount,
                TransactionType.TRANSFER_RECEIVED,
                sourceAccount, destAccount,
                sourceName, destName,
                details
        );
        destAccount.getTransactions().add(tIn);

        clientRepository.update(sourceClient);

        if (!isSameClient) {
            clientRepository.update(destClient);
        }
    }

    private Client findClientByAccountIban(String iban) {
        for (Client c : clientRepository.getAll()) {
            for (BankAccount acc : c.getAccounts()) {
                if (acc.getIban().equals(iban)) return c;
            }
        }
        return null;
    }

    private BankAccount getAccountFromClient(Client client, String iban) {
        for (BankAccount acc : client.getAccounts()) {
            if (acc.getIban().equals(iban)) return acc;
        }
        return null;
    }

    private String generateRandomIBAN() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder("RO");
        sb.append(String.format("%02d", rand.nextInt(100)));
        sb.append("PPOO");
        for (int i = 0; i < 14; i++) sb.append(rand.nextInt(10));
        return sb.toString();
    }

    public void deposit(String iban, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");

        Client client = findClientByAccountIban(iban);
        if (client == null) throw new IllegalArgumentException("Account not found.");

        BankAccount account = getAccountFromClient(client, iban);
        account.setBalance(account.getBalance() + amount);

        String clientName = client.getFirstname() + " " + client.getLastname();

        Transaction transaction = new Transaction(
                new java.util.Random().nextInt(1000000),
                LocalDate.now(),
                amount,
                TransactionType.DEPOSIT,
                null, account,
                "ATM Deposit", clientName,
                "Cash Deposit at ATM" // DETAILS
        );

        account.getTransactions().add(transaction);

        clientRepository.update(client);
        System.out.println("Deposit successful: " + amount + " to " + iban);
    }

    public void withdraw(String iban, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        Client client = findClientByAccountIban(iban);
        if (client == null) throw new IllegalArgumentException("Account not found.");

        BankAccount account = getAccountFromClient(client, iban);

        if (account.isBlocked()) {
            throw new IllegalArgumentException("Account is BLOCKED. Cannot withdraw funds.");
        }

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        account.setBalance(account.getBalance() - amount);

        String clientName = client.getFirstname() + " " + client.getLastname();

        Transaction transaction = new Transaction(
                new java.util.Random().nextInt(1000000),
                LocalDate.now(),
                amount,
                TransactionType.WITHDRAW,
                account, null,
                clientName, "ATM Withdraw",
                "Cash Withdrawal from ATM"
        );

        account.getTransactions().add(transaction);
        clientRepository.update(client);
    }
}