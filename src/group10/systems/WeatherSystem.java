package group10.systems;

import group10.entities.Cloud;
import group10.entities.Puddle;
import group10.entities.RainDrop;
import group10.entities.SnowFlake;
import group10.entities.WindParticle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeatherSystem {

	private final int WIDTH;
	private final int HEIGHT;
	private final Random random = new Random();

	// WEATHER BOOLEANS
	public boolean isRaining = false;
	public boolean isSnowing = false;
	public boolean isWindy = false;
	public boolean isNormal = true;

	// WEATHER LEVELS
	public double wetLevel = 0;
	public double snowLevel = 0;
	public double windLevel = 0;
	public static final double WIND_PUSH_PER_FRAME = 8;

	// PARTICLES
	public final List<RainDrop> rainDrops = new ArrayList<>();
	public final List<SnowFlake> snowFlakes = new ArrayList<>();
	public final List<Cloud> clouds = new ArrayList<>();
	public final List<Puddle> puddles = new ArrayList<>();
	public final List<WindParticle> windParticles = new ArrayList<>();

	// RANDOM WEATHER SCHEDULER
	private int weatherTicksLeft = 0;
	private static final int MIN_WEATHER_FRAMES = 60 * 8;   // 8 seconds
	private static final int MAX_WEATHER_FRAMES = 60 * 20;  // 20 seconds

	// CLOUD UPDATE
	private int cloudTick = 0;

	public WeatherSystem(int width, int height) {
		this.WIDTH = width;
		this.HEIGHT = height;
		spawnInitialParticles();
	}

	private void spawnInitialParticles() {
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
		for (int i = 0; i < 120; i++) {
			windParticles.add(new WindParticle(
					random.nextInt(WIDTH),
					random.nextInt(HEIGHT),
					25 + random.nextInt(45),
					9 + random.nextInt(11)
			));
		}
	}

	public void resetLevels() {
		wetLevel = 0;
		snowLevel = 0;
		windLevel = 0;
	}

	public void update() {

		if (!isRaining) wetLevel  -= 0.025;
		if (!isSnowing) snowLevel -= 0.025;
		if (!isWindy)   windLevel -= 0.025;
		if (wetLevel  < 0) wetLevel  = 0;
		if (snowLevel < 0) snowLevel = 0;
		if (windLevel < 0) windLevel = 0;

		if (isRaining) {
			wetLevel += 0.1;
			if (wetLevel > 1.0) wetLevel = 1.0;

			snowLevel -= 0.1;
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
			snowLevel += 0.1;
			if (snowLevel > 1.0) snowLevel = 1.0;

			wetLevel -= 0.1;
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

		if (isWindy) {
			windLevel += 0.005;
			if (windLevel > 1.0) windLevel = 1.0;
			wetLevel  -= 0.0012;
			if (wetLevel  < 0) wetLevel  = 0;
			snowLevel -= 0.0012;
			if (snowLevel < 0) snowLevel = 0;
			for (WindParticle wp : windParticles) {
				wp.x -= wp.speed;   // wind blows from right to left
				if (wp.x + wp.length < 0) {
					wp.x = WIDTH + random.nextInt(120);
					wp.y = random.nextInt(HEIGHT);
				}
			}
		} else {
			windLevel -= 0.004;
			if (windLevel < 0) windLevel = 0;
		}

		if (isNormal) {
			wetLevel -= 0.0022;
			if (wetLevel < 0) wetLevel = 0;

			snowLevel -= 0.0018;
			if (snowLevel < 0) snowLevel = 0;
		}
	}

	public void updateRandomWeather(boolean isRunning, boolean countdownActive) {
		if (!isRunning || countdownActive) return;
		weatherTicksLeft--;
		if (weatherTicksLeft > 0) return;

		int roll = random.nextInt(4);
		isNormal  = (roll == 0);
		isRaining = (roll == 1);
		isSnowing = (roll == 2);
		isWindy   = (roll == 3);

		weatherTicksLeft = MIN_WEATHER_FRAMES
				+ random.nextInt(MAX_WEATHER_FRAMES - MIN_WEATHER_FRAMES);
	}

	public void updateClouds() {
		cloudTick++;
		if (cloudTick % 3 == 0) {   // 1px to the right every 3 frames (~20px/sec)
			for (Cloud c : clouds) {
				c.x += 1;
				if (c.x > WIDTH + 120) {
					c.x = -180;
					c.y = 50 + random.nextInt(HEIGHT - 100);
				}
			}
		}
	}

	public void drawClouds(Graphics2D g2) {
		g2.setColor(new Color(255, 255, 255, 40));
		for (Cloud c : clouds) {
			int x = (int) c.x;
			int y = (int) c.y;
			int w = c.size;
			int h = c.size / 3;
			// base
			g2.fillOval(x, y + h / 2, (int) (w * 1.6), h);
			// two bumps on top
			g2.fillOval(x + w / 6,     y,            w, (int) (h * 1.4));
			g2.fillOval(x + w / 2 + 4, y + h / 6,    w, (int) (h * 1.2));
		}
	}

	public void drawAccumulatedWeather(Graphics2D g2, int rrX, int roadWidth) {
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

	public void drawWeatherEffects(Graphics2D g2) {
		if (wetLevel > 0) {
			int tintAlpha = (int) (127 * wetLevel);
			g2.setColor(new Color(35, 43, 56, tintAlpha));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			int dropAlpha = (int) (180 * wetLevel);
			g2.setColor(new Color(190, 225, 255, dropAlpha));
			g2.setStroke(new BasicStroke(2));
			for (RainDrop d : rainDrops) {
				g2.drawLine(d.x, d.y, d.x - 3, d.y + 12);
			}
		}

		if (snowLevel > 0) {
			int tintAlpha = (int) (85 * snowLevel);
			g2.setColor(new Color(255, 255, 255, tintAlpha));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			int flakeAlpha = (int) (225 * snowLevel);
			g2.setColor(new Color(255, 255, 255, flakeAlpha));
			for (SnowFlake f : snowFlakes) {
				g2.fillOval(f.x, f.y, f.size, f.size);
			}
		}

		if (windLevel > 0) {
			int tintAlpha = (int) (40 * windLevel);
			g2.setColor(new Color(220, 225, 230, tintAlpha));
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			int streakAlpha = (int) (200 * windLevel);
			g2.setColor(new Color(235, 240, 248, streakAlpha));
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(2f));
			for (WindParticle wp : windParticles) {
				g2.drawLine(wp.x, wp.y, wp.x + wp.length, wp.y);
			}
			g2.setStroke(oldStroke);
		}
	}
}
