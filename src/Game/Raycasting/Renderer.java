package Game.Raycasting;

import Game.Entities.Entity;
import Game.Entities.EntityWave;
import Game.Output.Window;

import java.util.ArrayList;

public class Renderer {

    private final Window window;
    private final Raycaster raycaster;

    private final int width;
    private final int height;
    private final int res;

    private EntityWave entityWave;

    public Renderer(Window window, Raycaster raycaster, int width, int height, int res) {
        this.window = window;
        this.raycaster = raycaster;
        this.width = width;
        this.height = height;
        this.res = Math.max(1, res);
    }

    public void setEntityWave(EntityWave wave) {
        this.entityWave = wave;
    }

    public void render(RayData[] zBuffer) {

        renderBackground(0x111111, 0x333333);
        renderWalls(zBuffer);

        entityWave.sort(raycaster.getPosX(), raycaster.getPosY());
        for (Entity e : entityWave.getEntities()) {
            renderEntity(e, zBuffer);
        }

        window.render();
    }

    private void renderWalls(RayData[] zBuffer) {

        int rayCount = zBuffer.length;

        for (int i = 0; i < rayCount; i++) {

            float dist = zBuffer[i].getDist();
            if (dist <= 0.0001f) continue;

            int wallHeight = (int)(height / dist);

            int startY = (height - wallHeight) / 2;
            int endY = startY + wallHeight;

            float brightness = Math.max(0f, 1f - dist / (Raycaster.MAX_DIST / 3.5f));
            int color = shade(0xaaaaaa, brightness);
            if (zBuffer[i].getSide() == 1) color = shade(color, 0.8f);

            int xStart = i * res;
            int xEnd = Math.min(width, xStart + res);

            for (int x = xStart; x < xEnd; x++) {
                for (int y = startY; y < endY; y++) {
                    if (y >= 0 && y < height) {
                        window.setPixel(x, y, color);
                    }
                }
            }
        }
    }

    private void renderBackground(int ceiling, int floor) {
        int half = height / 2;

        for (int y = 0; y < half; y++) {
            for (int x = 0; x < width; x++) {
                window.setPixel(x, y, ceiling);
            }
        }

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

        float dx = e.getPosX() - px;
        float dy = e.getPosY() - py;

        float invDet = 1.0f / (planeX * dirY - dirX * planeY);

        float transformX = invDet * (dirY * dx - dirX * dy);
        float transformY = invDet * (-planeY * dx + planeX * dy);

        if (transformY <= 0.01f) return;

        int spriteScreenX = (int) ((width / 2f) * (1 + transformX / transformY));

        int spriteSize = (int) (height / transformY);
        if (spriteSize <= 0) return;

        int[] tex = e.getPixels();
        int tw = e.getWidth();
        int th = e.getHeight();

        int startX = spriteScreenX - spriteSize / 2;
        int endX = spriteScreenX + spriteSize / 2;

        float stepX = tw / (float) spriteSize;

        for (int x = startX; x < endX; x++) {

            if (x < 0 || x >= width) continue;

            int rayX = x / res;
            if (rayX < 0 || rayX >= zBuffer.length) continue;

            if (transformY >= zBuffer[rayX].getDist()) continue;

            int texX = (int) ((x - startX) * stepX);
            if (texX < 0 || texX >= tw) continue;

            int startY = height / 2 - spriteSize / 2;

            float stepY = th / (float) spriteSize;

            for (int y = 0; y < spriteSize; y++) {

                int sy = startY + y;
                if (sy < 0 || sy >= height) continue;

                int texY = (int) (y * stepY);

                int color = tex[texY * tw + texX];

                if ((color >>> 24) != 0) {
                    window.setPixel(x, sy, color);
                }
            }
        }
    }

    private int shade(int color, float brightness) {
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;

        return ((int)(r * brightness) << 16) |
                ((int)(g * brightness) << 8) |
                (int)(b * brightness);
    }
}