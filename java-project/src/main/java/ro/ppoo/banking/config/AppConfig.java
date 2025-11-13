package ro.ppoo.banking.config;

import io.github.cdimascio.dotenv.Dotenv;
import ro.ppoo.banking.repository.ClientRepository;
import ro.ppoo.banking.service.ClientService;
import ro.ppoo.banking.service.security.DataEncryptionService;

public class AppConfig {
    private final Dotenv dotenv;
    private final DataEncryptionService encryptionService;
    private final ClientRepository clientRepository;
    private final ClientService clientService;

    public AppConfig(){
        this.dotenv = Dotenv.load();
        String aesKey = dotenv.get("AES_SECRET_KEY");
        this.encryptionService = new DataEncryptionService(aesKey);

        this.clientRepository = new ClientRepository();
        this.clientRepository.loadFromFile();

        this.clientService = new ClientService(clientRepository, encryptionService);
    }

    public DataEncryptionService getEncryptionService() {
        return encryptionService;
    }

    public ClientService getClientService() {
        return clientService;
    }

    public String getEnv(String key) {
        return dotenv.get(key);
    }

    public void onExit(){
        clientRepository.saveToFile();
    }
}
