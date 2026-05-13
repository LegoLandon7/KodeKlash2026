// Landon Lego
// 5/12/26
// this file is used to iterate over the game each frame

package Game.Instance;

import Game.Audio.SFX;
import Game.Entities.Entity;
import Game.Entities.EntityWave;
import Game.Map.Maps;
import Game.Output.ScreenImage;
import Game.Raycasting.RayData;
import Game.User.Camera;
import Game.User.Player;
import Game.Raycasting.Raycaster;
import Game.Raycasting.Renderer;
import Game.Output.Window;
import Game.Util.HealthBar;

import java.awt.image.BufferedImage;

public class GameLoop {
    private final Window window;
    private final Camera camera;
    private final Player player;
    private final Renderer renderer;
    private final Raycaster raycaster;
    private final Entity[] entities;
    private final GameInstance gameInstance;

    private final int maxFps;

    private final EntityWave entityWave;

    private boolean running = false;

    public static int weaponScale = 3;

    private int waveCount;

    public GameLoop(Window window, Camera camera, Player player,
                    Renderer renderer, Raycaster raycaster, EntityWave entityWave,
                    Entity[] entities, int maxFps, GameInstance gameInstance) {

        this.window = window;
        this.camera = camera;
        this.player = player;
        this.renderer = renderer;
        this.raycaster = raycaster;
        this.entityWave = entityWave;
        this.entities = entities;
        this.maxFps = maxFps;
        this.gameInstance = gameInstance;
        waveCount = 1;
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


            // reset entities
            if (entityWave.getCount() == 0) {
                entityWave.reset(entities);
                entityWave.randomize(Maps.mainMap.length, Maps.mainMap[0].length);

                waveCount++;
                gameInstance.changeHealth(HealthBar.MAX_HEALTH / 2);
                SFX.playSound("assets/sfx/ding.wav");
            }

            window.setEntityCount(entityWave.getCount());
            window.setWaveCount(waveCount);

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
        SFX.loopSound("assets/sfx/mainmenu-music.wav");
    }

    // getters
    public int getWaveCount() {return waveCount;}

    public void stop() {
        running = false;
    }
}