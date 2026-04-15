package group10;

import group10.entities.*; // import lahat dun sa package ng entities
import static group10.helpers.GUIHelpers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
// trad java libraries

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
	private final int WIDTH = 1280;
	private final int HEIGHT = 760;
	
	private final Timer timer = new Timer(16, this);
	private final Random random = new Random();
	
	private ScreenState screenState = ScreenState.MENU;
	
	private int roadX = 350;
	private final int roadWidth = 580;
	private final int laneWidth = roadWidth / 2;
	private final int finishY = 110;
	
	// x-center - half button width (inaalign yung center ng button sa center ng window)
	// y-center +/- (positive goes down, negative goes up)
	private final Rectangle menuStartButton = new Rectangle((WIDTH / 2) - (222 / 2), HEIGHT / 2 - 80, 222, 71);    
	private final Rectangle menuInfoButton = new Rectangle((WIDTH / 2) - (222 / 2), HEIGHT / 2, 222, 71);
	private final Rectangle menuExitButton = new Rectangle((WIDTH / 2) - (222 / 2), HEIGHT / 2 + 80, 222, 71);
	
	private final Rectangle startButton = new Rectangle(20, 20, 100, 42);
	private final Rectangle stopButton = new Rectangle(130, 20, 100, 42);
	private final Rectangle rainButton = new Rectangle(240, 20, 100, 42);
	private final Rectangle snowButton = new Rectangle(350, 20, 100, 42);
	private final Rectangle sunnyButton = new Rectangle(460, 20, 120, 42);
	private final Rectangle newGameButton = new Rectangle(590, 20, 130, 42);
	private final Rectangle exitButton = new Rectangle(730, 20, 100, 42);
	
	private final Rectangle summaryPlayAgainButton = new Rectangle(430, 495, 170, 50);
	private final Rectangle summaryMenuButton = new Rectangle(615, 495, 170, 50);	// originally (615, 495, 170, 50)
	private final Rectangle summaryExitButton = new Rectangle(800, 495, 110, 50);	// originally (800, 495, 110, 50)
	
	private boolean running = false;
	private boolean raining = false;
	private boolean snowing = false;
	private boolean sunny = true;
	
	private boolean p1Up, p1Left, p1Right;
	private boolean p2Up, p2Left, p2Right;
	
	private boolean countdownActive = false;
	private int countdownValue = 3;
	private int countdownTick = 0;
	private String countdownText = "";
	
	private int player1Wins = 0;
	private int player2Wins = 0;
	private int roundsPlayed = 0;
	private String statusMessage = "Welcome to the race!";
	
	private final Car player1;
	private final Car player2;
	
	private final List<RainDrop> rainDrops = new ArrayList<>();
	private final List<SnowFlake> snowFlakes = new ArrayList<>();
	private final List<Cloud> clouds = new ArrayList<>();
	private final List<Puddle> puddles = new ArrayList<>();
	
	private double snowLevel = 0;
	private double wetLevel = 0;
	
	private int trafficState = 0;
	private int trafficCounter = 0;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		
		// car placement
		// x = place in the middle of the right side, y - place somewhere
		player1 = new Car(roadX + 80, HEIGHT - 165, new Color(225, 55, 55), "P1");
		player2 = new Car(roadX + laneWidth + 80, HEIGHT - 165, new Color(55, 110, 230), "P2");
		
		for (int i = 0; i < 180; i++) {
			rainDrops.add(new RainDrop(random.nextInt(WIDTH), random.nextInt(HEIGHT), 6 + random.nextInt(8)));
		}
	
		for (int i = 0; i < 150; i++) {
			snowFlakes.add(new SnowFlake(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2 + random.nextInt(4)));
		}
		
		for (int i = 0; i < 5; i++) {
			clouds.add(new Cloud(60 + i * 250, 70 + random.nextInt(70), 80 + random.nextInt(40)));
			
		}
		
		for (int i = 0; i < 14; i++) {
			puddles.add(new Puddle(
				40 + random.nextInt(WIDTH - 80),
				420 + random.nextInt(280),
				30 + random.nextInt(70),
				12 + random.nextInt(24)
			));
		}
	
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		updateTrafficLight();
		updateWeather();
		updateClouds();
		updateCountdown();
		
		if (screenState == ScreenState.GAME && running && !countdownActive) {
			updateCars();
			checkWinner();
		}
		
		repaint();
	}
	
	private void updateCountdown() {
		if (!countdownActive) return;
		
		countdownTick++;
		if (countdownTick >= 60) {
			countdownTick = 0;
			countdownValue--;
		
			if (countdownValue > 0) {
				countdownText = String.valueOf(countdownValue);
			} else if (countdownValue == 0) {
				countdownText = "GO!";
			} else {
				countdownActive = false;
				running = true;
				countdownText = "";
				statusMessage = "Race in progress...";
			}
		}
	}
	
	private void startCountdown() {
		resetRacePositions();
		countdownActive = true;
		running = false;
		countdownValue = 3;
		countdownTick = 0;
		countdownText = "3";
		statusMessage = "Get ready...";
	}
	
	private void updateTrafficLight() {
		trafficCounter++;
		if (trafficCounter >= 140) {
			trafficCounter = 0;
			trafficState = (trafficState + 1) % 3;
		}
	}
	
	private void updateWeather() {
		if (raining) {
			wetLevel += 0.004;
			if (wetLevel > 1.0) wetLevel = 1.0;
			
			snowLevel -= 0.003;
			if (snowLevel < 0) snowLevel = 0;
			
			for (RainDrop drop : rainDrops) {
				drop.y += drop.speed;
				if (drop.y > HEIGHT) {
					drop.y = -20;
					drop.x = random.nextInt(WIDTH);
				}
			}
		}
		
		if (snowing) {
			snowLevel += 0.0035;
			if (snowLevel > 1.0) snowLevel = 1.0;
			
			wetLevel -= 0.0015;
			if (wetLevel < 0) wetLevel = 0;
			
			for (SnowFlake flake : snowFlakes) {
				flake.y += flake.speed;
				flake.x += flake.drift;
				if (flake.y > HEIGHT) {
					flake.y = -10;
					flake.x = random.nextInt(WIDTH);
				}
				if (flake.x < 0) flake.x = WIDTH;
				if (flake.x > WIDTH) flake.x = 0;
			}
		}
		
		if (sunny) {
			wetLevel -= 0.0022;
			if (wetLevel > 0) wetLevel = 0;
			
			snowLevel -= 0.0018;
			if (snowLevel < 0) snowLevel = 0;
		}
	}
	
	private void updateClouds() {
		for (Cloud c : clouds) {
			c.x += 1;
			if (c.x > WIDTH + 120) {
				c.x = -180;
				c.y = 50 + random.nextInt(90);
			}
		}
	}
	
	private void updateCars() {
		updateSingleCar(player1, p1Up, p1Left, p1Right, true);
		updateSingleCar(player2, p2Up, p2Left, p2Right, false);
	}
	
	private void updateSingleCar(Car car, boolean accelerate, boolean left, boolean right, boolean leftLane) {
		double accel = 0.18;
		double maxSpeed = 5.7;
		double friction = 0.06;
		int steering = 5;
		
		if (raining) {
			maxSpeed = 4.9;
			accel = 0.14;
			steering = 4;
		}
		
		if (snowing) {
			maxSpeed = 4.2;
			accel = 0.11;
			friction = 0.035;
			steering = 3;
		}
		
		if (wetLevel > 0.6) maxSpeed -= 0.35;
		if (snowLevel > 0.6) maxSpeed -= 0.45;
		
		if (trafficState == 0 && car.y < 280 && car.y > 180) {
			maxSpeed = Math.min(maxSpeed, 1.0);
		}
		
		if (accelerate) {
			car.speed += accel;
			if (car.speed > maxSpeed) car.speed = maxSpeed;
		} else {
			// needs to be cleared up (ln. 263 on paper)
			car.speed -= friction;
			if (car.speed < 0) car.speed = 0;
		}
		
		car.y -= car.speed;
		
		int leftBound = leftLane ? roadX + 20 : roadX + laneWidth + 20;
		int rightBound = leftLane ? roadX + laneWidth - car.width - 20 : roadX + roadWidth - car.width - 20;
		
		if (left) car.x -= steering;
		if (right) car.x += steering;
		
		if (car.x < leftBound) car.x = leftBound;
		if (car.x > rightBound) car.x = rightBound;
		if (car.y < 0) car.y = 0;
	}
	
	private void checkWinner() {
		if (player1.y <= finishY || player2.y <= finishY) {
			running = false;
			countdownActive = false;
			screenState = ScreenState.SUMMARY;
			roundsPlayed++;
		
			if (player1.y <= finishY && player2.y <= finishY) {
				statusMessage = "It's a tie! Both players reached the finish line.";
			} else if (player1.y <= finishY) {
				player1Wins++;
				statusMessage = "Player 1 wins this round!";
			} else {
				player2Wins++;
				statusMessage = "Player 2 wins this round!";
			}
		}
	}
	
	private void resetRacePositions() {
		player1.x = roadX + 80;
		player1.y = HEIGHT - 165;
		player1.speed = 0;
		
		player2.x = roadX + laneWidth + 80;
		player2.y = HEIGHT - 165;
		player2.speed = 0;
	}
	
	private void resetScores() {
		player1Wins = 0;
		player2Wins = 0;
		roundsPlayed = 0;
	}
	
	private void goToMenu() {
		running = false;
		countdownActive = false;
		screenState = ScreenState.MENU;
		resetRacePositions();
		statusMessage = "Welcome to the race!";
	}
	
	private void newGame() {
		resetScores();
		raining = false;
		snowing = false;
		sunny = true;
		wetLevel = 0;
		snowLevel = 0;
		screenState = ScreenState.GAME;
		startCountdown();
	}
	
	private void playAgain() {
		screenState = ScreenState.GAME;
		startCountdown();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (screenState == ScreenState.MENU) {
			drawMenuNew(g2);
			return;
		}
		
		drawSky(g2);
		drawSunOrMoon(g2);
		drawClouds(g2);
		drawGround(g2);
		drawAccumulatedWeather(g2);
		drawBridge(g2);
		drawRoad(g2);
		drawRoadMarks(g2);
		drawFinishLine(g2);
		drawHouses(g2);
		drawTrees(g2);
		drawTrafficLight(g2);
		drawCars(g2);
		drawWeatherEffects(g2);
		drawPremiumTopBar(g2);
		drawInstructions(g2);
		drawScoreboard(g2);
		
		if (countdownActive) {
			drawCountdown(g2);
		}
		
		if (screenState == ScreenState.SUMMARY) {
			drawSummary(g2);
		}
	}
	

	private void drawMenu(Graphics2D g2) {
		g2.setPaint(new GradientPaint(0, 0, new Color(18, 28, 48), 0, HEIGHT, new Color(42, 72, 108)));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		g2.setColor(new Color(255, 255, 255, 25));
		for (int i = 0; i < 25; i++) {
			g2.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), 6, 6);
		}
		
		g2.setColor(new Color(255, 255, 255, 235));
		g2.setFont(new Font("Arial", Font.BOLD, 52));
		g2.drawString("2-Player Weather Racing Game", 250, 180);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 24));
		g2.drawString("Race, change weather, and compete with a premium-style UI.", 305, 230);
		
		g2.setColor(new Color(10, 18, 32, 180));
		g2.fillRoundRect(420, 265, 420, 220, 30, 30);
		
		drawButton(g2, menuStartButton, "START GAME", new Color(52, 168, 83));
		drawButton(g2, menuExitButton, "EXIT", new Color(220, 68, 55));
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.PLAIN, 20));
		g2.drawString("Player 1: W A D", 535, 540);
		g2.drawString("Player 2: UP LEFT RIGHT", 495, 575);
	}
	
	private void drawMenuNew (Graphics2D g2) {
		// background (sky)
		g2.setColor(Color.decode("#72c2f8"));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		// grass
		g2.setColor(Color.decode("#89d957"));
		g2.fillRect(0, 620, WIDTH, 140);
		
		// road
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 620, WIDTH, 40);
		
		// road marks
		g2.setColor(Color.decode("#faff00"));
		int addLine = 17;
		for (int i = 0; i < 18; i++) {
			g2.fillRect(0 + addLine, 633 , 46, 14);
			addLine = addLine + 75;
		}
		
		drawButtonNew(g2, menuStartButton, "START", "#89d957");
		drawButtonNew(g2, menuInfoButton, "INFO", "#ffde59");
		drawButtonNew(g2, menuExitButton, "EXIT", "#ff5757");
		
		// main header
		drawUIText(g2, "The Weather Racing Game",
				Color.WHITE, 68, -0.05, true, 255, 196);
		
		drawUIText(g2, "Bantula, Gutierrez, Saurane, Simborio, & Vallestero presents...",
				Color.WHITE, 20, -0.05, true, 255, 135);
		
		
	}
	
	private void drawButtonNew(Graphics2D g2, Rectangle r, String text, String hexColor) {
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
		
		// text
		g2.setColor(Color.decode("#353535"));
		g2.setFont(new Font("Century Gothic", Font.PLAIN, 40));
		adjustKerning(g2, -0.1);
		FontMetrics fm = g2.getFontMetrics();
		int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
	    int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
		g2.drawString(text, tx, ty);
	}
	
	private void drawButton(Graphics2D g2, Rectangle r, String text, Color color) {
		g2.setColor(new Color(255, 255, 255, 25));
		g2.fillRoundRect(r.x + 2, r.y + 3, r.width, r.height, 18, 18);
		
		g2.setColor(color);
		g2.fillRoundRect(r.x, r.y, r.width, r.height, 18, 18);
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 16));
		FontMetrics fm = g2.getFontMetrics();
		int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
	    int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
		g2.drawString(text, tx, ty);
	}
	
	private void drawSky(Graphics2D g2) {
		if (sunny) {
			g2.setPaint(new GradientPaint(0, 0, new Color(115, 195, 255), 0, 350, new Color(220, 245, 255)));
		} else if (raining) {
			g2.setPaint(new GradientPaint(0, 0, new Color(90, 105, 130), 0, 350, new Color(165, 175, 185)));
		} else {
			g2.setPaint(new GradientPaint(0, 0, new Color(185, 220, 245), 0, 350, new Color(240, 247, 255)));
		}
	}
	
	private void drawSunOrMoon(Graphics2D g2) {
		if (sunny) {
			g2.setColor(new Color(255, 210, 70));
			g2.fillOval(1030, 70, 90, 90);
			g2.setColor(new Color(255, 225, 120));
			g2.fillOval(1050, 90, 50, 50);
		} else {
			g2.setColor(new Color(245, 245, 250));
			g2.fillOval(1045, 80, 70, 70);
		}
	}

	private void drawClouds(Graphics2D g2) {
		if (raining) {
			g2.setColor(new Color(190, 200, 210, 230));
		} else {
			g2.setColor(new Color(255, 255, 255, 220));
		}
		
		for (Cloud c : clouds) {
			g2.fillOval(c.x, c.y, c.size, c.size / 2);
			g2.fillOval(c.x + 26, c.y - 16, c.size, c.size / 2 + 10);
			g2.fillOval(c.x + 58, c.y, c.size, c.size / 2);
		}
	}

	private void drawGround(Graphics2D g2) {
		g2.setColor(new Color(96, 170, 85));
		g2.fillRect(0, 230, WIDTH, HEIGHT - 230);
	}

	private void drawAccumulatedWeather(Graphics2D g2) {
		if (wetLevel > 0) {
			for (Puddle p : puddles) {
				int alpha = (int) (100 * wetLevel);
				g2.setColor(new Color(70, 130, 190, alpha));
				g2.fillOval(p.x, p.y, p.w, p.h);
				g2.setColor(new Color(180, 220, 225, Math.max(alpha / 2, 1)));
				g2.drawOval(p.x + 5, p.y + 2, p.w - 10, p.h - 4);
			}
			
			g2.setColor(new Color(180, 210, 235, (int) (55 * wetLevel)));
			g2.fillRect(roadX, 90, roadWidth, HEIGHT - 60);
		}
		
		if (snowLevel > 0) {
			int snowHeight = (int) (35 * snowLevel);
			g2.setColor(new Color(245, 248, 250, 240));
			g2.fillRect(0, HEIGHT - snowHeight, WIDTH, snowHeight);
			
			g2.setColor(new Color(250, 250, 255, 220));
			g2.fillRect(0, HEIGHT - snowHeight - 6, WIDTH, 6);
		}
	}

	private void drawBridge(Graphics2D g2) {
		g2.setColor(new Color(120, 120, 130));
		g2.fillRect(roadX - 30, 305, roadWidth + 60, 35);
		
		g2.setColor(new Color(95, 95, 100));
		for (int i = roadX - 20; i < roadX + roadWidth + 20; i += 48) {
			g2.fillRect(i, 300, 20, 45);
		}
		
		g2.setColor(new Color(70, 145, 210));
		g2.fillRoundRect(0, 340, WIDTH, 70, 30, 30);
		
		g2.setColor(new Color(190, 230, 255, 110));
		for (int i = 0; i < WIDTH; i += 90) {
			g2.fillOval(i, 360 + (i % 4), 55, 10);
		}
	}

	private void drawRoad(Graphics2D g2) {
		g2.setColor(new Color(52, 53, 58));
		g2.fillRoundRect(roadX, 90, roadWidth, HEIGHT - 40, 30, 30);
		
		g2.setColor(new Color(255, 255, 255, 18));
		g2.fillRoundRect(roadX + 15, 100, roadWidth - 30, HEIGHT - 65, 25, 25);
		
		if (snowLevel > 0.1) {
			g2.setColor(new Color(240, 245, 250, (int) (90 * snowLevel)));
			g2.fillRoundRect(roadX, 90, roadWidth, HEIGHT - 40, 30, 30);
		}
	}

	private void drawRoadMarks(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		for (int y = 110; y < HEIGHT - 20; y += 45) {
			g2.fillRect(roadX + laneWidth - 5, y, 10, 26);
		}
		
		g2.setColor(new Color(220, 220, 220));
		g2.fillRect(roadX + 12, 90, 8, HEIGHT - 40);
		g2.fillRect(roadX + roadWidth - 20, 90, 8, HEIGHT - 40);
	}

	private void drawFinishLine(Graphics2D g2) {
		int blockSize = 20;
		
		for (int i = 0; i < roadWidth / blockSize; i++) {
			g2.setColor(i % 2 == 0 ? Color.WHITE : Color.BLACK);
			g2.fillRect(roadX + i * blockSize, finishY, blockSize, 18);
		}
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 18));
		g2.drawString("FINISH", roadX + roadWidth / 2 - 35, finishY - 10);
	}

	private void drawHouses(Graphics2D g2) {
		drawHouse(g2, 85, 455, new Color(242, 210, 150), new Color(155, 80, 50));
		drawHouse(g2, 1030, 470, new Color(250, 220, 175), new Color(185, 88, 58));
	}

	private void drawHouse(Graphics2D g2, int x, int y, Color bodyColor, Color roofColor) {
		g2.setColor(bodyColor);
		g2.fillRect(x, y, 160, 110);
		
		Polygon roof = new Polygon();
		roof.addPoint(x - 12, y);
		roof.addPoint(x + 80, y - 68);
		roof.addPoint(x + 172, y);
		g2.setColor(roofColor);
		g2.fillPolygon(roof);
		
		g2.setColor(new Color(118, 74, 42));
		g2.fillRect(x + 62, y + 54, 36, 56);
		
		g2.setColor(new Color(185, 228, 255));
		g2.fillRect(x + 18, y + 25, 32, 28);
		g2.fillRect(x + 108, y + 25, 32, 28);
		
		g2.setColor(Color.WHITE);
		g2.drawRect(x + 18, y + 25, 32, 28);
		g2.drawRect(x + 108, y + 25, 32, 28);
		
		if (snowLevel > 0.1) {
			g2.setColor(new Color(248, 250, 252, 220));
			g2.fillRoundRect(x - 8, y - 8, 176, 12, 8, 8);
		}
	}
	
	private void drawTrees(Graphics2D g2) {
		drawTree(g2, 265, 480);
		drawTree(g2, 1125, 385);
		drawTree(g2, 1125, 385);
	}
	
	private void drawTree(Graphics2D g2, int x, int y) {
		g2.setColor(new Color(112, 72, 36));
		g2.fillRect(x, y, 22, 76);
		
		g2.setColor(new Color(40, 132, 48));
		g2.fillOval(x - 30, y - 38, 84, 58);
		g2.fillOval(x - 20, y - 66, 64, 50);
		g2.fillOval(x - 42, y - 8, 52, 42);
		g2.fillOval(x + 14, y - 10, 52, 42);
		
		if (snowLevel > 0.08) {
			g2.setColor(new Color(248, 250, 252, (int) (220 * snowLevel)));
			g2.fillOval(x - 24, y - 40, 28, 16);
			g2.fillOval(x + 10, y - 58, 30, 16);
			g2.fillOval(x + 24, y - 8, 26, 14);
		}
	}

	private void drawTrafficLight(Graphics2D g2) {
		int poleX = roadX + roadWidth - 80;
		int poleY = 150;
		
		g2.setColor(new Color(70, 70, 70));
		g2.fillRect(poleX + 18, poleY + 75, 12, 125);
		g2.fillRoundRect(poleX, poleY, 48, 115, 15, 15);
		
		g2.setColor(trafficState == 0 ? Color.RED : new Color(90, 20, 20));
		g2.fillOval(poleX + 9, poleY + 10, 30, 30);

		g2.setColor(trafficState == 1 ? Color.YELLOW : new Color(105, 105, 20));
		g2.fillOval(poleX + 9, poleY + 42, 30, 30);
		
		g2.setColor(trafficState == 2 ? Color.GREEN : new Color(20, 90, 20));
		g2.fillOval(poleX + 9, poleY + 74, 30, 30);
	}

	private void drawCars(Graphics2D g2) {
		drawCar(g2, player1);
		drawCar(g2, player2);
	}
	
	private void drawCar(Graphics2D g2, Car car) {
		int x = (int) car.x;
		int y = (int) car.y;
		
		// outer body color
		g2.setColor(Color.decode("#b35fdb"));
		g2.fillRoundRect(x, y, car.width, car.height, 42,42);
		
		// rear view mirrors
		g2.setColor(Color.decode("#a548d5"));
		g2.fillOval(x - 10, y + car.height - 115, 18, 6);	//left
		g2.fillOval(x + car.width - 10, y + car.height - 115, 18, 6); //right
				
		// inner body color
		g2.setColor(Color.decode("#a548d5"));
		g2.fillRoundRect(x + 6, y, car.width - 11, car.height, 42, 42);
		
		
		// windows
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.DARK_GRAY);
		g2.fillRoundRect(x + 5, y + 30, 54, 33, 30, 30); // front
		g2.setColor(Color.BLACK);
		g2.drawRoundRect(x + 5, y + 30, 54, 33, 30, 30);
		g2.setColor(Color.DARK_GRAY);
		g2.fillRoundRect(x + 5, y + 100, 54, 18, 20, 20); // back
		g2.setColor(Color.BLACK);
		g2.drawRoundRect(x + 5, y + 100, 54, 18, 20, 20);
		
		// front lights
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.WHITE);
		g2.fillOval(x, y, 18, 5);	//front-left
		g2.setColor(Color.YELLOW);
		g2.drawOval(x, y, 18, 5);
		g2.setColor(Color.WHITE);
		g2.fillOval(x + car.width - 20, y, 18, 5); //front-right
		g2.setColor(Color.YELLOW);
		g2.drawOval(x + car.width - 20, y, 18, 5);
		
		// back lights
		g2.setColor(Color.RED);
		g2.fillOval(x, y + car.height - 6, 18, 6);	//bot-left
		g2.fillOval(x + car.width - 20, y + car.height - 6, 18, 6); //bot-right
		
		// text label
		g2.setColor(Color.GRAY);
		g2.setFont(new Font("Century Gothic", Font.BOLD, 16));
		g2.drawString(car.label, x + 22, y + car.height / 2 + 8);
	}
	
