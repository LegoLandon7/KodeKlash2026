package Game.Entities;

import Game.User.Camera;

public class EntityWave {
    private final Entity[] entities;
    private final int[][] map;

    public EntityWave(String filePath, Path path, Camera camera, int[][] map, int count, int difficulty) {
        entities = new Entity[count * difficulty];

        // fill entities array
        for (int i = 0; i < count * difficulty; i++) {
            entities[i] = new Entity(0, 0, filePath, path, camera);
        }

        this.map = map;
    }

    public void randomize(int limitX, int limitY) {
        for (Entity entity : entities) {
            if (entity == null) continue;

            // get random position and set it
            float randomX = (float) Math.random() * limitX;
            float randomY = (float) Math.random() * limitY;

            // cant be inside tile or close to player
            while((map[(int) randomX][(int) randomY] > 0)) {
                randomX = (float) Math.random() * limitX;
                randomY = (float) Math.random() * limitY;
            }

            entity.setPos(randomX, randomY);
        }
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

    public void doLogic() {
        for (Entity entity : entities) {
            if (entity == null) continue;
            entity.doPathFinding(entities);
        }
    }

    public void remove(Entity entity) {
        for (int i = 0; i < entities.length; i++) {
            if (entity == null) continue;
            if (entities[i] == entity) entities[i] = null;
        }
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
