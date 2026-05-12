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
import java.util.ArrayList;
import java.util.List;

public class Weapon {
    private final BufferedImage image;
    private final int damage;
    private final long reloadTime; // milliseconds
    private final float bulletSpeed;
    private final float range;
    private final int spread;

    private EntityWave entityWave;
    private final int[][] map;
    private final Camera camera;

    private long cooldown;

    public Weapon(String imagePath, EntityWave entityWave, int[][] map, Camera camera, int damage, long reloadTime, float bulletSpeed, float range, int spread) {
        try {
            this.image = ResourceLoader.loadImage(imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.damage = damage;
        this.reloadTime = reloadTime;
        this.bulletSpeed = bulletSpeed;
        this.range = range;
        this.spread = spread;

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

        List<Entity> hitEntities = new ArrayList<>();

        float spreadAngle = (float) Math.toRadians(spread); // spread is angle and bullet count

        // each weapon shot is only allowed to hit 1 entity at a time
        // no entity can be hit twice no matter what bullet shot it
        // multiple different entities can be hit though
        // this allows piercing or spreading the shot out


        // loop through spread
        for (int i = 0; i < spread; i++) {
            // ray data
            float rayX = camX;
            float rayY = camY;

            // reset hit
            int hitCount = 0;

            // calculate bullet angle
            float angle = (spread == 1) ? 0 : -spreadAngle / 2 + spreadAngle * i / (spread - 1); // left side of angle + current ray / angle

            // rotation matrix
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            float dirX = camDirX * cos - camDirY * sin;
            float dirY = camDirX * sin + camDirY * cos;

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
                        // check if entity can get damaged
                        if (hitEntities.contains(e)) break;
                        if (hitCount > 3) break;

                        dealDamage(e); // damage entity

                        // enemy was hit
                        hitCount++;
                        hitEntities.add(e);
                    }
                }

                // step ray
                rayX += dirX * bulletSpeed;
                rayY += dirY * bulletSpeed;
            }
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