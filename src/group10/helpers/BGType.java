package group10.helpers;

public class BGType {
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
