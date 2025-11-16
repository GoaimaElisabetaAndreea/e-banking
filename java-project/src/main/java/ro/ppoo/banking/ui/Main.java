package ro.ppoo.banking.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.ppoo.banking.config.AppConfig;
import ro.ppoo.banking.controller.RoleSelectionController;

import java.io.IOException;

public class Main extends Application {

    private AppConfig appConfig;

    @Override
    public void init() {
        appConfig = new AppConfig();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RoleSelectionView.fxml"));
        Parent root = loader.load();

        RoleSelectionController controller = loader.getController();
        controller.initData(appConfig);

        primaryStage.setTitle(" Welcome!");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            appConfig.onExit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}