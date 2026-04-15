package group10;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class WeatherRacingGame extends JFrame {
	public WeatherRacingGame() {
		setTitle("2-Player Weather Racing Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(new GamePanel());
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
	}
		
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new WeatherRacingGame().setVisible(true));
	}
}


	

