package ro.ppoo.banking.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.model.Client;

public class ClientEditController {

    @FXML
    private TextField firstnameField;
    @FXML
    private TextField lastnameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField cnpField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveButton;

    private AppConfig appConfig;
    private Client clientToEdit;

    public void initData(AppConfig config, Client client) {
        this.appConfig = config;
        this.clientToEdit = client;

        firstnameField.setText(client.getFirstname());
        lastnameField.setText(client.getLastname());
        emailField.setText(client.getEmail());
        phoneField.setText(client.getPhone());
        cnpField.setText("--- PROTECTED ---");
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
            clientToEdit.setFirstname(firstnameField.getText());
            clientToEdit.setLastname(lastnameField.getText());
            clientToEdit.setEmail(emailField.getText());
            clientToEdit.setPhone(phoneField.getText());

            appConfig.getClientService().update(clientToEdit);

            closeWindow();

        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText("An unexpected error occurred.");
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}