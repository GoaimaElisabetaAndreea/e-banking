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


    /**
     * Încearcă să autentifice un client pe baza CNP-ului și a numărului de telefon.
     * @param cnpPlaintext CNP-ul în clar, introdus de utilizator.
     * @param phone Telefonul introdus de utilizator.
     * @return Obiectul Client dacă datele se potrivesc, altfel null.
     */
    public Client loginClient(String cnpPlaintext, String phone) {
        String encryptedCNP;
        try {
            encryptedCNP = encryptionService.encrypt(cnpPlaintext);
        } catch (Exception e) {
            return null;
        }

        Client client = clientRepository.findByCNP(encryptedCNP);

        if (client != null && client.getPhone().equals(phone)) {
            return client;
        }

        return null;
    }

    public void delete(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Clientul nu poate fi nul.");
        }

        clientRepository.destroy(client);
    }
}
