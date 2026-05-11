package Menu;

import Game.Util.ResourceLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import javafx.scene.image.Image;

public class MainMenuApplication extends Application {
    @Override public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        Image icon = ResourceLoader.loadIcon("/global/icon.png");
        stage.getIcons().add(icon);
        FXMLLoader fxmlLoader = ResourceLoader.loadFXML("/menus/mainMenu.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Placeholder");
        stage.setScene(scene);
        stage.show();
    }

    public static void start(String[] args) {
        launch();
    }
}
