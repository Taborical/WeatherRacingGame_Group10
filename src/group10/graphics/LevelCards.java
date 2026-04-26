	package group10.graphics;
	
	import static group10.graphics.BackgroundElements.*;
	import static group10.graphics.Obstacles.*;
	import static group10.helpers.GUIHelpers.*;
	import java.awt.BasicStroke;
	import java.awt.Color;
	import java.awt.Graphics2D;
	import java.awt.Rectangle;
	
	import group10.helpers.GameSettings;
	
	public class LevelCards {
		public final static Rectangle proceed = new Rectangle(225, 510, 170, 60);
		public final static Rectangle nevermind = new Rectangle(410, 510, 170, 60);
		
		public static void drawCardWindow(Graphics2D g2) {
			
			String mainColor = "#426c10";
			String shadedColor = "#314e0e";
			
			switch (GameSettings.projectedlevelSelection) {
			case 1: mainColor = "#426c10"; shadedColor = "#314e0e"; break;
			case 2: mainColor = "#424348"; shadedColor = "#353535"; break;
			case 3:	mainColor = "#157596"; shadedColor = "#11576f"; break;
			case 4: mainColor = "#6d5f44"; shadedColor = "#554b36"; break;
			}
			
			g2.setColor(Color.decode(mainColor));
			g2.fillRect(175, 130, 925, 500);
			
			g2.setColor(Color.decode(shadedColor));
			g2.fillRect(175, 450, 925, 180);
			
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(8));
			g2.drawRect(185, 140, 900, 475);
			
			cardText(g2);
			cardElement(g2);
			drawSelectionButtons(g2);
		}
		
		public static void cardText(Graphics2D g2) {
			switch (GameSettings.projectedlevelSelection) {
			case 1:
				drawHeadingText(g2, "A Niche Grove", Color.WHITE, 80, 220, 250);
				drawParagraphText(g2, "With noise free to reach anywhere", Color.WHITE, 35, 220, 300);
				drawParagraphText(g2, "and equipped with a new asphalt", Color.WHITE, 35, 220, 340);
				drawParagraphText(g2, "road, local enthusiasts painted a", Color.WHITE, 35, 220, 380);
				drawParagraphText(g2, "finish line right in the middle of it,", Color.WHITE, 35, 220, 420);
				drawParagraphText(g2, "to entice racers like you!", Color.WHITE, 35, 220, 460);
				break;
			case 2: 
				drawHeadingText(g2, "Tenement Square", Color.WHITE, 70, 220, 250);
				drawParagraphText(g2, "Filled with run-down buildings and", Color.WHITE, 35, 220, 300);
				drawParagraphText(g2, "cant-be-bothered residents, you bet", Color.WHITE, 35, 220, 340);
				drawParagraphText(g2, "that it became a local hotspot for", Color.WHITE, 35, 220, 380);
				drawParagraphText(g2, "racers alike. Watch out for", Color.WHITE, 35, 220, 420);
				drawParagraphText(g2, "pedestrians though.", Color.WHITE, 35, 220, 460);
				break;
			case 3:
				drawHeadingText(g2, "Seaway", Color.WHITE, 80, 220, 250);
				drawParagraphText(g2, "A bumpless road bridging the main", Color.WHITE, 35, 220, 300);
				drawParagraphText(g2, "land to a nearby island. Racers are", Color.WHITE, 35, 220, 340);
				drawParagraphText(g2, "not welcome of course, but that", Color.WHITE, 35, 220, 380);
				drawParagraphText(g2, "didn’t stop nobody did it? It is", Color.WHITE, 35, 220, 420);
				drawParagraphText(g2, "pitch dark at night; road gets closed.", Color.WHITE, 35, 220, 460);
				break;
			case 4: 
				drawHeadingText(g2, "Savannahroading", Color.WHITE, 70, 220, 250);
				drawParagraphText(g2, "It may not be the most ideal spot", Color.WHITE, 35, 220, 300);
				drawParagraphText(g2, "to race on, but you only live once!", Color.WHITE, 35, 220, 340);
				drawParagraphText(g2, "There may be endangered and", Color.WHITE, 35, 220, 380);
				drawParagraphText(g2, "dangerous animals on and off the road,", Color.WHITE, 35, 220, 420);
				drawParagraphText(g2, "the savannah isn't exactly forgiving.", Color.WHITE, 35, 220, 460);
			}
		}
		
		public static void cardElement(Graphics2D g2) {
			switch (GameSettings.projectedlevelSelection) {
			case 1: drawLayeredTree(g2, 790, 80, 0.7); break;
			case 2: drawMan(g2, 790, 80, 0.7); break;
			case 3:	drawCargoShip(g2, 790, 80, 0.7); break;
			case 4:	drawOstrich(g2, 750, 100, 0.7); break;
			}
		}
		
		public static void drawSelectionButtons(Graphics2D g2) {
			drawPrimaryButton(g2, proceed, "GAME ON!", 28, "#89d957");
			drawPrimaryButton(g2, nevermind, "NEVERMIND...", 28, "#d42423");	
		}
	}
