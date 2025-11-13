package ro.ppoo.banking.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.enums.TransactionType;
import ro.ppoo.banking.model.BankAccount;
import ro.ppoo.banking.model.Client;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientManagementController implements Initializable {

    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, String> firstNameColumn;
    @FXML
    private TableColumn<Client, String> lastNameColumn;
    @FXML
    private TableColumn<Client, String> emailColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;
    @FXML private TableColumn<Client, String> cnpColumn;

    private AppConfig appConfig;
    private final ObservableList<Client> clientList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        cnpColumn.setCellValueFactory(cellData -> {
            String encryptedCnp = cellData.getValue().getCNP();

            if (appConfig == null || encryptedCnp == null) {
                return new SimpleStringProperty("Loading...");
            }

            try {
                String plainCnp = appConfig.getEncryptionService().decrypt(encryptedCnp);

                if (plainCnp.length() > 4) {
                    String last4 = plainCnp.substring(plainCnp.length() - 4);
                    return new SimpleStringProperty("****" + last4);
                } else {
                    return new SimpleStringProperty(plainCnp);
                }

            } catch (Exception e) {
                return new SimpleStringProperty("Data Error");
            }
        });
    }

    @FXML
    void handleRevealCNP(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setContentText("Please select a client to reveal CNP.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Security Check");
        confirm.setHeaderText("Reveal Sensitive Data");
        confirm.setContentText("Are you sure you want to view the full CNP for "
                + selectedClient.getFirstname() + " " + selectedClient.getLastname() + "?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String fullCNP = selectedClient.getCNP();
            fullCNP = appConfig.getEncryptionService().decrypt(fullCNP);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Client CNP");
            info.setHeaderText("Full Personal ID (CNP)");
            info.setContentText(fullCNP);
            info.showAndWait();
        }
    }

    public void initData(AppConfig config) {
        this.appConfig = config;
        loadClientData();
    }

    private void loadClientData() {
        List<Client> clients = appConfig.getClientService().getAll();
        clientList.setAll(clients);
        clientsTable.setItems(clientList);
        clientsTable.refresh();
    }

    @FXML
    void handleEditClient(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientEditView.fxml"));
                Parent root = loader.load();
                ClientEditController controller = loader.getController();
                controller.initData(appConfig, selectedClient);
                Stage editStage = new Stage();
                editStage.setTitle("Edit Client");
                editStage.setScene(new Scene(root));
                editStage.initModality(Modality.APPLICATION_MODAL);
                editStage.initOwner(clientsTable.getScene().getWindow());

                editStage.showAndWait();
                loadClientData();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No client selected.");
            alert.setContentText("Please select a client from the table to edit.");
            alert.showAndWait();
        }
    }

    @FXML
    void handleCashOperations(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a client first.");
            alert.showAndWait();
            return;
        }

        if (selectedClient.getAccounts().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("This client has no accounts.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cash Operations - " + selectedClient.getFirstname());
        dialog.setHeaderText("Select operation type, account and amount:");

        ButtonType okButton = new ButtonType("Execute", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 50, 10, 10));

        ComboBox<TransactionType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(TransactionType.DEPOSIT, TransactionType.WITHDRAW);
        typeCombo.getSelectionModel().select(TransactionType.DEPOSIT);

        ComboBox<BankAccount> accountCombo = new ComboBox<>();
        accountCombo.getItems().setAll(selectedClient.getAccounts());

        accountCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(BankAccount acc) {
                return (acc != null) ? acc.getCurrency() + " - " + acc.getIban() + " (Bal: " + acc.getBalance() + ")" : "";
            }
            @Override
            public BankAccount fromString(String string) { return null; }
        });
        accountCombo.getSelectionModel().selectFirst();

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        grid.add(new Label("Operation:"), 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(new Label("Account:"), 0, 1);
        grid.add(accountCombo, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        java.util.Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == okButton) {
            try {
                TransactionType type = typeCombo.getValue();
                BankAccount account = accountCombo.getValue();
                double amount = Double.parseDouble(amountField.getText());

                if (type == TransactionType.DEPOSIT) {
                    appConfig.getBankService().deposit(account.getIban(), amount);
                } else {
                    appConfig.getBankService().withdraw(account.getIban(), amount);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Transaction successful!");
                alert.showAndWait();

                String currentCnp = selectedClient.getCNP();

                loadClientData();

                for (Client c : clientsTable.getItems()) {
                    if (c.getCNP().equals(currentCnp)) {
                        clientsTable.getSelectionModel().select(c);
                        break;
                    }
                }

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid amount.");
                alert.show();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    @FXML
    void handleDeleteClient(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this client?");
            alert.setContentText(selectedClient.getFirstname() + " " + selectedClient.getLastname());

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    appConfig.getClientService().delete(selectedClient);

                    clientList.remove(selectedClient);
                } catch (Exception e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Error");
                    errorAlert.setHeaderText("Could not delete client.");
                    errorAlert.setContentText(e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No client selected.");
            alert.setContentText("Please select a client from the table to delete.");
            alert.showAndWait();
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboardView.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.initData(appConfig);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewHistory(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            try {
                if (appConfig != null) {
                    Client freshClient = appConfig.getClientService().findClientByCNP(selectedClient.getCNP());
                    if (freshClient != null) {
                        selectedClient = freshClient;
                    }
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TransactionsView.fxml"));
                Parent root = loader.load();

                TransactionsController controller = loader.getController();
                controller.initData(selectedClient);

                Stage stage = new Stage();
                stage.setTitle("History for: " + selectedClient.getFirstname() + " " + selectedClient.getLastname());
                stage.setScene(new Scene(root, 800, 500));
                stage.initModality(Modality.APPLICATION_MODAL); // Fereastra modală
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No client selected.");
            alert.setContentText("Please select a client from the table to view their history.");
            alert.showAndWait();
        }
    }

    @FXML
    void handleManageAccounts(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            try {
                if (appConfig != null) {
                    Client freshClient = appConfig.getClientService().findClientByCNP(selectedClient.getCNP());
                    if (freshClient != null) selectedClient = freshClient;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AccountManagementView.fxml"));
                Parent root = loader.load();

                AccountManagementController controller = loader.getController();
                controller.initData(appConfig, selectedClient);

                Stage stage = new Stage();
                stage.setTitle("Manage Accounts - " + selectedClient.getFirstname());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No client selected.");
            alert.setContentText("Please select a client to manage accounts.");
            alert.showAndWait();
        }
    }

    @FXML
    void handleResetPassword(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a client.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password for " + selectedClient.getFirstname());
        dialog.setContentText("Enter new password:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Password cannot be empty.");
                error.show();
            } else {
                try {
                    Client freshClient = appConfig.getClientService().findClientByCNP(selectedClient.getCNP());
                    if(freshClient != null) {
                        appConfig.getClientService().updatePassword(freshClient, newPassword);

                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setContentText("Password updated successfully!");
                        success.showAndWait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}