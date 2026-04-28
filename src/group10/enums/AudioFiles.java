package group10.enums;

public enum AudioFiles {
    GARAGE("/music/garage.wav"),
	LEVEL1("/music/level1.wav"),
	LEVEL2("/music/level2.wav"),
	LEVEL3("/music/level3.wav"),
	LEVEL4("/music/level4.wav"),
	REV("/music/rev.wav"),
	HONK("/music/honk.wav"),
	HIT("/music/crash.wav"),
	SNAP("/music/snapshot.wav"),
	MENU("/music/mainmenu.wav");

    private final String path;

    AudioFiles(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}