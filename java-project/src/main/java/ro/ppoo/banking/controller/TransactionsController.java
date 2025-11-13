package ro.ppoo.banking.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.ppoo.banking.enums.TransactionType;
import ro.ppoo.banking.model.BankAccount;
import ro.ppoo.banking.model.Client;
import ro.ppoo.banking.model.Transaction;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.util.Comparator;

public class TransactionsController {

    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML private TableColumn<Transaction, String> detailsColumn; // Nou
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> senderNameColumn;
    @FXML private TableColumn<Transaction, String> receiverNameColumn;
    @FXML private TableColumn<Transaction, String> sourceColumn;
    @FXML private TableColumn<Transaction, String> destinationColumn;

    public void initData(Client client) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        senderNameColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        receiverNameColumn.setCellValueFactory(new PropertyValueFactory<>("receiverName"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("sourceIban"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destinationIban"));

        amountColumn.setCellFactory(column -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);

                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaction transaction = getTableView().getItems().get(getIndex());

                    String text = String.format("%.2f", amount);

                    if (transaction.getType() == TransactionType.DEPOSIT ||
                            transaction.getType() == TransactionType.TRANSFER_RECEIVED) {

                        setText("+" + text);
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: bold;");

                    } else {
                        setText("-" + text);
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });

        loadTransactions(client);
    }

    private void loadTransactions(Client client) {
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();

        for (BankAccount account : client.getAccounts()) {
            if (account.getTransactions() != null) {
                allTransactions.addAll(account.getTransactions());
            }
        }

        allTransactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        transactionsTable.setItems(allTransactions);
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) transactionsTable.getScene().getWindow();
        stage.close();
    }
}