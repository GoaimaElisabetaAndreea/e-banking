package ro.ppoo.banking.service;

import ro.ppoo.banking.enums.Currency;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyService {

    private final Map<String, Double> rates = new HashMap<>();
    private final String FILE_PATH = "data/exchange_rates.txt";

    public CurrencyService() {
        loadRates();
    }

    private void loadRates() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String key = parts[0].trim() + "->" + parts[1].trim();
                    double rate = Double.parseDouble(parts[2].trim());
                    rates.put(key, rate);
                }
            }
            System.out.println("Loaded " + rates.size() + " exchange rates.");
        } catch (IOException e) {
            System.err.println("Could not load exchange rates: " + e.getMessage());
        }
    }

    public double getExchangeRate(Currency from, Currency to) {
        if (from == to) return 1.0;

        String keyDirect = from.name() + "->" + to.name();
        String keyReverse = to.name() + "->" + from.name();

        if (rates.containsKey(keyDirect)) {
            return rates.get(keyDirect);
        }

        if (rates.containsKey(keyReverse)) {
            return 1.0 / rates.get(keyReverse);
        }

        throw new IllegalArgumentException("No exchange rate found for " + from + " to " + to);
    }

    public double convert(double amount, Currency from, Currency to) {
        double rate = getExchangeRate(from, to);
        return amount * rate;
    }
}