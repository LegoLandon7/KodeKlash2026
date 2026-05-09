package Game.Instance;

import Game.Entities.Entity;
import Game.Entities.EntityWave;
import Game.Entities.Path;
import Game.Gameplay.Weapon;
import Game.Map.Maps;
import Game.User.Camera;
import Game.Raycasting.*;
import Game.Output.*;
import Game.User.Player;
import javafx.application.Platform;
import javafx.stage.Stage;

public class GameInstance {
    // game settings
    private final String windowTitle;
    private Stage menuStage;

    private final int windowWidth;
    private final int windowHeight;
    private final int resolution;
    private final float fov;

    private final int maxFps;

    private GameLoop gameLoop;

    private int difficulty;

    // default settings
    public GameInstance() {
        this.windowTitle =  "Placeholder";

        this.windowWidth = 1280;
        this.windowHeight = 720;
        this.resolution = 1;
        this.fov = (float) Math.toRadians(90);

        this.maxFps = 120;

        this.difficulty = 1;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setStage(Stage stage) {
        this.menuStage = stage;
    }

    public void start() {
        // initialize window
        Window window = new Window(windowTitle, windowWidth, windowHeight);
        window.clear(0x000000);

        // initialize camera
        Camera camera = new Camera(1.5f, 1.5f);

        // initialize entities
        Path path = new Path(Maps.map5);
        EntityWave entityWave = new EntityWave("entities/glorp3.png", path, camera, Maps.map5, 500, difficulty);
        entityWave.randomize(Maps.map5.length, Maps.map5[0].length);

        // initialize player
        Weapon weapon = new Weapon("entities/glorp3.png", entityWave, Maps.map5, camera, 100, 0.1f, 0.5f, 100.0f);
        Player player = new Player(window, camera, weapon, Maps.map5, this);

        // initialize raycaster & renderer
        Raycaster raycaster = new Raycaster(windowWidth, windowHeight, Maps.map5, fov, resolution);
        Renderer renderer = new Renderer(window, raycaster, windowWidth, windowHeight, resolution);
        renderer.setEntityWave(entityWave);

        // initialize game loop
        gameLoop = new GameLoop(window, camera, player, renderer, raycaster, path, entityWave, maxFps);
        Thread gameThread = new Thread(() -> gameLoop.start());
        gameThread.start();
    }

    public void stop() {
        gameLoop.stop();
        Platform.runLater(menuStage::show);
    }
}
