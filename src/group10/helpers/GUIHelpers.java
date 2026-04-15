package group10.helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class GUIHelpers {
	
	// line spacing basically
	public static void adjustKerning(Graphics2D g2, double spacing) {
		Map<TextAttribute, Object> attributes = new HashMap<>();
	    attributes.put(TextAttribute.TRACKING, spacing);
	    // ilagay sa attributes variable yung TRACKING ng line space as the key
	    // at yung value ay parameterized
	    Font fontWithKerning = g2.getFont().deriveFont(attributes);
	    g2.setFont(fontWithKerning);
	}
	
	// added helper method for stylized text creation
	public static void drawUIText (Graphics2D g2, String text, Color color,
			int fontSize, double spacing, boolean shadow, int x, int y) {
		
		g2.setFont(new Font("Century Gothic", Font.BOLD, fontSize)); 
		adjustKerning(g2, spacing);
		
		if (shadow == true) {
			g2.setColor(new Color(0, 0, 0, 50));	// drop shadow color
			g2.drawString(text, x + (fontSize / 12), y + (fontSize / 12));	// drop
		}
		
		g2.setColor(color);
		g2.drawString(text, x, y);	// main
		
	}
	
	// unused pa
	public void drawTriangle(Graphics2D g2, int x, int y, int width, int height) {
	    int[] xPoints = {x, x + (width / 2), x + width};   // Left, Middle, Right
	    int[] yPoints = {y + height, y, y + height};       // Bottom, Top, Bottom

	    Polygon triangle = new Polygon(xPoints, yPoints, 3);
	    
	    // Fill it
	    g2.setColor(Color.BLUE);
	    g2.fill(triangle);
	    
	    // Outline it
	    g2.setColor(Color.BLACK);
	    g2.draw(triangle);
	}
}
