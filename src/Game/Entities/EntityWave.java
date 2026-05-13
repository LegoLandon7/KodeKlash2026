// Landon Lego
// 5/12/26
// this is the class used for spawning many enemies and keeping track of them

package Game.Entities;

import Game.Instance.GameLoop;
import Game.User.Camera;
import Game.Util.VectorMath;

import java.util.Random;

public class EntityWave {
    private Entity[] entities;
    private final int difficulty;
    private final int[][] map;

    private final GameLoop gameLoop;

    public EntityWave(int[][] map, int difficulty, GameLoop gameLoop) {
        this.difficulty = difficulty;
        this.map = map;
        this.gameLoop = gameLoop;
    }

    public void addEntity(Entity entity, int count) {
        // cache current entities
        Entity[] entityCache = entities;
        Entity[] newEntities = new Entity[difficulty * count]; // scale by difficulty

        // fill entities array
        for (int i = 0; i < count * difficulty; i++)
            newEntities[i] = new Entity(
                    entity.getPosX(),
                    entity.getPosY(),
                    entity.getFilePath(),
                    entity.getPath(),
                    entity.getCamera(),
                    entity.getGameInstance(),
                    entity.getDamageMultiplier(),
                    entity.getEntitySize(),
                    entity.getPlayerSize(),
                    entity.getEntitySpeed(),
                    entity.getDamage());

        if (entities == null) {
            entities = newEntities;
            return;
        }

        // combine arrays
        int totalLength = entityCache.length + newEntities.length;
        Entity[] newEntityCache = new Entity[totalLength];

        System.arraycopy(entityCache, 0, newEntityCache, 0, entityCache.length);
        System.arraycopy(newEntities, 0, newEntityCache, entityCache.length, newEntities.length);

        entities = newEntityCache;
    }

    public void randomize(int limitX, int limitY) {
        for (Entity entity : entities) {
            if (entity == null) continue;

            float randomX = (float) Math.random() * limitX;
            float randomY = (float) Math.random() * limitY;

            int attempts = 0;

            while (isBadSpawn(randomX, randomY, entity)) {
                randomX = (float) Math.random() * limitX;
                randomY = (float) Math.random() * limitY;

                attempts++;
                if (attempts > 1000) { // if its tries 1000 times to get a good spawn give up
                    while(inTile(randomX, randomY,  entity)) {
                        randomX = (float) Math.random() * limitX;
                        randomY = (float) Math.random() * limitY;
                    }

                    entity.setPos(randomX, randomY);
                    break;
                }
            }

            entity.setPos(randomX, randomY);
        }
    }

    private boolean inTile(float x, float y, Entity entity) {
        // inside tile
        if ((int) x < 0 || (int) x >= map.length || (int) y < 0 || (int) y >= map[0].length) return true;
        if (map[(int) x][(int) y] > 0) return true;

        // check each corner
        float size = entity.getEntitySize();
        float[] offsets = {-size, size};

        for (float dx : offsets) {
            for (float dy : offsets) {
                int tx = (int)(x + dx);
                int ty = (int)(y + dy);

                // checks
                if (tx < 0 || ty < 0 || tx >= map.length || ty >= map[0].length) return true;
                if (map[tx][ty] > 0) return true;
            }
        }

        return false;
    }

    private boolean isBadSpawn(float x, float y, Entity spawn) {
        // inside wall
        if(inTile(x, y, spawn)) return true;

        // too close to player
        float camX = spawn.getCamera().getCamX();
        float camY = spawn.getCamera().getCamY();
        if (VectorMath.distance(x, y, camX, camY) < spawn.getPlayerSize()) return true;

        // too close to another entity
        for (Entity other : entities) {
            // checks
            if (other == null) continue;
            if (other == spawn) continue;
            if (VectorMath.distance(x, y, other.getPosX(), other.getPosY()) < other.getEntitySize()) return true;
        }

        return false;
    }

    public void sort(float camX, float camY) {
        float[] distances = new float[entities.length];

        // find distances to each entity
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == null) continue;

            // get entity data
            float posX = entities[i].getPosX();
            float posY = entities[i].getPosY();

            // sort by distance
            float distance = (float) Math.sqrt(((camX - posX) * (camX - posX)) + ((camY - posY) * (camY - posY)));
            distances[i] = distance;
        }

        for (int i = 1; i < distances.length; i++) {
            // get current index
            float distKey = distances[i];
            Entity entKey = entities[i];

            int prev = i - 1;

            // sort
            while (prev >= 0 && distances[prev] < distKey) {
                distances[prev + 1] = distances[prev];
                entities[prev + 1] = entities[prev];
                prev--;
            }

            distances[prev + 1] = distKey;
            entities[prev + 1] = entKey;
        }
    }

    public void doLogic(float delta) {
        // calculate pathing for one entity
        Entity first = null;
        for (Entity entity : entities) {
            if (entity == null) continue;
            first = entity;
            break;
        }

        if (first == null) return;

        Camera camera = first.getCamera();
        int camX = (int) camera.getCamX();
        int camY = (int) camera.getCamY();
        first.getPath().getPathing(camX, camY);

        // move all entities
        for (Entity entity : entities) {
            if (entity == null) continue;
            entity.doPathFinding(entities, delta);
        }
    }

    public void remove(Entity entity) {
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == null) continue;
            if (entities[i] == entity) entities[i] = null;
        }
    }

    // respawn many entities
    public void reset(Entity[] entities) {
        this.entities = new Entity[0];

        // spawn new entities
        Random rnd = new Random();

        int wave;
        if (gameLoop == null) wave = 1;
        else wave = gameLoop.getWaveCount();

        addEntity(entities[0], rnd.nextInt(wave * 2, wave * 2 + 10));
        addEntity(entities[1], rnd.nextInt(wave * 2, wave * 2 + 3));
        addEntity(entities[2], rnd.nextInt(wave * 2, wave * 2 + 5));
        addEntity(entities[3], rnd.nextInt(wave * 2, wave * 2 + 8));
        addEntity(entities[4], rnd.nextInt(wave * 2, wave * 2 + 7));
        addEntity(entities[5], rnd.nextInt(wave * 2, wave * 2 + 6));

        // secret
        if (rnd.nextInt(15) == 1) addEntity(entities[6], 2);
    }

    // getters
    public Entity[] getEntities() {return entities;}

    public int getCount() {
        int count = 0;

        for (Entity entity : entities) {
            if (entity == null) continue;
            count++;
        }

        return count;
    }
}
