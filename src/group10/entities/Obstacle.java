package group10.entities;

public class Obstacle {
    public double worldY;     // scrolls kasama camera
    public double screenX;    // x within the player's clip
    public double vx;         // vertical speed
    public int kind;
    public double scale;
    public int hitW, hitH; // collision box
    
    // constructor, tangina anghaba pero desisyon ko to e
    public Obstacle(int kind, double worldY, double screenX, double vx, double scale, int hitW, int hitH) {
        this.kind = kind;
        this.worldY = worldY;
        this.screenX = screenX;
        this.vx = vx;
        this.scale = scale;
        this.hitW = hitW;
        this.hitH = hitH;
    }
}
