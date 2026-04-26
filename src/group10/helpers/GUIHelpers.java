package group10.helpers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class GUIHelpers {
	// text line spacing basically
	public static void adjustKerning(Graphics2D g2, double spacing) {
		Map<TextAttribute, Object> attributes = new HashMap<>();
	    attributes.put(TextAttribute.TRACKING, spacing);
	    // ilagay sa attributes variable yung TRACKING ng line space as the key
	    // at yung value ay parameterized
	    Font fontWithKerning = g2.getFont().deriveFont(attributes);
	    g2.setFont(fontWithKerning);
	}
	
	// already stylized; ts is just bold lol
	// FIXME: there is something wrong with absolute positioning text with these helper methods tf
	public static void drawHeadingText(Graphics2D g2, String text, Color color,
		int fontSize, int x, int y) {
		
		g2.setFont(new Font("Century Gothic", Font.BOLD, fontSize)); 
		adjustKerning(g2, -0.05);
		
		g2.setColor(new Color(0, 0, 0, 50));	// drop shadow color
		g2.drawString(text, x + (fontSize / 12), y + (fontSize / 12));	// drop
		
		g2.setColor(color);
		g2.drawString(text, x, y);	// main
			
		}
	
	public static void drawBoldText(Graphics2D g2, String text, Color color,
			int fontSize, int x, int y) {
			
			g2.setFont(new Font("Century Gothic", Font.BOLD, fontSize)); 
			adjustKerning(g2, -0.05);
			
			g2.setColor(color);
			g2.drawString(text, x, y);	// main
				
			}
	
	// PLAIN
	public static void drawParagraphText(Graphics2D g2, String text, Color color,
		int fontSize, int x, int y) {
			
		g2.setFont(new Font("Century Gothic", Font.PLAIN, fontSize)); 
		adjustKerning(g2, -0.05);
			
		g2.setColor(new Color(0, 0, 0, 50));	// drop shadow color
		g2.drawString(text, x + (fontSize / 12), y + (fontSize / 12));	// drop
			
		g2.setColor(color);
		g2.drawString(text, x, y);	// main
			
	}
	
	// no drop shadow, no corner rounding
	public static void drawButton(Graphics2D g2, Rectangle r, String text, String mainColor, String strokeColor, String fontColor, int strokeWeight, int fontSize) {
		
		// drop shadow TODO: fix drop shadow scaling with size
		g2.setColor(new Color(0, 0, 0, 40));
		g2.fillRect(r.x + 5, r.y + 5, r.width, r.height);
		
		// main button
		g2.setColor(Color.decode(mainColor));
		g2.fillRect(r.x, r.y, r.width, r.height);
		
		// button stroke
		g2.setStroke(new BasicStroke(strokeWeight));
		g2.setColor(Color.decode(strokeColor));
		// adds and subtract strokeweight value to mimic inside stroking instead of outside
		g2.drawRect(r.x + (strokeWeight / 2), r.y + (strokeWeight / 2), r.width - strokeWeight, r.height - strokeWeight);
		
		// font & text
		g2.setColor(Color.decode(fontColor));
		g2.setFont(new Font("Century Gothic", Font.PLAIN, fontSize));
		adjustKerning(g2, -0.1);
		FontMetrics fm = g2.getFontMetrics();
		int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
	    int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
		g2.drawString(text, tx, ty);
	}
	
	public static void drawColorButton(Graphics2D g2, Rectangle r, ColorPalette palette) {
		
		// drop shadow TODO: fix drop shadow scaling with size
		g2.setColor(new Color(0, 0, 0, 40));
		g2.fillRect(r.x + 5, r.y + 5, r.width, r.height);
		
		// main button
		g2.setColor(Color.decode(palette.getHexCode()));
		g2.fillRect(r.x, r.y, r.width, r.height);
		
		// button stroke
		g2.setStroke(new BasicStroke(6));
		g2.setColor(Color.decode("#FFFFFF"));
		// adds and subtract strokeweight value to mimic inside stroking instead of outside
		g2.drawRect(r.x + (5 / 2), r.y + (6 / 2), r.width - 6, r.height - 6);
	}
	
	public static void drawWindow(Graphics2D g2, Rectangle r, String mainColor, String strokeColor, int strokeWeight) {
		g2.setColor(Color.decode(mainColor));
		g2.fillRect(r.x, r.y, r.width, r.height);
		
		// button stroke
		g2.setStroke(new BasicStroke(strokeWeight));
		g2.setColor(Color.decode(strokeColor));
		g2.drawRect(r.x + (strokeWeight / 2), r.y + (strokeWeight / 2), r.width - strokeWeight, r.height - strokeWeight);
	}
	
	public static void drawPrimaryButton(Graphics2D g2, Rectangle r, String text, int fontSize, String hexColor) {
		// drop shadow
		g2.setColor(new Color(0, 0, 0, 40));
		g2.fillRoundRect(r.x + 5, r.y + 5, r.width, r.height, 40, 40);
		
		// yung actual na button
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.decode(hexColor));
		g2.fillRoundRect(r.x, r.y, r.width, r.height, 40, 40);
		
		// stroke
		g2.setColor(Color.decode("#353535"));
		g2.drawRoundRect(r.x, r.y, r.width, r.height, 40, 40);
		
		// font & text
		g2.setColor(Color.decode("#353535"));
		g2.setFont(new Font("Century Gothic", Font.PLAIN, fontSize));
		adjustKerning(g2, -0.1);
		FontMetrics fm = g2.getFontMetrics();
		int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
	    int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
		g2.drawString(text, tx, ty);
	}
	
	
}