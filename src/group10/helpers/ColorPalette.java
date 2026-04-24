package group10.helpers;

public enum ColorPalette {

    // Row 1
    PINK("#ED9DB2"),
    CRIMSON_RED("#DA1140"),
    BURGUNDY("#922244"),
    SALMON("#F68571"),
    BRIGHT_RED("#E82434"),
    DARK_RED("#A81B34"),
    LIGHT_ORANGE("#FFAD64"),

    // Row 2
    ORANGE("#FC6B04"),
    RUST_BROWN("#983F23"),
    PALE_YELLOW("#EBE86C"),
    YELLOW("#FFD600"),
    DARK_ORANGE("#E98500"),
    LIGHT_GREEN("#BFE082"),
    YELLOW_GREEN("#A8D116"),

    // Row 3
    OLIVE("#99A109"),
    MINT_GREEN("#9BE4CA"),
    BRIGHT_GREEN("#1BBE1B"),
    DARK_OLIVE("#576B28"),
    LIGHT_TEAL("#A6D4CE"),
    TURQUOISE("#00C6CA"),
    TEAL("#299F7A"),

    // Row 4
    LIGHT_BLUE_GRAY("#A2C8D6"),
    BRIGHT_CYAN("#00B8E8"),
    DARK_CYAN("#008785"),
    SKY_BLUE("#6BB1E4"),
    AZURE_BLUE("#008DD8"),
    COBALT_BLUE("#005696"),
    PERIWINKLE("#6D72D6"),

    // Row 5
    ULTRAMARINE("#1405AD"),
    NAVY_BLUE("#003180"),
    PURPLE("#8344AC"),
    INDIGO("#3F009C"),
    DARK_SLATE_BLUE("#26217A"),
    HOT_PINK("#F76E9B"),
    MAGENTA("#D10078"),

    // Row 6
    DARK_PURPLE("#6B296A"),
    PALE_PEACH("#F3CDAC"),
    TAN("#E09D69"),
    BRONZE("#BA6726"),
    OCHRE("#9F6E36"),
    SIENNA("#694319"),
    DARK_BROWN("#42322A"),

    // Row 7 (Grayscale Bottom Row)
    WHITE("#FFFFFF"),
    LIGHT_GRAY("#A7ABAD"),
    MEDIUM_GRAY("#888C8F"),
    DARK_GRAY("#686C6F"),
    DARKER_GRAY("#4C4E51"),
    BLACK("#12161A"),
	
	DEFAULT("#FFFFFF");

    private final String hexCode;

    ColorPalette(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getHexCode() {
        return hexCode;
    }
}