package Game.Entities;

import Game.User.Camera;
import Game.User.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Entity {
    private float x, y;
    private final int width;
    private final int height;
    private final int[] pixels;

    private final Path path;
    private final Camera camera;

    private int health;

    public static final float ENTITY_SIZE = 0.5f;
    public static final float PLAYER_SIZE = 0.75f;
    public static final float ENTITY_SPEED = 0.033f;
    public static final int RANDOM_CHANCE = 100;

    public Entity(float x, float y, String filePath, Path path, Camera camera) {
        this.x = x;
        this.y = y;

        this.path = path;
        this.camera = camera;

        health = 100;

        try {
            // read image path
            BufferedImage img = ImageIO.read(
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath))
            );

            // get image data
            BufferedImage buf = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = buf.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();

            // set class data
            width = buf.getWidth();
            height = buf.getHeight();
            pixels = new int[width * height];
            buf.getRGB(0, 0, width, height, pixels, 0, width);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void moveTo(float tx, float ty, float step, Entity[] collision) {
        float dx = tx - x;
        float dy = ty - y;

        float dist = (float)Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.01f) return;

        float newX = x + dx / dist * step;
        float newY = y + dy / dist * step;

        boolean blockX = false;
        boolean blockY = false;

        // check entity collisions
        for (Entity e : collision) {
            if (e == null) continue;
            if (e.equals(this)) continue;

            float posX = e.getPosX();
            float posY = e.getPosY();

            // check each axis
            float distX = (float)Math.sqrt(((newX - posX) * (newX - posX)) + ((y - posY) * (y - posY)));
            float distY = (float)Math.sqrt(((x - posX) * (x - posX)) + ((newY - posY) * (newY - posY)));

            if (distX < ENTITY_SIZE) blockX = true;
            if (distY < ENTITY_SIZE) blockY = true;
        }

        // check camera collision
        float camX = camera.getCamX();
        float camY = camera.getCamY();

        float distX = (float)Math.sqrt(((newX - camX) * (newX - camX)) + ((y - camY) * (y - camY)));
        float distY = (float)Math.sqrt(((x - camX) * (x - camX)) + ((newY - camY) * (newY - camY)));

        if (distX < PLAYER_SIZE) blockX = true;
        if (distY < PLAYER_SIZE) blockY = true;

        // check wall collision
        int tileX = (int)Math.floor(newX);
        int tileY = (int)Math.floor(y);

        if(path.getMap()[tileX][tileY] > 0) blockX = true;

        tileX = (int)Math.floor(x);
        tileY = (int)Math.floor(newY);

        if(path.getMap()[tileX][tileY] > 0) blockY = true;

        // apply movement
        if(!blockX) x = newX;
        if(!blockY) y = newY;
    }

    public void doPathFinding(Entity[] collisionEntities) {
        float camX = camera.getCamX();
        float camY = camera.getCamY();

        // set random point to path to
        if (Math.random() < 1.0 / RANDOM_CHANCE) {
            camX += (float) (Math.random());
            camY += (float) (Math.random());
        }

        // get angle of ray
        float rayDirX = camX - x;
        float rayDirY = camY - y;
        
        // normalize angle
        float rayLength = (float)Math.sqrt(rayDirX * rayDirX + rayDirY * rayDirY);
        rayDirX /= rayLength;
        rayDirY /= rayLength;

        // start ray loop
        float rayPosX = x + 0.5f;
        float rayPosY = y + 0.5f;

        boolean hit = false;
        float stepSize = 0.05f;

        for(float d = 0; d < rayLength; d += stepSize) {
            // map position
            int mapX = (int) Math.floor(rayPosX);
            int mapY = (int) Math.floor(rayPosY);

            // out of bounds
            if(mapX < 0 || mapX >= path.getMap().length || mapY < 0 || mapY >= path.getMap()[0].length) break;

            // test collision
            if(path.getMap()[mapX][mapY] > 0) {
                hit = true;
                break;
            }

            // step ray
            rayPosX += rayDirX * stepSize;
            rayPosY += rayDirY * stepSize;
        }

        // move entity directly
        if(!hit) {
            moveTo(camX, camY, ENTITY_SPEED, collisionEntities);
            return;
        }

        // pathfinding
        path.getPathing((int) camX, (int) camY);
        Path.pos nextPos = path.getNextTile((int) x, (int) y);

        // move entity
        if(nextPos != null) moveTo(nextPos.x + 0.5f, nextPos.y + 0.5f, ENTITY_SPEED, collisionEntities);
    }

    public void doDamage(int damage, EntityWave entityWave) {
        health -= damage;

        // entity dead
        if(health <= 0) entityWave.remove(this);
    }

    // getters
    public float getPosX() { return x; }
    public float getPosY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int[] getPixels() { return pixels; }
}