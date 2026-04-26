package group10.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import group10.helpers.GameSettings;

public class CarModels {
	
	// method for darkening hex value colors
	public static String darken(String hex) {
	    Color c = Color.decode(hex).darker();
	    // since hex value are hex, its literally rgb pero pa-hexadecimal yung value
	    // 00 is the same as 0 sa rgb value, FF is 255 sa rgb value, every two characters
	    // corresponds to R,G,B, so #FFFFFF is 255, 255, 255. gets? gets!!!!!!!!
	    
	    // dito nai-return yung newly formatted rgb values into stringed hex value ulet
	    // kasi makulet hehehehehe
	    return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
	}
	
	// MCQUEEN
	public static void drawCarMcQueen(Graphics2D g2, int x, int y, double scale, boolean isPlayer1) {
		
		String mainColor = GameSettings.selectedCarColorP1;
		String shadedColor = darken(GameSettings.selectedCarColorP1);
		
		if (isPlayer1) {
			mainColor = GameSettings.selectedCarColorP1;
			shadedColor = darken(GameSettings.selectedCarColorP1);
		} else {
			mainColor = GameSettings.selectedCarColorP2;
			shadedColor = darken(GameSettings.selectedCarColorP2);
		}
		
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
	    g2.scale(scale, scale);
	    
		g2.setColor(Color.decode(mainColor));
		g2.fillOval(0, 403, 86, 357);
		g2.fillOval(267, 403, 86, 357);
		
		//BODY
		g2.setColor(Color.decode(shadedColor));
		g2.fillRoundRect(26, 0, 304, 743,100,100);
		g2.setColor(Color.decode(mainColor));
		g2.fillRoundRect(45, 0, 265, 743, 75, 75);
				
		//rearSpoiler
		Polygon spoiler = new Polygon();
		
		spoiler.addPoint(32 + 30, 719);
		spoiler.addPoint(32 + 291 - 30, 719);
		spoiler.addPoint(32 + 291, 719 + 41);
		spoiler.addPoint(32, 719 + 41);
		
		g2.setColor(Color.decode(mainColor));
		g2.fillPolygon(spoiler);
		
		
		//sideWindow
		g2.setColor(Color.decode("#828d90"));
		g2.fillRoundRect(52, 261, 50, 204,68,68);
		g2.fillRoundRect(253, 261, 50, 204,68,68);
		
		//top
		g2.setColor(Color.decode(mainColor));
		g2.fillRoundRect(65, 182, 226, 479,45,45);
		g2.setColor(Color.decode(shadedColor));
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(65, 182, 226, 479,45,45);
		
		//backWindow
		g2.setColor(Color.decode("#828d90"));
		g2.fillRoundRect(65, 479, 50, 180,10,10);
		g2.fillRoundRect(128, 479, 41, 180,10,10);
		g2.fillRoundRect(187, 479, 41, 180,10,10);
		g2.fillRoundRect(241, 479, 50, 180,10,10);
		
		//front
		g2.setColor(Color.decode(shadedColor));
		g2.fillOval(103, 27, 155, 155);
		
		//frontWindow
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(52, 160, 252, 87, 10, 10);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(52, 160, 252, 87, 10, 10);
		
		//eyes
		g2.setColor(Color.decode("#004aad"));
		g2.fillOval(103, 182, 43, 43);
		g2.fillOval(209, 182, 43, 43);
		
		g2.setColor(Color.decode("000000"));
		g2.fillOval(112, 190, 26, 26);
		g2.fillOval(218, 190, 26, 26);
		
		//headLight
		AffineTransform at2 = g2.getTransform();
		
		g2.rotate(Math.toRadians(45), 50 + 45/2, 0 + 64/2);
		g2.setColor(Color.decode("#ffbf28"));
		g2.fillOval(50, 0, 45, 64);
		g2.setTransform(at2);
		
		g2.rotate(Math.toRadians(315), 252 + 45/2, 0 + 64/2);
		g2.fillOval(252, 0, 45, 64);
		g2.setTransform(at2);
		
		g2.setTransform(at);
	}
}
