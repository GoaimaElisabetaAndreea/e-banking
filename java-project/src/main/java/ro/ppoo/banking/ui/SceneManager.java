package ro.ppoo.banking.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void switchScene(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }
}
