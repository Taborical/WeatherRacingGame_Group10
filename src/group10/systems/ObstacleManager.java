package group10.systems;

import group10.entities.Car;
import group10.entities.Obstacle;
import group10.graphics.Obstacles;
import group10.helpers.GameSettings;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ObstacleManager {

	private final int HEIGHT;
	private final int lrX;
	private final int rrX;
	private final int roadWidth;
	private final int carFixedYPos;
	private final Random random = new Random();

	public final ArrayList<Obstacle> p1Obstacles = new ArrayList<>();
	public final ArrayList<Obstacle> p2Obstacles = new ArrayList<>();
	private double p1NextSpawnY = -1;
	private double p2NextSpawnY = -1;

	// STUN STATE
	// 60 ticks * 6 deg/tick = exactly one 360 spin per second
	private static final int STUN_FRAMES = 60;
	private int    p1StunTicks = 0,  p2StunTicks = 0;
	private double p1PreStunAngle = 0, p2PreStunAngle = 0;

	public ObstacleManager(int height, int lrX, int rrX, int roadWidth, int carFixedYPos) {
		this.HEIGHT = height;
		this.lrX = lrX;
		this.rrX = rrX;
		this.roadWidth = roadWidth;
		this.carFixedYPos = carFixedYPos;
	}

	public void reset() {
		p1Obstacles.clear();
		p2Obstacles.clear();
		p1NextSpawnY = -1;
		p2NextSpawnY = -1;
		p1StunTicks = 0;
		p2StunTicks = 0;
	}

	public boolean isP1Stunned() { return p1StunTicks > 0; }
	public boolean isP2Stunned() { return p2StunTicks > 0; }

	public void update(boolean isRunning, boolean countdownActive,
			int p1CameraY, int p2CameraY, Car player1, Car player2) {
		if (!isRunning || countdownActive) return;
		int level = GameSettings.levelSelection;
		if (level != 2 && level != 3 && level != 4) return;
		updateObstaclesFor(true,  p1CameraY);
		updateObstaclesFor(false, p2CameraY);

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

	private void updateObstaclesFor(boolean isP1, int camY) {
		int level = GameSettings.levelSelection;
		ArrayList<Obstacle> list = isP1 ? p1Obstacles : p2Obstacles;
		int roadX = isP1 ? lrX : rrX;

		Iterator<Obstacle> it = list.iterator();
		while (it.hasNext()) {
			Obstacle o = it.next();
			o.screenX += o.vx;
			// ostrich bounces off sa edges ng player screen
			if (o.kind == 1) {
				final int margin = 80;
				if (o.screenX + o.hitW < roadX - margin)         o.vx =  Math.abs(o.vx);
				else if (o.screenX > roadX + roadWidth + margin) o.vx = -Math.abs(o.vx);
			}
			int screenY = carFixedYPos - (int)(o.worldY - camY);
			if (screenY > HEIGHT + 200) it.remove();   // scrolled off bottom
		}

		// spawn wave
		double nextY = isP1 ? p1NextSpawnY : p2NextSpawnY;
		if (nextY < 0) nextY = camY + nextSpawnDistance(level);
		if (camY >= nextY) {
			int batch = (level == 2) ? 1 + random.nextInt(2) : 1; // man: 1–2, others: 1
			for (int i = 0; i < batch; i++) spawnObstacleAhead(isP1, camY);
			nextY = camY + nextSpawnDistance(level);
		}

		if (isP1) p1NextSpawnY = nextY;
		else      p2NextSpawnY = nextY;
	}

	private void spawnObstacleAhead(boolean isP1, int camY) {
		int level = GameSettings.levelSelection;
		int roadX = isP1 ? lrX : rrX;
		ArrayList<Obstacle> list = isP1 ? p1Obstacles : p2Obstacles;
		double worldY = camY + 600 + random.nextInt(1800);   // 600–2400 px ahead
		if (level == 2) {
			// man spawn
			int hitW = 55, hitH = 115;
			double scale = 0.15;
			int x = roadX + 20 + random.nextInt(roadWidth - hitW - 40);
			double vx = (random.nextBoolean() ? 1 : -1) * (0.4 + random.nextDouble() * 0.7);
			list.add(new Obstacle(0, worldY, x, vx, scale, hitW, hitH));
		} else if (level == 3) {
			// 5-bollard wall
			int startX = roadX + 5 + (random.nextBoolean() ? 0 : roadWidth / 2);
			for (int i = 0; i < 5; i++) {
				list.add(new Obstacle(2, worldY, startX + i * 35, 0, 0.13, 27, 99));
			}
		} else if (level == 4) {
			// ostrich spawn
			int hitW = 105, hitH = 135;
			double scale = 0.20;
			int x = roadX + random.nextInt(roadWidth - hitW);
			double vx = (random.nextBoolean() ? 1 : -1) * (5 + random.nextDouble() * 4);
			list.add(new Obstacle(1, worldY, x, vx, scale, hitW, hitH));
		}
	}

	private double nextSpawnDistance(int level) {
		if (level == 2) return 600 + random.nextInt(900);    // man:    every  600–1500 world px
		if (level == 3) return 900 + random.nextInt(1200);   // bollard wall: every 900–2100 world px
		if (level == 4) return 700 + random.nextInt(1100);   // ostrich: every  700–1800 world px
		return Double.MAX_VALUE;
	}

	public void checkCollisions(Car player1, Car player2, int p1CameraY, int p2CameraY) {
		int level = GameSettings.levelSelection;
		if (level != 2 && level != 3 && level != 4) return;
		if (p1StunTicks <= 0) hitTest(player1, p1Obstacles, p1CameraY, true);
		if (p2StunTicks <= 0) hitTest(player2, p2Obstacles, p2CameraY, false);
	}

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

	public void draw(Graphics2D g2, boolean isP1, int cameraY) {
		int level = GameSettings.levelSelection;
		if (level != 2 && level != 3 && level != 4) return;
		ArrayList<Obstacle> list = isP1 ? p1Obstacles : p2Obstacles;
		for (Obstacle o : list) {
			int screenY = carFixedYPos - (int)(o.worldY - cameraY);
			if (screenY < -300 || screenY > HEIGHT + 200) continue;
			if (o.kind == 0) {
				Obstacles.drawMan(g2, (int) o.screenX, screenY, o.scale);
			} else if (o.kind == 1) {
				boolean facingRight = o.vx > 0;
				Obstacles.drawOstrichFacing(g2, (int) o.screenX, screenY,
						o.scale, o.hitW, facingRight);
			} else if (o.kind == 2) {
				Obstacles.drawBollard(g2, (int) o.screenX, screenY, o.scale);
			}
		}
	}
}
