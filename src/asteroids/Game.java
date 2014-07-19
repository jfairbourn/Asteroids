package asteroids;

import javax.swing.*;
import java.awt.*;
import static asteroids.Constants.*;
/**
 * Implements an asteroid game.
 * @author James Fairbourn
 * @author Joe Zachary
 *
 */
public class Game extends JApplet {
	
	/**
	 * Launches the game
	 */
//	public static void main (String[] args) {
//		Game a = new Game();
//		a.setVisible(true);
//	}
	
	//a label to keep track of the score.
	JLabel gameScore;
	//a label to keep track of the lives.
	JLabel lives;
	/**
	 * Lays out the game and creates the controller
	 */
	public Game () {
		
		setSize(1000,1000);
		// Title at the top
		//setTitle(TITLE);
		
		// Default behavior on closing
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// The main playing area and the controller
		Screen screen = new Screen();
		Controller controller = new Controller(this, screen);

		// This panel contains the screen to prevent the screen from being resized
		JPanel screenPanel = new JPanel();
		screenPanel.setLayout(new GridBagLayout());
		screenPanel.add(screen);
		
		// This panel contains buttons and labels
		JPanel controls = new JPanel();
		
		// The button that starts the game
		JButton startGame = new JButton(START_LABEL);
		controls.add(startGame);
		
		//Creates the labels of score and lives
		JPanel labels = new JPanel();
		lives = new JLabel();
		gameScore = new JLabel();
		labels.add(lives);
		labels.add(gameScore);
		controls.add(labels);
		
		
		// Organize everything
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(screenPanel, "Center");
		mainPanel.add(controls, "North");
		setContentPane(mainPanel);
		//pack();
		
		// Connect the controller to the start button
		startGame.addActionListener(controller);
		
		
	}	
/**
 * Sets the current text and score that was
 * sent in as a parameter.
 * @param score the score of the current game.
 */
public void getScore(int score)
{
	gameScore.setText("Score: " + score);
}

/**
 * Sets the current text and life count that was
 * sent in as a parameter.
 * @param live the number of lives of the current game. 
 */
public void getLive(int live)
{
	lives.setText("Lives: " + live);
}
}











		
