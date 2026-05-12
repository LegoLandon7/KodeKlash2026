// Landon Lego
// 5/12/26
// this file is used for entities to path-find towards the player

package Game.Entities;

import javax.swing.*;
import java.util.*;

public class Path {
    private final int[][] map;
    private pos[][] path;
    private pos[][] parent;

    public Path(int[][] map) {
        this.map = map;
    }

    public void getPathing(int targetX, int targetY) {
        int rowCount = map.length;
        int colCount = map[0].length;

        // setup arrays and queues
        path = new pos[rowCount][colCount];
        parent = new pos[rowCount][colCount];
        boolean[][] visited = new boolean[rowCount][colCount];

        Queue<pos> queue = new ArrayDeque<>(); // queues are first-in first-out

        // setup initial visited tile
        pos start = new pos(targetX, targetY);
        queue.add(start);
        visited[targetX][targetY] = true;

        // all directions tiles can face (north, south, east, west)
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        // loop goes through all 4 directions from a tile
        // then if it's a valid tile, mark the direction
        // if not skip to next direction / tile
        while (!queue.isEmpty()) {
            pos current = queue.poll();

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                // checks
                if (nx < 0 || nx >= rowCount || ny < 0 || ny >= colCount) continue;
                if (map[nx][ny] > 0 || visited[nx][ny]) continue;

                visited[nx][ny] = true;
                parent[nx][ny] = current;

                queue.add(new pos(nx, ny));
            }
        }
    }

    public pos getNextTile(int startX, int startY) {
        // out of bounds
        if (startX < 0 || startX >= path.length || startY < 0 || startY >= path[0].length) return null;

        // return direction
        pos current = new pos(startX, startY);
        pos next = parent[current.x][current.y];

        if (next == null) return null;

        return next;

    }

    // pos class to make queues and lists easier to manage
    public static class pos {
        public int x, y;

        public pos(int x, int y) {
            this.x = x; this.y = y;
        }
    }

    // getters
    public int[][] getMap() {return map;}
}
