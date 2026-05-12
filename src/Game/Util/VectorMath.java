// Landon Lego
// 5/12/26
// this file is used to simplify commonly used equations for math

package Game.Util;

public class VectorMath {
    public static float length(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }
    public static float distance(float x1, float y1, float x2, float y2) {
        return length(x2 - x1, y2 - y1);
    }
}
