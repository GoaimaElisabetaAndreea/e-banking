package ro.ppoo.banking.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.controller.RoleSelectionController;

import java.io.IOException;

public class AdminLoginController {

    private String adminPassword;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private AppConfig appConfig;

    public void initData(AppConfig config) {
        this.appConfig = config;
        this.adminPassword = appConfig.getEnv("ADMIN_PASSWORD");
        if (adminPassword == null || adminPassword.isEmpty()) {
            throw new IllegalStateException("ADMIN_PASSWORD is not configured in environment variables!");
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String password = passwordField.getText();

        if (password.equals(adminPassword)) {
            errorLabel.setVisible(false);

            try {
                showScene(event, "/view/AdminDashboardView.fxml", "Admin Dashboard");
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Failed to load dashboard.");
                errorLabel.setVisible(true);
            }

        } else {
            errorLabel.setVisible(true);
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
        if (controller instanceof AdminDashboardController) {
            ((AdminDashboardController) controller).initData(appConfig);
        } else if (controller instanceof RoleSelectionController) {
            ((RoleSelectionController) controller).initData(appConfig);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}