// Landon Lego & Simon
// 5/12/26
// main entry point

package Menu;

import Game.Util.ResourceLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

public class MainMenuApplication extends Application {
    @Override public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        Image icon = ResourceLoader.loadIcon("/assets/icons/icon.png");
        stage.getIcons().add(icon);
        FXMLLoader fxmlLoader = ResourceLoader.loadFXML("/menus/mainMenu.fxml");
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setResizable(false);
        stage.setTitle("Glorp Shooter");
        stage.setScene(scene);
        stage.show();
    }

    public static void start(String[] args) {
        launch();
    }
}
