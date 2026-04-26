package group10;

import group10.entities.*; // import lahat dun sa package ng entities
import group10.helpers.ColorPalette;
import group10.helpers.GameSettings;
import group10.graphics.LevelCards;
import static group10.graphics.LevelCards.*;
import static group10.graphics.Obstacles.*;

import static group10.graphics.BackgroundElements.*;
import static group10.graphics.CarModels.*;
import static group10.helpers.GUIHelpers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
// trad java libraries

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
	// DIMENSIONS BABY
	private final int WIDTH = 1280;
	private final int HEIGHT = 760;
	
	// CAMERA SYSTEM
	private int p1CameraY = 0;
	private int p2CameraY = 0;
	
	// 60fps (1000ms/16 == 62.5 approximated to 60)
	private final Timer timer = new Timer(16, this);
	private final Random random = new Random();
	
	// DEFAULT SCREENSTATE (pagkasimula nung game)
	private ScreenState screenState = ScreenState.MENU;
	
	// ROAD VARIABLES
	private final int roadWidth = 360;
	private final int lrX = 140;
	private final int rrX = 780;
    private int finishY = 1000;
	
	// MAIN MENU BUTTONS
	private final Rectangle startButton = new Rectangle(50, HEIGHT / 2 - 80, 222, 71);    
	private final Rectangle infoButton = new Rectangle(50, HEIGHT / 2, 222, 71);
	private final Rectangle exitButton = new Rectangle(50, HEIGHT / 2 + 80, 222, 71);
	
	// PAUSE SCREEN
	private boolean isPaused = false;
	private final Rectangle resumeButton = new Rectangle(50, HEIGHT / 2 - 80, 222, 71);    
	private final Rectangle restartButton = new Rectangle(50, HEIGHT / 2, 222, 71);
	private final Rectangle menuButton = new Rectangle(50, HEIGHT / 2 + 80, 222, 71);
	
	// LEVEL SELECT BUTTONS
	private final int levelButtonX = 135;
	private final int levelButtonY = 300;
	
	private final Rectangle level1Button = new Rectangle(levelButtonX, levelButtonY, 160, 160);
	private final Rectangle level2Button = new Rectangle(levelButtonX + 210, levelButtonY, 160, 160);
	private final Rectangle level3Button = new Rectangle(levelButtonX + 420, levelButtonY, 160, 160);
	private final Rectangle level4Button = new Rectangle(levelButtonX + 630, levelButtonY, 160, 160);
	
	
	// GAME BUTTONS
	
	// ui button starting coordinate values
	private final int uiX = 1135;
	private final int uiY = 35;
	
	private final Rectangle dayModeButton = new Rectangle(uiX, uiY, 42, 42);
	private final Rectangle nightModeButton = new Rectangle(uiX + 55, uiY, 42, 42);
	
	private final Rectangle normalCarButton = new Rectangle(uiX - 115, uiY + 55, 100, 42);
	private final Rectangle luxuryCarButton = new Rectangle(uiX - 115, uiY + 110, 100, 42);
	private final Rectangle sportsCarButton = new Rectangle(uiX - 115, uiY + 165, 100, 42);
	
	private final Rectangle controlsWindow = new Rectangle(uiX - 235, uiY + 55, 215, 200);
	private final Rectangle colorsWindow = new Rectangle(uiX - 235, uiY + 55, 215, 200);
	
	
	ColorPalette[] palette = ColorPalette.values();
	private final List<Rectangle> colorsWindowSelection = new ArrayList<>();
	private int colorButStartX = 400;
	private int colorButStartY = 235;
	private int colorButSize = 50; // button is square
	private int colorButSpacing = 44 / 2;
	// the for loop for this list is inside the constructor dito
	
	private BufferedImage summarySnapshot = null;
	private final Rectangle summaryPlayAgainButton = new Rectangle(430, 495, 170, 50);
	private final Rectangle summaryMenuButton = new Rectangle(615, 495, 170, 50);	// originally (615, 495, 170, 50)
	private final Rectangle summaryExitButton = new Rectangle(800, 495, 110, 50);	// originally (800, 495, 110, 50)
	
	// SLIDE TRANSITION
	private boolean isSliding = false;
	private float slideProgress = 0f;
	private final float slideSpeed = 0.06f;
	private BufferedImage slideSnapshot = null;
	private TransitionDirection direction = TransitionDirection.LEFT;
	
	// SIDE BUTTONS BOOLEANS
	private boolean isDay = true;
	private boolean showCarHUDButtons = false;
	private boolean showControlsWindow = false;
	private boolean showColorsWindow = false;
	private boolean showCardWindow = false;
	
	// WEATHER BOOLEANS
	private boolean isRunning = false;
	private boolean isRaining = false;
	private boolean isSnowing = false;
	private boolean isWindy = false;	// TODO: windy weather ay itutulak si car at a certain angle
	private boolean isNormal = true;
	
	// RANDOM WEATHER SCHEDULER YABADABADOO
	private int weatherTicksLeft = 0;          // frames until next na pagbabago ni weather
	private static final int MIN_WEATHER_FRAMES = 60 * 8;   // 8 seconds
	private static final int MAX_WEATHER_FRAMES = 60 * 20;  // 20 seconds

	// KOTSE CONTROLS
	private boolean p1Up, p1Down, p1Left, p1Right, p1Brake;
	private boolean p2Up, p2Down, p2Left, p2Right, p2Brake;
	
	// COUNTDOWN
	private boolean countdownActive = false;
	private int countdownValue = 3;
	private int countdownTick = 0;
	private String countdownText = "";
	
	// WINS COUNTER
	private int player1Wins = 0;
	private int player2Wins = 0;
	private int roundsPlayed = 0;
	private String statusMessage = "Welcome to the race!";
	
	// CAR MISCELLANEOUS (di ko macategorize ng maayos ih)
	private boolean player1IsLocked = false;
	private boolean player2IsLocked = false;
	private final Car player1;
	private final Car player2;
	private final int carFixedYPos = HEIGHT - 165;
	
	// PARTICLE ENTITIES
	private final List<RainDrop> rainDrops = new ArrayList<>();
	private final List<SnowFlake> snowFlakes = new ArrayList<>();
	private final List<Cloud> clouds = new ArrayList<>();
	private final List<Puddle> puddles = new ArrayList<>();
	
	private final ArrayList<BackgroundElement> p1BGE = new ArrayList<>();
	private final ArrayList<BackgroundElement> p2BGE = new ArrayList<>();
	
	// WEATHER LEVELS
	private double snowLevel = 0;
	private double wetLevel = 0;
	
	// UPDATION COUNTERS / TICKS
	private int cloudTick = 0;
	private int carSelectCounter = 0;
	
	// OBSTACLE VARIABLES & LISTS
	private final ArrayList<Obstacle> p1Obstacles = new ArrayList<>();
	private final ArrayList<Obstacle> p2Obstacles = new ArrayList<>();
	private double p1NextSpawnY = -1;
	private double p2NextSpawnY = -1;
	
	// STUN STATE
	// btw 60ticks * 6deg/tick = exactly one 360 spin per second yessir
	
	private static final int STUN_FRAMES = 60;
	private int    p1StunTicks = 0,  p2StunTicks = 0;
	private double p1PreStunAngle = 0, p2PreStunAngle = 0;
	
	// TODO: separate file! class ito e dapat talaga kaso tinamad si edu kasi i-cocommit na niya e
	private static final class BGType {
	    final int width;       // rendered footprint width
	    final int height;      // rendered footprint height para sa overlap detection
	    final int weight;      // spawn frequency
	    final int pad;         // gap between road at etong element
	    final int xScatter;    // horizontal random values
	    final int yScatter;    // vertical random values
	    BGType(int width, int height, int weight, int pad, int xScatter, int yScatter) {
	        this.width = width;
	        this.height = height;
	        this.weight = weight;
	        this.pad = pad;
	        this.xScatter = xScatter;
	        this.yScatter = yScatter;
	    }
	}
	
	// TODO: eto din, separate file, preferably group10.helpers
	private static final class BGConfig {
	    final BGType[] types;
	    final int totalWeight;
	    BGConfig(BGType... types) {
	        this.types = types;
	        int sum = 0;
	        for (BGType t : types) sum += t.weight;
	        this.totalWeight = sum;
	    }
	}
	
	private final BGConfig[] levelBGConfigs = { null,	// for index 0 to, pero nagstart ako sa 1
			// A Niche Grove
			new BGConfig(
					new BGType(137, 200, 1, 30,  80, 600),   // single tree
					new BGType(116, 240, 1, 30,  80, 600)    // layered tree
					),
			// Tenement Square
			new BGConfig(
					new BGType(220, 380, 1, 20,  60, 900),   // hotel
					new BGType(320, 200, 2, 20,  60, 900),   // shop
					new BGType(263, 200, 2, 20,  60, 900)    // sheriff station
					),
			// Seaway
			new BGConfig(
					new BGType(320, 620, 2,  30,   0, 1400),  // cargo ship
					new BGType(160, 200, 1, -30, 100,  700),  // yacht
					new BGType(110, 160, 1, -30, 100,  700)   // sailboat
					),
			// Savannahroading
			new BGConfig(
					new BGType(285, 340, 10, -100, 100, 800),  // savannah tree
					new BGType(320, 200, 2, -50, 100, 800),  // bushes A
					new BGType(320, 200, 2, -50, 100, 800)   // bushes B
					),
	};

	private BGConfig currentBGConfig() {
	    BGConfig c = (GameSettings.levelSelection >= 1 && GameSettings.levelSelection < levelBGConfigs.length)
	                 ? levelBGConfigs[GameSettings.levelSelection] : null;
	    return c != null ? c : levelBGConfigs[1];
	}
	
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
		
		spawnInitialBackgroundElements();
		
		player1 = new Car(lrX + 100, carFixedYPos, new Color(225, 55, 55), "P1", true);
		player2 = new Car(rrX + 100, carFixedYPos, new Color(55, 110, 230), "P2", false);
		
		for (int i = 0; i < 180; i++) {
			rainDrops.add(new RainDrop(random.nextInt(WIDTH), random.nextInt(HEIGHT), 6 + random.nextInt(8)));
		}
	
		for (int i = 0; i < 150; i++) {
			snowFlakes.add(new SnowFlake(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2 + random.nextInt(4)));
		}
		
		for (int i = 0; i < 5; i++) {
		    clouds.add(new Cloud(
		        random.nextInt(WIDTH + 200) - 100,
		        50 + random.nextInt(HEIGHT - 100),
		       200 + random.nextInt(40)
		    ));
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
		if (isPaused && isPlayingLevel()) {
	        repaint();
	        return;                         // freeze weather/countdown/cars LAHAT basta naka-pause
	    }
		
		// updates 60x per second as per the timer
		updateWeather();
		updateClouds();
		updateCountdown();
		updateSlide();
		updateCarSelect();
		updateRandomWeather();
		
		
		if (isPlayingLevel() && isRunning && !countdownActive) {
			updatePlayerSides();
			checkWinner();
			updateObstacles();
			checkObstacleCollisions();
		}
		
		// repaints every frame
		repaint();
	}
	
	private boolean isPlayingLevel() {
	    return screenState == ScreenState.LEVEL1
	            || screenState == ScreenState.LEVEL2
	            || screenState == ScreenState.LEVEL3
	            || screenState == ScreenState.LEVEL4;
	    }
	
	private void updateTimer() {
		if (!countdownActive) return;
		
		countdownTick++;
		if (countdownTick >= 60) {
			countdownTick = 0;
			countdownValue--;
		}
	}

	
	private void updateCountdown() {
		updateTimer();
		
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
	
	private void startCountdown() {
		resetRacePositions();
		countdownActive = true;
		isRunning = false;
		countdownValue = 3;
		countdownTick = 0;
		countdownText = "3";
		statusMessage = "Get ready...";
	}
	
	private void updateCarSelect() {
	    if (screenState != ScreenState.CARSELECT) {
	    	carSelectCounter = 0;
	        return;
	    }
	    if (player1IsLocked && player2IsLocked) {
	    	carSelectCounter++;
	        if (carSelectCounter >= 40) {       // 40 frames = 0.67 secs
	        	carSelectCounter = 0;
	        	newGame();
	        	player1IsLocked = false;
	        	player2IsLocked = false;
	        }
	    } else {
	    	carSelectCounter = 0;               // reset if either unlocks
	    }
	    
	    
	}
	
	private void updateRandomWeather() {
	    // roll for weather if tumatakbo na yung countdown at merong race
	    if (!isRunning || countdownActive) return;
	    weatherTicksLeft--;
	    if (weatherTicksLeft > 0) return;
	    // pick random weather
	    int roll = random.nextInt(3);
	    isNormal = (roll == 0);
	    isRaining   = (roll == 1);
	    isSnowing = (roll == 2);
	    // schedule yung next change in weather
	    weatherTicksLeft = MIN_WEATHER_FRAMES
	                     + random.nextInt(MAX_WEATHER_FRAMES - MIN_WEATHER_FRAMES);
	}
	
	// REMOVED TRAFFIC LIGHT, DAS BULLSHIT!
	
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
		
		if (isNormal) {
			wetLevel -= 0.0022;
			if (wetLevel < 0) wetLevel = 0;
			
			snowLevel -= 0.0018;
			if (snowLevel < 0) snowLevel = 0;
		}
	}
	
	private void updateClouds() {
		cloudTick++;
		if (cloudTick % 3 == 0) {   // cloud ay gagalaw 1px to the right every 3 frames, basically 20px/sec
		    for (Cloud c : clouds) {
		        c.x += 1;
		        if (c.x > WIDTH + 120) {
		            c.x = -180;
		            c.y = 50 + random.nextInt(HEIGHT - 100);
		        }
		    }
		}
	}


	
	private void updatePlayerSides() {
		updatePlayerSide(player1, p1Up, p1Down, p1Left, p1Right, true);
		updatePlayerSide(player2, p2Up, p2Down, p2Left, p2Right, false);
	}
	
	private void updateCarPhysics(Car car, boolean up, boolean down, boolean left, boolean right, boolean brake) {
	    double accel = 0.18;
	    double maxSpeed = 20.0;
	    double friction = brake ? 1.0 : 0.1;
	    double turnSpeed = 3.5;

	    // REMOVED WEATHER BASED CONDITIONS, CUZ THATS BULLSHIT! aka tamad lang si edu
	    
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
	
	private int pickWeightedType(BGConfig cfg) {
	    int roll = random.nextInt(cfg.totalWeight);
	    for (int i = 0; i < cfg.types.length; i++) {
	        roll -= cfg.types[i].weight;
	        if (roll < 0) return i;
	    }
	    return 0;
	}
	private void placeBackgroundElement(BackgroundElement bge, BGConfig cfg, int roadX) {
	    BGType t = cfg.types[bge.type];
	    int jitter = (t.xScatter > 0) ? random.nextInt(t.xScatter) : 0;
	    if (random.nextBoolean()) {
	        bge.x = roadX - t.pad - t.width - jitter;             // left of road
	    } else {
	        bge.x = roadX + roadWidth + t.pad + jitter;           // right of road
	    }
	}
	
	private void respawnBackgroundElement(BackgroundElement bge, BGConfig cfg, int roadX) {
	    bge.type = pickWeightedType(cfg);
	    BGType t = cfg.types[bge.type];
	    bge.y = -200 - random.nextInt(Math.max(1, t.yScatter));
	    placeBackgroundElement(bge, cfg, roadX);
	}
	
	private Rectangle elementBox(BackgroundElement bge, BGConfig cfg) {
	    BGType t = cfg.types[bge.type];
	    return new Rectangle((int) bge.x, (int) bge.y, t.width, t.height);
	}
	
	private void removeOverlappingElements(ArrayList<BackgroundElement> elements, BGConfig cfg) {
	    // walk newest-to-oldest; if a later element overlaps any earlier one, drop it
	    for (int i = elements.size() - 1; i >= 0; i--) {
	        Rectangle a = elementBox(elements.get(i), cfg);
	        for (int j = 0; j < i; j++) {
	            if (a.intersects(elementBox(elements.get(j), cfg))) {
	                elements.remove(i);
	                break;
	            }
	        }
	    }
	}
	
	private void spawnInitialBackgroundElements() {
	    p1BGE.clear();
	    p2BGE.clear();
	    BGConfig cfg = currentBGConfig();
	    for (int i = 0; i < 5; i++) {
	        p1BGE.add(makeBackgroundElement(cfg, lrX, i * -300));
	        p2BGE.add(makeBackgroundElement(cfg, rrX, i * -300));
	    }
	}
	private BackgroundElement makeBackgroundElement(BGConfig cfg, int roadX, int initialY) {
	    BackgroundElement bge = new BackgroundElement(0, initialY);
	    bge.type = pickWeightedType(cfg);
	    placeBackgroundElement(bge, cfg, roadX);
	    return bge;
	}
	
	private void updateBackgroundElements(ArrayList<BackgroundElement> elements,
			double movement, int roadX) {
		BGConfig cfg = currentBGConfig();
		for (BackgroundElement bge : elements) {
			bge.y += movement;
			if (bge.y > HEIGHT) respawnBackgroundElement(bge, cfg, roadX);
		}
		removeOverlappingElements(elements, cfg);
	}

	private void updatePlayerSide(Car car, boolean up, boolean down, boolean left, boolean right, boolean leftLane) {
		updateCarPhysics(car, up, down, left, right, leftLane ? p1Brake : p2Brake);

	    // dito naistore yung nai-return na forwardmovement sa applyMovement() method
	    double scrollSpeed = applyMovement(car, leftLane);

	    ArrayList<BackgroundElement> currentElements = leftLane ? p1BGE : p2BGE;
	    int currentRoadX = leftLane ? lrX : rrX;
	    
	    updateBackgroundElements(currentElements, scrollSpeed, currentRoadX);
	}
	
	private void updateObstacles() {
	    if (!isRunning || countdownActive) return;
	    int level = GameSettings.levelSelection;
	    if (level != 2 && level != 3 && level != 4) return;
	    updateObstaclesFor(true);	// ulitin ko lang, if true, then p1 tinutukoy
	    updateObstaclesFor(false);	// false ay p2
	    
	    // stun mechanic ni player 1
	    if (p1StunTicks > 0) {
	        p1StunTicks--;
	        player1.speed = 0;
	        if (p1StunTicks > 0) player1.angle += 6.0;
	        else                 player1.angle  = p1PreStunAngle;
	    }
	    // stun mechanic ni player 2
	    if (p2StunTicks > 0) {
	        p2StunTicks--;
	        player2.speed = 0;
	        if (p2StunTicks > 0) player2.angle += 6.0;
	        else                 player2.angle  = p2PreStunAngle;
	    }
	}
	
	private void updateObstaclesFor(boolean isP1) {
	    int level = GameSettings.levelSelection;
	    ArrayList<Obstacle> list = isP1 ? p1Obstacles : p2Obstacles;
	    int camY  = isP1 ? p1CameraY : p2CameraY;
	    int roadX = isP1 ? lrX : rrX;
	    
	    // Iterator class is basically used para i-loop through mga Collections like arraylist,
	    // hashset, linkedlist, etc, pero wag niyo na to problemahin LMAO
	    Iterator<Obstacle> it = list.iterator();
	    
	    while (it.hasNext()) {
	        Obstacle o = it.next();
	        o.screenX += o.vx;
	        // ostrich bounces off sa edges ng player screen parang tanga noh
	        if (o.kind == 1) {
	            final int margin = 80;                                 // how far past the road edge it may stray
	            if (o.screenX + o.hitW < roadX - margin)              o.vx =  Math.abs(o.vx);
	            else if (o.screenX > roadX + roadWidth + margin)      o.vx = -Math.abs(o.vx);
	        }
	        int screenY = carFixedYPos - (int)(o.worldY - camY);
	        if (screenY > HEIGHT + 200) it.remove();   // scrolled off bottom
	    }
	    
	    // spawn wave ni bro ostrich
	    double nextY = isP1 ? p1NextSpawnY : p2NextSpawnY;
	    if (nextY < 0) nextY = camY + nextSpawnDistance(level);
	    if (camY >= nextY) {
	    	int batch = (level == 2) ? 1 + random.nextInt(2) : 1;   // man: 1–2, others: 1 // ostrich: 1, man: 1–2
	        for (int i = 0; i < batch; i++) spawnObstacleAhead(isP1);
	        nextY = camY + nextSpawnDistance(level);
	    }
	    
	    if (isP1) p1NextSpawnY = nextY;
	    else      p2NextSpawnY = nextY;
	}
	
	private void spawnObstacleAhead(boolean isP1) {
	    int level = GameSettings.levelSelection;
	    int camY  = isP1 ? p1CameraY : p2CameraY;
	    int roadX = isP1 ? lrX : rrX;
	    ArrayList<Obstacle> list = isP1 ? p1Obstacles : p2Obstacles;
	    double worldY = camY + 600 + random.nextInt(1800);   // 600–2400 px ahead
	    if (level == 2) {
	        // eto spawn mechanic ni black guy
	        int hitW = 55, hitH = 115;
	        double scale = 0.15;
	        int x = roadX + 20 + random.nextInt(roadWidth - hitW - 40);
	        double vx = (random.nextBoolean() ? 1 : -1) * (0.4 + random.nextDouble() * 0.7);
	        list.add(new Obstacle(0, worldY, x, vx, scale, hitW, hitH));
	    }  else if (level == 3) {
	    	// eto spawn mechanic ng bollards; 5 bollards per row randomly deciding kung mag-
	    	// spspawn sa left or right side ng actual road
	        int startX = roadX + 5 + (random.nextBoolean() ? 0 : roadWidth / 2);
	        for (int i = 0; i < 5; i++) {
	            list.add(new Obstacle(2, worldY, startX + i * 35, 0, 0.13, 27, 99));
	        }
	    } else if (level == 4) {
	    	// eto spawn mechanic nila ostrich, aka mga kumag
	        int hitW = 105, hitH = 135;
	        double scale = 0.20;
	        int x = roadX + random.nextInt(roadWidth - hitW);
	        double vx = (random.nextBoolean() ? 1 : -1) * (5 + random.nextDouble() * 4);
	        list.add(new Obstacle(1, worldY, x, vx, scale, hitW, hitH));
	    }
	}
	// ts checks for car-obstacle collision duh
	private void checkObstacleCollisions() {
	    int level = GameSettings.levelSelection;
	    if (level != 2 && level != 3 && level != 4) return;
	    if (p1StunTicks <= 0) hitTest(player1, p1Obstacles, p1CameraY, true);
	    if (p2StunTicks <= 0) hitTest(player2, p2Obstacles, p2CameraY, false);
	}
	
	// ts actually punishes the motherfucking dumbass na tumama sa obstacle by spinning
	// them 360deg in exactly one second (6deg/frame) tapos di makakagalaw BWHAHAHAHHA
	private void hitTest(Car car, ArrayList<Obstacle> list, int camY, boolean isP1) {
	    Rectangle carBox = new Rectangle((int) car.x, (int) car.y, 50, 90);
	    Iterator<Obstacle> it = list.iterator();
	    while (it.hasNext()) {
	        Obstacle o = it.next();
	        int screenY = carFixedYPos - (int)(o.worldY - camY);
	        Rectangle oBox = new Rectangle((int) o.screenX, screenY, o.hitW, o.hitH);
	        if (carBox.intersects(oBox)) {
	            if (isP1) {
	                p1PreStunAngle = car.angle;
	                p1StunTicks    = STUN_FRAMES;
	            } else {
	                p2PreStunAngle = car.angle;
	                p2StunTicks    = STUN_FRAMES;
	            }
	            car.speed = 0;
	            it.remove();
	            break;
	        }
	    }
	}
	
	// need i say more
	private double nextSpawnDistance(int level) {
	    if (level == 2) return 600 + random.nextInt(900);    // man:    every  600–1500 world px
	    if (level == 3) return 900 + random.nextInt(1200);   // bollard wall: every 900–2100 world px
	    if (level == 4) return 700 + random.nextInt(1100);   // ostrich: every  700–1800 world px
	    return Double.MAX_VALUE;
	}
	
	private void transitionTo(ScreenState newState, TransitionDirection dir) {
	    if (isSliding) return;
	    // snapshot current screen, FOR SLIDING, SEPARATE YUNG SA SUMMARY
	    slideSnapshot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	    
	    // actually store yung image na nai-snapshot sa isang variable para maipaint ni swing/awt
	    Graphics2D sg = slideSnapshot.createGraphics();
	    // anti-aliasing makes da edges smoother
	    sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    // paints the picture
	    paintScreen(sg);
	    // disposes of the picture kasi nai-print na e, pag nai-store pa din sayang memory space
	    sg.dispose();
	    screenState = newState;
	    isSliding = true;
	    slideProgress = 0f;
	    direction = dir;
	}
	
	// slide transition updation BABYYYYYYYY
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
	    if (p1CameraY >= finishY || p2CameraY >= finishY) {
	        // #winnerwinnerchickendinner image ni panalo tapos print ulit
	    	// same logic sa transitionTo na pag-buffer ng image pero resized ito sa drawSummary()
	        summarySnapshot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D sg = summarySnapshot.createGraphics();
	        sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        paintScreen(sg);
	        sg.dispose();
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
		
		p1CameraY = 0;
	    p2CameraY = 0;
	}
	
	private void resetScores() {
		player1Wins = 0;
		player2Wins = 0;
		roundsPlayed = 0;
	}
	
	private void goToMenu() {
		isRunning = false;
		showCardWindow = false;
		countdownActive = false;
		transitionTo(ScreenState.MENU, TransitionDirection.LEFT);
		resetRacePositions();
		statusMessage = "Welcome to the race!";
	}
	
	private void newGame() {
		
		p1Obstacles.clear();
		p2Obstacles.clear();
		p1NextSpawnY = -1;
		p2NextSpawnY = -1;
	    p1StunTicks = 0;
	    p2StunTicks = 0;
	    
		spawnInitialBackgroundElements(); 
		switch (GameSettings.levelSelection) {
			case 1:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				transitionTo(ScreenState.LEVEL1, TransitionDirection.DOWN);
				startCountdown();
				break;
			case 2:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				transitionTo(ScreenState.LEVEL2, TransitionDirection.DOWN);
				startCountdown();
				break;
			case 3:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				transitionTo(ScreenState.LEVEL3, TransitionDirection.DOWN);
				startCountdown();
				break;
			case 4:
				resetScores();
				wetLevel = 0;
				snowLevel = 0;
				transitionTo(ScreenState.LEVEL4, TransitionDirection.DOWN);
				startCountdown();
				break;
		}
	}
	
	private void playAgain() {
	    p1Obstacles.clear();   p2Obstacles.clear();
	    p1NextSpawnY = -1;
	    p2NextSpawnY = -1;
	    p1StunTicks = 0;       p2StunTicks = 0;
	    summarySnapshot = null;
	    
	    spawnInitialBackgroundElements();
	    switch (GameSettings.levelSelection) {
	        case 1 -> screenState = ScreenState.LEVEL1;
	        case 2 -> screenState = ScreenState.LEVEL2;
	        case 3 -> screenState = ScreenState.LEVEL3;
	        case 4 -> screenState = ScreenState.LEVEL4;
	    }
	    startCountdown();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	    // etong if check na ito is for when nagtratransition na from one screenState
	    // to another
	    if (isSliding && slideSnapshot != null) {
	    	
	        // ease-out cubic animation curve (smoother sliding)
	    	// wag niyo na i-analyze to kung ayaw putok-ugat
	        float t = 1f - (float) Math.pow(1f - slideProgress, 3);
	        int dx = 0, dy = 0;     // offset for old screen
	        int nx = 0, ny = 0;     // offset for new screen
	        switch (direction) {
	            case LEFT -> {                       // new from right -> old to left
	                dx = -(int) (WIDTH * t);
	                nx = WIDTH + dx;                 // == WIDTH - WIDTH*t hane
	            }
	            case RIGHT -> {                      // new from left -> old to right
	                dx = (int) (WIDTH * t);
	                nx = -WIDTH + dx;
	            }
	            case UP -> {                         // new from bottom -> old to top
	                dy = -(int) (HEIGHT * t);
	                ny = HEIGHT + dy;
	            }
	            case DOWN -> {                       // new from top -> old to bottom
	                dy = (int) (HEIGHT * t);
	                ny = -HEIGHT + dy;
	            }
	        }
	        // old screen
	        g2.drawImage(slideSnapshot, dx, dy, null);
	        // new screen (live rendering)
	        Graphics2D ng = (Graphics2D) g2.create();
	        ng.translate(nx, ny);
	        paintScreen(ng);
	        ng.dispose();
	    } else {
	    	// tapos this is where the actual drawing happen, kaya nai-separate ko na sila sa
	    	// paintcomponent kasi magiging convoluted all the sudden si paintcomponent if
	    	// i kept those shit
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
		}
		
		if (countdownActive) {
			drawCountdown(g2);
		}
		
		if (screenState == ScreenState.SUMMARY) {
			drawSummary(g2);
		}
		
		if (isPaused && isPlayingLevel()) {
	        drawPauseOverlay(g2);
	    }

	}
	
	private void togglePause() {
	    if (!isPlayingLevel()) return;     // only pausable mid-race
	    isPaused = !isPaused;
	}
	
	private void drawCard(Graphics2D g2) {
		if (showCardWindow) {
			drawCardWindow(g2);
		}
	}
	
	private void drawNight(Graphics2D g2) {
		if (!isDay) {
			g2.setColor(new Color(0, 0, 0, 200));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
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
	    drawObstacles(g2, true);  
	    drawBackgroundElements(g2, true);

	    g2.setClip(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
	    drawRoad(g2);
	    drawRoadMarks(g2, false); // false = p2 logic
	    drawFinishLine(g2, false);
	    drawCar(g2, player2);
	    drawObstacles(g2, false);  
	    drawBackgroundElements(g2, false);

	    g2.setClip(fullScreen); // restore full screen drawing
	    g2.setColor(Color.WHITE);
	    g2.setStroke(new BasicStroke(4));
	    g2.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT); // gitna ng screen
	    
	    drawClouds(g2);
	    drawWeatherEffects(g2);
	    drawNight(g2);
	    
	    if (isPaused) {
	    	drawPauseOverlay(g2);
	    }
	}
	
	private void drawQuickScreen(Graphics2D g2) {
		Shape fullScreen = g2.getClip();
		int savedLevel = GameSettings.levelSelection;
		GameSettings.levelSelection = 1;      
				
		drawGround(g2);
		 
	    drawRoad(g2);
	    drawRoadMarks(g2, true);
	    drawBackgroundElements(g2, true);

	    drawRoadMarks(g2, false); // false = p2 logic
	    drawBackgroundElements(g2, false);

	    g2.setClip(fullScreen); 
	    
	    drawClouds(g2);
	    drawWeatherEffects(g2);
	    drawNight(g2);
	    
	    GameSettings.levelSelection = savedLevel;  
	}
	
	private void drawFadingScreen(Graphics2D g2, int r, int g, int b, String fadeFrom) {
		
		int c1v = 0;
		int c2v = 255;
		
		Color c1 = new Color(r, g, b, c1v);
		Color c2 = new Color(r, g, b, c2v);
		
		GradientPaint fade = new GradientPaint(0, HEIGHT, c1, WIDTH, HEIGHT, c2);
		
		switch (fadeFrom) {
		case "left" :
			fade = new GradientPaint(0, HEIGHT, c2, WIDTH, HEIGHT, c1);
			break;
		case "right" :
			fade = new GradientPaint(0, HEIGHT, c1, WIDTH, HEIGHT, c2);
			break;
		case "up" :
			fade = new GradientPaint(0, 0, c2, 0, HEIGHT, c1);
			break;
		case "down" :
			fade = new GradientPaint(0, 0, c1, 0, HEIGHT, c2);
			break;
		}
		g2.setPaint(fade);
		g2.fillRect(0, 0, WIDTH, HEIGHT);
	}
	
	private void drawMenuNew(Graphics2D g2) {
		
		drawQuickScreen(g2);
		drawFadingScreen(g2, 52, 56, 55, "down");
		
		// buttons duh
		drawPrimaryButton(g2, startButton, "START", 40, "#89d957");
		drawPrimaryButton(g2, infoButton, "INFO", 40, "#ffde59");
		drawPrimaryButton(g2, exitButton, "EXIT", 40, "#ff5757");
		
		// main header
		drawHeadingText(g2, "Travel Rivalry", Color.WHITE, 100, 50, 275);
		
		drawHeadingText(g2, "Bantula, Gutierrez, Saurane, Simborio, & Vallestero presents...",
				Color.WHITE, 30, 50, 190);
		
		
	}
	
	private void drawCarSelect(Graphics2D g2) {
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
		drawBoldText(g2, "Choose car paints: LMB for P1, RMB for P2", Color.decode("#242424"), 20, 450, 220);
		g2.setColor(Color.decode("#121213"));
		g2.fillOval(-230, 180, 600, 600);
		g2.fillOval(910, 180, 600, 600);
		
		if (!player1IsLocked) {
			g2.setColor(Color.decode("#121213"));
			g2.fillOval(-230, 180, 600, 600);	
		} else {
			g2.setColor(Color.decode("#559d00"));
			g2.fillOval(-230, 180, 600, 600);
		}
		
		if (!player2IsLocked) {
			g2.setColor(Color.decode("#121213"));
			g2.fillOval(910, 180, 600, 600);	
		} else {
			g2.setColor(Color.decode("#559d00"));
			g2.fillOval(910, 180, 600, 600);
		}
		
		for (int i = 0; i < colorsWindowSelection.size(); i++) {
			Rectangle currentButton = colorsWindowSelection.get(i);
			drawColorButton(g2, currentButton, palette[i]);
		}
		
		drawCarAtGarage(g2, player1);
		drawCarAtGarage(g2, player2);
	}
	
	private void drawLevelSelect(Graphics2D g2) {
		
		drawQuickScreen(g2);
		drawFadingScreen(g2, 52, 56, 55, "down");
		
		drawHeadingText(g2, "Level Select", Color.WHITE, 68, 130, 275);
		
		drawButton(g2, level1Button, "1", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level2Button, "2", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level3Button, "3", "#ffffff", "#424348", "#353535", 5, 50);
		drawButton(g2, level4Button, "4", "#ffffff", "#424348", "#353535", 5, 50);
		
		if (showCardWindow) {
	        // dim background so the card pops
	        g2.setColor(new Color(0, 0, 0, 120));
	        g2.fillRect(0, 0, WIDTH, HEIGHT);
	        LevelCards.drawCardWindow(g2);
	    }
		
	
	}

	private void drawClouds(Graphics2D g2) {
		g2.setColor(new Color(255, 255, 255, 40));   // soft gray, less than 20% transparency
													// dahil alpha only occupies 40/255

	    for (Cloud c : clouds) {
	        int x = (int) c.x;
	        int y = (int) c.y;
	        int w = c.size;
	        int h = c.size / 3;
	        // base
	        g2.fillOval(x, y + h / 2, (int) (w * 1.6), h);
	        // two bumps sa taas
	        g2.fillOval(x + w / 6,     y,            w, (int) (h * 1.4));
	        g2.fillOval(x + w / 2 + 4, y + h / 6,    w, (int) (h * 1.2));
	    }
	}

	private void drawGround(Graphics2D g2) {
		String color = "#000000";
		switch (GameSettings.levelSelection) {
			case 1 -> color ="#559d00";
		    case 2 -> color = "#c1c1c1";
		    case 3 -> color = "#3c99c2";
		    case 4 -> color = "#ddb97e";
		}

		g2.setColor(Color.decode(color));
		g2.fillRect(0, 0, WIDTH, HEIGHT);	
	}

	private void drawAccumulatedWeather(Graphics2D g2) {	// TODO: baka tanggalin idk di pa ako sure
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
	    // assign variables depending sa player na nilalaro natin
	    double camY = isPlayer1 ? p1CameraY : p2CameraY;
	    int roadX = isPlayer1 ? lrX : rrX;
	    
	    String sideColor = "#FFFFFF";
	    String dashColor = "#FFFFFF";
	    int dashOffset = (int) (camY % 75);
	    
	    
	    switch (GameSettings.levelSelection) {
	    // fall through 3
	    case 1 : dashColor = "#faff00";
	    case 2 : 
	    case 3 :
	    	g2.setColor(Color.decode(dashColor));
			    for (int y = -75; y < HEIGHT; y += 75) {
			        int drawY = y + dashOffset;
			        int centerX = roadX + (roadWidth / 2) - 5;
			        g2.fillRect(centerX, drawY, 10, 50);
			    }
			    // side lines
			g2.setColor(Color.decode(sideColor));
			g2.fillRect(roadX + 10, 0, 10, HEIGHT);
			g2.fillRect(roadX + roadWidth - 20, 0, 10, HEIGHT);
			break;
	    case 4 :
	    	g2.setColor(Color.decode("#958960"));
			int addLine = 25;
			for (int i = 0; i < 4; i++) {
				g2.fillRect(roadX + addLine, 0 , 40, HEIGHT);
				addLine = addLine + 90;
			}
			break;
	    }
	}

	private void drawFinishLine(Graphics2D g2, boolean isPlayer1) {
		int camY = isPlayer1 ? p1CameraY : p2CameraY;
	    int roadX = isPlayer1 ? lrX : rrX;
		int blockSize = 20;
		
		switch (GameSettings.levelSelection) {
		case 1 -> finishY = 15000;
		case 2 -> finishY = 20000;
		case 3 -> finishY = 25000;
		case 4 -> finishY = 30000;
		}
		
		int drawY = carFixedYPos - (finishY - camY);
		
		if (drawY > -50 && drawY < HEIGHT) {
			for (int i = 0; i < roadWidth / blockSize; i++) {
				g2.setColor(i % 2 == 0 ? Color.WHITE : Color.BLACK);
				g2.fillRect(roadX + (i * blockSize), drawY, blockSize, 18);
			}
		}
	}
	
	private void drawBackgroundElements(Graphics2D g2, boolean isPlayer1) {
		ArrayList<BackgroundElement> targetList = isPlayer1 ? p1BGE : p2BGE;
	    
		for (BackgroundElement bge : targetList) {
			switch (GameSettings.levelSelection) {
			case 1 : 
				if (bge.type == 0) {
					drawSingleTree(g2, (int) bge.x, (int) bge.y, 0.25);
				} else if (bge.type == 1) {
					drawLayeredTree(g2, (int) bge.x, (int) bge.y, 0.3);
				} // tangina wag na yung cabin SORRY
				break;
			case 2 : 
				if (bge.type == 0) {
					drawHotel(g2, (int) bge.x, (int) bge.y, 0.5);
				} else if (bge.type == 1) {
					drawShop(g2, (int) bge.x, (int) bge.y, 0.25);
				} else if (bge.type == 2) {
					drawSheriffStation(g2, (int) bge.x, (int) bge.y, 0.25);
				}
				break;
			case 3 :
				if (bge.type == 0) {
					drawCargoShip(g2, (int) bge.x, (int) bge.y, 1);
				} else if (bge.type == 1) {
					drawYacht(g2, (int) bge.x, (int) bge.y, 0.25);
				} else if (bge.type == 2) {
					drawSailboat(g2, (int) bge.x, (int) bge.y, 0.2);
				} 
				break;
			case 4 :
				if (bge.type == 0) {
			        drawSavannahTree(g2, (int) bge.x, (int) bge.y, 0.25);
			    } else if (bge.type == 1) {
			        drawBushesA(g2, (int) bge.x, (int) bge.y, 0.1);
			    } else if (bge.type == 2) {
			        drawBushesB(g2, (int) bge.x, (int) bge.y, 0.1);
			    }
				break;
			}
		}
	}
	
	private void drawObstacles(Graphics2D g2, boolean isP1) {
	    int level = GameSettings.levelSelection;
	    if (level != 2 && level != 3 && level != 4) return;
	    ArrayList<Obstacle> list = isP1 ? p1Obstacles : p2Obstacles;
	    int camY  = isP1 ? p1CameraY : p2CameraY;
	    int roadX = isP1 ? lrX : rrX;
	    for (Obstacle o : list) {
	        int screenY = carFixedYPos - (int)(o.worldY - camY);
	        if (screenY < -300 || screenY > HEIGHT + 200) continue;
	        if (o.kind == 0) {
	            drawMan(g2, (int) o.screenX, screenY, o.scale);
	        } else if (o.kind == 1) {
	            boolean facingRight = o.vx > 0;
	            drawOstrichFacing(g2, (int) o.screenX, screenY,
	                              o.scale, o.hitW, facingRight);
	        } else if (o.kind == 2) {
	            drawBollard(g2, (int) o.screenX, screenY, o.scale);
	        }
	    }
	}

	// BYE BYE TRAFFIC LIGHT HUHU
	
	private void drawCar(Graphics2D g2, Car car) {
		AffineTransform oldTransform = g2.getTransform();

	    g2.translate(car.x, car.y);

	    // scale constant taken dun sa main body ni mcqueen kasi yun yung value touching 0, 0 coords
	    // & the largest shape sa kotse
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
	
	// same bullshit pero mas malaki yung kotse kasi nasa garage wow paldo
	private void drawCarAtGarage(Graphics2D g2, Car car) {
		AffineTransform oldTransform = g2.getTransform();
		
		double scaledWidth = 304 * 0.7;
	    double scaledHeight = 743 * 0.7;

	    g2.translate(car.x, car.y);
	    
	    // twist kotse depending if player 1 or 2 para facing each other sila
	    if (car.isPlayer1) {
	        g2.rotate(Math.toRadians(car.angle + 180)); 
	    } else {
	        g2.rotate(Math.toRadians(car.angle)); 
	    }
	    
	    drawCarMcQueen(g2, (int)(-scaledWidth / 2), (int)(-scaledHeight / 2), 0.7, car.isPlayer1);
	    
	    g2.setTransform(oldTransform);
	}
	private void drawWeatherEffects(Graphics2D g2) {
	    // rain fades in/out according sa wetLevel (0.0 -> 1.0) odba magic
	    if (wetLevel > 0) {
	        int tintAlpha = (int) (127 * wetLevel);     // fixed talaga to 127 yung tint ni blue
	        g2.setColor(new Color(35, 43, 56, tintAlpha));
	        g2.fillRect(0, 0, WIDTH, HEIGHT);
	        int dropAlpha = (int) (180 * wetLevel);     // raindrops fixed 180 val
	        g2.setColor(new Color(190, 225, 255, dropAlpha));
	        for (RainDrop d : rainDrops) {
	            g2.drawLine(d.x, d.y, d.x - 3, d.y + 12);
	        }
	    }
	    // Snow overlay — fades in/out with snowLevel (0.0 → 1.0)
	    if (snowLevel > 0) {
	        int tintAlpha = (int) (85 * snowLevel);     // pale white, fixed at 85
	        g2.setColor(new Color(255, 255, 255, tintAlpha));
	        g2.fillRect(0, 0, WIDTH, HEIGHT);
	        int flakeAlpha = (int) (225 * snowLevel);   // snowflakes 225 val
	        g2.setColor(new Color(255, 255, 255, flakeAlpha));
	        for (SnowFlake f : snowFlakes) {
	            g2.fillOval(f.x, f.y, f.size, f.size);
	        }
	    }
	}
	
	// TODO: baka i-remove ko nalang, most likely irereplace ko yung window at contents dahil
	// madami na new additions to the game
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
	    // darkness sa likod
	    g2.setColor(Color.decode("#353535"));
	    g2.fillRect(175, 130, 925, 500);
	    // draws yung snapshot na kinuha sa checkWinner() IF hindi null (aka may content) yung variable na yon
	    if (summarySnapshot != null) {
	        g2.drawImage(summarySnapshot, 185, 140, 900, 475, null);
	    }
	    // white border around the photo
	    g2.setColor(Color.WHITE);
	    g2.setStroke(new BasicStroke(8));
	    g2.drawRect(185, 140, 900, 475);
	    
	    // overlay na buttons fr
	    drawPrimaryButton(g2, summaryPlayAgainButton, "PLAY AGAIN", 22, "#89d957");
	    drawPrimaryButton(g2, summaryMenuButton,      "MENU",       22, "#ffde59");
	    drawPrimaryButton(g2, summaryExitButton,      "EXIT",       22, "#ff5757");
	}
	
	private void drawPauseOverlay(Graphics2D g2) {
		g2.setColor(new Color(125, 123, 106, 150));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		g2.setColor(Color.WHITE);
		g2.fillOval(-175, 30, 670, 670);
		
		drawBoldText(g2, "Paused", Color.BLACK, 100, 40, 280);
		
		drawPrimaryButton(g2, resumeButton, "RESUME", 40, "#89d957");
		drawPrimaryButton(g2, restartButton, "RESTART", 40, "#ffde59");
		drawPrimaryButton(g2, menuButton, "MENU", 40, "#ff5757");
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		
		 if (isPaused && isPlayingLevel()) {
		        if (resumeButton.contains(p)) {
		            isPaused = false;
		        } else if (restartButton.contains(p)) {
		            isPaused = false;
		            newGame();                  // restarts current level + countdown
		        } else if (menuButton.contains(p)) {
		        	isPaused = false;  
		            transitionTo(ScreenState.MENU, TransitionDirection.UP);
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
			for (int i = 0; i < colorsWindowSelection.size(); i++) {
		        if (colorsWindowSelection.get(i).contains(p)) {
		        	if (player2IsLocked) return;
		        	if (SwingUtilities.isRightMouseButton(e)) {
		                GameSettings.selectedCarColorP2 = palette[i].getHexCode();
		                System.out.println("P2 Color changed to: " + palette[i].name());
		            } 
		            else if (SwingUtilities.isLeftMouseButton(e)) {
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
		
		if (key == KeyEvent.VK_ESCAPE) togglePause();
		
		if (key == KeyEvent.VK_W) p1Up = true;
		if (key == KeyEvent.VK_S) p1Down = true;
		if (key == KeyEvent.VK_A) p1Left = true;
		if (key == KeyEvent.VK_D) p1Right = true;
		if (key == KeyEvent.VK_E) p1Brake = true;
		
		if (key == KeyEvent.VK_ENTER) isDay = true;	// TEST EVENT
		if (key == KeyEvent.VK_BACK_SPACE) isDay = false; // TEST EVENT
		
		if (key == KeyEvent.VK_1) isRaining = true;
		if (key == KeyEvent.VK_2) isSnowing = true;
		
		if (key == KeyEvent.VK_3) player1IsLocked = true;
		if (key == KeyEvent.VK_4) player2IsLocked = true;
		
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
		
		if (key == KeyEvent.VK_1) isRaining = false;	// TEST EVENT
		if (key == KeyEvent.VK_2) isSnowing = false;	// TEST EVENT
		
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