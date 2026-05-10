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
import Game.Util.HealthBar;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

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
    private int health;

    // default settings
    public GameInstance() {
        this.windowTitle =  "Placeholder";

        this.windowWidth = 1280;
        this.windowHeight = 720;
        this.resolution = 1;
        this.fov = (float) Math.toRadians(90);

        this.maxFps = 120;

        this.difficulty = 4;
        health = HealthBar.MAX_HEALTH;
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
        Path path = new Path(Maps.mainMap);

        Entity testEntity2 = new Entity(0, 0, "entities/glorp3.png", path, camera, this, 0.25f, 1f, 0.005f, 5);
        Entity testEntity = new Entity(0, 0, "entities/glorp3.png", path, camera, this,0.75f, 0.5f, 0.02f, 1);
        Entity tank = new Entity(0, 0, "entities/glorp3.png", path, camera, this,0.01f, 0.5f, 0.025f, 2);

        EntityWave entityWave = new EntityWave(Maps.mainMap, difficulty);

        entityWave.addEntity(testEntity2, 3);
        entityWave.addEntity(testEntity, 5);
        entityWave.addEntity(tank, 1);

        entityWave.randomize(Maps.mainMap.length, Maps.mainMap[0].length);

        // initialize player and weapons
        Weapon weapon = new Weapon("entities/glorp3.png", entityWave, Maps.mainMap, camera, 10, 5, 0.5f, 100.0f);

        Player player = new Player(window, camera, weapon, Maps.mainMap, this);

        // initialize raycaster & renderer
        Raycaster raycaster = new Raycaster(windowWidth, windowHeight, Maps.mainMap, fov, resolution);
        Renderer renderer = new Renderer(window, raycaster, this, windowWidth, windowHeight, resolution);
        renderer.setEntityWave(entityWave);

        // initialize game loop
        gameLoop = new GameLoop(window, camera, player, renderer, raycaster, entityWave, maxFps);
        Thread gameThread = new Thread(() -> gameLoop.start());
        gameThread.start();
    }

    public void stop() {
        gameLoop.stop();
        Platform.runLater(menuStage::show);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) stop();
    }

    public void renderHud(Window window) {
        int barWidth = windowWidth / 2;
        int barHeight = 16;
        int barX = windowWidth / 2 - 20;
        int barY = windowHeight - 60;

        HealthBar.render(health, barX, barY, barWidth, barHeight, window);
    }
}
