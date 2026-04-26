package group10.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Random;

public class BackgroundElements {
	// LEVEL 1
	public static void drawSingleTree(Graphics2D g2, int x, int y, double scale) {
		
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		//shade
		g2.setColor(Color.decode("#4d7e13"));
		g2.fillOval(0, 0, 505, 505);
		//leaves
		g2.setColor(Color.decode("#8cb348"));
		g2.fillOval(42, 0, 505, 505);

		//trunks
		Polygon trunks = new Polygon();
		trunks.addPoint(218 + 154/2, 321); 
		trunks.addPoint(218 + 154, 321 + 439); 
		trunks.addPoint(218, 321 + 439);

		g2.setColor(Color.decode("#2b1a12"));
		g2.fillPolygon(trunks);

		//2nd trunk
		AffineTransform old = g2.getTransform();
		Polygon trunk = new Polygon();

		trunk.addPoint(334 + 87/2, 342); 
		trunk.addPoint(334 + 87, 342 + 166); 
		trunk.addPoint(334, 342 + 166);

		g2.rotate(Math.toRadians(60), 334 + 87/2, 342 + 166/2);
		g2.fillPolygon(trunk);
		g2.setTransform(old);
		
		g2.setTransform(at);
	}
	
	public static void drawLayeredTree(Graphics2D g2, int x, int y, double scale) {
		
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		//shade
		g2.setColor(Color.decode("#4d7e13"));
		g2.fillOval(0, 290, 357, 357);
		g2.fillOval(22, 134, 312, 312);
		g2.fillOval(45, 0, 268, 268);
		//leaves
		g2.setColor(Color.decode("#8cb348"));
		g2.fillOval(30, 290, 357, 357);
		g2.fillOval(52, 134, 312, 312);
		g2.fillOval(75, 0, 268, 268);

		//big trunk
		Polygon trunks = new Polygon();
		trunks.addPoint(147 + 116/2, 429); 
		trunks.addPoint(147 + 116, 429 + 331); 
		trunks.addPoint(147, 429 + 331);

		g2.setColor(Color.decode("#2b1a12"));
		g2.fillPolygon(trunks);

		//small trunk
		AffineTransform old = g2.getTransform();
		Polygon trunk = new Polygon();

		trunk.addPoint(232 + 65/2, 444); 
		trunk.addPoint(232 + 65, 444 + 125); 
		trunk.addPoint(232, 444 + 125);

		g2.rotate(Math.toRadians(60), 232 + 65/2, 444 + 125/2);
		g2.fillPolygon(trunk);
		g2.setTransform(old);
		
		g2.setTransform(at);
	}
	
	public static void drawCabin(Graphics2D g2, int x, int y, double scale) {
		
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		//triangle outer
		Polygon tri = new Polygon();
		tri.addPoint(0 + 616/2, 180);
		tri.addPoint(0 + 616, 180 + 499);
		tri.addPoint(0, 180 + 499);

		g2.setColor(Color.decode("#624500"));
		g2.fillPolygon(tri);

		//triangle inner
		Polygon tria = new Polygon();
		tria.addPoint(67 + 493/2, 238);
		tria.addPoint(67 + 493, 238 + 394);
		tria.addPoint(67, 238 + 394);

		g2.setColor(Color.decode("#ffde59"));
		g2.fillPolygon(tria);


		//door
		g2.setColor(Color.decode("#9d6f00"));
		g2.fillRect(267, 435, 81, 155);

		//floor
		g2.setColor(Color.decode("#372219"));
		g2.fillRect(38, 617, 1064, 72);

		//foundation
		int f[] = {37, 293, 548, 810, 1073};
		for (int i = 0; i < f.length; i++) {
			g2.fillRect(f[i], 688, 30, 72);
		}

		//roof
		Polygon roof = new Polygon();

		roof.addPoint(286, 180);
		roof.addPoint(610, 180);
		roof.addPoint(286 + 915, 180 + 499);
		roof.addPoint(286 + 286, 180 + 499);

		g2.setColor(Color.decode("#624500"));
		g2.fillPolygon(roof);

		g2.setColor(Color.decode("#424348"));
		g2.fillRoundRect(519, 0, 57, 359, 51, 51);

		g2.setTransform(at);
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
		
		g2.setColor(Color.decode("#e4e4e4"));
		g2.fillRect(177, 79, 928, 681);
		
		//top
		g2.setColor(Color.decode("#d7d3d3"));
		g2.fillRect(155, 57, 971, 45);
		
		//windows
		g2.setColor(Color.decode("#004aad"));
		g2.fillRect(213, 451, 141, 154);
		g2.fillRect(911, 451, 147, 154);
		
		g2.setColor(Color.decode("#5170ff"));
		g2.fillRect(386, 451, 147, 154);
		g2.fillRect(738, 451, 141, 154);
		
		//bottom
		g2.setColor(Color.decode("#835851"));
		g2.fillRect(152, 629, 971, 131);
		//door
		g2.setColor(Color.decode("#080229"));
		g2.fillRect(561, 490, 147, 269);
		
		Polygon awning2 = new Polygon();
		awning2.addPoint(0 + 128, 257);
		awning2.addPoint(0 + 1280 - 128, 257);
		awning2.addPoint(0 + 1280, 257 + 194);
		awning2.addPoint(0, 257 + 194);
		
		g2.setColor(Color.decode("#919191"));
		g2.fillPolygon(awning2);
		
		g2.setTransform(at);
	}
	
