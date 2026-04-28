package group10.systems;

import group10.entities.BackgroundElement;
import group10.graphics.BackgroundElements;
import group10.helpers.BGConfig;
import group10.helpers.BGConfig.BGType;
import group10.helpers.GameSettings;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class BackgroundManager {

	private final int HEIGHT;
	private final int lrX;
	private final int rrX;
	private final int roadWidth;
	private final Random random = new Random();

	public final ArrayList<BackgroundElement> p1BGE = new ArrayList<>();
	public final ArrayList<BackgroundElement> p2BGE = new ArrayList<>();

	public BackgroundManager(int height, int lrX, int rrX, int roadWidth) {
		this.HEIGHT = height;
		this.lrX = lrX;
		this.rrX = rrX;
		this.roadWidth = roadWidth;
	}

	public void spawnInitial() {
		p1BGE.clear();
		p2BGE.clear();
		BGConfig cfg = BGConfig.forCurrentLevel();
		for (int i = 0; i < 5; i++) {
			p1BGE.add(makeBackgroundElement(cfg, lrX, i * -300));
			p2BGE.add(makeBackgroundElement(cfg, rrX, i * -300));
		}
	}

	/** Update one player's side based on its scroll movement. */
	public void updatePlayer(boolean isP1, double movement) {
		ArrayList<BackgroundElement> elements = isP1 ? p1BGE : p2BGE;
		int roadX = isP1 ? lrX : rrX;
		updateBackgroundElements(elements, movement, roadX);
	}

	private void updateBackgroundElements(ArrayList<BackgroundElement> elements,
			double movement, int roadX) {
		BGConfig cfg = BGConfig.forCurrentLevel();
		for (BackgroundElement bge : elements) {
			bge.y += movement;
			if (bge.y > HEIGHT) respawnBackgroundElement(bge, cfg, roadX);
		}
		removeOverlappingElements(elements, cfg);
	}

	private BackgroundElement makeBackgroundElement(BGConfig cfg, int roadX, int initialY) {
		BackgroundElement bge = new BackgroundElement(0, initialY);
		bge.type = pickWeightedType(cfg);
		placeBackgroundElement(bge, cfg, roadX);
		return bge;
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

	public void draw(Graphics2D g2, boolean isP1) {
		ArrayList<BackgroundElement> targetList = isP1 ? p1BGE : p2BGE;

		for (BackgroundElement bge : targetList) {
			switch (GameSettings.levelSelection) {
			case 1:
				if (bge.type == 0) {
					BackgroundElements.drawSingleTree(g2, (int) bge.x, (int) bge.y, 0.25);
				} else if (bge.type == 1) {
					BackgroundElements.drawLayeredTree(g2, (int) bge.x, (int) bge.y, 0.3);
				}
				break;
			case 2:
				if (bge.type == 0) {
					BackgroundElements.drawHotel(g2, (int) bge.x, (int) bge.y, 0.5);
				} else if (bge.type == 1) {
					BackgroundElements.drawShop(g2, (int) bge.x, (int) bge.y, 0.25);
				} else if (bge.type == 2) {
					BackgroundElements.drawSheriffStation(g2, (int) bge.x, (int) bge.y, 0.25);
				}
				break;
			case 3:
				if (bge.type == 0) {
					BackgroundElements.drawCargoShip(g2, (int) bge.x, (int) bge.y, 1);
				} else if (bge.type == 1) {
					BackgroundElements.drawYacht(g2, (int) bge.x, (int) bge.y, 0.25);
				} else if (bge.type == 2) {
					BackgroundElements.drawSailboat(g2, (int) bge.x, (int) bge.y, 0.2);
				}
				break;
			case 4:
				if (bge.type == 0) {
					BackgroundElements.drawSavannahTree(g2, (int) bge.x, (int) bge.y, 0.25);
				} else if (bge.type == 1) {
					BackgroundElements.drawBushesA(g2, (int) bge.x, (int) bge.y, 0.1);
				} else if (bge.type == 2) {
					BackgroundElements.drawBushesB(g2, (int) bge.x, (int) bge.y, 0.1);
				}
				break;
			}
		}
	}
}