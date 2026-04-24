package group10;

import group10.entities.*; // import lahat dun sa package ng entities
import group10.helpers.ColorPalette;

import static group10.graphics.BackgroundElements.*;
import static group10.graphics.CarModels.*;
import static group10.helpers.GUIHelpers.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
// trad java libraries

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
	private final int WIDTH = 1280;
	private final int HEIGHT = 760;
	
	// CAMERA SYSTEM
	private int p1CameraY = 0;
	private int p2CameraY = 0;
	
	// 60fps (1000ms/16 == 62.5 approximated to 60)
	private final Timer timer = new Timer(16, this);
	private final Random random = new Random();
	
	private ScreenState screenState = ScreenState.MENU;
	
	// ROAD VARIABLES
	private final int roadWidth = 360;
	private final int lrX = 140;
	private final int rrX = 780;
    private int finishY = 1000;
	
	// BUTTONS
	// MENU BUTTONS
	// x-center - half button width (inaalign yung center ng button sa center ng window)
	// y-center +/- (positive goes down, negative goes up)
	// TODO: clunky yung code neto, baka lang naman i-fix natin neh
	private final Rectangle menuStartButton = new Rectangle((WIDTH / 2) - (222 / 2), HEIGHT / 2 - 80, 222, 71);    
	private final Rectangle menuInfoButton = new Rectangle((WIDTH / 2) - (222 / 2), HEIGHT / 2, 222, 71);
	private final Rectangle menuExitButton = new Rectangle((WIDTH / 2) - (222 / 2), HEIGHT / 2 + 80, 222, 71);
	
	// LEVEL SELECT BUTTONS
	private final int levelButtonX = 135;
	private final int levelButtonY = 300;
	
	// TODO: turn into arraylist
	private final Rectangle level1Button = new Rectangle(levelButtonX, levelButtonY, 160, 160);
	private final Rectangle level2Button = new Rectangle(levelButtonX + 210, levelButtonY, 160, 160);
	private final Rectangle level3Button = new Rectangle(levelButtonX + 420, levelButtonY, 160, 160);
	private final Rectangle level4Button = new Rectangle(levelButtonX + 630, levelButtonY, 160, 160);
	private final Rectangle level5Button = new Rectangle(levelButtonX + 840, levelButtonY, 160, 160);
	
	// PLACEHOLDER: image para i-plot yung placement ng drawn objects
	private BufferedImage level1Background;
	
	// GAME BUTTONS
	
	// ui button starting coordinate values
	private final int uiX = 1135;
	private final int uiY = 35;
	
	private final Rectangle dayModeButton = new Rectangle(uiX, uiY, 42, 42);
	private final Rectangle nightModeButton = new Rectangle(uiX + 55, uiY, 42, 42);
	
	private final Rectangle levelsButton = new Rectangle(uiX, uiY + 55, 100, 42);
	private final Rectangle carsButton = new Rectangle(uiX, uiY + 110, 100, 42);
	private final Rectangle colorsButton = new Rectangle(uiX, uiY + 165, 100, 42);
	private final Rectangle controlsButton = new Rectangle(uiX, uiY + 220, 100, 42);
	private final Rectangle menuButton = new Rectangle(uiX, uiY + 275, 100, 42);
	
	private final Rectangle normalCarButton = new Rectangle(uiX - 115, uiY + 55, 100, 42);
	private final Rectangle luxuryCarButton = new Rectangle(uiX - 115, uiY + 110, 100, 42);
	private final Rectangle sportsCarButton = new Rectangle(uiX - 115, uiY + 165, 100, 42);
	
	private final Rectangle controlsWindow = new Rectangle(uiX - 235, uiY + 55, 215, 200);
	private final Rectangle colorsWindow = new Rectangle(uiX - 235, uiY + 55, 215, 200);
	
	// TODO: add car color logic per player
	ColorPalette[] palette = ColorPalette.values();
	private final List<Rectangle> colorsWindowSelection = new ArrayList<>();
	private int colorButStartX = 400;
	private int colorButStartY = 235;
	private int colorButSize = 42; // button is square
	private int colorButSpacing = 64 / 2;
	// the for loop for this list is inside the constructor
	
	private final Rectangle summaryPlayAgainButton = new Rectangle(430, 495, 170, 50);
	private final Rectangle summaryMenuButton = new Rectangle(615, 495, 170, 50);	// originally (615, 495, 170, 50)
	private final Rectangle summaryExitButton = new Rectangle(800, 495, 110, 50);	// originally (800, 495, 110, 50)
	
	// LOGIC
	
	private int levelSelection;
	
	private boolean isDay = true;
	private boolean isNight = false;
	private boolean showCarHUDButtons = false;
	private boolean showControlsWindow = false;
	private boolean showColorsWindow = false;
	
	// nairename ko to is--- para easier to know na boolean value siya
	private boolean isRunning = false;
	private boolean isRaining = false;
	private boolean isSnowing = false;
	private boolean isWindy = false;
	private boolean isSunny = true;
	
	private boolean p1Up, p1Down, p1Left, p1Right;
	private boolean p2Up, p2Down, p2Left, p2Right;
	
	private boolean countdownActive = false;
	private int countdownValue = 3;
	private int countdownTick = 0;
	private String countdownText = "";
	
	private int player1Wins = 0;
	private int player2Wins = 0;
	private int roundsPlayed = 0;
	private String statusMessage = "Welcome to the race!";
	
	private int carSelection = 1; // default to normal car, paste this on level start
	private final Car player1;
	private final Car player2;
	private final int carFixedYPos = HEIGHT - 165;
	
	private final List<RainDrop> rainDrops = new ArrayList<>();
	private final List<SnowFlake> snowFlakes = new ArrayList<>();
	private final List<Cloud> clouds = new ArrayList<>();
	private final List<Puddle> puddles = new ArrayList<>();
	private final List<Star> stars = new ArrayList<>();
	
	private final ArrayList<BackgroundElement> p1BGE = new ArrayList<>();
	private final ArrayList<BackgroundElement> p2BGE = new ArrayList<>();
	
	private double snowLevel = 0;
	private double wetLevel = 0;
	
	// traffic state: red = 0, yellow = 1, green = 2
	private int trafficState = 0;
	private int trafficCounter = 0;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		
		for (int row = 0; row < 7; row++) {
	        for (int col = 0; col < 7; col++) {
	            int currentX = colorButStartX + (col * (colorButSize + colorButSpacing));
	            int currentY = colorButStartY + (row * (colorButSize + colorButSpacing));
	            colorsWindowSelection.add(new Rectangle(currentX, currentY, colorButSize, colorButSize));
	        }
	    }
		
		for (int i = 0; i < 5; i++) {
		    // spawn trees at x-value outside of view range so you cant see them at first spawn
		    p1BGE.add(new BackgroundElement(lrX + random.nextInt(roadWidth) - 1000, i * -300));
		    p2BGE.add(new BackgroundElement(rrX + random.nextInt(roadWidth) - 1000, i * -300));
		}
		
		player1 = new Car(lrX + 100, carFixedYPos, new Color(225, 55, 55), "P1");
		player2 = new Car(rrX + 100, carFixedYPos, new Color(55, 110, 230), "P2");
		
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
		
		for (int i = 0; i < 150; i++) {
			stars.add(new Star(random.nextInt(WIDTH), random.nextInt(HEIGHT), 6 + random.nextInt(8)));
		}
	
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// updates 60x per second as per the timer
		updateWeather();
		updateClouds();
		updateCountdown();
		
		// checks if at least one level screen is active, then it returns true if it does
		boolean isPlayingLevel = 
				(screenState == ScreenState.LEVEL1 || 
	            screenState == ScreenState.LEVEL2 || 
	            screenState == ScreenState.LEVEL3 || 
	            screenState == ScreenState.LEVEL4 || 
	            screenState == ScreenState.LEVEL5);
		
		if (isPlayingLevel && isRunning && !countdownActive) {
			updatePlayerSides();
			checkWinner();
		}
		
		// repaints every frame
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
				isRunning = true;
				countdownText = "";
				statusMessage = "Race in progress...";
			}
		}
	}
	
	private void startCountdown() {
		resetRacePositions();
		countdownActive = true;
		isRunning = false;
		countdownValue = 3;
		countdownTick = 0;
		countdownText = "3";
		statusMessage = "Get ready...";
	}
	
	private void updateTrafficLight() {
		
		// TODO: penalize movement under red light, penalize movement but not rotation while yellow
		trafficCounter++;
		if (trafficCounter >= 300) {
			trafficCounter = 0;
			trafficState = (trafficState + 1) % 3;
		}
	}
	
	private void updateWeather() {
		if (isRaining) {
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
		
		if (isSnowing) {
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
		
		if (isSunny) {
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
	
	private void updatePlayerSides() {
		updatePlayerSide(player1, p1Up, p1Down, p1Left, p1Right, true);
		updatePlayerSide(player2, p2Up, p2Down, p2Left, p2Right, false);
	}
	
	private void updateCarPhysics(Car car, boolean up, boolean down, boolean left, boolean right) {
	    double accel = 0.18;
	    double maxSpeed = 20.0;
	    double friction = 0.06;
	    double turnSpeed = 3.5;

	    // weather adjustments, nai-inline ko na yung brackets para mas readable & less lines
	    if (isRaining) { maxSpeed = 4.9; accel = 0.14; turnSpeed = 4; }
	    if (isSnowing) { maxSpeed = 4.2; accel = 0.11; friction = 0.035; turnSpeed = 3; }
	    if (wetLevel > 0.6) maxSpeed -= 0.35;
	    if (snowLevel > 0.6) maxSpeed -= 0.45;

	    // acceleration at braking
	    if (up) {
	    	// while key input ay up, car ay mag-foforward and if it hits yung maxSpeed threshold,
	    	// di lalampas dun sa number na yon kasi n-aauto set to the maxSpeed yung ating speed
	    	// pag-naabot yun
	        car.speed += accel;
	        if (car.speed > maxSpeed) car.speed = maxSpeed;
	    } else if (down) {
	    	// kabaligtaran lang nung logic above duh pababa to e BWHAHAHAHAHHAHA
	        car.speed -= accel;
	        if (car.speed < -maxSpeed / 2) car.speed = -maxSpeed / 2; // Reverse speed cap
	    } else {
	        // dito na lilitaw si friction, it gradually makes the car halt by subtracting the
	    	// friction to the current speed
	        if (car.speed > 0) {
	            car.speed -= friction;
	            if (car.speed < 0) car.speed = 0;
	        } else if (car.speed < 0) {
	        	// kabaligtaran broski
	            car.speed += friction;
	            if (car.speed > 0) car.speed = 0;
	        }
	    }

	    // Steering
	    if (Math.abs(car.speed) > 0.1) {
	        double currentTurnSpeed = (car.speed > 0) ? turnSpeed : -turnSpeed;
	        if (left) car.angle -= currentTurnSpeed;
	        if (right) car.angle += currentTurnSpeed;
	    }
	}
	
	private double applyMovement(Car car, boolean leftLane) {
	    // horizontal movement
	    car.x += car.speed * Math.cos(Math.toRadians(car.angle));

	    // vertical world scrolling i2
	    double forwardMovement = -(car.speed * Math.sin(Math.toRadians(car.angle)));

	    // update camera of player1 or 2
	    if (leftLane) {
	        p1CameraY += forwardMovement;
	    } else {
	        p2CameraY += forwardMovement;
	    }

	    // car ay naka-anchor sa HEIGHT - 165
	    car.y = carFixedYPos;

	    // boundaries ng road para di makaalis sa road yung car
	    int currentRoadX = leftLane ? lrX : rrX;
	    if (car.x < currentRoadX) car.x = currentRoadX;
	    if (car.x > currentRoadX + roadWidth - car.width) {
	        car.x = currentRoadX + roadWidth - car.width;
	    }

	    // returns yung value ng forwardmovement para magamit sa pag-update ng background element
	    return forwardMovement;
	}
	
	private void updateBackgroundElements(ArrayList<BackgroundElement> elements, double movement, int roadX) {
	    for (BackgroundElement t : elements) {
	        t.y += movement;

	        // respawn logic
	        if (t.y > HEIGHT) {
	            t.y = -200; 
	            // randomly place sa ground (left of road at right of road)
	            boolean spawnOnLeft = random.nextBoolean();
	            if (spawnOnLeft) {
	                t.x = roadX - 100 - random.nextInt(100);
	            } else {
	                t.x = roadX + roadWidth - 60 + random.nextInt(100);
	            }
	        }
	    }
	}
	
	private void updatePlayerSide(Car car, boolean up, boolean down, boolean left, boolean right, boolean leftLane) {
		updateCarPhysics(car, up, down, left, right);

	    // dito naistore yung nai-return na forwardmovement sa applyMovement() method
	    double scrollSpeed = applyMovement(car, leftLane);

	    ArrayList<BackgroundElement> currentElements = leftLane ? p1BGE : p2BGE;
	    int currentRoadX = leftLane ? lrX : rrX;
	    
	    updateBackgroundElements(currentElements, scrollSpeed, currentRoadX);
	}
	
	private void checkWinner() {
		if (p1CameraY >= finishY || p2CameraY >= finishY) {
			isRunning = false;
			countdownActive = false;
			screenState = ScreenState.SUMMARY;
			roundsPlayed++;
		
			if (p1CameraY >= finishY && p2CameraY >= finishY) {
				statusMessage = "It's a tie! Both players reached the finish line.";
			} else if (p1CameraY >= finishY) {
				player1Wins++;
				statusMessage = "Player 1 wins this round!";
			} else {
				player2Wins++;
				statusMessage = "Player 2 wins this round!";
			}
		}
	}
	
	private void resetRacePositions() {
		player1.x = lrX + (roadWidth - 130);
		player1.y = carFixedYPos;
		player1.speed = 0;
		player1.angle = -90.0;
		
		player2.x = rrX + (roadWidth - 130);
		player2.y = carFixedYPos;
		player2.speed = 0;
		player2.angle = -90.0;
	}
	
	private void resetScores() {
		player1Wins = 0;
		player2Wins = 0;
		roundsPlayed = 0;
	}
	
	private void goToMenu() {
		isRunning = false;
		countdownActive = false;
		screenState = ScreenState.MENU;
		resetRacePositions();
		statusMessage = "Welcome to the race!";
	}
	
	private void newGame() {
		
		switch (levelSelection) {
			case 1:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				screenState = ScreenState.LEVEL1;
				startCountdown();
				break;
			case 2:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				screenState = ScreenState.LEVEL2;
				startCountdown();
				break;
			case 3:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				screenState = ScreenState.LEVEL3;
				startCountdown();
				break;
			case 4:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				screenState = ScreenState.LEVEL4;
				startCountdown();
				break;
			case 5:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				screenState = ScreenState.LEVEL5;
				startCountdown();
				break;
		}
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
		
		Shape fullScreen = g2.getClip();
		if (screenState == ScreenState.MENU) {
			drawMenuNew(g2);
			return;
		}
		
		if (screenState == ScreenState.LEVELSELECT) {
			drawLevelSelect(g2);
			return;
		}
		
		if (screenState == ScreenState.CARSELECT) {
			drawCarSelect(g2);
			return;
		}
		
		switch (levelSelection) {
		case 1 :
			drawFullScreen(g2);
			break;
		case 2 :
			drawFullScreen(g2);
			break;
		case 3 :
			drawFullScreen(g2);
			break;
		case 4 :
			drawFullScreen(g2);
			break;
		case 5 :
			drawFullScreen(g2);
			break;
		}
		
		if (countdownActive) {
			drawCountdown(g2);
		}
		
		if (screenState == ScreenState.SUMMARY) {
			drawSummary(g2);
		}
	}
	private void drawFullScreen(Graphics2D g2) {
		Shape fullScreen = g2.getClip();

		drawGround(g2);
		
	    g2.setClip(0, 0, WIDTH / 2, HEIGHT);   
	    drawRoad(g2);
	    drawRoadMarks(g2, true); // true = p1 logic
	    drawFinishLine(g2, true);
	    drawCar(g2, player1);
	    drawBackgroundElements(g2, true);

	    g2.setClip(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
	    drawRoad(g2);
	    drawRoadMarks(g2, false); // false = p2 logic
	    drawFinishLine(g2, false);
	    drawCar(g2, player2);
	    drawBackgroundElements(g2, false);

	    g2.setClip(fullScreen); // restore full screen drawing
	    g2.setColor(Color.WHITE);
	    g2.setStroke(new BasicStroke(4));
	    g2.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT); // gitna ng screen
	}
	
	private void drawDefaultScreenGraphics(Graphics2D g2) {
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
	}
	
	private void drawMenuNew(Graphics2D g2) {
		
		drawDefaultScreenGraphics(g2);
		
		// buttons duh
		drawMenuButton(g2, menuStartButton, "START", "#89d957");
		drawMenuButton(g2, menuInfoButton, "INFO", "#ffde59");
		drawMenuButton(g2, menuExitButton, "EXIT", "#ff5757");
		
		// main header
		drawHeadingText(g2, "The Weather Racing Game",
				Color.WHITE, 68, 255, 196);
		
		drawHeadingText(g2, "Bantula, Gutierrez, Saurane, Simborio, & Vallestero presents...",
				Color.WHITE, 20, 255, 135);
		
		
	}
	
	private void drawCarSelect(Graphics2D g2) {
		g2.setColor(Color.decode("#343837"));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		g2.setColor(Color.decode("#3c3c3c"));
		g2.fillRect(0, 170, WIDTH, 590);
		g2.setColor(Color.decode("#242424"));
		g2.fillRect(0, 380, WIDTH, 380);

		drawBoldText(g2, "Garage", Color.decode("#242424"), 150, 370, 150);
		
		g2.setColor(Color.decode("#121213"));
		g2.fillOval(-230, 180, 600, 600);
		g2.fillOval(910, 180, 600, 600);
		
		
		g2.setColor(Color.YELLOW);
		g2.drawRect(362, 104, 555, 555);
		
		for (int i = 0; i < colorsWindowSelection.size(); i++) {
			Rectangle currentButton = colorsWindowSelection.get(i);
			drawColorButton(g2, currentButton, palette[i]);
		}
		
		
	}
	
	private void drawLevelSelect(Graphics2D g2) {
		
		drawDefaultScreenGraphics(g2);
		
		drawHeadingText(g2, "Level Select", Color.WHITE, 68, 130, 275);
		
		drawButton(g2, level1Button, "1", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level2Button, "2", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level3Button, "3", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level4Button, "4", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level5Button, "5", "#ffffff", "#424348", "#353535", 5, 50);
	
	}

	private void drawClouds(Graphics2D g2) { // TODO: new cloud system
		if (isDay) {
			g2.setColor(Color.decode("#ffffff"));
		} else if (isNight) {
			g2.setColor(Color.decode("#424348"));
		}
		
		for (Cloud c : clouds) {
			g2.fillOval(c.x, c.y, c.size, c.size / 2);
			g2.fillOval(c.x + 26, c.y - 16, c.size, c.size / 2 + 10);
			g2.fillOval(c.x + 58, c.y, c.size, c.size / 2);
		}
	}

	private void drawGround(Graphics2D g2) {
		String color = "#000000";
		switch (levelSelection) {
			case 1 -> color = isDay ? "#559d00" : "#234100";
		    case 2 -> color = isDay ? "#c1c1c1" : "#323232";
		    case 3 -> color = isDay ? "#3c99c2" : "#ffffff";
		    case 4 -> color = isDay ? "#ddb97e" : "#180f0a";
		    case 5 -> color = "#000000";
		}
		
		g2.setColor(Color.decode(color));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
	}

	private void drawAccumulatedWeather(Graphics2D g2) {	// TODO
		if (wetLevel > 0) {
			for (Puddle p : puddles) {
				int alpha = (int) (100 * wetLevel);
				g2.setColor(new Color(70, 130, 190, alpha));
				g2.fillOval(p.x, p.y, p.w, p.h);
				g2.setColor(new Color(180, 220, 225, Math.max(alpha / 2, 1)));
				g2.drawOval(p.x + 5, p.y + 2, p.w - 10, p.h - 4);
			}
			
			g2.setColor(new Color(180, 210, 235, (int) (55 * wetLevel)));
			g2.fillRect(rrX, 90, roadWidth, HEIGHT - 60);
		}
		
		if (snowLevel > 0) {
			int snowHeight = (int) (35 * snowLevel);
			g2.setColor(new Color(245, 248, 250, 240));
			g2.fillRect(0, HEIGHT - snowHeight, WIDTH, snowHeight);
			
			g2.setColor(new Color(250, 250, 255, 220));
			g2.fillRect(0, HEIGHT - snowHeight - 6, WIDTH, 6);
		}
	}

	private void drawRoad(Graphics2D g2) {
		String color = "#000000";
		switch (levelSelection) {	// TODO: night colors
			case 1 -> color = isDay ? "#424348" : "#234100";
		    case 2 -> color = isDay ? "#323232" : "#323232";
		    case 3 -> color = isDay ? "#919191" : "#ffffff";
		    case 4 -> color = isDay ? "#9e9165" : "#180f0a";
		    case 5 -> color = "#000000";	// TODO: rainbow road
		}
		
		g2.setColor(Color.decode(color));
		g2.fillRect(lrX, 0, roadWidth, HEIGHT);
		g2.fillRect(rrX, 0, roadWidth, HEIGHT);
	}

	private void drawRoadMarks(Graphics2D g2, boolean isPlayer1) {
	    // assign variables depending sa player na nilalaro natin
	    double camY = isPlayer1 ? p1CameraY : p2CameraY;
	    int roadX = isPlayer1 ? lrX : rrX;
	    
	    String sideColor = "#FFFFFF";
	    String dashColor = "#FFFFFF";
	    int dashOffset = (int) (camY % 75);
	    
	    
	    if (levelSelection != 5) {
			if (levelSelection == 1) dashColor = isDay ? "#faff00" : "#234100";
			else if (levelSelection == 4) return;
		    
		    // dash lines (gitna)
		    g2.setColor(Color.decode(dashColor));
		    for (int y = -75; y < HEIGHT; y += 75) {
		        int drawY = y + dashOffset;
		        int centerX = roadX + (roadWidth / 2) - 5;
		        g2.fillRect(centerX, drawY, 10, 50);
		    }
	    }
	    
	    if (levelSelection == 5) sideColor = isDay ? "#faff00" : "#234100";
		 
		 // side lines
		    g2.setColor(Color.decode(sideColor));
		    g2.fillRect(roadX + 10, 0, 10, HEIGHT);
		    g2.fillRect(roadX + roadWidth - 20, 0, 10, HEIGHT);
	}

	private void drawFinishLine(Graphics2D g2, boolean isPlayer1) {
		int camY = isPlayer1 ? p1CameraY : p2CameraY;
	    int roadX = isPlayer1 ? lrX : rrX;
		int blockSize = 20;
		
		switch (levelSelection) {
		case 1 : finishY = 10000;
		case 2 : finishY = 12000;
		case 3 : finishY = 15000;
		case 4 : finishY = 20000;
		case 5 : finishY = 30000;
		}
		
		int drawY = carFixedYPos - (finishY - camY);
		
		if (drawY > -50 && drawY < HEIGHT) {
			for (int i = 0; i < roadWidth / blockSize; i++) {
				g2.setColor(i % 2 == 0 ? Color.WHITE : Color.BLACK);
				g2.fillRect(roadX + (i * blockSize), drawY, blockSize, 18);
			}
		}
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
	
	private void drawBackgroundElements(Graphics2D g2, boolean isPlayer1) {
		ArrayList<BackgroundElement> targetList = isPlayer1 ? p1BGE : p2BGE;
	    
		// TODO: IN PROGRESS
		switch (levelSelection) {
		case 1 : return;
	    case 2 : return;
	    case 3 : return;
	    case 4 :
	    	for (BackgroundElement bge : targetList) {
		        drawSavannahTree(g2, (int) bge.x, (int) bge.y, 0.15);
		    }
		    break;
	    case 5 : return;
		}
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
		// TODO: add highlights to the lights duh (check canva but u alr know ts)
		// pole & body
		g2.setColor(Color.decode("#121213"));
		g2.fillRect(75, 685, 30, 80);
		g2.fillRoundRect(50, 480, 80, 220, 15, 15);
		
		g2.setColor(trafficState == 0 ? Color.RED : new Color(90, 20, 20));
		g2.fillOval(62, 500, 55, 55);

		g2.setColor(trafficState == 1 ? Color.YELLOW : new Color(105, 105, 20));
		g2.fillOval(62, 565, 55, 55);
		
		g2.setColor(trafficState == 2 ? Color.GREEN : new Color(20, 90, 20));
		g2.fillOval(62, 630, 55, 55);
	}
	
	private void drawModeButtons(Graphics2D g2) {
		// TODO
	}
	private void drawHUDCarButtons(Graphics2D g2) {
		if (showCarHUDButtons == true) {
			drawButton(g2, normalCarButton, "NORMAL", "#89d957", "#ffffff", "#353535", 5, 16);
			drawButton(g2, luxuryCarButton, "LUXURY", "#ffde59", "#ffffff", "#353535", 5, 16);
			drawButton(g2, sportsCarButton, "SPORTS", "#ff5757", "#ffffff", "#353535", 5, 16);
		}
	}
	
	
	private void drawCar(Graphics2D g2, Car car) {
		AffineTransform oldTransform = g2.getTransform();

	    // 2. Move the "pen" to the car's position
	    g2.translate(car.x, car.y);

	    double scaledWidth = 304 * 0.2;  // Your car width * scale
	    double scaledHeight = 743 * 0.2; // Your car height * scale
	    
	    g2.rotate(Math.toRadians(car.angle + 90), scaledWidth / 2, scaledHeight / 2);

	    drawCarMcQueen(g2, 0, 0, 0.2);

	    // 5. Restore the canvas so other things (like the UI) don't draw tilted
	    g2.setTransform(oldTransform);
	}
	
	private void drawWeatherEffects(Graphics2D g2) {
		if (isRaining) {
			g2.setColor(new Color(190, 225, 255, 180));
			for (RainDrop d : rainDrops) {
				g2.drawLine(d.x, d.y, d.x - 3, d.y + 12);
			}
		}
		
		if (isSnowing) {
			g2.setColor(new Color(255, 255, 255, 225));
			for (SnowFlake f : snowFlakes) {
				g2.fillOval(f.x, f.y, f.size, f.size);
			}
		}
	}

	// TODO: fix appearance
	private void drawStars(Graphics2D g2) {
		if (isNight) {
			g2.setColor(Color.WHITE);
			for (RainDrop d : rainDrops) {
				g2.drawLine(d.x, d.y, d.x - 3, d.y + 12);
			}
		}
	}
	
	private void drawHUDSection(Graphics2D g2) {
		
		drawButton(g2, dayModeButton, "LEVELS", "#72c2f8", "#ffffff", "#ffffff", 5, 0);
		g2.setColor(Color.decode("#f8b30f"));
		g2.fillOval(uiX + (dayModeButton.width / 2) - 13, uiY + (dayModeButton.height / 2) - 13, 25, 25);
		g2.setColor(Color.decode("#ffffff"));
		g2.fillOval(uiX + (dayModeButton.width / 2) - 5, uiY + (dayModeButton.height / 2) + 3, 20, 10);
		
		drawButton(g2, nightModeButton, "LEVELS", "#000000", "#ffffff", "#ffffff", 5, 0);
		g2.setColor(Color.decode("#ffffff"));
		g2.fillOval(uiX + 55 + (dayModeButton.width / 2) - 13, uiY + (dayModeButton.height / 2) - 13, 25, 25);
		g2.setColor(Color.decode("#919191"));
		g2.fillOval(uiX + 55 + (dayModeButton.width / 2) - 5, uiY + (dayModeButton.height / 2) + 3, 20, 10);
		
		drawButton(g2, levelsButton, "LEVELS", "#919191", "#ffffff", "#ffffff", 5, 14);
		drawButton(g2, carsButton, "CARS", "#919191","#ffffff", "#ffffff", 5, 14);
		drawButton(g2, colorsButton, "COLORS", "#919191","#ffffff", "#ffffff", 5, 14);
		drawButton(g2, controlsButton, "CONTROLS", "#919191", "#ffffff", "#ffffff", 5, 14);
		drawButton(g2, menuButton, "MENU", "#919191", "#ffffff", "#ffffff", 5, 14);
	}
	
	private void drawControlsWindow(Graphics2D g2) {
		if (showControlsWindow == true) {
			drawWindow(g2, controlsWindow, "#919191", "#ffffff", 5);
			
			drawHeadingText(g2, "PLAYER 1", Color.WHITE, 25, 920, 130);
			drawParagraphText(g2, "Controls: WASD", Color.WHITE, 16, 920, 150);
			drawParagraphText(g2, "Turbo: Left SHIFT", Color.WHITE, 16, 920, 170);
			
			drawHeadingText(g2, "PLAYER 2", Color.WHITE, 25, 920, 225);
			drawParagraphText(g2, "Controls: Arrow Keys", Color.WHITE, 16, 920, 245);
			drawParagraphText(g2, "Turbo: ENTER", Color.WHITE, 16, 920, 265);
		}
	}
	
	private void drawColorsWindow(Graphics2D g2) {
		if (showColorsWindow == true) {
			drawWindow(g2, colorsWindow, "#919191", "#ffffff", 5);
		}
	}
	
	private void drawBuildingA(Graphics2D g2, int xOffset, int yOffset, double size) {
		//BUILDING1<<
		//wholeBuilding
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
		        g2.fillRect(currentX + xOffset, currentY , width, height);
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
		
		g2.setFont(new Font("Century Gothic", Font.BOLD, 150));
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
		
		// TODO: Re-add summary buttons
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		
		if (screenState == ScreenState.MENU) {
			if (menuStartButton.contains(p)) {
				screenState = ScreenState.LEVELSELECT;
			} else if (menuExitButton.contains(p)) {
				System.exit(0);
			}
			requestFocusInWindow();
			repaint();
			return;
		}
		
		if (screenState == ScreenState.LEVELSELECT) {
			if (level1Button.contains(p)) {
				levelSelection = 1;
				newGame();
			} else if (level2Button.contains(p)) {
				levelSelection = 2;
				newGame();
			} else if (level3Button.contains(p)) {
				levelSelection = 3;
				newGame();
			} else if (level4Button.contains(p)) {
				levelSelection = 4;
				newGame();
			} else if (level5Button.contains(p)) {
				levelSelection = 5;
				newGame();
			}
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
		
		// main HUD buttons
		// TODO: add necessary logic for each section
		if (dayModeButton.contains(p)) {
			isDay = true;
			isNight = false;
		} else if (nightModeButton.contains(p)) {
			isNight = true;
			isDay = false;
		} else if (levelsButton.contains(p)) {
			screenState = ScreenState.LEVELSELECT;
		} else if (carsButton.contains(p)) {
			showCarHUDButtons = !showCarHUDButtons;
			showControlsWindow = false;
			showColorsWindow = false;
		} else if (controlsButton.contains(p)) {
			showControlsWindow = !showControlsWindow;
			showCarHUDButtons = false;
			showColorsWindow = false;
		} else if (colorsButton.contains(p)) {
			showColorsWindow = !showColorsWindow;
			showCarHUDButtons = false;
			showControlsWindow = false;
		} else if (menuButton.contains(p)) {
			screenState = ScreenState.MENU;
		}
		
		// car HUD buttons
		if (normalCarButton.contains(p)) {
			carSelection = 1;
		} else if (luxuryCarButton.contains(p)) {
			carSelection = 2;
		} else if (sportsCarButton.contains(p)) {
			carSelection = 3;
		}
		
		
		// TODO: add car color change button logic
		
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
		
		// REMOVE: ts just for testing
		if (key == KeyEvent.VK_ESCAPE) screenState = ScreenState.LEVELSELECT;
		
		if (key == KeyEvent.VK_W) p1Up = true;
		if (key == KeyEvent.VK_S) p1Down = true;
		if (key == KeyEvent.VK_A) p1Left = true;
		if (key == KeyEvent.VK_D) p1Right = true;
		
		
		if (key == KeyEvent.VK_I) p2Up = true;
		if (key == KeyEvent.VK_K) p2Down = true;
		if (key == KeyEvent.VK_J) p2Left = true;
		if (key == KeyEvent.VK_L) p2Right = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_W) p1Up = false;
		if (key == KeyEvent.VK_S) p1Down = false;
		if (key == KeyEvent.VK_A) p1Left = false;
		if (key == KeyEvent.VK_D) p1Right = false;
		
		if (key == KeyEvent.VK_I) p2Up = false;
		if (key == KeyEvent.VK_K) p2Down = false;
		if (key == KeyEvent.VK_J) p2Left = false;
		if (key == KeyEvent.VK_L) p2Right = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
}