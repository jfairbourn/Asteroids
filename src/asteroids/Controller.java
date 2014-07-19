package asteroids;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import static asteroids.Constants.*;

/**
 * Controls a game of asteroids
 * @author James Fairbourn
 * @author Joe Zachary
 * 
 */
public class Controller implements CollisionListener, ActionListener, KeyListener, CountdownTimerListener {
	
	// Shared random number generator
	private Random random;
	
	//Part of the debris created when a ship has collided with an asteroid.
	private ShipDebris shipDebris;
	
	// The ship (if one is active) or null (otherwise)
	private Ship ship;
	//The Bullets
	private Bullet bullet;
	//The debris that appears when things collide.
	private Debris debris;
	
	// When this timer goes off, it is time to refresh the animation
	private Timer refreshTimer; 
	
	// Count of how many transitions have been made.  This is used to keep two
	// conflicting transitions from being made at almost the same time.
	private int transitionCount;
	
	//Keeps track of how many bullets appear on the screen.
	private ArrayList<Bullet> bulletTrack;
	
	//Keeps tack of how many asteroids are currently on the screen.
	private int asteroidCount;
	
	// Number of lives left
	private int lives;
	
	//Keeps track of the score of the game.
	private int score;
	
	// The Game and Screen objects being controlled
	private Game game;
	private Screen screen;
	
	//Boolean if the left key is pressed.
	private boolean leftPressed;
	
	//Boolean if the right key is pressed.
	private boolean rightPressed;
	
	//Boolean if the up arrow key is pressed.
	private boolean shipAccel;
	
	//sets the speed of the ship
	private double shipSpeed;
	
	//Keeps track of what level the game is currently on.
	private int levelCount;
	
	
	
	
	
	/**
	 * Constructs a controller to coordinate the game and screen
	 */
	public Controller (Game game, Screen screen) {
		
		
		// Record the game and screen objects
		this.game = game;
		this.screen = screen;
		
		// Initialize the random number generator
		random = new Random();
		
		// Set up the refresh timer.
		refreshTimer = new Timer(FRAME_INTERVAL, this);
		transitionCount = 0;
		
		// Bring up the splash screen and start the refresh timer
		splashScreen();
		refreshTimer.start();
		
		//sets up all of the booleans to false and sets the ship speed
		//to one and level to 0.
		leftPressed = false;
		rightPressed = false;
		shipAccel = false;
		shipSpeed = 1;
		levelCount=0;
		bulletTrack = new ArrayList<Bullet>();
		
	}

	
	/**
	 * Configures the game screen to display the splash screen
	 */
	private void splashScreen () {
		
		// Clear the screen and display the legend
		screen.clear();
		screen.setLegend("Asteroids");
		
		
		// Place four asteroids near the corners of the screen.
		placeAsteroids();
		
		// Make sure there's no ship
		ship = null;
		
	}
	
	
	/**
	 * Get the number of transitions that have occurred.
	 */
	public int getTransitionCount () {
		return transitionCount;
		
	}
	
	
	/**
	 * The game is over.  Displays a message to that effect and
	 * enables the start button to permit playing another game.
	 */
	private void finalScreen () {
		screen.setLegend(GAME_OVER);
		screen.removeCollisionListener(this);
		screen.removeKeyListener(this);
	}
	
	
	
	/**
	 * Places four asteroids near the corners of the screen.
	 * Gives them random velocities and rotations.
	 */
	private void placeAsteroids () {
		asteroidCount = 0;
		Participant a = new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET);
		a.setVelocity(3+levelCount, random.nextDouble()*2*Math.PI);
		a.setRotation(2*Math.PI * random.nextDouble());
		screen.addParticipant(a);
		
		a = new Asteroid(1, 2, SIZE-EDGE_OFFSET, EDGE_OFFSET);
		a.setVelocity(3+levelCount, random.nextDouble()*2*Math.PI);
		a.setRotation(2*Math.PI * random.nextDouble());
		screen.addParticipant(a);
		
		a = new Asteroid(2, 2, EDGE_OFFSET, SIZE-EDGE_OFFSET);
		a.setVelocity(3+levelCount, random.nextDouble()*2*Math.PI);
		a.setRotation(2*Math.PI * random.nextDouble());
		screen.addParticipant(a);
		
