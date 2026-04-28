package group10.systems;

import java.awt.Color;
import java.awt.Graphics2D;

public class DayNightCycle {

	private int dayCycleTick = 0;

	private static final int DAY_FRAMES     = 60 * 20; // 20s of full day
	private static final int SUNSET_FRAMES  = 60 * 4;  // 4s sunset
	private static final int NIGHT_FRAMES   = 60 * 20; // 20s of full night
	private static final int SUNRISE_FRAMES = 60 * 4;  // 4s sunrise
	private static final int CYCLE_LENGTH   = DAY_FRAMES + SUNSET_FRAMES + NIGHT_FRAMES + SUNRISE_FRAMES;

	public void update() {
		dayCycleTick = (dayCycleTick + 1) % CYCLE_LENGTH;
	}

	public void draw(Graphics2D g2, int width, int height) {
		int t = dayCycleTick;
		double n; // 0 = day, 1 = night
		if (t < DAY_FRAMES) n = 0;
		else if (t < DAY_FRAMES + SUNSET_FRAMES) n = (t - DAY_FRAMES) / (double) SUNSET_FRAMES;
		else if (t < DAY_FRAMES + SUNSET_FRAMES + NIGHT_FRAMES) n = 1;
		else n = 1 - (t - DAY_FRAMES - SUNSET_FRAMES - NIGHT_FRAMES) / (double) SUNRISE_FRAMES;
		double tint = 4 * n * (1 - n); // peaks at n=0.5 (sunset/sunrise)
		int r = Math.min(255, (int)(255 * tint));
		int g = Math.min(255, (int)(110 * tint));
		int b = Math.min(255, (int)(40  * tint + 30 * n));
		int a = Math.min(255, (int)(200 * n));
		g2.setColor(new Color(r, g, b, a));
		g2.fillRect(0, 0, width, height);
	}
}