package ro.ppoo.banking.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.model.Client;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.stage.Modality;
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

    private AppConfig appConfig;
    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    public void initData(AppConfig config) {
        this.appConfig = config;
        loadClientData();
    }

    private void loadClientData() {
        List<Client> clients = appConfig.getClientService().getAll();
        clientList.setAll(clients);
        clientsTable.setItems(clientList);
    }

    @FXML
    void handleManageAccounts(ActionEvent event) {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            System.out.println("Managing accounts for: " + selectedClient.getFirstname());
            // TODO: Deschide o nouă fereastră "AccountManagementView.fxml"
            // și trimite-i 'selectedClient'
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No client selected.");
            alert.setContentText("Please select a client.");
            alert.showAndWait();
        }
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
}