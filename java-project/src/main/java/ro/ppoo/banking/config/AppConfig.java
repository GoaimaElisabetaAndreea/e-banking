package ro.ppoo.banking.config;

import io.github.cdimascio.dotenv.Dotenv;
import ro.ppoo.banking.repository.ClientRepository;
import ro.ppoo.banking.service.BankService;
import ro.ppoo.banking.service.ClientService;
import ro.ppoo.banking.service.CurrencyService;
import ro.ppoo.banking.service.security.DataEncryptionService;

/**
 * Clasa centrală de configurare a aplicației (Dependency Injection Container simplificat).
 * <p>
 * Responsabilități:
 * <ul>
 * <li>Încarcă variabilele de mediu din fișierul <code>.env</code>.</li>
 * <li>Inițializează serviciile singleton (Encryption, Repository, BankService).</li>
 * <li>Gestionează ciclul de viață al aplicației (ex: salvarea datelor la închidere).</li>
 * </ul>
 * </p>
 */
public class AppConfig {
    private final Dotenv dotenv;
    private final DataEncryptionService encryptionService;
    private final ClientRepository clientRepository;
    private final ClientService clientService;
    private final BankService bankService;
    private final CurrencyService currencyService;

    public AppConfig(){
        this.dotenv = Dotenv.load();
        String aesKey = dotenv.get("AES_SECRET_KEY");
        this.encryptionService = new DataEncryptionService(aesKey);

        this.clientRepository = new ClientRepository();
        this.clientRepository.loadFromFile();
        this.currencyService = new CurrencyService();
        this.clientService = new ClientService(clientRepository, encryptionService);
        this.bankService = new BankService(clientRepository, currencyService);
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

    /**
     * Metodă apelată la închiderea aplicației pentru a asigura persistența datelor.
     * Salvează starea curentă a tuturor clienților în fișierul binar.
     */
    public void onExit(){
        clientRepository.saveToFile();
    }

    public BankService getBankService() {
        return this.bankService;
    }
}
