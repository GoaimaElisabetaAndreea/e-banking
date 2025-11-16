package ro.ppoo.banking.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.model.BankAccount;
import ro.ppoo.banking.model.Client;
import javafx.beans.property.SimpleStringProperty;
import java.util.Optional;

public class AccountManagementController {

    @FXML private Label clientNameLabel;
    @FXML private TableView<BankAccount> accountsTable;
    @FXML private TableColumn<BankAccount, String> ibanColumn;
    @FXML private TableColumn<BankAccount, String> currencyColumn;
    @FXML private TableColumn<BankAccount, String> typeColumn;
    @FXML private TableColumn<BankAccount, Double> balanceColumn;
    @FXML private Button blockButton;
    @FXML private TableColumn<BankAccount, String> statusColumn;

    private AppConfig appConfig;
    private Client currentClient;
    private final ObservableList<BankAccount> accountsList = FXCollections.observableArrayList();

    public void initData(AppConfig config, Client client) {
        this.appConfig = config;
        this.currentClient = client;

        clientNameLabel.setText("Client: " + client.getFirstname() + " " + client.getLastname());

        ibanColumn.setCellValueFactory(new PropertyValueFactory<>("iban"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        statusColumn.setCellValueFactory(cellData -> {
            boolean blocked = cellData.getValue().isBlocked();
            return new SimpleStringProperty(blocked ? "BLOCKED" : "Active");
        });
        refreshTable();
    }

    private void refreshTable() {
        accountsList.setAll(currentClient.getAccounts());
        accountsTable.setItems(accountsList);
        accountsTable.refresh();
    }

    @FXML
    void handleDeleteAccount(ActionEvent event) {
        BankAccount selectedAccount = accountsTable.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select an account to delete.");
            alert.showAndWait();
            return;
        }

        if (selectedAccount.getBalance() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Account has funds!");
            alert.setContentText("You cannot delete an account with a positive balance. Withdraw the money first.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete account " + selectedAccount.getIban() + "?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentClient.getAccounts().remove(selectedAccount);
            appConfig.getClientService().update(currentClient);
            refreshTable();
        }
    }

    @FXML
    void handleBlockUnblock(ActionEvent event) {
        BankAccount selectedAccount = accountsTable.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select an account.");
            alert.showAndWait();
            return;
        }

        boolean currentStatus = selectedAccount.isBlocked();
        String action = currentStatus ? "UNBLOCK" : "BLOCK";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Status Change");
        confirm.setHeaderText(action + " account " + selectedAccount.getIban() + "?");
        confirm.setContentText("Are you sure?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedAccount.setBlocked(!currentStatus);
            appConfig.getClientService().update(currentClient);

            refreshTable();

            String msg = "Account is now " + (selectedAccount.isBlocked() ? "BLOCKED" : "ACTIVE");
        }
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) accountsTable.getScene().getWindow();
        stage.close();
    }
}