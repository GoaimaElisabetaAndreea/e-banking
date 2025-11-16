package ro.ppoo.banking.validation;

import ro.ppoo.banking.exception.InvalidClientDataException;
import ro.ppoo.banking.model.Client;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Clasă utilitară responsabilă cu validarea datelor de intrare.
 * Conține metode statice pentru verificarea formatului și integrității informațiilor
 * introduse de utilizatori (CNP, Email, Telefon).
 */
public class Validator {

    private Validator(){}

    public static void validateClient(Client client) {
        if (client == null) {
            throw new InvalidClientDataException("Empty client.");
        }

        if (isNullOrEmpty(client.getFirstname()) || isNullOrEmpty(client.getLastname())) {
            throw new InvalidClientDataException("Firstname and lastname can not be empty");
        }

        if (!validateEmail(client.getEmail())) {
            throw new InvalidClientDataException("Invalid email: " + client.getEmail());
        }

        if (!validatePhone(client.getPhone())) {
            throw new InvalidClientDataException("Invalid phone number: " + client.getPhone());
        }

        if (!validateCnp(client.getCNP())) {
            throw new InvalidClientDataException("Invalid CNP");
        }

        if (!client.isGdprAccepted()) {
            throw new InvalidClientDataException("Client needs to accept the gdpr");
        }
    }

    /**
     * Validează un Cod Numeric Personal (CNP) conform standardului românesc.
     * <p>
     * Verificarea include:
     * <ul>
     * <li>Lungimea exactă de 13 cifre.</li>
     * <li>Validarea componentei de sex și secol (prima cifră).</li>
     * <li>Validarea datei de naștere extrase din CNP.</li>
     * <li><b>Calculul cifrei de control (Checksum):</b> Se utilizează vectorul de control
     * Constant <code>279146358279</code> pentru a verifica ultima cifră a CNP-ului.</li>
     * </ul>
     * </p>
     *
     * @param cnp String-ul reprezentând CNP-ul de validat.
     * @return <code>true</code> dacă CNP-ul este valid, altfel <code>false</code>.
     */
    public static boolean validateCnp(String cnp) {
        if (cnp == null || !cnp.matches("\\d{13}")) return false;

        int[] d = cnp.chars().map(ch -> ch - '0').toArray();

        int S = d[0];
        if (S < 1 || S > 8) return false;

        int AA = d[1] * 10 + d[2];
        int LL = d[3] * 10 + d[4];
        int ZZ = d[5] * 10 + d[6];
        int JJ = d[7] * 10 + d[8];
        int NNN = d[9] * 100 + d[10] * 10 + d[11];

        boolean judetValid = (JJ >= 1 && JJ <= 46) || JJ == 51 || JJ == 52;
        if (!judetValid) return false;

        if (NNN < 1 || NNN > 999) return false;

        int year;
        switch (S) {
            case 1: case 2: year = 1900 + AA; break;
            case 3: case 4: year = 1800 + AA; break;
            case 5: case 6: year = 2000 + AA; break;
            case 7: case 8:
                year = 2000 + AA;
                break;
            default: return false;
        }

        try {
            LocalDate.of(year, LL, ZZ);
        } catch (DateTimeException ex) {
            return false;
        }

        int[] coef = {2,7,9,1,4,6,3,5,8,2,7,9};
        int sum = 0;
        for (int i = 0; i < 12; i++) sum += d[i] * coef[i];
        int r = sum % 11;
        int control = (r == 10) ? 1 : r;

        return control == d[12];
    }

    public static boolean validateEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean validatePhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}