	public static void drawSheriffStation(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		//wholeBuilding
		g2.setColor(Color.decode("#6b695c"));
		g2.fillRect(164, 28, 744, 732);

		//top
		g2.setColor(Color.decode("#ffeea7"));
		g2.fillRect(137, 0, 798, 56);

		//shades
		g2.setColor(Color.decode("#7d7b6a"));
		g2.fillRect(169, 107, 372, 88);
		g2.setColor(Color.decode("#9f9c89"));
		g2.fillRect(535, 205, 372, 88);

		//windowsBg
		g2.setColor(Color.decode("#343837"));
		g2.fillRect(193, 487, 361, 200);
		g2.fillRect(752, 487, 114, 200);

		//windowsOutline
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(5));
		g2.drawRect(193, 487, 361, 200);
		g2.drawRect(752, 487, 114, 200);

		//door
		g2.setColor(Color.decode("#343837"));
		g2.fillRect(596, 487, 114, 272);

		//awningRoof(Top)
		Polygon awning = new Polygon();
		awning.addPoint(0 + 162, 344);
		awning.addPoint(0 + 1050 - 148, 344);
		awning.addPoint(0 + 1050, 344 + 90);
		awning.addPoint(0, 344 + 90);

		g2.setColor(Color.decode("#9e9165"));
		g2.fillPolygon(awning);

		//awningRoof(bottom)
		g2.setColor(Color.decode("#6d5f44"));
		g2.fillRoundRect(4, 399, 1043, 111, 30, 30);

