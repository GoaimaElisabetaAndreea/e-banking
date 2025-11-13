package ro.ppoo.banking.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;

import java.io.IOException;

public class RoleSelectionController {

    private AppConfig appConfig;

    public void initData(AppConfig config) {
        this.appConfig = config;
    }

    @FXML
    void handleAdminLogin(ActionEvent event) {
        try {
            showScene(event, "/view/AdminLoginView.fxml", "Admin Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleClientLogin(ActionEvent event) {
        try {
            showScene(event, "/view/ClientLoginView.fxml", "Client Portal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSaveAndExit(ActionEvent event) {
        if (appConfig != null) {
            appConfig.onExit();
        }
        Platform.exit();
    }

    private void showScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof AdminLoginController) {
            ((AdminLoginController) controller).initData(appConfig);
        } else if (controller instanceof ClientLoginController) {
            ((ClientLoginController) controller).initData(appConfig);
        } else if (controller instanceof ClientRegistrationController) {
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