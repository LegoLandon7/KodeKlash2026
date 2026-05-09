package Menu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuApplication extends Application {
    @Override public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        FXMLLoader fxmlLoader = new FXMLLoader(MainMenuApplication.class.getResource("/menus/mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Placeholder");
        stage.setScene(scene);
        stage.show();
    }

    public static void start(String[] args) {
        launch();
    }
}
