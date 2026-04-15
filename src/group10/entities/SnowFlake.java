package group10.entities;

public class SnowFlake {
	public int x, y, size, speed, drift;
	
	public SnowFlake(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.speed = 1 + size;
		this.drift = (size % 2 == 0) ? 1 : -1;
	}
}
