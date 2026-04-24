package group10.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class BackgroundElements {

	// LEVEL 1
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
	
	// LEVEL 2
	public static void drawHotel(Graphics2D g2, int x, int y, double scale) {
		
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setColor(Color.decode("#835851"));
		g2.fillRect(28, 0, 380, 760);

		//top
		g2.setColor(Color.decode("#372219"));
		g2.fillRect(30, 0, 411, 43);

		//bottom
		g2.setColor(Color.decode("#353535"));
		g2.fillRect(0, 681, 408, 79);

		//door
		g2.setColor(Color.decode("#121213"));
		g2.fillRect(163, 620, 110, 140);

		//windowBg
		g2.setColor(Color.decode("#3a6582"));

		int startX = 72;
		int startY = 96;
		int width = 63;
		int height = 120;
		int spacing = 115;

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				int currentX = startX + (col * spacing);
				int currentY = startY + (row * spacing);
				g2.fillRect(currentX, currentY, width, height);
			}
		}

		//windowOutline
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(5));

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				int currentX = startX + (col * spacing);
				int currentY = startY + (row * spacing);
				g2.drawRect(currentX, currentY, width, height);
			}
		}
		
		g2.setTransform(at);
	}
	
	public static void drawShop(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	public static void drawSheriffStation(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	public static void drawLamppost(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	// LEVEL 3
	
	public static void drawSailboat(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	public static void drawYacht(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	public static void drawCargoShip(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	// LEVEL 4
	public static void drawSavannahTree(Graphics2D g2, int x, int y, double scale) {
		
		AffineTransform oldTransform = g2.getTransform();

	    g2.translate(x, y);
	    g2.scale(scale, scale);
	    
		Polygon trunk = new Polygon();
		
		trunk.addPoint(418 + 240/2, 75); 
		trunk.addPoint(418 + 240, 75 + 685); 
		trunk.addPoint(418, 75 + 685); 
		
		g2.setColor(Color.decode("#2b1a12"));
		g2.fillPolygon(trunk);
		
		//leaves
		g2.setColor(Color.decode("#89823a"));
		g2.fillOval(0, 0, 965, 125);
		g2.setColor(Color.decode("#383b18"));
		g2.fillOval(176, 64, 965, 125);
		
		g2.setTransform(oldTransform);
	}
	
	public static void drawBushesA(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	public static void drawBushesB(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	// LEVEL 5
	public static void drawStar(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
}
