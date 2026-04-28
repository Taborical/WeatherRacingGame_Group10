package group10.systems;

import group10.enums.AudioFiles;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class SoundPlayer {
	private final Map<AudioFiles, Clip> clips = new EnumMap<>(AudioFiles.class);
	private float volume = 1.0f; // 0.0 (silent) to 1.0 (full)
	private boolean muted = false;

	public SoundPlayer() {
	}

	/** Play a sound once from the start. If it's already playing, restart it. */
	public void play(AudioFiles audio) {
		Clip clip = getClip(audio);
		if (clip == null) return;
		if (clip.isRunning()) clip.stop();
		clip.setFramePosition(0);
		applyVolume(clip);
		clip.start();
	}

	/** Loop a sound continuously (good for background music). */
	public void loop(AudioFiles audio) {
		Clip clip = getClip(audio);
		if (clip == null) return;
		if (clip.isRunning()) clip.stop();
		clip.setFramePosition(0);
		applyVolume(clip);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	/** Stop a specific sound if it is playing. */
	public void stop(AudioFiles audio) {
		Clip clip = clips.get(audio);
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}

	/** Stop every sound that has been loaded. */
	public void stopAll() {
		for (Clip clip : clips.values()) {
			if (clip != null && clip.isRunning()) clip.stop();
		}
	}

	/** True if the given sound is currently playing. */
	public boolean isPlaying(AudioFiles audio) {
		Clip clip = clips.get(audio);
		return clip != null && clip.isRunning();
	}

	/** Set master volume between 0.0 (silent) and 1.0 (full). */
	public void setVolume(float volume) {
		this.volume = Math.max(0f, Math.min(1f, volume));
		for (Clip clip : clips.values()) {
			applyVolume(clip);
		}
	}

	public float getVolume() {
		return volume;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
		for (Clip clip : clips.values()) {
			applyVolume(clip);
		}
	}

	public boolean isMuted() {
		return muted;
	}

	/** Release all clip resources. Call when shutting down. */
	public void dispose() {
		for (Clip clip : clips.values()) {
			if (clip != null) {
				if (clip.isRunning()) clip.stop();
				clip.close();
			}
		}
		clips.clear();
	}

	// --- internal -------------------------------------------------------

	private Clip getClip(AudioFiles audio) {
		Clip clip = clips.get(audio);
		if (clip == null) {
			clip = loadClip(audio);
			if (clip != null) clips.put(audio, clip);
		}
		return clip;
	}

	private Clip loadClip(AudioFiles audio) {
		try {
			AudioInputStream ais = openStream(audio.getPath());
			if (ais == null) {
				System.err.println("Audio file not found: " + audio.getPath());
				return null;
			}
			Clip clip = AudioSystem.getClip();
			try (AudioInputStream in = ais) {
				clip.open(in);
			}
			return clip;
		} catch (Exception e) {
			System.err.println("Failed to load " + audio + ": " + e.getMessage());
			return null;
		}
	}

	private AudioInputStream openStream(String path) throws Exception {
		// Try classpath resource first (works when packaged in a jar).
		InputStream raw = SoundPlayer.class.getResourceAsStream(path);
		if (raw != null) {
			return AudioSystem.getAudioInputStream(new BufferedInputStream(raw));
		}
		// Fallback: try the filesystem (handy during development).
		String fsPath = path.startsWith("/") ? path.substring(1) : path;
		File file = new File(fsPath);
		if (file.exists()) {
			return AudioSystem.getAudioInputStream(file);
		}
		return null;
	}

	private void applyVolume(Clip clip) {
		if (clip == null) return;
		if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;
		FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float effective = muted ? 0f : volume;
		float dB;
		if (effective <= 0.0001f) {
			dB = gain.getMinimum();
		} else {
			dB = (float) (Math.log10(effective) * 20.0);
			dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
		}
		gain.setValue(dB);
	}
}