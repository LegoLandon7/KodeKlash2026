package Game.User;

import Game.Gameplay.Weapon;
import Game.Instance.GameInstance;
import Game.Instance.GameLoop;
import Game.Output.Window;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;
import java.util.Set;

public class Player {

    private final Window window;
    private final Camera camera;
    private Weapon weapon;
    private final int[][] map;
    private final GameInstance gameInstance;

    private boolean weaponChanged;

    public static final float playerSize = 0.1f;

    public Player(Window window, Camera camera, Weapon weapon, int[][] map, GameInstance gameInstance) {
        this.window = window;
        this.camera = camera;
        this.weapon = weapon;
        this.map = map;
        this.gameInstance = gameInstance;

        weaponChanged = false;
    }

    public void doPlayerInput(double delta) {
        double move = 0;
        double strafe = 0;

        Set<Integer> keys = window.getPressedKeys();

        // movement
        if (keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_UP))
            move += Camera.stepSize * delta;

        if (keys.contains(KeyEvent.VK_S) || keys.contains(KeyEvent.VK_DOWN))
            move -= Camera.stepSize * delta;

        if (keys.contains(KeyEvent.VK_A))
            strafe -= Camera.stepSize * delta;

        if (keys.contains(KeyEvent.VK_D))
            strafe += Camera.stepSize * delta;

        if (keys.contains(KeyEvent.VK_LEFT))
            camera.rotateCamera(-Camera.rotSize * delta);

        if (keys.contains(KeyEvent.VK_RIGHT))
            camera.rotateCamera(Camera.rotSize * delta);

        // other
        if (keys.contains(KeyEvent.VK_SPACE))
            weapon.fire();

        if (keys.contains(KeyEvent.VK_ESCAPE)) {
            gameInstance.stop();
        }

        if (keys.contains(KeyEvent.VK_Z)) {
            if (!weaponChanged) gameInstance.changeWeapon();
            weaponChanged = true;
        } else {
            if (weaponChanged) weaponChanged = false;
        }

        // initial starting position
        float oldX = camera.getCamX();
        float oldY = camera.getCamY();

        float dirX = camera.getDirX();
        float dirY = camera.getDirY();

        float strafeX = -dirY;
        float strafeY = dirX;

        // get new position after movement
        float newX = oldX + (float)(dirX * move + strafeX * strafe);
        float newY = oldY + (float)(dirY * move + strafeY * strafe);

        // axis separation for sliding
        camera.setPos(newX, oldY);
        if (isInsideTile()) newX = oldX;

        camera.setPos(oldX, newY);
        if (isInsideTile()) newY = oldY;

        camera.setPos(newX, newY);
    }

    private boolean isInsideTile() {
        // each corner
        float[] o = {-playerSize, playerSize};

        // check all 4 corners
        for (float dx : o) {
            for (float dy : o) {
                // get map position
                int tx = (int) Math.floor(camera.getCamX() + dx);
                int ty = (int) Math.floor(camera.getCamY() + dy);

                // check if there is a tile there or out of bounds
                if (tx < 0 || ty < 0 || tx >= map.length || ty >= map[0].length || map[tx][ty] > 0)
                    return true;
            }
        }
        return false;
    }

    // setters
    public void setWeapon(Weapon weapon) {this.weapon = weapon;}

    // getters
    public Weapon getWeapon() {return weapon;}
}