//	private void drawCar(Graphics2D g2, Car car) {
//	int x = (int) car.x;
//	int y = (int) car.y;
//	
//	// eto yung drop shadow
//	g2.setColor(car.color.darker());
//	g2.fillRoundRect(x + 4, y + 8, car.width - 8, car.height - 10, 24, 24);
//	
//	GradientPaint body = new GradientPaint(x, y, car.color.brighter(), x, y + car.height, car.color.darker());
//	g2.setPaint(body);
//	g2.fillRoundRect(x, y, car.width, car.height, 24, 24);
//	
//	g2.setColor(new Color(220, 240, 255));
//	g2.fillRoundRect(x + 14, y + 14, car.width - 28, 22, 12, 12);
//	g2.fillRoundRect(x + 18, y + 44, car.width - 36, 18, 10, 10);
//	
//	g2.setColor(Color.BLACK);
//	g2.fillOval(x + 6, y + 12, 18, 28);
//	g2.fillOval(x + car.width - 24, y + 12, 18, 28);
//	g2.fillOval(x + 6, y + car.height - 40, 18, 28);
//	g2.fillOval(x + car.width - 24, y + car.height - 40, 18, 28);
//	
//	g2.setColor(new Color(255, 245, 130));
//	g2.fillOval(x + 10, y + car.height - 14, 10, 8);
//	g2.fillOval(x + car.width - 20, y + car.height - 14, 10, 8);
//	
//	g2.setColor(Color.WHITE);
//	g2.setFont(new Font("Arial", Font.BOLD, 16));
//	g2.drawString(car.label, x + 22, y + car.height / 2 + 8);
//}
	
	private void drawWeatherEffects(Graphics2D g2) {
		if (raining) {
			g2.setColor(new Color(190, 225, 255, 180));
			for (RainDrop d : rainDrops) {
				g2.drawLine(d.x, d.y, d.x - 3, d.y + 12);
			}
		}
		
		if (snowing) {
			g2.setColor(new Color(255, 255, 255, 225));
			for (SnowFlake f : snowFlakes) {
				g2.fillOval(f.x, f.y, f.size, f.size);
			}
		}
	}

	private void drawPremiumTopBar(Graphics2D g2) {
		GradientPaint top = new GradientPaint(10, 10, new Color(8, 20, 40, 240), 850, 72, new Color (22, 40, 70, 220));
		g2.setPaint(top);
		g2.fillRoundRect(10, 10, 840, 62, 22, 22);
		
		drawButton(g2, startButton, "START", new Color(52, 168, 83));
		drawButton(g2, stopButton, "STOP", new Color(220, 68, 55));
		drawButton(g2, rainButton, "RAIN", new Color(66, 133, 244));
		drawButton(g2, snowButton, "SNOW", new Color(140, 180, 220));
		drawButton(g2, sunnyButton, "SUNNY DAY", new Color(244, 180, 0));
		drawButton(g2, newGameButton, "NEW GAME", new Color(110, 120, 140));
		drawButton(g2, exitButton, "EXIT", new Color(80, 80, 80));
	}

	private void drawInstructions(Graphics2D g2) {
		GradientPaint box = new GradientPaint(870, 18, new Color(12, 14, 46, 235), 1260, 118, new Color(22, 42, 70, 215));
		g2.setPaint(box);
		g2.fillRoundRect(870, 18, 390, 100, 20, 20);
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 18));
		g2.drawString("2-Player Controls", 892, 45);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 16));
		g2.drawString("Player 1: W = Go, A = Left, D = Right", 892, 72);
		g2.drawString("Player 2: UP = Go, LEFT = Left, RIGHT = Right", 892, 96);
	}

	private void drawScoreboard(Graphics2D g2) {
		GradientPaint box = new GradientPaint(20, 615, new Color(12, 24, 46, 235), 410, 730, new Color(22, 42, 70, 215));
		g2.setPaint(box);
		g2.fillRoundRect(20, 615, 390, 115, 20, 20);
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 21));
		g2.drawString("Score Board", 35, 645);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 17));
		g2.drawString("Rounds Played: " + roundsPlayed, 35, 672);
		g2.drawString("Player 1 Wins: " + player1Wins, 35, 698);
		g2.drawString("Player 2 Wins: " + player2Wins, 210, 698);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 15));
		g2.drawString("Status: " + statusMessage, 35, 720);
	}
	
	private void drawCountdown(Graphics2D g2) {
		g2.setColor(new Color(0, 0, 0, 90));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		g2.setFont(new Font("Arial", Font.BOLD, 110));
		g2.setColor(new Color(255, 255, 255, 240));
		FontMetrics fm = g2.getFontMetrics();
		int x = (WIDTH - fm.stringWidth(countdownText)) / 2;
		int y = HEIGHT / 2;
		g2.drawString(countdownText, x, y);
	}
	
	private void drawSummary(Graphics2D g2) {
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		GradientPaint card = new GradientPaint(360, 180, new Color(255, 255, 255, 245), 930, 580, new Color(232, 238, 248, 240));
		g2.setPaint(card);
		g2.fill(new RoundRectangle2D.Double(360, 180, 570, 390, 28, 28));
		
		g2.setColor(new Color(25, 35, 55));
		g2.setFont(new Font("Arial", Font.BOLD, 30));
		g2.drawString("Race Summary", 540, 230);
		
		g2.setFont(new Font("Arial", Font.BOLD, 22));
		g2.drawString(statusMessage, 420, 285);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 20));
		g2.drawString("Rounds Played: " + roundsPlayed, 445, 340);
		g2.drawString("Player 1 Wins: " + player1Wins, 445, 380);
		g2.drawString("Player 2 Wins: " + player2Wins, 445, 420);
		g2.drawString("Choose what to do next:", 445, 460);
		
		drawButton(g2, summaryPlayAgainButton, "PLAY AGAIN", new Color(52, 168, 83));
		drawButton(g2, summaryMenuButton, "BACK TO MENU", new Color(66, 133, 244));
		drawButton(g2, summaryExitButton, "EXIT", new Color(220, 68, 55));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		
		if (screenState == ScreenState.MENU) {
			if (menuStartButton.contains(p)) {
				newGame();
			} else if (menuExitButton.contains(p)) {
				System.exit(0);
			}
			requestFocusInWindow();
			repaint();
			return;
		}
		
		if (screenState == ScreenState.SUMMARY) {
			if (summaryPlayAgainButton.contains(p)) {
				playAgain();
			} else if (summaryMenuButton.contains(p)) {
				goToMenu();
			} else if (summaryExitButton.contains(p)) {
				System.exit(0);
			}
			requestFocusInWindow();
			repaint();
			return;
		}
		
		if (startButton.contains(p)) {
			startCountdown();
		} else if (stopButton.contains(p)) {
			running = false;
			countdownActive = false;
			statusMessage = "Race stopped.";
		} else if (rainButton.contains(p)) {
			raining = true;
			snowing = false;
			sunny = false;
			statusMessage = "Weather set to rain.";
		} else if (snowButton.contains(p)) {
			raining = false;
			snowing = true;
			sunny = false;
			statusMessage = "Weather set to snow.";
		} else if (sunnyButton.contains(p)) {
			raining = false;
			snowing = false;
			sunny = true;
			statusMessage = "Weather set to sunny.";
		} else if (newGameButton.contains(p)) {
			newGame();
		} else if (exitButton.contains(p)) {
			System.exit(0);
		}
		
		requestFocusInWindow();
		repaint();
	}
	
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_W) p1Up = true;
		if (key == KeyEvent.VK_A) p1Left = true;
		if (key == KeyEvent.VK_D) p1Right = true;
		
		if (key == KeyEvent.VK_UP) p2Up = true;
		if (key == KeyEvent.VK_LEFT) p2Left = true;
		if (key == KeyEvent.VK_RIGHT) p2Right = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_W) p1Up = false;
		if (key == KeyEvent.VK_A) p1Left = false;
		if (key == KeyEvent.VK_D) p1Right = false;
		
		if (key == KeyEvent.VK_UP) p2Up = false;
		if (key == KeyEvent.VK_LEFT) p2Left = false;
		if (key == KeyEvent.VK_RIGHT) p2Right = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
}
