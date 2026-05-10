package Game.Raycasting;

import Game.Entities.Entity;
import Game.Entities.EntityWave;
import Game.Instance.GameInstance;
import Game.Output.Window;
import Game.Util.HealthBar;

import java.util.ArrayList;

public class Renderer {

    private final Window window;
    private final Raycaster raycaster;
    private final GameInstance gameInstance;

    private final int width;
    private final int height;
    private final int res;

    private EntityWave entityWave;

    public Renderer(Window window, Raycaster raycaster, GameInstance gameInstance,int width, int height, int res) {
        this.window = window;
        this.raycaster = raycaster;
        this.gameInstance = gameInstance;
        this.width = width;
        this.height = height;
        this.res = Math.max(1, res);
    }

    public void setEntityWave(EntityWave wave) {
        this.entityWave = wave;
    }

    public void render(RayData[] zBuffer) {
        // render walls and background
        renderBackground(0x111111, 0x333333);
        renderWalls(zBuffer);

        // render entities
        entityWave.sort(raycaster.getPosX(), raycaster.getPosY());
        for (Entity e : entityWave.getEntities()) {
            renderEntity(e, zBuffer);
        }

        gameInstance.renderHud(window);

        // show on window
        window.render();
    }

    private void renderWalls(RayData[] zBuffer) {
        // render walls
        int rayCount = zBuffer.length;

        // loop through all the rays
        for (int i = 0; i < rayCount; i++) {
            float dist = zBuffer[i].getDist();

            // checks
            if (dist <= 0.0001f) continue;

            // calculate wall height and where to draw
            int wallHeight = (int)(height / dist);
            int startY = (height - wallHeight) / 2;
            int endY = startY + wallHeight;

            // calculate color
            float brightness = Math.max(0f, 1f - dist / (Raycaster.MAX_DIST / 3.5f));
            int color = shade(0xaaaaaa, brightness);
            if (zBuffer[i].getSide() == 1) color = shade(color, 0.8f);

            // get thickness of wall segment
            int xStart = i * res;
            int xEnd = Math.min(width, xStart + res);

            // loop through every pixel and color it
            for (int x = xStart; x < xEnd; x++) {
                for (int y = startY; y < endY; y++) {
                    if (y >= 0 && y < height) window.setPixel(x, y, color);
                }
            }
        }
    }

    private void renderBackground(int ceiling, int floor) {
        int half = height / 2;

        // render ceiling
        for (int y = 0; y < half; y++) {
            for (int x = 0; x < width; x++) {
                window.setPixel(x, y, ceiling);
            }
        }

        // render floor
        for (int y = half; y < height; y++) {
            for (int x = 0; x < width; x++) {
                window.setPixel(x, y, floor);
            }
        }
    }

    private void renderEntity(Entity e, RayData[] zBuffer) {
        if (e == null) return;

        float px = raycaster.getPosX();
        float py = raycaster.getPosY();
        float dirX = raycaster.getDirX();
        float dirY = raycaster.getDirY();
        float planeX = raycaster.getPlaneX();
        float planeY = raycaster.getPlaneY();

        // vector from player to entity
        float dx = e.getPosX() - px;
        float dy = e.getPosY() - py;

        // reverse projection from entity into camera space
        float invDet = 1.0f / (planeX * dirY - dirX * planeY);
        float camX = invDet * (dirY * dx - dirX * dy);
        float camZ = invDet * (-planeY * dx + planeX * dy);

        if (camZ <= 0.01f) return;

        // projected screen position and size
        int screenX = (int) ((width / 2f) * (1 + camX / camZ));
        int size = (int) (height / camZ);
        if (size <= 0) return;

        int[] pixels = e.getPixels();
        int texW = e.getWidth();
        int texH = e.getHeight();

        int startX = screenX - size / 2;
        int endX = screenX + size / 2;
        float stepX = texW / (float) size;

        for (int x = startX; x < endX; x++) {
            if (x < 0 || x >= width) continue;

            int rayIdx = x / res;
            if (rayIdx < 0 || rayIdx >= zBuffer.length) continue;
            // skip if wall is closer
            if (camZ >= zBuffer[rayIdx].getDist()) continue;

            int texX = (int) ((x - startX) * stepX);
            if (texX < 0 || texX >= texW) continue;

            int startY = height / 2 - size / 2;
            float stepY = texH / (float) size;

            for (int row = 0; row < size; row++) {
                int sy = startY + row;
                if (sy < 0 || sy >= height) continue;

                int texY = (int) (row * stepY);
                int color = pixels[texY * texW + texX];
                if ((color >>> 24) != 0) window.setPixel(x, sy, color);
            }
        }

        renderHealthbar(e, zBuffer, screenX, size, camZ);
    }

    private void renderHealthbar(Entity e, RayData[] zBuffer, int screenX, int size, float camZ) {
        if (e.getHealth() == 100) return;

        int barWidth = size;
        int barHeight = Math.max(2, size / 16);
        int barX = screenX - barWidth / 2;
        int barY = (height / 2) - (size / 2) - barHeight - 2;

        HealthBar.render(e.getHealth(), barX, barY, barWidth, barHeight, window, res, zBuffer, camZ);
    }

    private int shade(int color, float brightness) {
        // separate color channels
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;

        // combine color channels
        return ((int)(r * brightness) << 16) |
                ((int)(g * brightness) << 8) |
                (int)(b * brightness);
    }
}