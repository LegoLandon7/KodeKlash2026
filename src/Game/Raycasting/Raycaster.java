package Game.Raycasting;

import java.util.Arrays;

public class Raycaster {

    private final int width;
    private final int[][] map;
    private final int res;
    private final float fov;

    private float posX, posY;
    private float dirX, dirY;
    private float planeX, planeY;

    private RayData[] zBuffer;

    public static final float MAX_DIST = 100f;

    public Raycaster(int width, int height, int[][] map, float fov, int res) {
        this.width = width;
        this.map = map;
        this.res = res;
        this.fov = fov;

        posX = 0;
        posY = 0;
        dirX = 1;
        dirY = 0;
        planeX = 0.66f;
        planeY = 0;
        int rayCount = width / res;
        zBuffer = new RayData[rayCount];
        for (int i = 0; i < rayCount; i++)
            zBuffer[i] = new RayData();
    }

    public void setPlayer(float x, float y, float dx, float dy) {
        posX = x;
        posY = y;
        dirX = dx;
        dirY = dy;

        // plane = tan(1/2fov)
        float planeMag = (float)Math.tan(fov * 0.5f);

        // inverse plane (plane is perpendicular to camera
        planeX = -dirY * planeMag;
        planeY = dirX * planeMag;
    }

    public RayData[] cast() {
        // loop through every ray
        for (int i = 0; i < zBuffer.length; i++) {

            float cameraX = 2f * i / (float)(zBuffer.length - 1) - 1f;

            // initialize the starting ray position
            float rayX = dirX + planeX * cameraX;
            float rayY = dirY + planeY * cameraX;

            // map position of array
            int mapX = (int) posX;
            int mapY = (int) posY;

            // distance ray will travel every step
            float deltaX = rayX == 0 ? 1e30f : Math.abs(1f / rayX);
            float deltaY = rayY == 0 ? 1e30f : Math.abs(1f / rayY);

            // direction ray will step
            int stepX = rayX < 0 ? -1 : 1;
            int stepY = rayY < 0 ? -1 : 1;

            // get initial distance needed to step to get to the first grid line
            float sideX = rayX < 0
                    ? (posX - mapX) * deltaX
                    : (mapX + 1f - posX) * deltaX;

            float sideY = rayY < 0
                    ? (posY - mapY) * deltaY
                    : (mapY + 1f - posY) * deltaY;

            boolean hit = false;
            int side = 0;
            float dist = 50f;

            // step the ray
            for (int s = 0; s < MAX_DIST; s++) {
                // check if the ray should move in the x direction or y direction to next grid line
                // then step the ray by delta
                if (sideX < sideY) {
                    sideX += deltaX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideY += deltaY;
                    mapY += stepY;
                    side = 1;
                }

                // ray out of bounds = collided
                if (mapX < 0 || mapY < 0 || mapX >= map.length || mapY >= map[0].length) {
                    hit = true;
                    dist = Math.min(sideX, sideY);
                    break;
                }

                // check if ray collided with map
                if (map[mapX][mapY] > 0) {
                    hit = true;
                    dist = (side == 0)
                            ? sideX - deltaX
                            : sideY - deltaY;
                    break;
                }
            }

            // put data into an array to pass to renderer
            zBuffer[i].setDist(hit ? Math.max(0.01f, dist) : 50f);
            zBuffer[i].setSide(side);
        }

        return zBuffer;
    }

    // getters
    public float getDirX()   { return dirX; }
    public float getDirY()   { return dirY; }
    public float getPosX()   { return posX; }
    public float getPosY()   { return posY; }
    public float getPlaneX() { return planeX; }
    public float getPlaneY() { return planeY; }
    public float getFov()    { return fov; }
}