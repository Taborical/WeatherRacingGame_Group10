package group10.helpers;

public class BGConfig {
	
    final int[] typeWidths; // visual width ng type of bg element after nung scaling
    final int leftPad;      // gap between road's left edge at yung element's right edge
    final int rightPad;     // gap between road's right edge and element's left edge
    final int xScatter;     // extra x-axis scattering
    final int yScatterMax;  // extra x-axis scattering, max value
    
    // constructor yezzir
    BGConfig(int[] typeWidths, int leftPad, int rightPad, int xScatter, int yScatterMax) {
        this.typeWidths = typeWidths;
        this.leftPad = leftPad;
        this.rightPad = rightPad;
        this.xScatter = xScatter;
        this.yScatterMax = yScatterMax;
    }
    
    // getter ni length ng typeWidth since array xia
    int typeCount() {
    	return typeWidths.length;
    }
}
