package group10.entities;

import java.awt.Color;

public class Car {
	public double x, y, speed;
	public int width = 63; // originally 72
	public int height = 159; // originally 120
	// added angle for rotational logic yezzirrrrrrrrrr
	public double angle = -90.0;
	public Color color;
	public String label;
	
	public Car(double x, double y, Color color, String label) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.label = label;
	}
}
