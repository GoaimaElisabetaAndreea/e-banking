package ro.ppoo.banking.service.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Serviciu responsabil pentru securitatea datelor sensibile.
 * Utilizează algoritmul AES pentru criptarea și decriptarea informațiilor (ex: CNP).
 */
public class DataEncryptionService {

    private static final String ALGORITHM = "AES";
    private final String secretKey;

    public DataEncryptionService(String secretKey) {
        if (secretKey == null || secretKey.length() != 16) {
            throw new IllegalArgumentException("Invalid AES key: must be 16 characters");
        }
        this.secretKey = secretKey;
    }

    /**
     * Criptează un șir de caractere folosind cheia secretă configurată.
     *
     * @param data Șirul de caractere ce urmează a fi criptat.
     * @return Șirul criptat și codificat în Base64.
     * @throws RuntimeException Dacă apare o eroare în timpul procesului de criptare.
     */
    public String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption error: " + e.getMessage(), e);
        }
    }

    /**
     * Decriptează un text criptat pentru a obține informația originală.
     * Această metodă este utilizată, de exemplu, la verificarea parolei la login
     * sau la afișarea CNP-ului decriptat în panoul de Admin.
     *
     * @param encryptedData Textul criptat (format Base64).
     * @return Textul original (în clar).
     * @throws RuntimeException Dacă datele sunt corupte sau cheia nu se potrivește.
     */
    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption error: " + e.getMessage(), e);
        }
    }
}
