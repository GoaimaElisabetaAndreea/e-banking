package ro.ppoo.banking.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.model.Client;

import java.io.IOException;

public class ClientLoginController {

    @FXML
    private TextField cnpField;

    @FXML
    private Label errorLabel;

    private AppConfig appConfig;
    @FXML private PasswordField passwordField;
    public void initData(AppConfig config) {
        this.appConfig = config;
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String cnp = cnpField.getText();
        String pass = passwordField.getText();

        if (cnp.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please fill in both fields.");
            errorLabel.setVisible(true);
            return;
        }

        Client client = appConfig.getClientService().loginClient(cnp, pass);

        if (client != null) {
            errorLabel.setVisible(false);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientDashboardView.fxml"));
                Parent root = loader.load();

                ClientDashboardController controller = loader.getController();
                controller.initData(appConfig, client);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("My Banking Dashboard");
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid CNP or Password.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    void handleSignUp(ActionEvent event) {
        System.out.println("Navigating to Sign Up screen...");
        try {
            showScene(event, "/view/ClientRegistrationView.fxml", "Create Account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            showScene(event, "/view/RoleSelectionView.fxml", "PPOO Bank - Welcome!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof ClientRegistrationController) {
            ((ClientRegistrationController) controller).initData(appConfig);
        } else if (controller instanceof RoleSelectionController) {
            ((RoleSelectionController) controller).initData(appConfig);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}