		a = new Asteroid(3, 2, SIZE-EDGE_OFFSET, SIZE-EDGE_OFFSET);
		a.setVelocity(3+levelCount, random.nextDouble()*2*Math.PI);
		a.setRotation(2*Math.PI * random.nextDouble());
		screen.addParticipant(a);
		
	}
	
	
	/**
	 * Set things up and begin a new game.
	 */
	private void initialScreen () {
		
		// Clear the screen
		screen.clear();
		
		// Place four asteroids
		placeAsteroids();
		
		// Place the ship
		placeShip();
		
		// Reset statistics
		lives = 3;
		score = 0;
		
		//Sends statistics to be updated on the GUI
		game.getScore(score);
		game.getLive(lives);
		
		// Start listening to events
		screen.addCollisionListener(this);
		screen.addKeyListener(this);
		
		// Give focus to the game screen
		screen.requestFocusInWindow();
		
		//Clears the ArrayList of all Bullet objects.
		bulletTrack.clear();
	}
	

	
	/**
	 * Place a ship in the center of the screen.
	 */
	private void placeShip () {
		if (ship == null) {
			ship = new Ship();
		}
		ship.setPosition(SIZE/2, SIZE/2);
		ship.setRotation(-Math.PI/2);
		ship.setVelocity(0, 0);
		screen.addParticipant(ship);
	}

	
	/**
	 * Deal with collisions between participants.
	 */
	@Override
	public void collidedWith(Participant p1, Participant p2) 
	{
		if (p1 instanceof Asteroid && p2 instanceof Ship) {
			asteroidCollision((Asteroid)p1);
			shipCollision((Ship)p2);
		}
		else if (p1 instanceof Ship && p2 instanceof Asteroid) {
			asteroidCollision((Asteroid)p2);
			shipCollision((Ship)p1);
		}
		else if(p1 instanceof Bullet && p2 instanceof Asteroid)
		{	
			//removes the bullet from the screen.
			screen.removeParticipant((Bullet)p1);
			//removes the bullet from the ArrayList.
			bulletTrack.remove((Bullet)p1);
			asteroidCollision((Asteroid)p2);
		}
		else if(p1 instanceof Asteroid && p2 instanceof Bullet)
		{
			//removes the bullet from the screen.
			screen.removeParticipant((Bullet)p2);
			//removes the bullet from the ArrayList.
			bulletTrack.remove((Bullet)p2);
			asteroidCollision((Asteroid)p1);
		}
	}
	
	
	/**
	 * The ship has collided with something
	 */
	private void shipCollision (Ship s) {
		
		shipDebrisControl(s);
		// Remove the ship from the screen and null it out
		screen.removeParticipant(s);
		ship = null;
		
		// Display a legend and make it disappear in one second
		//screen.setLegend("Ouch!");
		new CountdownTimer(this, null, 1000);
		
		// Decrement lives
		lives--;
		game.getLive(lives);
		// Start the timer that will cause the next round to begin.
		new TransitionTimer(END_DELAY, transitionCount, this);
		
		//sets the booleans for controls to false and clears
		//the ArrayList for bullets.
		shipAccel=false;
		leftPressed = false;
		rightPressed=false;
		bulletTrack.clear();
		
	}
	

	
	/**
	 * Something has hit an asteroid
	 */
	private void asteroidCollision (Asteroid a) {
		
		// The asteroid disappears
		screen.removeParticipant(a);
		
		//increments the count by one.
		asteroidCount++;
		
		//calls the method that displays the debris
		//for asteroids.
		asteroidDebris(a);
		
		//gets the size of the asteroid.
		int size = a.getSize();
		
		//increments the score depending
		//on what type of asteroid was hit.
		if(size == 0)
		{
			score+= 100;
			
		}
		else if(size==1)
		{
			score+= 50;
		}
		else
		{
			score+= 20;
		}
		
		//plays the sound for asteroid collision.
		asteroidAudio(size);
		
		//sends the GUI an update of the score.
		game.getScore(score);
		
		// Create two smaller asteroids.  Put them at the same position
		// as the one that was just destroyed and give them a random
		// direction.
		
		size = size - 1;
		if (size >= 0) {
			int speed = 5 - size;
			Asteroid a1 = new Asteroid(random.nextInt(4), size, a.getX(), a.getY());
			Asteroid a2 = new Asteroid(random.nextInt(4), size, a.getX(), a.getY());
			a1.setVelocity(speed+levelCount, random.nextDouble()*2*Math.PI);
			a2.setVelocity(speed+levelCount, random.nextDouble()*2*Math.PI);
			a1.setRotation(2*Math.PI*random.nextDouble());
			a2.setRotation(2*Math.PI*random.nextDouble());
			screen.addParticipant(a1);
			screen.addParticipant(a2);
			
		
		}
		
	
		//if all asteroids have been destroyed, new asteroids will be
		//placed and level and transitioncount will increment by one.
		if(asteroidCount == 28)
		{
			screen.removeParticipant(ship);
			new TransitionTimer(5000, transitionCount, this);
			placeShip();
			placeAsteroids();
			transitionCount++;
			levelCount++;
		}
	}
	
	/**
	 * Creates debris when a certain asteroid object
	 * has been hit.
	 * @param a Asteroid Object.
	 */
	private void asteroidDebris(Asteroid a)
	{
		//Creates new Debris Ojbects.
		debris = new Debris();
		Debris d2 = new Debris();
		Debris d3 = new Debris();
		Debris d4 = new Debris();
		Debris d5 = new Debris();
		Debris d6 = new Debris();
		
		//Sets the position of each Debris Object.
		debris.setPosition(a.getX(), a.getY());
		d2.setPosition(a.getX(), a.getY());
		d3.setPosition(a.getX()+1, a.getY());
		d4.setPosition(a.getX()+2, a.getY());
		d5.setPosition(a.getX(), a.getY());
		d6.setPosition(a.getX(), a.getY());
		
		//Sets the velocity of each Debris Object.
		debris.setVelocity(1, -Math.PI/2);
		d2.setVelocity(1, Math.PI/2);
		d3.setVelocity(1, 0);
		d4.setVelocity(1, Math.PI);
		d5.setVelocity(1, Math.PI/4);
		d6.setVelocity(1, Math.PI/6);
		
		//Adds each Debris Object to the screen.
		screen.addParticipant(debris);
		screen.addParticipant(d2);
		screen.addParticipant(d3);
		screen.addParticipant(d4);
		screen.addParticipant(d5);
		screen.addParticipant(d6);
		
		//Creates a countdown timer for each Debris
		//Object.
		new CountdownTimer(this,debris,1000);
		new CountdownTimer(this,d2,1000);
		new CountdownTimer(this,d3,1000);
		new CountdownTimer(this,d4,1000);
		new CountdownTimer(this,d5,1000);	
		new CountdownTimer(this,d6,1000);
	}
	
	/**
	 * Creates the debris when a ship is hit.
	 * 
	 * @param s Ship Object.
	 */
	private void shipDebrisControl(Ship s)
	{
		//Creates new shipDebris Objects.
		shipDebris = new ShipDebris(1);
		ShipDebris shipDebris2 = new ShipDebris(1);
		ShipDebris shipDebris3 = new ShipDebris(2);
		
		//Sets the positions of each Object.
		shipDebris.setPosition(s.getXNose(), s.getYNose());
		shipDebris2.setPosition(s.getXNose(), s.getYNose());
		shipDebris3.setPosition(s.getXNose(), s.getYNose());
		
		//Sets the velocity of each Object.
		shipDebris.setVelocity(1, 0);
		shipDebris2.setVelocity(2, Math.PI/16);
		shipDebris3.setVelocity(1, Math.PI/2);
		
		//adds each debris object to the screen.
		screen.addParticipant(shipDebris);
		screen.addParticipant(shipDebris2);
		screen.addParticipant(shipDebris3);
		
		//creates a countdowntimer for each object.
		new CountdownTimer(this,shipDebris,1500);
		new CountdownTimer(this,shipDebris2,1600);
		new CountdownTimer(this,shipDebris3,1700);
	}
	
	/**
	 * Creates a new countdowntimer for each Bullet
	 * @param a Bullet Object
	 */
	private void removeBullet(Bullet a)
	{
		new CountdownTimer(this, a, BULLET_DURATION);
		
	}
	
	/**
	 * Creates the audio when a bullet is fired.
	 */
	private void bulletAudio()
	{
		if(ship==null)
		{
			return;
		}
		try{
			File bulletaf = new File("fire.wav");
		     AudioInputStream astream = AudioSystem.getAudioInputStream(bulletaf);
			Clip audio = AudioSystem.getClip();
				audio.open(astream);
				audio.setFramePosition(0);
				audio.start();
				
		}
		catch(Exception audio)
		{
			
		}
	}
	
	/**
	 * Creates the audio for the ship.
	 */
	private void thrusterAudio() {
		try {
			File thrusteraf = new File("thrust.wav");
			AudioInputStream astream = AudioSystem.getAudioInputStream(thrusteraf);
			Clip audio = AudioSystem.getClip();
			audio.open(astream);
			audio.setFramePosition(0);
			audio.start();
		} catch (Exception audio) {

		}
	}
	
	/**
	 * Creates the audio when an asteriod is hit.
	 * @param a Asteroid Object.
	 */
	private void asteroidAudio(int a)
	{
		//Each Asteroid size has it's own sound.
		try 
		{
		if(a==0)
		{
			File thrusteraf = new File("bangsmall.wav");
			AudioInputStream astream = AudioSystem.getAudioInputStream(thrusteraf);
			Clip audio = AudioSystem.getClip();
			audio.open(astream);
			audio.setFramePosition(0);
			audio.start();
		} 
		if(a==1)
		{
			File thrusteraf = new File("bangmedium.wav");
			AudioInputStream astream = AudioSystem.getAudioInputStream(thrusteraf);
			Clip audio = AudioSystem.getClip();
			audio.open(astream);
			audio.setFramePosition(0);
			audio.start();
		}
		if(a==2)
		{
			File thrusteraf = new File("banglarge.wav");
			AudioInputStream astream = AudioSystem.getAudioInputStream(thrusteraf);
			Clip audio = AudioSystem.getClip();
			audio.open(astream);
			audio.setFramePosition(0);
			audio.start();
		}
		}
		catch (Exception audio) 
		{

		}
	}

	
	/**
	 * This method will be invoked because of button presses and timer events.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// The start button has been pressed.  Stop whatever we're doing
		// and bring up the initial screen
		if (e.getSource() instanceof JButton) {
			transitionCount++;
			initialScreen();	
		}
		
		
		// Time to refresh the screen
		else if (e.getSource() == refreshTimer) {
			try { // this controls the key events as long as ship is not null.
				if (ship != null) {
					
					// if the right is pressed, the ship turns right.
					if (rightPressed == true) {
						shipAccel = false;
						ship.rotate(Math.PI / 16);
						
					}
					
					// if the left key is pressed, the ship turns left.
					if (leftPressed == true) {
						shipAccel = false;
						ship.rotate(-Math.PI / 16);
						
					}
					
					// if the up key is pressed, the ship accelerates in the
					// direction
					// it is pointing.
					if (shipAccel == true) {

						ship.setVelocity(0, ship.getRotation());

						if (shipSpeed < SPEED_LIMIT) {
							ship.accelerate(shipSpeed += 0.5);

						} else {
							ship.setVelocity(SPEED_LIMIT, ship.getRotation());
							ship.accelerate(SPEED_LIMIT);
							
						}
					}
					
					if(shipAccel==false)
					{
						if(shipSpeed > 0)
						{
							shipSpeed-=0.5;
						}
					}

				}
			} catch (Exception except) {

			}
			
			// Refresh screen
			screen.refresh();
		}
	}
	
	
	/**
	 * Based on the state of the controller, transition to the next state.
	 */
	public void performTransition () {
		
		// Record that a transition was made.  That way, any other pending
		// transitions will be ignored.
		transitionCount++;

		// If there are no lives left, the game is over.  Show
		// the final screen.
		if (lives == 0) {
			finalScreen();
		}
		
		// The ship must have been destroyed.  Place a new one and
		// continue on the current level
		else {
			placeShip();
			shipSpeed=0;
		}
		
	}


	/**
	 * Deals with certain key presses
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) { 
			if (ship != null) 
			leftPressed = true; //sets the boolean to true.
			
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (ship != null) 
			rightPressed = true;	//set the boolean to true.
			
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			if(ship!=null)
			{	
				//creates the audio for the ship.
				thrusterAudio();
				
				//sets the boolean to true.
				shipAccel = true;
			}
			
			
			
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
		
					if(ship!=null)
					{
						//as long as there less than eight
						//bullet objects on the screen,
						//another one is created when this is called.
						if(bulletTrack.size()<8)
					{
					//creates a new bullet object.
					bullet = new Bullet();
					
					//creates the audio for each bullet object.
					bulletAudio();
					
					//sets the position of each bullet object.
					bullet.setPosition(ship.getXNose(), ship.getYNose());
					
					//sets the rotation of each bullet object.
					bullet.rotate(ship.getRotation());
					
					//sets the velocity of each bullet object.
					bullet.setVelocity(BULLET_SPEED, ship.getRotation());
					
					//adds the bullet to the screen.
					screen.addParticipant(bullet);
					
					//adds the bullet object to the ArrayList.
					bulletTrack.add(bullet);
					
					//calls the removeBullet object.
					removeBullet(bullet);
					
					}
					}
		}
	
	}

	/**
	 * Deals with certain items when the key is released.
	 */
	@Override
	public void keyReleased(KeyEvent e) 
	{
		try
		{
		if(e.getKeyCode() == KeyEvent.VK_LEFT);
		{
			//when the left key is released, the boolean is set to false.
			leftPressed = false;
			ship.friction();
		}
		 if(e.getKeyCode()== KeyEvent.VK_RIGHT)
		{
			 //when the right key is released, the boolean is set to false.
			rightPressed = false;
			ship.friction();
		}
		if(e.getKeyCode()== KeyEvent.VK_UP)
		{
			//when the up key is released, the boolean is set to false
			//and shipSpeed decreases.
			shipAccel = false;
			ship.friction();
			
			
		}
		
		}
		catch(Exception ex)
		{
			
		}
	}


	@Override
	public void keyTyped(KeyEvent e) {
		
	}


	/**
	 * Callback for countdown timer.  Used to create transient effects.
	 */
	@Override
	public void timeExpired(Participant p) {
		screen.setLegend("");
		
		//removes the Ojbect from the screen.
		screen.removeParticipant(p);
		
		//removes the bullet object from the ArrayList.
		bulletTrack.remove(p);
	
	}	


}
