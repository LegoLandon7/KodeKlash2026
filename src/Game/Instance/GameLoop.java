package Game.Instance;

import Game.Entities.EntityWave;
import Game.Entities.Path;
import Game.Gameplay.Weapon;
import Game.Map.Maps;
import Game.Output.ScreenImage;
import Game.Raycasting.RayData;
import Game.User.Camera;
import Game.User.Player;
import Game.Raycasting.Raycaster;
import Game.Raycasting.Renderer;
import Game.Output.Window;

import java.awt.image.BufferedImage;

public class GameLoop {

    private final Window window;
    private final Camera camera;
    private final Player player;
    private final Renderer renderer;
    private final Raycaster raycaster;

    private final int maxFps;

    private final EntityWave entityWave;

    private boolean running = false;

    public static int weaponScale = 3;

    public GameLoop(Window window, Camera camera, Player player,
                    Renderer renderer, Raycaster raycaster, EntityWave entityWave, int maxFps) {

        this.window = window;
        this.camera = camera;
        this.player = player;
        this.renderer = renderer;
        this.raycaster = raycaster;
        this.entityWave = entityWave;
        this.maxFps = maxFps;
    }

    public void start() {
        long last = System.nanoTime();
        int fpsCount = 0;
        running = true;

        while (running) {
            long now = System.nanoTime();

            // calculate fps
            long totalFrameTime = now - last;
            fpsCount = (int) (1e9 / totalFrameTime);
            window.showFps("FPS: " + fpsCount);

            // calculate delta time
            float delta = (float) ((now - last) / 1e9 * 60.0);
            last = now;

            // do input
            player.doPlayerInput(delta);

            // clear window
            window.clear(0x000000);

            // draw weapon
            BufferedImage weaponImage = player.getWeapon().getImage();
            int scaledWidth  = weaponImage.getWidth()  * weaponScale;
            int scaledHeight = weaponImage.getHeight() * weaponScale;

            ScreenImage image = new ScreenImage(weaponImage,
                    window.getWidth() / 2 - scaledWidth  / 2,
                    window.getHeight() - scaledHeight,
                    scaledWidth, scaledHeight);
            window.addImage(image);

            // move entities
            entityWave.doLogic(delta);

            // do ray-casting
            raycaster.setPlayer(
                    camera.getCamX(),
                    camera.getCamY(),
                    camera.getDirX(),
                    camera.getDirY()
            );
            RayData[] zBuffer = raycaster.cast();
            renderer.render(zBuffer);

            // get frame times
            long targetFrameTime = 1_000_000_000 / maxFps;
            long elapsedTime = System.nanoTime() - now;

            // amount of time to sleep (subtract time already taken during loop)
            long sleepTime = (targetFrameTime - elapsedTime) / 1_000_000;

            if (sleepTime > 0) { // don't sleep if loop took longer than target frame time
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // stop program
        window.close();
    }

    public void stop() {
        running = false;
    }
}