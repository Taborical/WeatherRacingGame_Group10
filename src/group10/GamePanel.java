package group10;

import group10.entities.*; // import lahat dun sa package ng entities
import static group10.helpers.GUIHelpers.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
// trad java libraries

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
	private final int WIDTH = 1280;
	private final int HEIGHT = 760;
	
	// 60fps (1000ms/16 == 62.5 approximated to 60)
	private final Timer timer = new Timer(16, this);
	private final Random random = new Random();
	
	private ScreenState screenState = ScreenState.MENU;
	
	// TODO: revamp
	private int roadX = 350;
	private final int roadWidth = 580;
	private final int laneWidth = roadWidth / 2;
	private final int finishY = 110;
	
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
	private final String[] hexCarColorPalette = { "#FF0000", "#FFA500", "#FFFF00", "#008000", "#008080", "#800080", "#964B00", "#FFFFFF", "#000000" };
	private final List<Rectangle> colorsWindowSelection = new ArrayList<>();
	private int colorButStartX = 920;
	private int colorButStartY = 105;
	private int colorButSize = 42; // button is square
	private int colorButSpacing = 42 / 2;
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
	
		// try catch block handles errors
		try {
			level1Background = ImageIO.read(getClass().getResource("/res/level1.png"));
		} catch (IOException e) {
			e.printStackTrace();	// pag nagthrow si java ng IOException, print da reason
		}
		
		for (int row = 0; row < 3; row++) {
	        for (int col = 0; col < 3; col++) {
	            int currentX = colorButStartX + (col * (colorButSize + colorButSpacing));
	            int currentY = colorButStartY + (row * (colorButSize + colorButSpacing));
	            colorsWindowSelection.add(new Rectangle(currentX, currentY, colorButSize, colorButSize));
	        }
	    }
		
		// TODO: new roads means new placement logic soon too
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
		
		// updates 60x per second as per the timer
		updateTrafficLight();
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
			System.out.println("UPDATING CARS! P1 UP IS: " + p1Up);
			updateCars();
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
		if (trafficCounter >= 140) {
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
	
	private void updateCars() {
		updateSingleCar(player1, p1Up, p1Down, p1Left, p1Right, true);
		updateSingleCar(player2, p2Up, p2Down, p2Left, p2Right, false);
	}
	
	private void updateSingleCar(Car car, boolean up, boolean down, boolean left, boolean right, boolean leftLane) {
		double accel = 0.18;
		double maxSpeed = 5.7;
		double friction = 0.06;
		double turnSpeed = 3.5;
		
		if (isRaining) {
			maxSpeed = 4.9;
			accel = 0.14;
			turnSpeed = 4;
		}
		
		if (isSnowing) {
			maxSpeed = 4.2;
			accel = 0.11;
			friction = 0.035;
			turnSpeed = 3;
		}
		
		if (wetLevel > 0.6) maxSpeed -= 0.35;
		if (snowLevel > 0.6) maxSpeed -= 0.45;
		
		if (trafficState == 0 && car.y < 280 && car.y > 180) {
			maxSpeed = Math.min(maxSpeed, 1.0);
		}
		
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
	    
	    if (Math.abs(car.speed) > 0.1) {
	        double currentTurnSpeed = (car.speed > 0) ? turnSpeed : -turnSpeed;
	        
	        if (left) car.angle -= currentTurnSpeed;
	        if (right) car.angle += currentTurnSpeed;
	    }

	    car.x += car.speed * Math.cos(Math.toRadians(car.angle));
	    car.y += car.speed * Math.sin(Math.toRadians(car.angle));

	    if (car.x < roadX) car.x = roadX;
	    if (car.x > roadX + roadWidth - car.width) car.x = roadX + roadWidth - car.width;
	    if (car.y < 0) car.y = 0;
	    if (car.y > HEIGHT - car.height) car.y = HEIGHT - car.height;
	}
	
	private void checkWinner() {
		if (player1.y <= finishY || player2.y <= finishY) {
			isRunning = false;
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
		player1.angle = -90.0;
		
		player2.x = roadX + laneWidth + 80;
		player2.y = HEIGHT - 165;
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
		
		if (screenState == ScreenState.MENU) {
			drawMenuNew(g2);
			return;
		}
		
		if (screenState == ScreenState.LEVELSELECT) {
			drawLevelSelect(g2);
			return;
		}
		
		switch (levelSelection) {
		case 1 :
			drawSky(g2);
			drawSunOrMoon(g2);
			drawClouds(g2);
			drawGround(g2);
			drawAccumulatedWeather(g2);
			drawTrees(g2);
			drawTrafficLight(g2);
			drawCars(g2);
			drawWeatherEffects(g2);
			drawHUDSection(g2);
			drawHUDCarButtons(g2);
			drawColorsWindow(g2);
			drawControlsWindow(g2);
			drawCurrentLevelText(g2);
			break;
		case 2 :
			drawSky(g2);
			drawSunOrMoon(g2);
			drawClouds(g2);
			drawGround(g2);
			drawAccumulatedWeather(g2);
			drawTrees(g2);
			drawTrafficLight(g2);
			drawCars(g2);
			drawWeatherEffects(g2);
			drawHUDSection(g2);
			drawHUDCarButtons(g2);
			drawColorsWindow(g2);
			drawControlsWindow(g2);
			drawCurrentLevelText(g2);
			break;
		case 3 :
			drawSky(g2);
			drawSunOrMoon(g2);
			drawClouds(g2);
			drawGround(g2);
			drawAccumulatedWeather(g2);
			drawTrees(g2);
			drawTrafficLight(g2);
			drawCars(g2);
			drawWeatherEffects(g2);
			drawHUDSection(g2);
			drawHUDCarButtons(g2);
			drawColorsWindow(g2);
			drawControlsWindow(g2);
			drawCurrentLevelText(g2);
			break;
		case 4 :
			drawSky(g2);
			drawSunOrMoon(g2);
			drawClouds(g2);
			drawGround(g2);
			drawAccumulatedWeather(g2);
			drawTrees(g2);
			drawTrafficLight(g2);
			drawCars(g2);
			drawWeatherEffects(g2);
			drawHUDSection(g2);
			drawHUDCarButtons(g2);
			drawColorsWindow(g2);
			drawControlsWindow(g2);
			drawCurrentLevelText(g2);
			break;
		case 5 :
			drawSky(g2);
			drawSunOrMoon(g2);
			drawClouds(g2);
			drawGround(g2);
			drawAccumulatedWeather(g2);
			drawTrees(g2);
			drawTrafficLight(g2);
			drawCars(g2);
			drawWeatherEffects(g2);
			drawHUDSection(g2);
			drawHUDCarButtons(g2);
			drawColorsWindow(g2);
			drawControlsWindow(g2);
			drawCurrentLevelText(g2);
			break;
		}
		
		if (level1Background != null) {
			drawTransparentImage(g2, level1Background, 0, 0, WIDTH, HEIGHT, 0.1f);
		}
		
		if (countdownActive) {
			drawCountdown(g2);
		}
		
		if (screenState == ScreenState.SUMMARY) {
			drawSummary(g2);
		}
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
	
	private void drawLevelSelect(Graphics2D g2) {
		
		drawDefaultScreenGraphics(g2);
		
		drawHeadingText(g2, "Level Select", Color.WHITE, 68, 130, 275);
		
		drawButton(g2, level1Button, "1", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level2Button, "2", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level3Button, "3", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level4Button, "4", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level5Button, "5", "#ffffff", "#424348", "#353535", 5, 50);
		
	}
	
	private void drawCurrentLevelText(Graphics2D g2) {
		// TODO: screenState == LEVELZ as final value
		if (isDay == true && isNight == false) {
			g2.setColor(Color.decode("#624500"));
			g2.fillRect(30, 35, 240, 85);
			g2.setColor(Color.decode("#241a00"));
			g2.drawRect(30, 35, 240, 85);
		} else if (isNight == true && isDay == false) {
			g2.setColor(Color.decode("#251a00"));
			g2.fillRect(30, 35, 240, 85);
			g2.setColor(Color.decode("#100a07"));
			g2.drawRect(30, 35, 240, 85);
		}
		
		switch (levelSelection) {
		case 1:
			drawHeadingText(g2, "Level 1", Color.WHITE, 40, 40, 80);
			drawParagraphText(g2, "The Road Less Traveled", Color.WHITE, 21, 43, 105);
			break;
		case 2:
			drawHeadingText(g2, "Level 2", Color.WHITE, 40, 40, 80);
			drawParagraphText(g2, "Tenement Square", Color.WHITE, 21, 43, 105);
			break;
		case 3:
			drawHeadingText(g2, "Level 3", Color.WHITE, 40, 40, 80);
			drawParagraphText(g2, "Intersecting Highways", Color.WHITE, 21, 43, 105);
			break;
		case 4:
			drawHeadingText(g2, "Level 4", Color.WHITE, 40, 40, 80);
			drawParagraphText(g2, "Savannah Offroading", Color.WHITE, 21, 43, 105);
			break;
		case 5:
			drawHeadingText(g2, "Level 5", Color.WHITE, 40, 40, 80);
			drawParagraphText(g2, "Rainbow Road", Color.WHITE, 21, 43, 105);
			break;
		}
	}
	
	// TODO: read the method name brotha
	private void drawCurrentLevelSky() {
		return;
	}
	
	// TODO: unfinished duh
	private void drawSky(Graphics2D g2) {
		
		switch (levelSelection) {
		case 1 :
			if (isDay) {
				g2.setColor(Color.decode("#72c2f8"));
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			} else if (isNight) {
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			}
			break;
		case 2 :
			if (isDay) {
				g2.setColor(Color.decode("#72c2f8"));
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			} else if (isNight) {
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			}
			break;
		case 3 :
			if (isDay) {
				g2.setColor(Color.decode("#72c2f8"));
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			} else if (isNight) {
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			}
			break;
		case 4 :
			if (isDay) {
				g2.setColor(Color.decode("#99b9e2"));
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			} else if (isNight) {
				g2.setColor(Color.decode("#b03509"));
				g2.fillRect(0, 0, WIDTH, HEIGHT);
			}
			break;
		case 5 :
			g2.setColor(Color.decode("#05052b"));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			break;
		}
	}
	
	private void drawSunOrMoon(Graphics2D g2) {
		if (isDay) {
			g2.setColor(Color.decode("#f8b30f"));
			g2.fillOval(1100, 20, 150, 150);
			g2.setColor(Color.decode("#ffbf28"));
			g2.fillOval(1120, 40, 110, 110);
			g2.setColor(Color.decode("#ffcd57"));
			g2.fillOval(1150, 50, 80, 45);
		} else if (isNight) {
			g2.setColor(Color.decode("#ffffff"));
			g2.fillOval(1100, 20, 150, 150);
			g2.setColor(Color.decode("#c1c1c1"));
			g2.fillOval(1120, 40, 110, 110);
			g2.setColor(Color.decode("#000000"));
			g2.fillOval(1125, 0, 150, 150);
		}
	}

	private void drawClouds(Graphics2D g2) {
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
		// NOTE: DON'T DRAW THE GROUND IN LEVEL 2 & 5! HANE!
		// TODO: unfinished colors
		switch (levelSelection) {
		case 1 :
			if (isDay == true) {
				g2.setColor(Color.decode("#559d00"));
				g2.fillRect(0, 280, WIDTH, HEIGHT - 280);
				g2.setColor(Color.decode("#529600"));
				g2.fillRect(0, 475, WIDTH, HEIGHT - 475);	
			} else if (isNight == true && isDay == false) {
				g2.setColor(Color.decode("#234100"));
				g2.fillRect(0, 280, WIDTH, HEIGHT - 280);
				g2.setColor(Color.decode("#1e3700"));
				g2.fillRect(0, 475, WIDTH, HEIGHT - 475);
			}
			break;
		case 3:
			if (isDay == true) {
				g2.setColor(Color.decode("#559d00"));
				g2.fillRect(0, 280, WIDTH, HEIGHT - 280);
				g2.setColor(Color.decode("#529600"));
				g2.fillRect(0, 475, WIDTH, HEIGHT - 475);	
			} else if (isNight == true && isDay == false) {
				g2.setColor(Color.decode("#234100"));
				g2.fillRect(0, 280, WIDTH, HEIGHT - 280);
				g2.setColor(Color.decode("#1e3700"));
				g2.fillRect(0, 475, WIDTH, HEIGHT - 475);
			}
			break;
		case 4 :
			if (isDay == true) {
				g2.setColor(Color.decode("#ddb97e"));
				g2.fillRect(0, 280, WIDTH, HEIGHT - 280);
				g2.setColor(Color.decode("#bf9f6a"));
				g2.fillRect(0, 475, WIDTH, HEIGHT - 475);	
			} else if (isNight == true && isDay == false) {
				g2.setColor(Color.decode("#180f0a"));
				g2.fillRect(0, 280, WIDTH, HEIGHT - 280);
				g2.setColor(Color.decode("#110906"));
				g2.fillRect(0, 475, WIDTH, HEIGHT - 475);
			}
			break;
		}
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
		
		// default w/h nung trees
		int dfltWidthA = 220;
		int dfltHeightA = 280;
		int dfltWidthB = 160;
		int dfltHeightB = 280;
		
		// medium version & small version nung tree, add lang ito sa w/h as scale
		double szM = 0.8;
		double szS = 0.6;
		
		drawSingleTree(g2, dfltWidthA, dfltHeightA, -70, 200, this.isDay);
		drawLayeredTree(g2, dfltWidthB, dfltHeightB, 60, 140, this.isDay);
		drawSingleTree(g2, dfltWidthA * szM, dfltHeightA * szM, 145, 160, this.isDay);
		drawLayeredTree(g2, dfltWidthB * szM, dfltHeightB * szM, 250, 135, this.isDay);
		drawSingleTree(g2, dfltWidthA * szS, dfltHeightA * szS, 320, 170, this.isDay);
		drawLayeredTree(g2, dfltWidthB * szS, dfltHeightB * szS, 420, 150, this.isDay);
		
		drawSingleTree(g2, dfltWidthA * szS, dfltHeightA * szS, 750, 150, this.isDay);
		drawSingleTree(g2, dfltWidthA, dfltHeightA, 790, 60, this.isDay);
		drawLayeredTree(g2, dfltWidthB * szM, dfltHeightB * szM, 930, 140, this.isDay);
		drawLayeredTree(g2, dfltWidthB * szS, dfltHeightB * szS, 1030, 220, this.isDay);
		drawSingleTree(g2, dfltWidthA * szS, dfltHeightA * szS, 1080, 250, this.isDay);
		drawLayeredTree(g2, dfltWidthB, dfltHeightB, 1160, 190, this.isDay);
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
	
	private void drawCars(Graphics2D g2) {
		drawCar(g2, player1);
		drawCar(g2, player2);
	}
	
	private void drawCar(Graphics2D g2, Car car) {
		int x = (int) car.x;
		int y = (int) car.y;
		
		// rotational logic
	    AffineTransform oldTransform = g2.getTransform();
	    g2.rotate(Math.toRadians(car.angle + 90), x + (car.width / 2.0), y + (car.height / 2.0));
	    
	    // START DRAWING THE CAR
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
		
		// ENDS OR RESTORES THE DRAW STATE PARA DI MA-ROTATE YUNG FOLLOWING CHANGES SA G2
		// kumbaga naka sandwich sa rotate method at setTransform method yung car graphics
		// at iyun lang maaapektuhan ng rotation
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
			
			for (int i = 0; i < colorsWindowSelection.size(); i++) {
				Rectangle currentButton = colorsWindowSelection.get(i);
				drawButton(g2, currentButton, " ", hexCarColorPalette[i], "#FFFFFF", hexCarColorPalette[i], 5, 1);
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
		
		if (key == KeyEvent.VK_W) p1Up = true;
		if (key == KeyEvent.VK_S) p1Down = true;
		if (key == KeyEvent.VK_A) p1Left = true;
		if (key == KeyEvent.VK_D) p1Right = true;
		
		if (key == KeyEvent.VK_UP) p2Up = true;
		if (key == KeyEvent.VK_DOWN) p2Down = true;
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
