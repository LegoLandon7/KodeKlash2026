package Game.User;

public class Camera {

    private float camX, camY;

    private float dirX = 1;
    private float dirY = 0;

    public final static float stepSize = 0.125f;
    public final static float rotSize = 0.05f;

    public Camera(float x, float y) {
        camX = x;
        camY = y;
    }

    // forward / back
    public void stepCamera(double step) {
        camX += (float) (dirX * step);
        camY += (float) (dirY * step);
    }

    // left / right
    public void strafeCamera(double step) {
        camX += (float) (-dirY * step);
        camY += (float) (dirX * step);
    }

    public void rotateCamera(double rot) {
        // rotate the vector direction using an angle
        float cos = (float) Math.cos(rot);
        float sin = (float) Math.sin(rot);

        float oldDirX = dirX;

        // final vector using 2d rotation matrix
        // https://en.wikipedia.org/wiki/Rotation_matrix
        dirX = dirX * cos - dirY * sin;
        dirY = oldDirX * sin + dirY * cos;
    }

    // getters
    public float getCamX() { return camX; }
    public float getCamY() { return camY; }

    public float getDirX() { return dirX; }
    public float getDirY() { return dirY; }

    public void setPos(float x, float y) {
        camX = x;
        camY = y;
    }
}