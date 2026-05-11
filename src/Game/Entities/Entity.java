// Landon Lego
// 5/11/26
// this is the main class for doing entity logic and storing entity data

package Game.Entities;

import Game.Instance.GameInstance;
import Game.User.Camera;
import Game.Util.HealthBar;
import Game.Util.ResourceLoader;
import Game.Util.VectorMath;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Entity {
    private float x, y;
    private final int width;
    private final int height;
    private final int[] pixels;
    private final String filePath;
    
    private final Path path;
    private final Camera camera;
    private final GameInstance gameInstance;
    
    private final float healthMultiplier;
    private final float entitySize;
    private final float playerDistance;
    private final float entitySpeed;
    private final int damageOut;
    
    private int currentHealth;
    private long lastDamageCooldown;

    public static final int RANDOM_CHANCE = 100;
    public static final long DAMAGE_COOLDOWN = 1000;

    public Entity(float x, float y, String filePath, Path path, Camera camera, GameInstance gameInstance, float healthMultiplier, float entitySize, float playerSize, float entitySpeed, int damageOut) {
        this.x = x;
        this.y = y;

        this.filePath = filePath;
        this.path = path;
        this.camera = camera;
        this.gameInstance = gameInstance;

        this.healthMultiplier = healthMultiplier;
        this.entitySize = entitySize;
        this.playerDistance = playerSize;
        this.entitySpeed = entitySpeed;
        this.damageOut = damageOut;

        currentHealth = HealthBar.MAX_HEALTH;
        lastDamageCooldown = System.currentTimeMillis();
        
        try {
            // read image path
            BufferedImage image = ResourceLoader.loadImage(filePath);

            // get image data
            BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = buffer.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            // set class data
            width = buffer.getWidth();
            height = buffer.getHeight();
            pixels = new int[width * height];
            buffer.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveTo(float tx, float ty, float step, Entity[] collision, float delta) {
        // distance to point
        float dx = tx - x;
        float dy = ty - y;

        float dist = (float)Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.01f) return; // already there

        // new position to walk to
        float newX = x + dx / dist * step * delta;
        float newY = y + dy / dist * step * delta;

        boolean blockX = false;
        boolean blockY = false;

        // check entity collisions
        for (Entity e : collision) {
            if (e == null) continue;
            if (e.equals(this)) continue;

            float posX = e.getPosX();
            float posY = e.getPosY();

            // check each axis distance
            float distX = (float)Math.sqrt(((newX - posX) * (newX - posX)) + ((y - posY) * (y - posY)));
            float distY = (float)Math.sqrt(((x - posX) * (x - posX)) + ((newY - posY) * (newY - posY)));

            // check collision
            if (distX < entitySize) blockX = true;
            if (distY < entitySize) blockY = true;
        }

        // check player collision
        float camX = camera.getCamX();
        float camY = camera.getCamY();

        float distX = (float)Math.sqrt(((newX - camX) * (newX - camX)) + ((y - camY) * (y - camY)));
        float distY = (float)Math.sqrt(((x - camX) * (x - camX)) + ((newY - camY) * (newY - camY)));

        if (distX < playerDistance) blockX = true;
        if (distY < playerDistance) blockY = true;

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

        // deal damage
        float distanceToPlayer = VectorMath.distance(camX, camY, x, y);
        
        if (distanceToPlayer < playerDistance + entitySize) {
            long now = System.currentTimeMillis();
            if (now - lastDamageCooldown < DAMAGE_COOLDOWN) return; // cooldown
            
            gameInstance.takeDamage(damageOut);
            lastDamageCooldown = now;
        }
    }

    public void doPathFinding(Entity[] collisionEntities, float delta) {
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
        float rayLength = VectorMath.length(rayDirX, rayDirY);
        rayDirX /= rayLength;
        rayDirY /= rayLength;

        // start ray loop
        float rayPosX = x + 0.5f;
        float rayPosY = y + 0.5f;

        boolean hit = false;
        float stepSize = 0.25f;

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
            moveTo(camX, camY, entitySpeed, collisionEntities, delta);
            return;
        }

        // pathfinding
        Path.pos nextPos = path.getNextTile((int) x, (int) y);

        // move entity (centered)
        if(nextPos != null) moveTo(nextPos.x + 0.5f, nextPos.y + 0.5f, entitySpeed, collisionEntities, delta);
    }

    public void doDamage(int damage, EntityWave entityWave) {
        currentHealth -= (int) Math.max(1, damage * healthMultiplier);
        if(currentHealth <= 0) entityWave.remove(this);
    }
    
    // setters
    public void setPos(float x, float y) {this.x = x; this.y = y;}

    // getters
    public float getPosX() {return x;}
    public float getPosY() {return y;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public int[] getPixels() {return pixels;}
    public int getHealth() {return currentHealth;}
    public Camera getCamera() {return camera;}
    public float getEntitySize() {return entitySize;}
    public float getDamageMultiplier() {return healthMultiplier;}
    public float getEntitySpeed() {return entitySpeed;}
    public Path getPath() {return path;}
    public String getFilePath() {return filePath;}
    public int getDamage(){return damageOut;}
    public GameInstance getGameInstance() {return gameInstance;}
    public float getPlayerSize() {return playerDistance;}
}