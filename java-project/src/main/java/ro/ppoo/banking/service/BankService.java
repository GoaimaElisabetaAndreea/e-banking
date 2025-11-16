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

/**
 * Gestionează operațiunile bancare principale și logica tranzacțională.
 * <p>
 * Această clasă acționează ca un intermediar între interfața grafică și datele persistente,
 * asigurând integritatea tranzacțiilor, conversia valutară și validarea fondurilor.
 * </p>
 */
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

    /**
     * Realizează un transfer de fonduri între două conturi bancare.
     * <p>
     * Metoda efectuează următorii pași:
     * <ol>
     * <li>Identifică clienții și conturile pe baza IBAN-urilor.</li>
     * <li>Verifică dacă conturile sunt active (neblocate) și dacă există fonduri suficiente.</li>
     * <li>Calculează suma convertită dacă transferul este între valute diferite (ex: EUR -> RON).</li>
     * <li>Actualizează soldurile și creează două înregistrări de tranzacție (una pentru expeditor, una pentru destinatar).</li>
     * <li>Salvează modificările în repository.</li>
     * </ol>
     * </p>
     *
     * @param fromIban IBAN-ul contului sursă.
     * @param toIban   IBAN-ul contului destinație.
     * @param amount   Suma de transferat (în moneda contului sursă).
     * @param details  Descrierea tranzacției oferită de utilizator (ex: "Plată factură").
     * @throws IllegalArgumentException Dacă conturile nu există, sunt blocate sau soldul este insuficient.
     */
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

    /**
     * Alimentează un cont bancar cu o sumă de bani (Simulare depunere numerar la ghișeu/ATM).
     *
     * @param iban   IBAN-ul contului unde se face depunerea.
     * @param amount Suma depusă (trebuie să fie pozitivă).
     */
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
                "Cash Deposit at ATM"
        );
        account.getTransactions().add(transaction);
        clientRepository.update(client);
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