		g2.setTransform(at);
	}
	
	public static void drawLamppost(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setTransform(at);
	}
	
	// LEVEL 3
	
	public static void drawYacht(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		//whiteUnder
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(17, 157, 233, 552, 100, 100);
		
		//front white use rotate
		AffineTransform old = g2.getTransform();
		
		g2.rotate(Math.toRadians(30), 54 + 50/2, -3 + 233/2);
		g2.fillOval(54, -3, 50, 233);
		g2.setTransform(old);
		
		g2.rotate(Math.toRadians(-30), 163 + 50/2, -3 + 233/2);
		g2.fillOval(163, -3, 50, 233);
		g2.setTransform(old);
		
		//brown base
		g2.setColor(Color.decode("#c88c54"));
		g2.fillRoundRect(36, 183, 196, 530, 100, 100);
		
		Polygon tri = new Polygon();
		tri.addPoint(57 + 151/2, 64);
		tri.addPoint(57 + 151, 64 + 148);
		tri.addPoint(57, 64 + 148);
		
		g2.fillPolygon(tri);
		
		//rotated brown base
		g2.rotate(Math.toRadians(30), 78 + 28/2, 33 + 226/2);
		g2.fillOval(79, 33, 28, 226);
		g2.setTransform(old);
		
		g2.rotate(Math.toRadians(-30), 160 + 28/2, 33 + 226/2);
		g2.fillOval(160, 33, 28, 226);
		g2.setTransform(old);
		
		//gitna
		g2.setColor(Color.decode("#031b32"));
		g2.fillRoundRect(58, 332, 150, 226, 15, 15);
		
		g2.setColor(new Color(255, 255, 255, 100));
		g2.fillRoundRect(58, 317, 150, 81, 40, 40);
		
		g2.setColor(Color.decode("#ffffff"));
		g2.fillRoundRect(57, 291, 150, 81, 30, 30);
		
		//back part
		g2.setColor(Color.decode("#826d68"));
		g2.fillRect(80, 587, 109, 72);
		
		g2.setColor(Color.decode("#6d1f05"));
		g2.fillRoundRect(17, 640, 233, 120, 40, 40);
		
		g2.setColor(Color.decode("#e0eaee"));
		g2.setStroke(new BasicStroke(10));
		g2.drawRoundRect(17, 640, 233, 120, 40, 40);
		
		//square in front
		g2.setColor(Color.decode("#031b32"));
		g2.fillRect(116, 44, 34, 34);
		
		g2.setTransform(at);
	}

	public static void drawSailboat(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setColor(Color.decode("#6d5f44"));
		//ovals
		g2.fillOval(0,139, 105, 486);
		g2.fillOval(105, 139, 105, 486);

		g2.setColor(Color.decode("#554a33"));
		g2.setStroke(new BasicStroke(10));

		g2.drawOval(0,139, 105, 486);
		g2.drawOval(105, 139, 105, 486);

		//triangles
		Polygon tri1 = new Polygon();
		tri1.addPoint(3 + 207/2, 0);
		tri1.addPoint(3 + 207, 0 + 212);
		tri1.addPoint(3, 0 + 212);

		g2.setColor(Color.decode("#6d5f44"));
		g2.fillPolygon(tri1);

		g2.setColor(Color.decode("#554a33"));
		g2.setStroke(new BasicStroke(10));
		g2.drawPolygon(tri1);

		Polygon tri2 = new Polygon();
		tri2.addPoint(0, 548);
		tri2.addPoint(0 + 207, 548);
		tri2.addPoint(0 + 207/2, 548 + 212);

		g2.setColor(Color.decode("#6d5f44"));
		g2.fillPolygon(tri2);

		g2.setColor(Color.decode("#554a33"));
		g2.setStroke(new BasicStroke(10));
		g2.drawPolygon(tri2);

		//base
		g2.setColor(Color.decode("#6d5f44"));
		g2.fillRect(45, 156, 123, 434);

		//seat
		g2.setColor(Color.decode("#554a33"));
		g2.fillRect(19, 271, 171, 65);
		g2.fillRect(19, 424, 171, 65);

		g2.setTransform(at);
	}
	
	public static void drawCargoShip(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		//base
		g2.setColor(Color.decode("#8f2b36"));
		g2.fillRoundRect(80, 167, 250, 591, 100, 100);
		
		g2.setColor(Color.decode("#3a6582"));
		g2.setStroke(new BasicStroke(8));
		g2.drawRoundRect(80, 167, 250, 591, 100, 100);
		
		//triangle
		Polygon tri = new Polygon();
		tri.addPoint(114 + 183/2, 35);
		tri.addPoint(114 + 183, 35 + 166);
		tri.addPoint(114, 35 + 166);

		g2.setColor(Color.decode("#66252c"));
		g2.fillPolygon(tri);
		
		//top edge
		AffineTransform old = g2.getTransform();
		g2.rotate(Math.toRadians(30), 120 + 54/2, -3 + 250/2);
		
		g2.setColor(Color.decode("#8f2b36"));
		g2.fillOval(120, -3, 54, 250);
		g2.setTransform(old);
		
		g2.rotate(Math.toRadians(-30), 237 + 54/2, -3 + 250/2);
		g2.fillOval(237, -3, 54, 250);
		g2.setTransform(old);
		
		//pakpak
		Polygon tria = new Polygon();
		
		tria.addPoint(0 + 410/2, 161);
		tria.addPoint(0 + 410, 161 + 73);
		tria.addPoint(0, 161 + 73);
		
		g2.setColor(Color.decode("#3a6582"));
		g2.fillPolygon(tria);
		
		//shadows
		g2.setColor(new Color(0, 0, 0, 55));
		g2.fillRect(133, 178, 160, 87);
		g2.fillRect(81, 306, 250, 190);
		g2.fillRect(81, 527, 250, 190);
		
		String[] A = {"#ec4f45", "#1f6d5c", "#19a1d0"};
		int[] B = {126, 179, 232};
		for (int i = 0; i < A.length; i++) {
			g2.setColor(Color.decode(A[i]));
			g2.fillRect(B[i], 169, 53, 89);
		}
		
		//cargo
		Random rand = new Random(67);	
		String Colors[] = {"#ec4f45", "#1f6d5c", "#19a1d0", "#ffbf28", };
		int[] cx = {80, 126, 179, 232, 285};
		int[] cy = {307, 397, 527, 618};
		
		int h = 89;
		int w = 53;
		
		for (int row = 0; row < cy.length; row++ ) {
			for(int col = 0; col < cx.length; col ++) {
				String hex = Colors[rand.nextInt(Colors.length)];
				g2.setColor(Color.decode(hex));
				
				g2.fillRoundRect(cx[col], cy[row], w, h, 6, 6);
			}
		}
				
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
		
		g2.setColor(Color.decode("#89823a"));
		g2.fillOval(0, 105, 732, 334);

		g2.setColor(Color.decode("#6d5f44"));
		g2.fillOval(451, 74, 653, 198);

		g2.setColor(Color.decode("#475b24"));
		g2.fillOval(980, 0, 300, 198);
		
		g2.setTransform(at);
	}
	
	public static void drawBushesB(Graphics2D g2, int x, int y, double scale) {
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);
		
		g2.setColor(Color.decode("#383b18"));
		g2.fillOval(0, 116, 255, 168);

		g2.setColor(Color.decode("#6d5f44"));
		g2.fillOval(191, 0, 623, 285);

		g2.setColor(Color.decode("#624500"));
		g2.fillOval(724, 124, 556, 168);
		
		g2.setTransform(at);
	}
}
