package group10.helpers;

public final class BGConfig {
	// BGType: defines one kind of background element (a tree, a building, etc)
	public static final class BGType {
		public final int width;     // rendered footprint width
		public final int height;    // rendered footprint height (for overlap detection)
		public final int weight;    // spawn frequency
		public final int pad;       // gap between road and this element
		public final int xScatter;  // horizontal random range
		public final int yScatter;  // vertical random range
		public BGType(int width, int height, int weight, int pad, int xScatter, int yScatter) {
			this.width    = width;
			this.height   = height;
			this.weight   = weight;
			this.pad      = pad;
			this.xScatter = xScatter;
			this.yScatter = yScatter;
		}
	}
	// BGConfig itself: a bundle of BGTypes for one level
	public final BGType[] types;
	public final int totalWeight;
	public BGConfig(BGType... types) {
		this.types = types;
		int sum = 0;
		for (BGType t : types) sum += t.weight;
		this.totalWeight = sum;
	}
	// Per-level configs. Index 0 is null because levels start at 1.
	public static final BGConfig[] LEVEL_CONFIGS = { null,
			// Level 1 — A Niche Grove
			new BGConfig(
					new BGType(137, 200, 1, 30,  80, 600),  // single tree
					new BGType(116, 240, 1, 30,  80, 600)   // layered tree
					),
			// Level 2 — Tenement Square
			new BGConfig(
					new BGType(220, 380, 1, 20,  60, 900),  // hotel
					new BGType(320, 200, 2, 20,  60, 900),  // shop
					new BGType(263, 200, 2, 20,  60, 900)   // sheriff station
					),
			// Level 3 — Seaway
			new BGConfig(
					new BGType(320, 620, 2,  30,   0, 1400), // cargo ship
					new BGType(160, 200, 1, -30, 100,  700), // yacht
					new BGType(110, 160, 1, -30, 100,  700)  // sailboat
					),
			// Level 4 — Savannahroading
			new BGConfig(
					new BGType(285, 340, 10, -100, 100, 800), // savannah tree
					new BGType(320, 200,  2,  -50, 100, 800), // bushes A
					new BGType(320, 200,  2,  -50, 100, 800)  // bushes B
					),
	};
	// Helper: get the BGConfig for whatever level is currently selected.
	// Falls back to level 1 if the index is out of range.
	public static BGConfig forCurrentLevel() {
		int sel = GameSettings.levelSelection;
		if (sel >= 1 && sel < LEVEL_CONFIGS.length && LEVEL_CONFIGS[sel] != null) {
			return LEVEL_CONFIGS[sel];
		}
		return LEVEL_CONFIGS[1];
	}
}