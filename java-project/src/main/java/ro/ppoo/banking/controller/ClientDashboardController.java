package ro.ppoo.banking.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.enums.AccountType;
import ro.ppoo.banking.enums.Currency;
import ro.ppoo.banking.model.BankAccount;
import ro.ppoo.banking.model.Client;

import java.io.IOException;
import java.util.Optional;

public class ClientDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<BankAccount> accountsTable;
    @FXML private TableColumn<BankAccount, String> ibanColumn;
    @FXML private TableColumn<BankAccount, Currency> currencyColumn;
    @FXML private TableColumn<BankAccount, String> typeColumn;
    @FXML private TableColumn<BankAccount, Double> balanceColumn;

    private AppConfig appConfig;
    private Client currentClient;
    private final ObservableList<BankAccount> accountsList = FXCollections.observableArrayList();

    public void initData(AppConfig config, Client client) {
        this.appConfig = config;
        this.currentClient = client;

        welcomeLabel.setText(client.getFirstname() + " " + client.getLastname());

        ibanColumn.setCellValueFactory(new PropertyValueFactory<>("iban"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));

        refreshTable();
    }

    private void refreshTable() {
        accountsList.setAll(currentClient.getAccounts());
        accountsTable.setItems(accountsList);
        accountsTable.refresh();
    }

    @FXML
    void handleOpenAccount(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Open New Account");
        dialog.setHeaderText("Select currency and account type:");

        ButtonType createButtonType = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<Currency> currencyBox = new ComboBox<>();
        currencyBox.getItems().setAll(Currency.values());
        currencyBox.getSelectionModel().selectFirst();

        ComboBox<AccountType> typeBox = new ComboBox<>();
        typeBox.getItems().setAll(AccountType.values());
        typeBox.getSelectionModel().selectFirst();

        grid.add(new Label("Currency:"), 0, 0);
        grid.add(currencyBox, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == createButtonType) {
            Currency selectedCurrency = currencyBox.getValue();
            AccountType selectedType = typeBox.getValue();

            appConfig.getBankService().createAccount(currentClient, selectedCurrency, selectedType);
            reloadClientData();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Account created successfully!");
            alert.show();
        }
    }

    @FXML
    void handleTransfer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TransferView.fxml"));
            Parent root = loader.load();

            TransferController controller = loader.getController();
            controller.initData(appConfig, currentClient);

            Stage stage = new Stage();
            stage.setTitle("Transfer Money");
            stage.setScene(new Scene(root));

            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            reloadClientData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewHistory(ActionEvent event) {
        reloadClientData();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TransactionsView.fxml"));
            Parent root = loader.load();

            TransactionsController controller = loader.getController();
            controller.initData(currentClient);

            Stage stage = new Stage();
            stage.setTitle("My Transaction History");
            stage.setScene(new Scene(root, 800, 500));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadClientData() {
        String currentCnp = currentClient.getCNP();

        Client refreshedClient = appConfig.getClientService().findClientByCNP(currentCnp);

        if (refreshedClient != null) {
            this.currentClient = refreshedClient;
            refreshTable();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RoleSelectionView.fxml"));
            Parent root = loader.load();
            RoleSelectionController controller = loader.getController();
            controller.initData(appConfig);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("PPOO Bank - Welcome");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}