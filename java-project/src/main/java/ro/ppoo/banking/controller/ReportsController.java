package ro.ppoo.banking.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.enums.AccountType;
import ro.ppoo.banking.enums.Currency;
import ro.ppoo.banking.model.BankAccount;
import ro.ppoo.banking.model.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsController {

    @FXML private Label totalClientsLabel;
    @FXML private Label totalAccountsLabel;
    @FXML private Label totalTransactionsLabel;
    @FXML private PieChart currencyPieChart;
    @FXML private BarChart<String, Number> typeBarChart;

    public void initData(AppConfig config) {
        List<Client> allClients = config.getClientService().getAll();

        calculateKPIs(allClients);
        generateCharts(allClients);
    }

    private void calculateKPIs(List<Client> clients) {
        int clientCount = clients.size();
        int accountCount = 0;
        int transactionCount = 0;

        for (Client client : clients) {
            accountCount += client.getAccounts().size();
            for (BankAccount account : client.getAccounts()) {
                if (account.getTransactions() != null) {
                    transactionCount += account.getTransactions().size();
                }
            }
        }

        totalClientsLabel.setText(String.valueOf(clientCount));
        totalAccountsLabel.setText(String.valueOf(accountCount));
        totalTransactionsLabel.setText(String.valueOf(transactionCount));
    }

    private void generateCharts(List<Client> clients) {
        Map<Currency, Integer> currencyCounts = new HashMap<>();
        for (Currency c : Currency.values()) currencyCounts.put(c, 0);

        Map<AccountType, Integer> typeCounts = new HashMap<>();
        for (AccountType t : AccountType.values()) typeCounts.put(t, 0);

        for (Client client : clients) {
            for (BankAccount account : client.getAccounts()) {
                currencyCounts.put(account.getCurrency(), currencyCounts.getOrDefault(account.getCurrency(), 0) + 1);
                typeCounts.put(account.getType(), typeCounts.getOrDefault(account.getType(), 0) + 1);
            }
        }

        for (Map.Entry<Currency, Integer> entry : currencyCounts.entrySet()) {
            if (entry.getValue() > 0) {
                PieChart.Data slice = new PieChart.Data(entry.getKey().name(), entry.getValue());
                currencyPieChart.getData().add(slice);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Number of Accounts");

        for (Map.Entry<AccountType, Integer> entry : typeCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().name(), entry.getValue()));
        }

        typeBarChart.getData().add(series);
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) totalClientsLabel.getScene().getWindow();
        stage.close();
    }
}