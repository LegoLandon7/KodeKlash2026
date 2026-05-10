package Game.Util;

import Game.Output.Window;
import Game.Raycasting.RayData;

public class HealthBar {
    public static int MAX_HEALTH = 1000;
    public static int HEAL_COLOR = 0x00ff00;
    public static int DAMAGE_COLOR = 0xff0000;

    public static void render(int health, int x, int y, int width, int height, Window window) {
        float percentFilled = Math.max(0, health / (float) MAX_HEALTH);
        int fillWidth = (int)(width * percentFilled);

        for (int py = y; py < y + height; py++) {
            if (fillWidth > 0) window.fillRow(x, py, fillWidth, HEAL_COLOR);
            if (fillWidth < width) window.fillRow(x + fillWidth, py, width - fillWidth, DAMAGE_COLOR);
        }
    }

    public static void render(int health, int x, int y, int width, int height, Window window, int res, RayData[] zBuffer, float camZ) {
        if (health == MAX_HEALTH) return;
        float percentFilled = Math.max(0, health / (float) MAX_HEALTH);
        int fillWidth = (int)(width * percentFilled);

        for (int px = 0; px < width; px++) {
            int rayIdx = (x + px) / res;
            if (rayIdx < 0 || rayIdx >= zBuffer.length) continue;
            if (camZ >= zBuffer[rayIdx].getDist()) continue;

            int color = (px < fillWidth) ? HEAL_COLOR : DAMAGE_COLOR;
            for (int py = y; py < y + height; py++)
                window.setPixel(x + px, py, color);
        }
    }
}
