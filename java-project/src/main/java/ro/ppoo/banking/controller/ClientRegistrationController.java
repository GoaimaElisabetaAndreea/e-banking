package ro.ppoo.banking.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.model.Client;

import java.io.IOException;

public class ClientRegistrationController {

    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField cnpField;
    @FXML private CheckBox gdprCheckbox;
    @FXML private Label errorLabel;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    private AppConfig appConfig;

    public void initData(AppConfig config) {
        this.appConfig = config;
    }

    @FXML
    void handleRegister(ActionEvent event) {
        try {
            if (!gdprCheckbox.isSelected()) {
                throw new IllegalArgumentException("You must accept the GDPR terms.");
            }

            if (firstnameField.getText().isEmpty() || lastnameField.getText().isEmpty() ||
                    emailField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                    cnpField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                throw new IllegalArgumentException("Please fill in all fields.");
            }

            String pass = passwordField.getText();
            String confirmPass = confirmPasswordField.getText();

            if (!pass.equals(confirmPass)) {
                throw new IllegalArgumentException("Passwords do not match!");
            }

            if (pass.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long.");
            }

            Client newClient = new Client(
                    firstnameField.getText(),
                    lastnameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    cnpField.getText(),
                    gdprCheckbox.isSelected(),
                    pass
            );

            appConfig.getClientService().add(newClient);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Registration Successful");
            success.setHeaderText(null);
            success.setContentText("Account created! You can now log in.");
            success.showAndWait();

            handleBack(event);

        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientLoginView.fxml"));
            Parent root = loader.load();

            ClientLoginController controller = loader.getController();
            controller.initData(appConfig);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Client Portal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}