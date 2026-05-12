// Landon Lego
// 5/12/26
// used to control difficulty

package Menu;

import Game.Instance.GameInstance;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MainMenuController {
    @FXML private RadioButton btnEasy;
    @FXML private RadioButton btnHard;
    @FXML private RadioButton btnMed;
    @FXML private ToggleGroup difficultyGroup;

    // setup game instance
    GameInstance gameInstance = new GameInstance();

    // start game button
    @FXML void startGame(){
        Stage stage = (Stage) btnEasy.getScene().getWindow();

        // calculate difficulty
        int difficulty = 0;
        if (difficultyGroup.getSelectedToggle() == btnEasy) difficulty = 1;
        if (difficultyGroup.getSelectedToggle() == btnMed)  difficulty = 5;
        if (difficultyGroup.getSelectedToggle() == btnHard) difficulty = 10;

        // start game
        gameInstance.setStage(stage);
        gameInstance.setDifficulty(difficulty);
        gameInstance.start();

        // hide current window
        stage.hide();
    }

    // initialize
    @FXML void initialize(){
        // difficulty buttons
        btnEasy.setToggleGroup(difficultyGroup);
        btnMed.setToggleGroup(difficultyGroup);
        btnHard.setToggleGroup(difficultyGroup);

        btnEasy.setSelected(true);

        // key presses
        Platform.runLater(() -> {
            Stage stage = (Stage) btnEasy.getScene().getWindow();

            stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) exit();
                if (event.getCode() == KeyCode.ENTER) startGame();
            });
        });
    }

    // other buttons
    @FXML void exit(){System.exit(0);}
}
