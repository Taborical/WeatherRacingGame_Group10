package group10.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

public class Obstacles {
	
	public static void drawBollard(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
	    g2.translate(x, y);
	    g2.scale(scale, scale);
		//base
		g2.setColor(Color.decode("#9d2828"));
		g2.fillRoundRect(0, 0, 207, 760, 210, 210);
		
		//stripes
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 85, 207, 72);
		g2.fillRect(0, 202, 207, 72);
		
		g2.setTransform(at);
	}
	
	public static void drawPuddle(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
	    g2.translate(x, y);
	    g2.scale(scale, scale);
	    
		g2.setColor(Color.decode("#004aad"));
		g2.fillOval(232, 0, 1048, 274);
		g2.fillOval(0, 147, 594, 152);
		
		g2.setTransform(at);
	}
	
	public static void drawMan(Graphics2D g2, int x, int y, double scale) {
	    AffineTransform old = g2.getTransform();

	    g2.translate(x, y);
	    g2.scale(scale, scale);

	    // SHADOW
	    g2.setColor(new Color(0, 0, 0, 100));
	    g2.fillOval(0, 605, 365, 154);

        //DITO ARMS

	    //BODY
	    g2.setColor(Color.decode("#000000"));
	    g2.fillRoundRect(90, 280, 213, 292, 20, 20);
	    
	    //WHITE SHIRT
	    g2.setColor(Color.decode("#ffffff"));
	    g2.fillRect(145, 355, 105, 176);
	    
	    //TIE
	    g2.setColor(Color.decode("#8f2b36"));
	    g2.fillRoundRect(175, 335, 44, 176, 50, 50);
	    
	    //HEAD
	    g2.setColor(Color.decode("#44312b"));
	    g2.fillOval(55, 130, 280, 280);
	    
	    //LEFT LEG
	    g2.setColor(Color.decode("#000000"));
	    g2.fillRect(125, 565, 32, 132);
	    
	    //RIGHT LEG
	    g2.fillRect(235, 565, 32, 132);
	    
	    //LEFT EYE
	    g2.fillOval(105, 205, 32, 77);
	    
	    //RIGHT EYE
	    g2.fillOval(165, 205, 32, 77);
	    
	    //HAT BRIM (DOWN)
	    g2.fillRect(55, 150, 282, 40);
	    
	    //HAT SQUARE (UP)
	    g2.fillRect(115, -20, 161, 176);
	    
	    // restore EVERYTHING at the end
	    g2.setTransform(old);
	}
	
	public static void drawOstrich(Graphics2D g2, int x, int y, double scale) {
		AffineTransform old = g2.getTransform();

		g2.translate(x,y);
		g2.scale(scale, scale);
		//SHADOW

		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillOval(60, 570, 455, 192);
		
		g2.setColor(Color.decode("#bb9d96"));
		g2.fillRoundRect(220, 400, 45, 268, 35, 35);

		g2.setColor(Color.decode("#bb9d96"));
		g2.fillRoundRect(360, 400 , 45, 268, 35, 35);

		// BODY1
		g2.setColor(Color.decode("#231a18"));
		g2.fillOval(106, 206, 385, 227);
		
		//NECK
		g2.setColor(Color.decode("#bb9d96"));
		g2.fillRoundRect(105, 18, 45, 340, 35, 35);

		// BEAK
		g2.setColor(Color.decode("#8c6812"));
		Polygon beak = new Polygon();
		beak.addPoint(40, 28);   // tip
		beak.addPoint(62, 20);   // top of base, against head
		beak.addPoint(62, 36);   // bottom of base, against head
		g2.fillPolygon(beak);

		// HEAD
		g2.setColor(Color.decode("#bb9d96"));
		g2.fillOval(56, 0, 95, 56);

		// EYE (black)
		g2.setColor(Color.decode("#000000"));
		g2.fillOval(88, 10, 41, 29);

		// NECK
		g2.setColor(Color.decode("#231a18"));
		g2.fillRoundRect(106, 206, 45, 154, 35, 35);

		// FEET
		g2.setColor(Color.decode("#776561"));
		g2.fillOval(192, 660, 70, 22);

		g2.setColor(Color.decode("#776561"));
		g2.fillOval(331, 660, 70, 22);

		// LEG WHITE LEFT
		g2.setColor(Color.decode("#ffffff"));
		g2.fillRect(221, 418, 45, 102);

		// LEG WHITE RIGHT
		g2.setColor(Color.decode("#ffffff"));
		g2.fillRect(360, 418, 45, 102);

		// BODY 2
		g2.setColor(Color.decode("#1a1312"));
		g2.fillOval(129, 243, 336, 198);

		// NECK WHITE
		g2.setColor(Color.decode("#ffffff"));
		g2.fillRect(106, 196, 45, 32);

		// EYE WHITE
		g2.setColor(Color.decode("#ffffff"));
		g2.fillOval(87, 15, 19, 19);

		// RESET TRANSFORM
		g2.setTransform(old);	  
	}
	
	public static void drawOstrichFacing(Graphics2D g2, int x, int y, double scale,
			int hitW, boolean facingRight) {
		if (!facingRight) {
			drawOstrich(g2, x, y, scale);          // default = facing left
			return;
		}
		AffineTransform old = g2.getTransform();
		g2.translate(x + hitW, y);                 // pivot on the right edge of its hitbox
		g2.scale(-1, 1);                           // mirror horizontally
		drawOstrich(g2, 0, 0, scale);
		g2.setTransform(old);
	}

}
