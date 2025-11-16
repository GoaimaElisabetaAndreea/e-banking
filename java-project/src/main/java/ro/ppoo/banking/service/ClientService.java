package ro.ppoo.banking.service;

import ro.ppoo.banking.model.Client;
import ro.ppoo.banking.repository.ClientRepository;
import ro.ppoo.banking.validation.Validator;
import ro.ppoo.banking.service.security.DataEncryptionService;
import java.util.List;


public class ClientService {
    private final ClientRepository clientRepository;
    private final DataEncryptionService encryptionService;

    public ClientService(ClientRepository clientRepository, DataEncryptionService encryptionService){
        this.clientRepository = clientRepository;
        this.encryptionService = encryptionService;
    }

    public void add(Client client){
        Validator.validateClient(client);

        String encryptedCNP = encryptionService.encrypt(client.getCNP());
        if (clientRepository.findByCNP(encryptedCNP) != null) {
            throw new IllegalArgumentException("This CNP is already registered");
        }

        String encryptedPass = encryptionService.encrypt(client.getPassword());
        client.setPassword(encryptedPass);

        client.setCNP(encryptedCNP);
        clientRepository.add(client);
    }

    public void update(Client client) {
        if (client == null || client.getFirstname().isEmpty() ||
                client.getLastname().isEmpty() || client.getEmail().isEmpty()) {
            throw new IllegalArgumentException("First name, last name, and email are required.");
        }

        clientRepository.update(client);
    }

    public List<Client> getAll() {
        return clientRepository.getAll();
    }
    public Client findClientByCNP(String encryptedCNP) {
        return clientRepository.findByCNP(encryptedCNP);
    }

    /**
     * Autentifică un client pe baza CNP-ului și a parolei.
     * CNP-ul introdus este criptat înainte de a fi comparat cu datele din repository.
     *
     * @param cnpPlaintext CNP-ul în format text simplu, introdus de utilizator.
     * @param passwordPlaintext Parola
     * @return Obiectul {@link Client} dacă datele sunt valide, sau null dacă autentificarea eșuează.
     * @see ro.ppoo.banking.service.security.DataEncryptionService#encrypt(String)
     */
    public Client loginClient(String cnpPlaintext, String passwordPlaintext) {
        String encryptedCNP;
        String encryptedPass;
        try {
            encryptedCNP = encryptionService.encrypt(cnpPlaintext);
            encryptedPass = encryptionService.encrypt(passwordPlaintext);
        } catch (Exception e) {
            return null;
        }

        Client client = clientRepository.findByCNP(encryptedCNP);

        if (client != null && client.getPassword().equals(encryptedPass)) {
            return client;
        }

        return null;
    }

    public void updatePassword(Client client, String newPlainPassword) {
        String encryptedPass = encryptionService.encrypt(newPlainPassword);
        client.setPassword(encryptedPass);
        clientRepository.update(client);
    }

    public void delete(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client is null.");
        }

        clientRepository.destroy(client);
    }
}
