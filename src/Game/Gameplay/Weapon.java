// Landon Lego
// 5/12/26
// class used for dealing damage and storing weapon data

package Game.Gameplay;

import Game.Entities.Entity;
import Game.Entities.EntityWave;
import Game.User.Camera;
import Game.Util.ResourceLoader;
import Game.Util.VectorMath;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Weapon {
    private final BufferedImage image;
    private final int damage;
    private final long reloadTime; // milliseconds
    private final float bulletSpeed;
    private final float range;

    private EntityWave entityWave;
    private final int[][] map;
    private final Camera camera;

    private long cooldown;

    public Weapon(String imagePath, EntityWave entityWave, int[][] map, Camera camera, int damage, long reloadTime, float bulletSpeed, float range) {
        try {
            this.image = ResourceLoader.loadImage(imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.damage = damage;
        this.reloadTime = reloadTime;
        this.bulletSpeed = bulletSpeed;
        this.range = range;

        this.entityWave = entityWave;
        this.map = map;
        this.camera = camera;

        cooldown = System.currentTimeMillis();
    }

    public void fire() {
        // check cooldowns
        long now = System.currentTimeMillis();
        if (now - cooldown < reloadTime) return;
        cooldown = now;

        // get camera data
        float camX = camera.getCamX();
        float camY = camera.getCamY();

        // normalizes the camera direction
        float camDirX = camera.getDirX();
        float camDirY = camera.getDirY();
        float length = VectorMath.length(camDirX, camDirY);
        camDirX /= length;
        camDirY /= length;

        // ray data
        float rayX = camX;
        float rayY = camY;
        boolean hit = false;

        // cast bullet(s)
        for (int s = 0; s < range; s++) {
            // current map position
            int mapX = (int) rayX;
            int mapY = (int) rayY;

            // checks
            if (map[mapX][mapY] > 0) break;

            for (Entity e : entityWave.getEntities()) {
                if (e == null) continue;

                // get entity data
                float posX = e.getPosX();
                float posY = e.getPosY();

                float dist = VectorMath.distance(rayX, rayY, posX, posY);

                // check if bullet hit
                if (dist < e.getEntitySize()) {
                    dealDamage(e); // damage entity

                    hit = true;
                    break;
                }
            }

            // checks
            if (hit) break;

            // step ray
            rayX += camDirX * bulletSpeed;
            rayY += camDirY * bulletSpeed;
        }
    }

    private void dealDamage(Entity e) {
        e.doDamage(damage, entityWave);
    }

    // setters
    public void setEntityWave(EntityWave entityWave) {this.entityWave = entityWave;}

    // getters
    public BufferedImage getImage() {return image;}
}