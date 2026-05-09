package Game.Raycasting;

public class RayData {
    private float dist;
    private int side;

    public RayData(float dist, int side) {
        this.dist = dist;
        this.side = side;
    }

    public RayData() {
        this.dist = -1.0f;
        this.side = 0;
    }

    // setters
    public void setDist(float dist) {this.dist = dist;}
    public void setSide(int side)   {this.side = side;}

    // getters
    public float getDist() { return dist; }
    public int getSide()   { return side; }
}
