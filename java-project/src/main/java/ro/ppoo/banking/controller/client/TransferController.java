package ro.ppoo.banking.controller.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.model.BankAccount;
import ro.ppoo.banking.model.Client;

public class TransferController {

    @FXML private ComboBox<BankAccount> fromAccountCombo;
    @FXML private TextField destinationIbanField;
    @FXML private TextField amountField;
    @FXML private Label errorLabel;
    @FXML private TextField detailsField;
    private AppConfig appConfig;

    public void initData(AppConfig config, Client client) {
        this.appConfig = config;

        fromAccountCombo.getItems().setAll(client.getAccounts());
        fromAccountCombo.setConverter(new StringConverter<BankAccount>() {
            @Override
            public String toString(BankAccount account) {
                if (account == null) return "";
                return account.getCurrency() + " - " + account.getIban() + " (Balance: " + account.getBalance() + ")";
            }

            @Override
            public BankAccount fromString(String string) {
                return null;
            }
        });

        if (!client.getAccounts().isEmpty()) {
            fromAccountCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    void handleTransfer(ActionEvent event) {
        try {
            BankAccount source = fromAccountCombo.getValue();
            String destination = destinationIbanField.getText().trim();
            String amountText = amountField.getText().trim();

            String userDetails = detailsField.getText().trim();
            if (userDetails.isEmpty()) {
                userDetails = "Transfer";
            }

            if (source == null) throw new IllegalArgumentException("Please select a source account.");
            if (destination.isEmpty()) throw new IllegalArgumentException("Please enter a destination IBAN.");
            if (amountText.isEmpty()) throw new IllegalArgumentException("Please enter an amount.");

            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount format.");
            }

            appConfig.getBankService().transferMoney(source.getIban(), destination, amount, userDetails);
            closeWindow();

        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.close();
    }
}