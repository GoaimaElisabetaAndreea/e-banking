package ro.ppoo.banking.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;

import java.io.IOException;

public class AdminDashboardController {

    private AppConfig appConfig;

    public void initData(AppConfig config) {
        this.appConfig = config;
    }

    @FXML
    void handleManageClients(ActionEvent event) {
        System.out.println("Navigating to Client Management page...");
        try {
            showScene(event, "/view/ClientManagementView.fxml", "Client Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddClient(ActionEvent event) {
        try {
            showScene(event, "/view/ClientRegistrationView.fxml", "Create New Client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewReports(ActionEvent event) {
        System.out.println("Navigating to Reports page...");
        // TODO: Aici vom crea și deschide ReportsView.fxml
        // try {
        //     showScene(event, "/view/ReportsView.fxml", "Reports and Statistics");
        // } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void handleLogout(ActionEvent event) {
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
        } else if (controller instanceof ClientManagementController) {
            ((ClientManagementController) controller).initData(appConfig);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}