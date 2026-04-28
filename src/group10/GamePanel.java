package group10;

import group10.entities.*;
import group10.enums.ScreenState;
import group10.enums.TransitionDirection;
import group10.helpers.ColorPalette;
import group10.helpers.GameSettings;
import group10.graphics.LevelCards;
import group10.enums.AudioFiles;
import static group10.graphics.LevelCards.*;

import group10.systems.BackgroundManager;
import group10.systems.DayNightCycle;
import group10.systems.ObstacleManager;
import group10.systems.RaceState;
import group10.systems.SoundPlayer;
import group10.systems.WeatherSystem;

import static group10.graphics.CarModels.*;
import static group10.helpers.GUIHelpers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
	// DIMENSIONS
	private final int WIDTH = 1280;
	private final int HEIGHT = 760;

	// CAMERA VARIABLES
	private int p1CameraY = 0;
	private int p2CameraY = 0;

	// 60fps timer
	private final Timer timer = new Timer(16, this);

	// DEFAULT SCREENSTATE
	private ScreenState screenState = ScreenState.MENU;

	// ROAD VARIABLES
	private final int roadWidth = 360;
	private final int lrX = 140;
	private final int rrX = 780;

	// MAIN MENU BUTTONS
	private final Rectangle startButton = new Rectangle(WIDTH / 2 - 115, HEIGHT / 2 + 20, 222, 71);
	private final Rectangle exitButton  = new Rectangle(WIDTH / 2 - 115, HEIGHT / 2 + 100,  222, 71);

	// PAUSE SCREEN
	private final Rectangle resumeButton  = new Rectangle(50, HEIGHT / 2 - 80, 222, 71);
	private final Rectangle restartButton = new Rectangle(50, HEIGHT / 2,       222, 71);
	private final Rectangle menuButton    = new Rectangle(50, HEIGHT / 2 + 80,  222, 71);

	// LEVEL SELECT BUTTONS
	private final int levelButtonX = 240;
	private final int levelButtonY = 300;
	private final Rectangle level1Button = new Rectangle(levelButtonX,        levelButtonY, 160, 160);
	private final Rectangle level2Button = new Rectangle(levelButtonX + 210,  levelButtonY, 160, 160);
	private final Rectangle level3Button = new Rectangle(levelButtonX + 420,  levelButtonY, 160, 160);
	private final Rectangle level4Button = new Rectangle(levelButtonX + 630,  levelButtonY, 160, 160);
	
	// SOUND PLAYER OBJECT
	private final SoundPlayer aux = new SoundPlayer();
	
	// CONTROL PROCEED BUTTON
	private final Rectangle proceedToGame = new Rectangle(550, 510, 170, 60);

	ColorPalette[] palette = ColorPalette.values();
	private final List<Rectangle> colorSelection = new ArrayList<>();
	private int colorButStartX = 400;
	private int colorButStartY = 235;
	private int colorButSize = 50;
	private int colorButSpacing = 44 / 2;

	private final Rectangle summaryPlayAgainButton = new Rectangle(400, 600, 170, 50);
	private final Rectangle summaryMenuButton = new Rectangle(585, 600, 170, 50);
	private final Rectangle summaryExitButton = new Rectangle(770, 600, 110, 50);

	// SLIDE TRANSITION
	private boolean isSliding = false;
	private float slideProgress = 0f;
	private final float slideSpeed = 0.06f;
	private BufferedImage slideSnapshot = null;
	private TransitionDirection direction = TransitionDirection.LEFT;

	// SHOW WITH BUTTONS BOOLEANS
	private boolean showControlWindow = false;
	private boolean showCardWindow = false;

	// KEYBOARD CONTROLS
	private boolean p1Up, p1Down, p1Left, p1Right, p1Brake, p1Rev, p1Horn;
	private boolean p2Up, p2Down, p2Left, p2Right, p2Brake, p2Rev, p2Horn;

	// CAR OTHER STUFF BRUH
	private boolean player1IsLocked = false;
	private boolean player2IsLocked = false;
	private final Car player1;
	private final Car player2;
	private final int carFixedYPos = HEIGHT - 165;
	
	// CAR SELECT COUNTER
	private int carSelectCounter = 0;
	
	// SYSTEMS
	private final WeatherSystem weather = new WeatherSystem(WIDTH, HEIGHT);
	private final DayNightCycle dayNight = new DayNightCycle();
	private final BackgroundManager bgManager = new BackgroundManager(HEIGHT, lrX, rrX, roadWidth);
	private final ObstacleManager obstacles = new ObstacleManager(HEIGHT, lrX, rrX, roadWidth, carFixedYPos);
	private final RaceState race = new RaceState();

	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);

		// car color palette
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 7; col++) {
				int currentX = colorButStartX + (col * (colorButSize + colorButSpacing));
				int currentY = colorButStartY + (row * (colorButSize + colorButSpacing));
				colorSelection.add(new Rectangle(currentX, currentY, colorButSize, colorButSize));
			}
		}

		bgManager.spawnInitial();

		player1 = new Car(lrX + 100, carFixedYPos, new Color(225, 55, 55), "P1", true);
		player2 = new Car(rrX + 100, carFixedYPos, new Color(55, 110, 230), "P2", false);

		timer.start();
		
		aux.loop(AudioFiles.MENU);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (race.isPaused && race.isPlayingLevel(screenState)) {
			repaint();
			return;
		}

		// updates every frame on approximately 60fps
		weather.update();
		weather.updateClouds();
		race.updateCountdown();
		updateSlide();
		updateCarSelect();
		weather.updateRandomWeather(race.isRunning, race.countdownActive);
		dayNight.update();

		if (race.isPlayingLevel(screenState) && race.isRunning && !race.countdownActive) {
			updatePlayerSides();
			checkWinner();
			obstacles.update(race.isRunning, race.countdownActive,
					p1CameraY, p2CameraY, player1, player2);
			obstacles.checkCollisions(player1, player2, p1CameraY, p2CameraY);
		}

		repaint();
	}

	private void startCountdown() {
		resetRacePositions();
		race.startCountdown();
	}

	private void updateCarSelect() {
		if (screenState != ScreenState.CARSELECT) {
			carSelectCounter = 0;
			return;
		}
		if (player1IsLocked && player2IsLocked) {
			carSelectCounter++;
			if (carSelectCounter >= 40) {
				carSelectCounter = 0;
				newGame();
				player1IsLocked = false;
				player2IsLocked = false;
			}
		} else {
			carSelectCounter = 0;
		}
	}

	private void updatePlayerSides() {
		updatePlayerSide(player1, p1Up, p1Down, p1Left, p1Right, p1Rev, p1Horn, true);
		updatePlayerSide(player2, p2Up, p2Down, p2Left, p2Right, p2Rev, p2Horn, false);
	}

	private void updateCarPhysics(Car car, boolean up, boolean down, 
			boolean left, boolean right, boolean isRevving, boolean isHonking, boolean brake) {
		double accel = 0.18;
		double maxSpeed = 17.5;
		double friction = brake ? 1.0 : 0.1;
		double turnSpeed = 3.5;

		if (isRevving && Math.abs(car.speed) < 0.1) {
			car.revCharge = Math.min(car.revCharge + 0.05, 1.0);
		}
		
		if (weather.isRaining) turnSpeed += 2.0;
		if (weather.isSnowing) turnSpeed -= 3.0; 
		
		if (up) {
			// If launching from a stop with rev charged, dump it as a boost.
			double boost = (Math.abs(car.speed) < 0.1) ? car.revCharge * 6.0 : 0.0;
			car.speed += accel + boost;
			car.revCharge = 0;
			if (car.speed > maxSpeed) car.speed = maxSpeed;
		} else if (down) {
			car.speed -= accel;
			if (car.speed < -maxSpeed / 2) car.speed = -maxSpeed / 2;
		} else {
			if (car.speed > 0) {
				car.speed -= friction;
				if (car.speed < 0) car.speed = 0;
			} else if (car.speed < 0) {
				car.speed += friction;
				if (car.speed > 0) car.speed = 0;
			}
		}

		if (Math.abs(car.speed) > 0.1) {
			double currentTurnSpeed = (car.speed > 0) ? turnSpeed : -turnSpeed;
			if (left)  car.angle -= currentTurnSpeed;
			if (right) car.angle += currentTurnSpeed;
		}
	}

	private double applyMovement(Car car, boolean leftLane) {
		car.x += car.speed * Math.cos(Math.toRadians(car.angle));

		if (weather.isWindy && weather.windLevel > 0) {
			car.x -= WeatherSystem.WIND_PUSH_PER_FRAME * weather.windLevel;
		}

		double forwardMovement = -(car.speed * Math.sin(Math.toRadians(car.angle)));

		if (leftLane) {
			p1CameraY += forwardMovement;
		} else {
			p2CameraY += forwardMovement;
		}

		car.y = carFixedYPos;

		int currentRoadX = leftLane ? lrX : rrX;
		if (car.x < currentRoadX) car.x = currentRoadX;
		if (car.x > currentRoadX + roadWidth - car.width) {
			car.x = currentRoadX + roadWidth - car.width;
		}

		return forwardMovement;
	}

	private void updatePlayerSide(Car car, boolean up, boolean down, 
			boolean left, boolean right, boolean isRevving, boolean isHonking, boolean leftLane) {
		updateCarPhysics(car, up, down, left, right, isRevving, isHonking, leftLane ? p1Brake : p2Brake);
		double scrollSpeed = applyMovement(car, leftLane);
		bgManager.updatePlayer(leftLane, scrollSpeed);
	}

	private void transitionTo(ScreenState newState, TransitionDirection dir) {
		boolean wasInMenuZone = (screenState == ScreenState.MENU
				|| screenState == ScreenState.LEVELSELECT);
		boolean goingToMenuZone = (newState == ScreenState.MENU
				|| newState == ScreenState.LEVELSELECT);
		
		if (wasInMenuZone != goingToMenuZone) {
	        aux.stopAll();
	    }

		if (isSliding) return;
		slideSnapshot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = slideSnapshot.createGraphics();
		sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintScreen(sg);
		sg.dispose();
		screenState = newState;
		isSliding = true;
		slideProgress = 0f;
		direction = dir;
		
		if (goingToMenuZone && !wasInMenuZone) {
	        aux.loop(AudioFiles.MENU);
	    }

	}

	private void updateSlide() {
		if (!isSliding) return;
		slideProgress += slideSpeed;
		if (slideProgress >= 1f) {
			slideProgress = 1f;
			isSliding = false;
			slideSnapshot = null;
		}
	}

	private void checkWinner() {
		// nagvavary per level yung race state, refer to RaceState sa group10.systems
		race.updateFinishY(GameSettings.levelSelection);

		int result = race.checkWinner(p1CameraY, p2CameraY);
		if (result != RaceState.NO_WINNER) {
			aux.play(AudioFiles.SNAP);
			// snapshot the screen for the summary card
			race.summarySnapshot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D sg = race.summarySnapshot.createGraphics();
			sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			paintScreen(sg);
			sg.dispose();
			screenState = ScreenState.SUMMARY;
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

		p1CameraY = 0;
		p2CameraY = 0;
	}

	private void goToMenu() {
		race.isRunning = false;
		showCardWindow = false;
		showControlWindow = false;
		race.countdownActive = false;
		transitionTo(ScreenState.MENU, TransitionDirection.LEFT);
		resetRacePositions();
	}

	private void newGame() {
		obstacles.reset();
		bgManager.spawnInitial();
		aux.stopAll();
		
		switch (GameSettings.levelSelection) {
		case 1:
			race.resetScores();
			weather.resetLevels();
			transitionTo(ScreenState.LEVEL1, TransitionDirection.DOWN);
			playLevelAudio();
			resetRacePositions();
			showControlWindow = true;
			break;
		case 2:
			race.resetScores();
			weather.resetLevels();
			transitionTo(ScreenState.LEVEL2, TransitionDirection.DOWN);
			playLevelAudio();
			resetRacePositions();
			showControlWindow = true;
			break;
		case 3:
			race.resetScores();
			weather.resetLevels();
			transitionTo(ScreenState.LEVEL3, TransitionDirection.DOWN);
			playLevelAudio();
			resetRacePositions();
			showControlWindow = true;
			break;
		case 4:
			race.resetScores();
			weather.resetLevels();
			transitionTo(ScreenState.LEVEL4, TransitionDirection.DOWN);
			playLevelAudio();
			resetRacePositions();
			showControlWindow = true;
			break;
		}
	}

	private void playAgain() {
		obstacles.reset();
		// removes yung screenshot from the last game
		race.summarySnapshot = null;

		bgManager.spawnInitial();
		switch (GameSettings.levelSelection) {
		case 1 -> screenState = ScreenState.LEVEL1;
		case 2 -> screenState = ScreenState.LEVEL2;
		case 3 -> screenState = ScreenState.LEVEL3;
		case 4 -> screenState = ScreenState.LEVEL4;
		}
		
		resetRacePositions();
		showControlWindow = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// check if a transition is happening, before i-paint yung normal screen with normal draw calls
		if (isSliding && slideSnapshot != null) {
			float t = 1f - (float) Math.pow(1f - slideProgress, 3);
			int dx = 0, dy = 0;
			int nx = 0, ny = 0;
			
			switch (direction) {
			case LEFT -> {
				dx = -(int) (WIDTH * t);
				nx = WIDTH + dx;
			}
			case RIGHT -> {
				dx = (int) (WIDTH * t);
				nx = -WIDTH + dx;
			}
			case UP -> {
				dy = -(int) (HEIGHT * t);
				ny = HEIGHT + dy;
			}
			case DOWN -> {
				dy = (int) (HEIGHT * t);
				ny = -HEIGHT + dy;
			}
			}
			
			g2.drawImage(slideSnapshot, dx, dy, null);
			Graphics2D ng = (Graphics2D) g2.create();
			ng.translate(nx, ny);
			paintScreen(ng);
			ng.dispose();
			
		} else {
			paintScreen(g2);
		}
	}

	private void paintScreen(Graphics2D g2) {
		if (screenState == ScreenState.MENU) {
			drawMenuNew(g2);
			return;
		}

		if (screenState == ScreenState.LEVELSELECT) {
			drawLevelSelect(g2);
			drawCard(g2);
			return;
		}

		if (screenState == ScreenState.CARSELECT) {
			drawCarSelect(g2);
			return;
		}

		switch (GameSettings.levelSelection) {
		case 1: drawFullScreen(g2); break;
		case 2: drawFullScreen(g2); break;
		case 3: drawFullScreen(g2); break;
		case 4: drawFullScreen(g2); break;
		}

		drawControlWindow(g2);

		if (race.countdownActive) {
			drawCountdown(g2);
		}

		if (screenState == ScreenState.SUMMARY) {
			drawSummary(g2);
		}

		if (race.isPaused && race.isPlayingLevel(screenState)) {
			drawPauseOverlay(g2);
		}
	}

	private void togglePause() {
		race.togglePause(screenState);
	}

	private void drawCard(Graphics2D g2) {
		if (showCardWindow) {
			drawCardWindow(g2);
		}
	}

	private void drawFullScreen(Graphics2D g2) {
		Shape fullScreen = g2.getClip();	
		
		drawGround(g2);

		g2.setClip(0, 0, WIDTH / 2, HEIGHT);
		drawRoad(g2);
		drawRoadMarks(g2, true);
		drawFinishLine(g2, true);
		drawCar(g2, player1);
		obstacles.draw(g2, true, p1CameraY);
		bgManager.draw(g2, true);

		g2.setClip(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
		drawRoad(g2);
		drawRoadMarks(g2, false);
		drawFinishLine(g2, false);
		drawCar(g2, player2);
		obstacles.draw(g2, false, p2CameraY);
		bgManager.draw(g2, false);

		g2.setClip(fullScreen);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(4));
		g2.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

		weather.drawClouds(g2);
		weather.drawWeatherEffects(g2);
		dayNight.draw(g2, WIDTH, HEIGHT);
		
		drawTracker(g2);

		if (race.isPaused) {
			drawPauseOverlay(g2);
		}
	}

	private void playLevelAudio() {
		switch (GameSettings.levelSelection) {
		case 1 -> aux.loop(AudioFiles.LEVEL1);
		case 2 -> aux.loop(AudioFiles.LEVEL2);
		case 3 -> aux.loop(AudioFiles.LEVEL3);
		case 4 -> aux.loop(AudioFiles.LEVEL4);
		}
	}
	
	private void drawTracker(Graphics2D g2) {
		int p1Meters = p1CameraY / 20;
		int p2Meters = p2CameraY / 20;
		
		Color leading = Color.decode("#7ed957");
		Color dragging = Color.decode("#ff5757");
		
		int finishing = 1500;
		
		if (p1Meters == p2Meters) {
			drawHeadingText(g2, p1Meters + " m", leading, 28, lrX - 120, 40);
			drawHeadingText(g2, p2Meters + " m", leading, 28, rrX - 120, 40);
		} else if (p1Meters > p2Meters) {
			drawHeadingText(g2, p1Meters + " m", leading, 28, lrX - 120, 40);
			drawHeadingText(g2, p2Meters + " m", dragging, 28, rrX - 120, 40);
		} else {
			drawHeadingText(g2, p1Meters + " m", dragging, 28, lrX - 120, 40);
			drawHeadingText(g2, p2Meters + " m", leading, 28, rrX - 120, 40);
		}
		
		switch (GameSettings.levelSelection) {
		case 1 -> finishing = 20000 / 20;
		case 2 -> finishing = 40000 / 20;
		case 3 -> finishing = 60000 / 20;
		case 4 -> finishing = 80000 / 20;
		}
		
		drawHeadingText(g2, finishing + " m", Color.WHITE, 28, lrX - 120, 70);
		drawHeadingText(g2, finishing + " m", Color.WHITE, 28, rrX - 120, 70);
	}
	
	// less stuff compared to drawFullScreen, meant for MENU at LEVELSELECT screenStates
	private void drawQuickScreen(Graphics2D g2) {
		Shape fullScreen = g2.getClip();
		int savedLevel = GameSettings.levelSelection;
		GameSettings.levelSelection = 1;

		drawGround(g2);

		drawRoad(g2);
		drawRoadMarks(g2, true);
		bgManager.draw(g2, true);

		drawRoadMarks(g2, false);
		bgManager.draw(g2, false);

		g2.setClip(fullScreen);

		weather.drawClouds(g2);
		weather.drawWeatherEffects(g2);

		GameSettings.levelSelection = savedLevel;
	}

	// the slight gradient (gray) on most screens para medyo may smooth transition & less
	// flatter atmosphere
	private void drawFadingScreen(Graphics2D g2, int r, int g, int b, String fadeFrom) {
		int c1v = 0;
		int c2v = 255;

		Color c1 = new Color(r, g, b, c1v);
		Color c2 = new Color(r, g, b, c2v);

		GradientPaint fade = new GradientPaint(0, HEIGHT, c1, WIDTH, HEIGHT, c2);

		switch (fadeFrom) {
		case "left": fade = new GradientPaint(0, HEIGHT, c2, WIDTH, HEIGHT, c1); break;
		case "right": fade = new GradientPaint(0, HEIGHT, c1, WIDTH, HEIGHT, c2); break;
		case "up": fade = new GradientPaint(0, 0, c2, 0, HEIGHT, c1); break;
		case "down": fade = new GradientPaint(0, 0, c1, 0, HEIGHT, c2); break;
		}
		
		g2.setPaint(fade);
		g2.fillRect(0, 0, WIDTH, HEIGHT);
	}

	private void drawMenuNew(Graphics2D g2) {
		drawQuickScreen(g2);
		drawFadingScreen(g2, 52, 56, 55, "down");

		drawPrimaryButton(g2, startButton, "START", 40, "#89d957");
		drawPrimaryButton(g2, exitButton,  "EXIT",  40, "#ff5757");

		drawHeadingText(g2, "Travel Rivalry", Color.WHITE, 100, 375, 350);
		drawHeadingText(g2, "Bantula, Gutierrez, Saurane, Simborio, & Vallestero presents...",
				Color.WHITE, 30, 250, 260);
	}

	private void drawCarSelect(Graphics2D g2) {
		resetRacePositions();
		player1.x = 0;
		player1.y = (HEIGHT / 2) + 100;

		player2.x = WIDTH;
		player2.y = (HEIGHT / 2) + 130;

		g2.setColor(Color.decode("#343837"));
		g2.fillRect(0, 0, WIDTH, HEIGHT);

		g2.setColor(Color.decode("#3c3c3c"));
		g2.fillRect(0, 170, WIDTH, 590);
		g2.setColor(Color.decode("#242424"));
		g2.fillRect(0, 380, WIDTH, 380);

		drawBoldText(g2, "Garage", Color.decode("#242424"), 150, 370, 150);
		drawBoldText(g2, "Left Mouse Button for P1, Right Mouse Button for P2", Color.decode("#242424"), 20, 420, 220);
		g2.setColor(Color.decode("#121213"));
		g2.fillOval(-230, 180, 600, 600);
		g2.fillOval(910, 180, 600, 600);
		
		if (!player1IsLocked) {
			g2.setColor(Color.decode("#121213"));
			g2.fillOval(-230, 180, 600, 600);
			drawBoldText(g2, "Z to Lock", Color.decode("#242424"), 60, 10, 360);
		} else {
			g2.setColor(Color.decode("#559d00"));
			g2.fillOval(-230, 180, 600, 600);
		}

		if (!player2IsLocked) {
			g2.setColor(Color.decode("#121213"));
			g2.fillOval(910, 180, 600, 600);
			drawBoldText(g2, "M to Lock", Color.decode("#242424"), 60, 1000, 360);
		} else {
			g2.setColor(Color.decode("#559d00"));
			g2.fillOval(910, 180, 600, 600);
		}
		
		for (int i = 0; i < colorSelection.size(); i++) {
			Rectangle currentButton = colorSelection.get(i);
			drawColorButton(g2, currentButton, palette[i]);
		}

		drawCarAtGarage(g2, player1);
		drawCarAtGarage(g2, player2);
	}

	private void drawLevelSelect(Graphics2D g2) {
		drawQuickScreen(g2);
		drawFadingScreen(g2, 52, 56, 55, "down");

		drawHeadingText(g2, "Level Select", Color.WHITE, 68, 240, 275);

		drawButton(g2, level1Button, "1", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level2Button, "2", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level3Button, "3", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level4Button, "4", "#ffffff", "#424348", "#353535", 5, 50);

		if (showCardWindow) {
			g2.setColor(new Color(0, 0, 0, 120));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			LevelCards.drawCardWindow(g2);
		}
	}

	private void drawControlWindow(Graphics2D g2) {
		if (showControlWindow) {
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		drawHeadingText(g2, "Controls", Color.WHITE, 100, 470, 200);
		drawControlsText(g2, "Player 1", 280, 185);
		drawControlButton(g2, "W", 290, 225);
		drawControlButton(g2, "A", 230, 285);
		drawControlButton(g2, "S", 290, 285);
		drawControlButton(g2, "D", 350, 285);
		
		drawControlButton(g2, "Q", 240, 365);
		drawControlsText(g2, "Horn", 320, 405);
		drawControlButton(g2, "E", 240, 445);
		drawControlsText(g2, "Slam Brake", 320, 485);
		drawControlButton(g2, "C", 240, 525);
		drawControlsText(g2, "Rev", 320, 565);
		
		drawControlsText(g2, "Player 2", 920, 185);
		drawControlButton(g2, "I", 930, 225);
		drawControlButton(g2, "J", 870, 285);
		drawControlButton(g2, "K", 930, 285);
		drawControlButton(g2, "L", 990, 285);
		
		drawControlButton(g2, "U", 880, 365);
		drawControlsText(g2, "Horn", 960, 405);
		drawControlButton(g2, "O", 880, 445);
		drawControlsText(g2, "Slam Brake", 960, 485);
		drawControlButton(g2, ".", 880, 525);
		drawControlsText(g2, "Rev", 960, 565);
		
		drawControlButton(g2, "Esc", 525, 365);
		drawControlsText(g2, "Pause Game", 605, 405);
		
		drawPrimaryButton(g2, proceedToGame, "WE GET IT...", 28, "#89d957");
		} else {
			return;
		}
	}
	private void drawGround(Graphics2D g2) {
		String color = "#000000";
		switch (GameSettings.levelSelection) {
		case 1 -> color = "#559d00";
		case 2 -> color = "#c1c1c1";
		case 3 -> color = "#3c99c2";
		case 4 -> color = "#ddb97e";
		}
		
		g2.setColor(Color.decode(color));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		if (GameSettings.levelSelection == 2) {
	        int brickW = 60;
	        int brickH = 30;
	        int gap = 3;                        
	        g2.setColor(Color.decode("#c3c3c3"));
	        int row = 0;
	        for (int y = 0; y < HEIGHT; y += brickH + gap) {
	            int xOffset = (row % 2 == 0) ? 0 : -brickW / 2;
	            for (int x = xOffset; x < WIDTH; x += brickW + gap) {
	                g2.fillRect(x, y, brickW, brickH);
	            }
	            row++;
	        }
	    }
	}

	private void drawRoad(Graphics2D g2) {
		String color = "#000000";
		switch (GameSettings.levelSelection) {
		case 1 -> color = "#424348";
		case 2 -> color = "#323232";
		case 3 -> color = "#919191";
		case 4 -> color = "#9e9165";
		}

		g2.setColor(Color.decode(color));
		g2.fillRect(lrX, 0, roadWidth, HEIGHT);
		g2.fillRect(rrX, 0, roadWidth, HEIGHT);
	}

	private void drawRoadMarks(Graphics2D g2, boolean isPlayer1) {
		double camY = isPlayer1 ? p1CameraY : p2CameraY;
		int roadX = isPlayer1 ? lrX : rrX;

		String sideColor = "#FFFFFF";
		String dashColor = "#FFFFFF";
		int dashOffset = (int) (camY % 75);

		switch (GameSettings.levelSelection) {
		case 1 : dashColor = "#faff00";
		case 2 :
		case 3 :
			g2.setColor(Color.decode(dashColor));
			for (int y = -75; y < HEIGHT; y += 75) {
				int drawY = y + dashOffset;
				int centerX = roadX + (roadWidth / 2) - 5;
				g2.fillRect(centerX, drawY, 10, 50);
			}
			g2.setColor(Color.decode(sideColor));
			g2.fillRect(roadX + 10, 0, 10, HEIGHT);
			g2.fillRect(roadX + roadWidth - 20, 0, 10, HEIGHT);
			break;
		case 4 :
			g2.setColor(Color.decode("#958960"));
			int addLine = 25;
			for (int i = 0; i < 4; i++) {
				g2.fillRect(roadX + addLine, 0, 40, HEIGHT);
				addLine = addLine + 90;
			}
			break;
		}
	}

	private void drawFinishLine(Graphics2D g2, boolean isPlayer1) {
		int camY = isPlayer1 ? p1CameraY : p2CameraY;
		int roadX = isPlayer1 ? lrX : rrX;
		int blockSize = 20;

		race.updateFinishY(GameSettings.levelSelection);

		int drawY = carFixedYPos - (race.finishY - camY);

		if (drawY > -50 && drawY < HEIGHT) {
			for (int i = 0; i < roadWidth / blockSize; i++) {
				g2.setColor(i % 2 == 0 ? Color.WHITE : Color.BLACK);
				g2.fillRect(roadX + (i * blockSize), drawY, blockSize, 18);
			}
		}
	}

	private void drawCar(Graphics2D g2, Car car) {
		AffineTransform oldTransform = g2.getTransform();

		g2.translate(car.x, car.y);

		double scaledWidth = 304 * 0.2;
		double scaledHeight = 743 * 0.2;

		g2.rotate(Math.toRadians(car.angle + 90), scaledWidth / 2, scaledHeight / 2);

		if (car.isPlayer1) {
			drawCarMcQueen(g2, 0, 0, 0.2, true);
		} else {
			drawCarMcQueen(g2, 0, 0, 0.2, false);
		}

		g2.setTransform(oldTransform);
	}

	private void drawCarAtGarage(Graphics2D g2, Car car) {
		AffineTransform oldTransform = g2.getTransform();

		double scaledWidth = 304 * 0.7;
		double scaledHeight = 743 * 0.7;

		g2.translate(car.x, car.y);

		if (car.isPlayer1) {
			g2.rotate(Math.toRadians(car.angle + 180));
		} else {
			g2.rotate(Math.toRadians(car.angle));
		}

		drawCarMcQueen(g2, (int)(-scaledWidth / 2), (int)(-scaledHeight / 2), 0.7, car.isPlayer1);

		g2.setTransform(oldTransform);
	}

	private void drawCountdown(Graphics2D g2) {
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(0, 0, WIDTH, HEIGHT);

		if (race.countdownText.equals("GO!")) {
			drawHeadingText(g2, race.countdownText, Color.WHITE, 200, (WIDTH / 2) - 175, (HEIGHT / 2) + 50);
		} else {
			drawHeadingText(g2, race.countdownText, Color.WHITE, 200, (WIDTH / 2) - 50, (HEIGHT / 2) + 50);
		}
		
	}

	private void drawSummary(Graphics2D g2) {
		g2.setColor(Color.decode("#353535"));
		g2.fillRect(175, 130, 925, 500);
		
		if (race.summarySnapshot != null) {
			g2.drawImage(race.summarySnapshot, 185, 140, 900, 475, null);
		}
		
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(8));
		g2.drawRect(185, 140, 900, 475);

		drawPrimaryButton(g2, summaryPlayAgainButton, "PLAY AGAIN", 22, "#89d957");
		drawPrimaryButton(g2, summaryMenuButton, "MENU", 22, "#ffde59");
		drawPrimaryButton(g2, summaryExitButton, "EXIT", 22, "#ff5757");
	}

	private void drawPauseOverlay(Graphics2D g2) {
		g2.setColor(new Color(125, 123, 106, 150));
		g2.fillRect(0, 0, WIDTH, HEIGHT);

		g2.setColor(Color.WHITE);
		g2.fillOval(-175, 30, 670, 670);

		drawBoldText(g2, "Paused", Color.BLACK, 100, 40, 280);

		drawPrimaryButton(g2, resumeButton,  "RESUME",  40, "#89d957");
		drawPrimaryButton(g2, restartButton, "RESTART", 40, "#ffde59");
		drawPrimaryButton(g2, menuButton,    "MENU",    40, "#ff5757");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();

		if (race.isPaused && race.isPlayingLevel(screenState)) {
			if (resumeButton.contains(p)) {
				race.isPaused = false;
			} else if (restartButton.contains(p)) {
				race.isPaused = false;
				newGame();
			} else if (menuButton.contains(p)) {
				race.isPaused = false;
				transitionTo(ScreenState.MENU, TransitionDirection.UP);
			}
			requestFocusInWindow();
			repaint();
			return;
		}

		if (race.isPlayingLevel(screenState) && showControlWindow) {
			if (proceedToGame.contains(p)) {
				showControlWindow = false;
				startCountdown();
			}
			requestFocusInWindow();
			repaint();
			return;
		}

		if (screenState == ScreenState.MENU) {
			if (startButton.contains(p)) {
				transitionTo(ScreenState.LEVELSELECT, TransitionDirection.LEFT);
			} else if (exitButton.contains(p)) {
				System.exit(0);
			}
			requestFocusInWindow();
			repaint();
			return;
		}

		if (screenState == ScreenState.LEVELSELECT) {
			if (showCardWindow) {
				if (LevelCards.proceed.contains(p)) {
					GameSettings.levelSelection = GameSettings.projectedlevelSelection;
					showCardWindow = false;
					transitionTo(ScreenState.CARSELECT, TransitionDirection.UP);
					aux.play(AudioFiles.GARAGE);
				} else if (LevelCards.nevermind.contains(p)) {
					showCardWindow = false;
				}
				requestFocusInWindow();
				repaint();
				return;
			}

			if (level1Button.contains(p))      { GameSettings.projectedlevelSelection = 1; showCardWindow = true; }
			else if (level2Button.contains(p)) { GameSettings.projectedlevelSelection = 2; showCardWindow = true; }
			else if (level3Button.contains(p)) { GameSettings.projectedlevelSelection = 3; showCardWindow = true; }
			else if (level4Button.contains(p)) { GameSettings.projectedlevelSelection = 4; showCardWindow = true; }
		}

		if (screenState == ScreenState.CARSELECT) {
			for (int i = 0; i < colorSelection.size(); i++) {
				if (colorSelection.get(i).contains(p)) {
					if (player2IsLocked) return;
					if (SwingUtilities.isRightMouseButton(e)) {
						GameSettings.selectedCarColorP2 = palette[i].getHexCode();
						System.out.println("P2 Color changed to: " + palette[i].name());
					} else if (SwingUtilities.isLeftMouseButton(e)) {
						if (player1IsLocked) return;
						GameSettings.selectedCarColorP1 = palette[i].getHexCode();
						System.out.println("P1 Color changed to: " + palette[i].name());
					}
					break;
				}
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

		if (screenState == ScreenState.LEVELSELECT) {
			if (key == KeyEvent.VK_ESCAPE) transitionTo(ScreenState.MENU, TransitionDirection.RIGHT);
		} else if (screenState == ScreenState.CARSELECT) {
			if (key == KeyEvent.VK_ESCAPE) transitionTo(ScreenState.LEVELSELECT, TransitionDirection.DOWN);
		} else {
			if (key == KeyEvent.VK_ESCAPE) togglePause();
		}

		if (key == KeyEvent.VK_W) p1Up = true;
		if (key == KeyEvent.VK_S) p1Down = true;
		if (key == KeyEvent.VK_A) p1Left = true;
		if (key == KeyEvent.VK_D) p1Right = true;
		if (key == KeyEvent.VK_E) p1Brake = true;
		
		if (GameSettings.levelSelection > 0 && key == KeyEvent.VK_C && !p1Rev) {
			p1Rev = true;
			aux.play(AudioFiles.REV);
		}
		if (GameSettings.levelSelection > 0 && key == KeyEvent.VK_PERIOD && !p2Rev) {
			p2Rev = true;
			aux.play(AudioFiles.REV);
		}
		

		if (GameSettings.levelSelection > 0 && key == KeyEvent.VK_Q && !p1Horn) {
		    p1Horn = true;
		    aux.play(AudioFiles.HONK);
		}
		if (GameSettings.levelSelection > 0 && key == KeyEvent.VK_U && !p2Horn) {
		    p2Horn = true;
		    aux.play(AudioFiles.HONK);
		}
		
		if (key == KeyEvent.VK_1) weather.isRaining = true;
		if (key == KeyEvent.VK_2) weather.isSnowing = true;
		if (key == KeyEvent.VK_5) weather.isWindy   = true;

		if (screenState == ScreenState.CARSELECT) {
			if (key == KeyEvent.VK_Z) player1IsLocked = true;
			if (key == KeyEvent.VK_M) player2IsLocked = true;
		}

		if (key == KeyEvent.VK_I) p2Up = true;
		if (key == KeyEvent.VK_K) p2Down = true;
		if (key == KeyEvent.VK_J) p2Left = true;
		if (key == KeyEvent.VK_L) p2Right = true;
		if (key == KeyEvent.VK_O) p2Brake = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_W) p1Up = false;
		if (key == KeyEvent.VK_S) p1Down = false;
		if (key == KeyEvent.VK_A) p1Left = false;
		if (key == KeyEvent.VK_D) p1Right = false;
		if (key == KeyEvent.VK_E) p1Brake = false;
		
		if (key == KeyEvent.VK_C) p1Rev = false;
		if (key == KeyEvent.VK_PERIOD) p2Rev = false;
		
		if (key == KeyEvent.VK_Q) p1Horn = false;
		if (key == KeyEvent.VK_U) p2Horn = false;

		if (key == KeyEvent.VK_1) weather.isRaining = false;
		if (key == KeyEvent.VK_2) weather.isSnowing = false;
		if (key == KeyEvent.VK_5) weather.isWindy   = false;

		if (key == KeyEvent.VK_Z) showCardWindow = false;

		if (key == KeyEvent.VK_I) p2Up = false;
		if (key == KeyEvent.VK_K) p2Down = false;
		if (key == KeyEvent.VK_J) p2Left = false;
		if (key == KeyEvent.VK_L) p2Right = false;
		if (key == KeyEvent.VK_O) p2Brake = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
