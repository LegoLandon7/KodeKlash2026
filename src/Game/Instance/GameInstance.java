// Landon Lego
// 5/12/26
// this file starts the game with certain settings

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
import Game.Util.ResourceLoader;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    private int currentWeapon;
    private int maxWeapon;

    Player player;
    Entity[] entities;
    Weapon[] weapons;

    // default settings
    public GameInstance() {
        this.windowTitle =  "Glorp Shooter";

        this.windowWidth = 1280;
        this.windowHeight = 720;
        this.resolution = 1;
        this.fov = (float) Math.toRadians(90);

        this.maxFps = 120;
        this.difficulty = 1;

        currentWeapon = 0;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    public void setStage(Stage stage) {
        this.menuStage = stage;
    }

    public void start() {
        health = HealthBar.MAX_HEALTH;

        // initialize window
        Window window = new Window(windowTitle, windowWidth, windowHeight);
        window.clear(0x000000);

        // initialize camera
        Camera camera = new Camera(1.5f, 1.5f);

        // initialize entities
        Path path = new Path(Maps.mainMap);

        entities = ResourceLoader.loadEntities("/data/entities.txt", path, camera, this);

        EntityWave entityWave = new EntityWave(Maps.mainMap, difficulty);

        entityWave.reset(entities);
        entityWave.randomize(Maps.mainMap.length, Maps.mainMap[0].length);

        // initialize player and weapons
        weapons = ResourceLoader.loadWeapons("/data/weapons.txt", entityWave, Maps.mainMap, camera);

        player = new Player(window, camera, weapons[0], Maps.mainMap, this);
        maxWeapon = weapons.length;
        player.setWeapon(weapons[currentWeapon]);

        // initialize raycaster & renderer
        Raycaster raycaster = new Raycaster(windowWidth, windowHeight, Maps.mainMap, fov, resolution);
        Renderer renderer = new Renderer(window, raycaster, this, windowWidth, windowHeight, resolution);
        renderer.setEntityWave(entityWave);

        // initialize game loop
        gameLoop = new GameLoop(window, camera, player, renderer, raycaster, entityWave, entities, maxFps, this);
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

    public void changeHealth(int health) {
        this.health += health;
        if (health > HealthBar.MAX_HEALTH)
            this.health = HealthBar.MAX_HEALTH;
    }

    public void changeWeapon() {
        currentWeapon++;
        if (currentWeapon == maxWeapon) currentWeapon = 0;
        player.setWeapon(weapons[currentWeapon]);
    }

    public void renderHud(Window window) {
        int barWidth = windowWidth / 3 - 40;
        int barHeight = 16;
        int barX = 20;
        int barY = windowHeight - 24;

        HealthBar.render(health, barX, barY, barWidth, barHeight, window);
    }
}
