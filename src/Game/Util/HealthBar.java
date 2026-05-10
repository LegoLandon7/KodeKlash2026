package Game.Util;

import Game.Output.Window;
import Game.Raycasting.RayData;

public class HealthBar {
    public static void render(int health, int x, int y, int width, int height, Window window) {
        float percentFilled = Math.max(0, health / 100f);
        int fillWidth = (int)(width * percentFilled);

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                int color = (px < fillWidth) ? 0xFF22CC22 : 0xFF880000;
                window.setPixel(px + x, py + y, color);
            }
        }
    }

    public static void render(int health, int x, int y, int width, int height, Window window, int res, RayData[] zBuffer, float camZ) {
        float percentFilled = Math.max(0, health / 100f);
        int fillWidth = (int)(width * percentFilled);

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                int rayIdx = (x + px) / res;
                if (rayIdx < 0 || rayIdx >= zBuffer.length) continue;
                if (camZ >= zBuffer[rayIdx].getDist()) continue;

                int color = (px < fillWidth) ? 0xFF22CC22 : 0xFF880000;
                window.setPixel(px + x, py + y, color);
            }
        }
    }
}
