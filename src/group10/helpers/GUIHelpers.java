package group10.helpers;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GUIHelpers {
	
	public static void drawTransparentImage(Graphics2D g2, BufferedImage img, int x, int y, int w, int h, float alpha) {
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	    g2.drawImage(img, x, y, w, h, null);
	    g2.setComposite(AlphaComposite.SrcOver); // Resets it back to solid
	}
	
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
	
	public static void drawWindow(Graphics2D g2, Rectangle r, String mainColor, String strokeColor, int strokeWeight) {
		g2.setColor(Color.decode(mainColor));
		g2.fillRect(r.x, r.y, r.width, r.height);
		
		// button stroke
		g2.setStroke(new BasicStroke(strokeWeight));
		g2.setColor(Color.decode(strokeColor));
		g2.drawRect(r.x + (strokeWeight / 2), r.y + (strokeWeight / 2), r.width - strokeWeight, r.height - strokeWeight);
	}
	
	public static void drawMenuButton(Graphics2D g2, Rectangle r, String text, String hexColor) {
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
		g2.setFont(new Font("Century Gothic", Font.PLAIN, 40));
		adjustKerning(g2, -0.1);
		FontMetrics fm = g2.getFontMetrics();
		int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
	    int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
		g2.drawString(text, tx, ty);
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
	
	public static void drawSingleTree(Graphics2D g2, double boxWidth, double boxHeight, int x, int y, boolean isDay) {
	    
		Color leafColor = isDay ? Color.decode("#8cb348") : Color.decode("#475b24");
	    Color shadowColor = isDay ? Color.decode("#4d7e13") : Color.decode("#131e06");
	    Color trunkColor = isDay ? Color.decode("#2b1a12") : Color.decode("#100a07");
	    
		// original bounding box (width: 220, height: 280)
	    double leavesX = x + (boxWidth * 0.01);	// 
	    double leavesY = y + (boxHeight * 0.05);	// 
	    double leavesWidth = (boxWidth * 0.91);	// 200/220
	    double leavesHeight = (boxHeight * 0.71);	// 200/280
	    double leavesXShadow = x - (boxWidth * 0.05);
	    
	    // eto yung shadow duh
	    g2.setColor(shadowColor);
	    g2.fill(new Ellipse2D.Double(leavesXShadow, leavesY, leavesWidth, leavesHeight));
	    
	    g2.setColor(leafColor);
	    g2.fill(new Ellipse2D.Double(leavesX, leavesY, leavesWidth, leavesHeight));
	    
	    g2.setColor(trunkColor);
	    
	    // original triangle bounding box (w: 60, h: 160, x: 80, y: 120)
	    double trunkBBX = x + (boxWidth * 0.36);  // 80/220
	    double trunkBBY = y + (boxHeight * 0.42); // 120/280
	    double trunkBBW = boxWidth * 0.27;       // 60/220
	    double trunkBBH = boxHeight * 0.57;      // 160/280
	    
	    g2.setColor(Color.decode("#2b1a12"));
	    Path2D.Double trunk = new Path2D.Double();
	    
	    double trunkV1X = trunkBBX + (trunkBBW * 0.50);
	    double trunkV1Y = trunkBBY;
	    double trunkV2X = trunkBBX;
	    double trunkV2Y = trunkBBY + trunkBBH;
	    double trunkV3X = trunkBBX + trunkBBW;
	    double trunkV3Y = trunkBBY + trunkBBH;
	    
	    trunk.moveTo(trunkV1X, trunkV1Y);
	    trunk.lineTo(trunkV2X, trunkV2Y);
	    trunk.lineTo(trunkV3X, trunkV3Y);
	    trunk.closePath();
	    
	    g2.fill(trunk);
	}
	
	public static void drawLayeredTree(Graphics2D g2, double width, double height, int x, int y, boolean isDay) {
        
		Color leafColor = isDay ? Color.decode("#8cb348") : Color.decode("#475b24");
	    Color shadowColor = isDay ? Color.decode("#4d7e13") : Color.decode("#131e06");
	    Color trunkColor = isDay ? Color.decode("#2b1a12") : Color.decode("#100a07");
		
	    // bottom sirkol
        double botW = width * 0.85;
        double botH = height * 0.45;
        double botX = x + (width - botW) / 2.0; 
        double botY = y + (height * 0.40);
        double botXShadow = x + (width - botW * 1.15) / 2.0; 

        // middle sirkol
        double midW = width * 0.75;
        double midH = height * 0.40;
        double midX = x + (width - midW) / 2.0; 
        double midY = y + (height * 0.20);
        double midXShadow = x + (width - midW * 1.15) / 2.0; 

        // top sirkol
        double topW = width * 0.60;
        double topH = height * 0.35;
        double topX = x + (width - topW) / 2.0; 
        double topY = y;
        double topXShadow = x + (width - topW * 1.15) / 2.0; 
        
        g2.setColor(shadowColor);
        g2.fill(new Ellipse2D.Double(botXShadow, botY, botW, botH));
        g2.fill(new Ellipse2D.Double(midXShadow, midY, midW, midH));
        g2.fill(new Ellipse2D.Double(topXShadow, topY, topW, topH));
        
        g2.setColor(leafColor);
        g2.fill(new Ellipse2D.Double(botX, botY, botW, botH));
        g2.fill(new Ellipse2D.Double(midX, midY, midW, midH));
        g2.fill(new Ellipse2D.Double(topX, topY, topW, topH));
        
        g2.setColor(trunkColor);
	    
	    // original triangle bounding box (w: 60, h: 160, x: 80, y: 120)
	    double trunkBBX = x + (width * 0.36);  // 80/220
	    double trunkBBY = y + (height * 0.42); // 120/280
	    double trunkBBW = width * 0.27;       // 60/220
	    double trunkBBH = height * 0.57;      // 160/280
	    
	    g2.setColor(Color.decode("#2b1a12"));
	    Path2D.Double trunk = new Path2D.Double();
	    
	    double trunkV1X = trunkBBX + (trunkBBW * 0.50);
	    double trunkV1Y = trunkBBY;
	    double trunkV2X = trunkBBX;
	    double trunkV2Y = trunkBBY + trunkBBH;
	    double trunkV3X = trunkBBX + trunkBBW;
	    double trunkV3Y = trunkBBY + trunkBBH;
	    
	    trunk.moveTo(trunkV1X, trunkV1Y);
	    trunk.lineTo(trunkV2X, trunkV2Y);
	    trunk.lineTo(trunkV3X, trunkV3Y);
	    trunk.closePath();
	    
	    g2.fill(trunk);
    }
}
