package com.combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board_StoryMode extends JPanel implements ActionListener, KeyListener {
	//Global variables initialised at the start, and used later
	private Timer timer;
	private Timer timer2;
	Image image;
	Image speed;
	Image fire;
	Image shield;
	Image nospeed;
	Image nofire;
	Image explosion;
	private int countdown = 3;
	private int p1score = 0;
	public static String Player1;
	public static String Player2;
	private int p2score = 0;
	private int counttime = 120;
	private boolean pause = false;
	private boolean gameOver = false;
	public boolean start = false;
	private ArrayList<Powerup> powerupArray = new ArrayList<Powerup>();
	private ArrayList<Wall> wallArray = new ArrayList<Wall>();
	private boolean names_set = false;
	public Hero hero;
	public ArrayList<Hero_AI> computer = new ArrayList<Hero_AI>();
	public int stage = 1;
	private boolean orange;
	private boolean red;
	private boolean dispTime = true;
	public boolean levelComplete = false;
	public int level = 1;
	public int tankToRemove = 0;
	private double posyt1;
	private double posxt1;
	private Timer timer3;
	protected int explosiontime = 1;
	private boolean explodet2 = false;
	private boolean explodet1 = false;
	private double posxt2;
	private double posyt2;
	private int mode = 4;
	private srcImage imageSet;
	
	// constructor
	public Board_StoryMode(int stage) {
		this.stage = stage;
		level = stage;
		init();
		setBackground(Color.black);
		setDoubleBuffered(true);
		setFocusable(true);

	}
	//start, counttime, gameOver are variables used to start and pause the games, and stop everything from running once the 120 seconds has passed
	public boolean isStart() {
		return start;
	}

	public void setGameOver() {
		gameOver = true;
	}

	public void ResetTime() {
		counttime = 120;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
	// this play function is used to play sounds when collisions happen, you input parameter is the filename of the music file
	public static void play(String filename) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(filename)));
			clip.start();
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
	}
	
	private void init() {

		getWalls(); //get walls function is called, the walls are stores in an array called wallArray
		
		imageSet = new srcImage(mode);//secImage is a class which contains all the different images, modeSelect defines which set tanks, bullets and walls you want
		
		//these are the inilialization of the new tanks into the board. getValidRespawnPosition checks for a valid place to spawn
		hero = new Hero(900, 350, 180, mode);
		hero.spawnGenerator();
		while (getValidRespawnPosition((int) hero.getX(), (int) hero.getY(), hero.getImageWidth(),
				hero.getImageHeight())) {
			hero.spawnGenerator();
		}

		for (int i = 0; i < level; i++) {
			computer.add(new Hero_AI(50, 350, 0, mode, wallArray, powerupArray));
			(computer.get(i)).spawnGenerator();
			while (getValidRespawnPosition((int) (computer.get(i)).getX(), (int) (computer.get(i)).getY(),
					(computer.get(i)).getImageWidth(), (computer.get(i)).getImageHeight())) {
				(computer.get(i)).spawnGenerator();
			}
		}

		//call the function which contains all the different threads. Timer is used for the count down of the time the (120 second) countdown.
		gameStartThread();
		timer = new Timer(1000, this);
		timer.start();

	}

	public void setNames_set(boolean names_set) {
		this.names_set = names_set;
	}

	//all these timer function is used to start/pasue the 120 second count down timer
	public void updateTimer() {
		if (pause) {
			this.timer.stop();
		} else {
			this.timer.start();
		}
	}

	public void StopTimer() {
		this.timer.stop();
	}

	public void StartTimer() {
		this.timer.start();
	}

	// depending on the mapChoice you selected in the menu stage, it will load
	// the correspoind map. MapChoice is a static variable, so it is changed at
	// the menu state, before it enters the board class
	
	//either files are read, or if random is picked, the random map generator class is called
	public void getWalls() {
		RandomMapGenerator rmg = new RandomMapGenerator();

		boolean[][] cm = rmg.cellmap;

		for (int x = 0; x < rmg.width; x++) {
			for (int y = 0; y < rmg.height; y++) {
				if (cm[x][y]) {
					wallArray.add(new Wall(x * 12, y * 12, mode));
				}
			}
		}
	}

	//paint method is used to paint the different objects to the JPanel
	//other functions are called within it
	public void paint(Graphics g) {
		super.paint(g);
		checkCollision(g);
		if (gameOver) {
			drawGameOver(g);
		} else {
			drawObjects(g);
			drawScores(g);
			if (dispTime){
				 drawCountdown(g);
			}
			if (hero.isPowerup()) {
				drawPowerupHero(g);
			}
			if(explodet1){
				timer3.start();
				drawExplosion(g, posxt1, posyt1);
				
			}
			if(explodet2){
				timer3.start();
				drawExplosion(g, posxt2, posyt2);
				
			}
		}
		if (names_set) {
			drawStartCountdown(g);
			if (start) {
				names_set = false;
			}
		}
	}

	//firstly what the checkButtetWallCollision does it that it find if the bullet is in the left, right, top or bottom side of the wall.
	//depending on the side of the wall the bullet is in, it reflects it at its composite angle
	//veriable isReflected is set to true, so the own bullet can now kill the tank, becaose only waay a bullet can come and hit your tank back is if it has been reflected
	//plays the sound accordingly
	private void checkBulletWallCollision() {
		ArrayList<Missile> heroMissiles = hero.getMissiles();

		double x1Wall, y1Wall, x2Wall, y2Wall, x1Bullet, y1Bullet, x2Bullet, y2Bullet;
		boolean left, right, top, bottom;

		// tank 1 bullets and wall collision
		for (int i = 0; i < heroMissiles.size(); i++) {
			Missile m1 = heroMissiles.get(i);
			if (m1 == null) {
				break;
			}
			Rectangle r2b = m1.getBounds();
			for (int j = 0; j < wallArray.size(); j++) {
				Wall w1 = wallArray.get(j);
				Rectangle r2w = w1.getBounds();

				x1Wall = r2w.getMinX();
				y1Wall = r2w.getMinY();
				x2Wall = r2w.getMaxX();
				y2Wall = r2w.getMaxY();

				x1Bullet = r2b.getMinX();
				y1Bullet = r2b.getMinY();
				x2Bullet = r2b.getMaxX();
				y2Bullet = r2b.getMaxY();

				left = (x2Bullet < x1Wall);
				right = (x2Wall < x1Bullet);
				top = (y2Bullet < y1Wall);
				bottom = (y2Wall < y1Bullet);

				if ((left && !right && top && !bottom) || (!left && right && top && !bottom)
						|| (left && !right && !top && bottom) || (!left && right && !top && bottom)) {
				} else if (left && !right && !top && !bottom) {
					double leftDist = x1Wall - x2Bullet;
					if (leftDist < 2) {
						m1.setAngle(180 - (m1.getAngle()));
						m1.setReflected(true);
						play("bullet-wall.wav");
						break;
					}
				} else if (!left && right && !top && !bottom) {
					double rightDist = x1Bullet - x2Wall;
					if (rightDist < 2) {
						m1.setAngle(180 - (m1.getAngle()));
						m1.setReflected(true);
						play("bullet-wall.wav");
						break;
					}
				} else if (!left && !right && top && !bottom) {
					double topDist = y1Wall - y2Bullet;
					if (topDist < 2) {
						m1.setAngle(-(m1.getAngle()));
						m1.setReflected(true);
						play("bullet-wall.wav");
						break;
					}
				} else if (!left && !right && !top && bottom) {
					double bottomDist = y1Bullet - y2Wall;
					if (bottomDist < 2) {
						m1.setAngle(-(m1.getAngle()));
						m1.setReflected(true);
						play("bullet-wall.wav");
						break;
					}
				}
			}
		}

		for (int ind = 0; ind < stage; ind++) {
			ArrayList<Missile> hero2Missile = (computer.get(ind)).getMissiles();

			// tank 2 bullets and wall collision
			for (int i = 0; i < hero2Missile.size(); i++) {
				Missile m1 = hero2Missile.get(i);
				if (m1 == null) {
					break;
				}
				Rectangle r2b = m1.getBounds();
				for (int j = 0; j < wallArray.size(); j++) {
					Wall w1 = wallArray.get(j);
					Rectangle r2w = w1.getBounds();

					x1Wall = r2w.getMinX();
					y1Wall = r2w.getMinY();
					x2Wall = r2w.getMaxX();
					y2Wall = r2w.getMaxY();

					x1Bullet = r2b.getMinX();
					y1Bullet = r2b.getMinY();
					x2Bullet = r2b.getMaxX();
					y2Bullet = r2b.getMaxY();

					left = (x2Bullet < x1Wall);
					right = (x2Wall < x1Bullet);
					top = (y2Bullet < y1Wall);
					bottom = (y2Wall < y1Bullet);

					if ((left && !right && top && !bottom) || (!left && right && top && !bottom)
							|| (left && !right && !top && bottom) || (!left && right && !top && bottom)) {
					} else if (left && !right && !top && !bottom) {
						double leftDist = x1Wall - x2Bullet;
						if (leftDist < 2) {
							m1.setAngle(180 - (m1.getAngle()));
							m1.setReflected(true);
							play("bullet-wall.wav");
							break;
						}
					} else if (!left && right && !top && !bottom) {
						double rightDist = x1Bullet - x2Wall;
						if (rightDist < 2) {
							m1.setAngle(180 - (m1.getAngle()));
							m1.setReflected(true);
							play("bullet-wall.wav");
							break;
						}
					} else if (!left && !right && top && !bottom) {
						double topDist = y1Wall - y2Bullet;
						if (topDist < 2) {
							m1.setAngle(-(m1.getAngle()));
							m1.setReflected(true);
							play("bullet-wall.wav");
							break;
						}
					} else if (!left && !right && !top && bottom) {
						double bottomDist = y1Bullet - y2Wall;
						if (bottomDist < 2) {
							m1.setAngle(-(m1.getAngle()));
							m1.setReflected(true);
							play("bullet-wall.wav");
							break;
						}
					}
				}
			}
		}
	}

	//getValidRespawn position finds a void place a tank can respawn, so it cannot respawn on a wall or a powerup
	private boolean getValidRespawnPosition(int x, int y, int imageW, int imageH) {
		Rectangle iRect = new Rectangle(x, y, imageW, imageH);
		// dont spawn on a wall
		for (Wall w : wallArray) {
			Rectangle wRect = w.getBounds();
			if (iRect.intersects(wRect)) {
				return true;
			}
		}

		// dont spawn on a powerup
		for (Powerup p : powerupArray) {
			Rectangle pRect = p.getBounds();
			if (iRect.intersects(pRect)) {
				return true;
			}
		}

		return false;
	}
	
	// checkCollision checks any overlap between two rectangles (rectangles
	// present particular objects)
	// we use the .rotate function so we may find the intersection between the
	// different rotated obejcts
	// is it not a good idea to check the interestion via two rectangles only,
	// we need to create a new object called gerneral path which takes into
	// accout the rotated orentation of the object. Then it calculates the
	// overlap. This way is muh more effective.
	// AffineTransform must also be used, it is to set the coordinates back to
	// the original position, without this line all the objests begin spinning
	// with the tank
	private void checkCollision(Graphics g) {

		Graphics2D g2d = (Graphics2D) g.create();
		Rectangle heroRectangle = hero.getBounds();
		ArrayList<Missile> heroMissiles = hero.getMissiles();

		// hero1 --------------------
		AffineTransform at = g2d.getTransform();
		g2d.setTransform(at);
		at.rotate(Math.toRadians(hero.getAngle()), hero.getX() + hero.getImageWidth() / 2,
				hero.getY() + hero.getImageHeight() / 2);
		GeneralPath heroPath = new GeneralPath();
		heroPath.append(heroRectangle.getPathIterator(at), true);
		Area heroArea = new Area(heroPath);

		for (int ind = 0; ind < stage; ind++) {

			// collision detection for the tanks
			Rectangle hero2Rectangle = (computer.get(ind)).getBounds();
			ArrayList<Missile> hero2Missile = (computer.get(ind)).getMissiles();

			// hero2 ------------------------------
			at = new AffineTransform();
			g2d.setTransform(at);
			at.rotate(Math.toRadians((computer.get(ind)).getAngle()),
					(computer.get(ind)).getX() + (computer.get(ind)).getImageWidth() / 2,
					(computer.get(ind)).getY() + (computer.get(ind)).getImageHeight() / 2);
			GeneralPath hero2Path = new GeneralPath();
			hero2Path.append(hero2Rectangle.getPathIterator(at), true);
			Area hero2Area = new Area(hero2Path);

			// tank1 -- tank2 collision
			hero2Area.intersect(heroArea);
			if (!hero2Area.isEmpty()) {
				play("tank-tank.wav");
				posxt2 = computer.get(ind).getX();
				posyt2 = computer.get(ind).getY();
				posyt1 = hero.getY();
				posxt1 = hero.getX();
				
				explodet2 = true;
				explodet1 = true;
				g2d.setColor(Color.RED);
				g2d.fill(heroArea);
				(computer.get(ind)).setCounter(0);
				(computer.get(ind)).spawnGenerator();
				while (getValidRespawnPosition((int) (computer.get(ind)).getX(), (int) (computer.get(ind)).getY(),
						(computer.get(ind)).getImageWidth(), (computer.get(ind)).getImageHeight())) {
					(computer.get(ind)).spawnGenerator();
				}

				hero.spawnGenerator();
				while (getValidRespawnPosition((int) hero.getX(), (int) hero.getY(), hero.getImageWidth(),
						hero.getImageHeight())) {
					hero.spawnGenerator();
				}
			}

			// tank2 -- tank1 bullet collision
			at = new AffineTransform();
			g2d.setTransform(at);
			for (int i = 0; i < heroMissiles.size(); i++) {
				Missile m = heroMissiles.get(i);
				Rectangle tank1BulletRect = m.getBounds();
				GeneralPath tank1BulletPath = new GeneralPath();
				tank1BulletPath.append(tank1BulletRect.getPathIterator(at), true);
				heroArea = new Area(heroPath);
				hero2Area = new Area(hero2Path);
				Area tank1BulletArea = new Area(tank1BulletPath);
				hero2Area.intersect(tank1BulletArea);
				if (!hero2Area.isEmpty()) {
					play("bullet-tank.wav");
					posxt2 = computer.get(ind).getX();
					posyt2 = computer.get(ind).getY();
					explodet2 = true;
					if ((computer.get(ind)).getCurrentPowerup() == 5) {
					} else {
						(computer.get(ind)).setCounter(0);
						p1score++;
						tankToRemove = ind + 1;
						(computer.get(ind)).spawnGenerator();
						while (getValidRespawnPosition((int) (computer.get(ind)).getX(),
								(int) (computer.get(ind)).getY(), (computer.get(ind)).getImageWidth(),
								(computer.get(ind)).getImageHeight())) {
							(computer.get(ind)).spawnGenerator();
						}
					}
					heroMissiles.remove(i);
					(computer.get(ind)).setCurrentPowerup(0);
				}
			}

			// tank1 bullet -- tank2 bullet collision
			for (int inx = 0; inx < stage; inx++) {
				at = new AffineTransform();
				g2d.setTransform(at);
				hero2Missile = (computer.get(inx)).getMissiles();
				for (int i = 0; i < hero2Missile.size(); i++) {
					Missile m1 = hero2Missile.get(i);
					Rectangle tank2BulletRect = m1.getBounds();
					GeneralPath tank2BulletPath = new GeneralPath();
					tank2BulletPath.append(tank2BulletRect.getPathIterator(at), true);
					Area tank2BulletArea = new Area(tank2BulletPath);

					for (int j = 0; j < heroMissiles.size(); j++) {
						Missile m2 = heroMissiles.get(j);
						Rectangle tankBulletRect = m2.getBounds();
						GeneralPath tankBulletPath = new GeneralPath();
						tankBulletPath.append(tankBulletRect.getPathIterator(at), true);
						Area tankBulletArea = new Area(tankBulletPath);
						tank2BulletArea = new Area(tank2BulletPath);

						tankBulletArea.intersect(tank2BulletArea);
						if (!tankBulletArea.isEmpty()) {
							play("bullet-wall.wav");
							hero2Missile.remove(i);
							heroMissiles.remove(j);
						}
					}
				}
			}

			// tank2 -- powerup collision
			at = new AffineTransform();
			g2d.setTransform(at);
			for (int i = 0; i < powerupArray.size(); i++) {
				Rectangle powerupRectangle = (powerupArray.get(i)).getBounds();
				GeneralPath powerupPath = new GeneralPath();
				powerupPath.append(powerupRectangle.getPathIterator(at), true);
				heroArea = new Area(heroPath);
				hero2Area = new Area(hero2Path);
				Area powerupArea = new Area(powerupPath);
				hero2Area.intersect(powerupArea);
				if (!hero2Area.isEmpty()) {
					play("powerup.wav");
					if ((powerupArray.get(i)).getPowerupType() == 5) {
						(computer.get(ind)).setIndex(1);
					}
					int powerupType = (powerupArray.get(i)).getPowerupType();
					(computer.get(ind)).setCurrentPowerup(powerupType);
					powerupArray.remove(i);
				}
			}

			// tank1 -- tank2 bullet collision
			at = new AffineTransform();
			g2d.setTransform(at);
			for (int i = 0; i < hero2Missile.size(); i++) {
				Missile m1 = hero2Missile.get(i);
				Rectangle tank2BulletRect = m1.getBounds();
				GeneralPath tank2BulletPath = new GeneralPath();
				tank2BulletPath.append(tank2BulletRect.getPathIterator(at), true);
				heroArea = new Area(heroPath);
				hero2Area = new Area(hero2Path);
				Area tank2BulletArea = new Area(tank2BulletPath);
				heroArea.intersect(tank2BulletArea);
				if (!heroArea.isEmpty()) {
					play("bullet-tank.wav");
					posyt1 = hero.getY();
					posxt1 = hero.getX();
					explodet1 = true;
					if (hero.getCurrentPowerup() == 5) {
					} else {
						p2score++;
						hero.spawnGenerator();
						while (getValidRespawnPosition((int) hero.getX(), (int) hero.getY(), hero.getImageWidth(),
								hero.getImageHeight())) {
							hero.spawnGenerator();
						}
					}
					hero2Missile.remove(i);
					hero.setCurrentPowerup(0);
				}
			}

			// tank2 -- tank2 bullet collision
			at = new AffineTransform();
			g2d.setTransform(at);
			for (int i = 0; i < hero2Missile.size(); i++) {
				Missile m1 = hero2Missile.get(i);
				Rectangle tank2BulletRect = m1.getBounds();
				GeneralPath tank2BulletPath = new GeneralPath();
				tank2BulletPath.append(tank2BulletRect.getPathIterator(at), true);
				heroArea = new Area(heroPath);
				hero2Area = new Area(hero2Path);
				Area tank2BulletArea = new Area(tank2BulletPath);
				hero2Area.intersect(tank2BulletArea);
				if (!hero2Area.isEmpty() && m1.isReflected()) {
					play("bullet-tank.wav");
					posxt2 = computer.get(ind).getX();
					posyt2 = computer.get(ind).getY();
					explodet2 = true;
					if ((computer.get(ind)).getCurrentPowerup() == 5) {
					} else {
						p1score++;
						tankToRemove = ind + 1;
						(computer.get(ind)).spawnGenerator();
						while (getValidRespawnPosition((int) (computer.get(ind)).getX(),
								(int) (computer.get(ind)).getY(), (computer.get(ind)).getImageWidth(),
								(computer.get(ind)).getImageHeight())) {
							(computer.get(ind)).spawnGenerator();
						}
					}
					hero2Missile.remove(i);
					(computer.get(ind)).setCurrentPowerup(0);
				}
			}
		}

		// tank1 -- tank1 bullet collision
		at = new AffineTransform();
		g2d.setTransform(at);
		for (int i = 0; i < heroMissiles.size(); i++) {
			Missile m1 = heroMissiles.get(i);
			Rectangle tankBulletRect = m1.getBounds();
			GeneralPath tankBulletPath = new GeneralPath();
			tankBulletPath.append(tankBulletRect.getPathIterator(at), true);
			heroArea = new Area(heroPath);
			Area tankBulletArea = new Area(tankBulletPath);
			heroArea.intersect(tankBulletArea);
			if (!heroArea.isEmpty() && m1.isReflected()) {
				play("bullet-tank.wav");
				posyt1 = hero.getY();
				posxt1 = hero.getX();
				explodet1 = true;
				if (hero.getCurrentPowerup() == 5) {
				} else {
					p2score++;
					hero.spawnGenerator();
					while (getValidRespawnPosition((int) hero.getX(), (int) hero.getY(), hero.getImageWidth(),
							hero.getImageHeight())) {
						hero.spawnGenerator();
					}
				}
				heroMissiles.remove(i);
				hero.setCurrentPowerup(0);
			}
		}

		// tank1 -- powerup collision
		at = new AffineTransform();
		g2d.setTransform(at);
		for (

		int i = 0; i < powerupArray.size(); i++) {
			Rectangle powerupRectangle = (powerupArray.get(i)).getBounds();
			GeneralPath powerupPath = new GeneralPath();
			powerupPath.append(powerupRectangle.getPathIterator(at), true);
			heroArea = new Area(heroPath);
			Area powerupArea = new Area(powerupPath);
			heroArea.intersect(powerupArea);
			if (!heroArea.isEmpty()) {
				play("powerup.wav");
				int powerupType = (powerupArray.get(i)).getPowerupType();
				if ((powerupArray.get(i)).getPowerupType() == 5) {
					hero.setIndex(1);
				}
				hero.setCurrentPowerup(powerupType);
				powerupArray.remove(i);
			}
		}

		// tank 1 and wall collision
		at = new AffineTransform();
		g2d.setTransform(at);
		for (Wall w : wallArray) {
			Rectangle wallRect = w.getBounds();
			GeneralPath wallPath = new GeneralPath();
			wallPath.append(wallRect.getPathIterator(at), true);
			heroArea = new Area(heroPath);
			Area wallArea = new Area(wallPath);
			heroArea.intersect(wallArea);
			if (!heroArea.isEmpty()) {
				hero.setX(hero.getPrevX());
				hero.setY(hero.getPrevY());
			}
		}
		
		if (tankToRemove != 0){
			stage--;
			computer.remove(tankToRemove-1);
			tankToRemove = 0;
			if (p1score == level){
				levelComplete = true;
				start = false;
			}
		}
	}

	//this will be displayed in the last screen, indicating who won
	private void drawGameOver(Graphics g) {
		String msg;
		Font small = new Font("Helvetica", Font.BOLD, 75);
		FontMetrics fm = getFontMetrics(small);

		if (p1score == p2score) {
			ImageIcon ii = new ImageIcon("t71.gif");
			image = ii.getImage();
		} else {
			ImageIcon ii = new ImageIcon("g_over.gif");
			image = ii.getImage();
		}

		g.drawImage(image, 0, 0, 1024, 768, this);
		if (p1score > p2score) {
			msg = "Congratulations COMPUTER" + "!";
			g.setColor(Color.white);
			g.setFont(small);
			g.drawString(msg, (1024 - fm.stringWidth(msg)) / 2, 768 / 2);
		} else if (p1score < p2score) {
			msg = "Congratulations " + Player2 + "!";
			g.setColor(Color.white);
			g.setFont(small);
			g.drawString(msg, (1024 - fm.stringWidth(msg)) / 2, 768 / 2);
		}

	}

	private void drawPowerupHero(Graphics g) {

		switch (hero.getCurrentPowerup()) {
		case 0:

			break;
		case 1:

			g.drawImage(speed, 974, 0, 50, 50, this); // speed = 4.5;
			break;
		case 2:

			g.drawImage(nospeed, 974, 0, 50, 50, this);
			break;
		case 3:

			g.drawImage(fire, 949, 0, 75, 50, this);
			break;
		case 4:

			g.drawImage(nofire, 949, 0, 75, 50, this);
			break;
		case 5:

			g.drawImage(shield, 974, 0, 50, 50, this); // powerup = true;
			break;
		}

	}

	private void drawStartCountdown(Graphics g) {
		String count_down = "" + countdown;
		Font small = new Font("Helvetica", Font.BOLD, 600);
		FontMetrics fm = getFontMetrics(small);
		g.setColor(Color.yellow);
		g.setFont(small);
		g.drawString(count_down, 1024 / 2 - 200, 768 / 2 + 200);
	}

	private void drawCountdown(Graphics g) {
		String count_down = "Time Left: " + counttime + " seconds";
		Font small = new Font("Helvetica", Font.BOLD, 25);
		FontMetrics fm = getFontMetrics(small);
		g.setColor(Color.green);
		if(orange){
			g.setColor(Color.orange);
		}
		if(red){
			g.setColor(Color.red);
		}
		g.setFont(small);
		g.drawString(count_down, (1024 - fm.stringWidth(count_down)) / 2, 25);
	}
	private void drawExplosion(Graphics g, double x, double y){
		ImageIcon vii = new ImageIcon("explosion.gif");
		explosion = vii.getImage();
		g.drawImage(explosion, (int)x, (int)y, null);
	}

	private void drawScores(Graphics g) {
		g.setColor(new Color(0, 0, 0, 150));
		g.drawRect(0, 0, 1024, 38);
		g.fillRect(0, 0, 1024, 38);

		String count_down1 = Player1 + " : " + p1score;
		String count_down2 = Player2 + " : " + p2score;
		Font small = new Font("Helvetica", Font.BOLD, 30);
		FontMetrics fm1 = getFontMetrics(small);
		FontMetrics fm2 = getFontMetrics(small);
		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(count_down1, 75, 30);
		g.drawString(count_down2, 1024 - fm2.stringWidth(count_down2) - 75, 30);
	}

	private void drawObjects(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g.drawImage(imageSet.getBackgroundImage(), 0, 0, 1024, 768, this);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform old = g2d.getTransform();

		// walls
		for (Wall wall : wallArray) {
			g2d.drawImage(wall.getImage(), (int) wall.getX(), (int) wall.getY(), this);
		}

		if (!pause) {
			// hero1
			g2d.rotate(Math.toRadians(hero.getAngle()), hero.getX() + hero.getImageWidth() / 2,
					hero.getY() + hero.getImageHeight() / 2);
			g2d.drawImage(hero.getImage(), (int) hero.getX(), (int) hero.getY(), hero.getImageWidth(),
					hero.getImageHeight(), this);
			g2d.setTransform(old);

			for (int ind = 0; ind < stage; ind++) {
				// hero2
				g2d.rotate(Math.toRadians((computer.get(ind)).getAngle()),
						(computer.get(ind)).getX() + (computer.get(ind)).getImageWidth() / 2,
						(computer.get(ind)).getY() + (computer.get(ind)).getImageHeight() / 2);
				g2d.drawImage((computer.get(ind)).getImage(), (int) (computer.get(ind)).getX(),
						(int) (computer.get(ind)).getY(), (computer.get(ind)).getImageWidth(),
						(computer.get(ind)).getImageHeight(), this);
				g2d.setTransform(old);

				// hero2 bullets
				ArrayList<Missile> ms2 = (computer.get(ind)).getMissiles();
				for (Missile m2 : ms2) {
					g2d.drawImage(m2.getImage(), (int) m2.getX(), (int) m2.getY(), this);
				}
			}
			// hero1 bullets
			ArrayList<Missile> ms = hero.getMissiles();
			for (Missile m : ms) {
				g2d.drawImage(m.getImage(), (int) m.getX(), (int) m.getY(), this);
			}

			// powerups
			for (Powerup m : powerupArray) {
				g2d.drawImage(m.getImage(), (int) m.getX(), (int) m.getY(), this);
			}

		}
	}

	private void updateMissiles() {

		ArrayList<Missile> ms = hero.getMissiles();
		for (int i = 0; i < ms.size(); i++) {
			Missile m = (Missile) ms.get(i);
			if (m == null) {
				break;
			}
			m.move();
		}

		for (int ind = 0; ind < stage; ind++) {
			ArrayList<Missile> ms2 = (computer.get(ind)).getMissiles();
			for (int i = 0; i < ms2.size(); i++) {
				Missile m1 = (Missile) ms2.get(i);
				if (m1 == null) {
					break;
				}
				m1.move();
			}
		}
	}

	public void update() {

		if (names_set) {
			timer2.start();
		}
		if (gameOver) {
			StopTimer();
		}
		if (start) {
			timer2.stop();
			hero.play();

			for (int ind = 0; ind < stage; ind++) {
				(computer.get(ind)).play();
			}

			if ((hero.getX() == 0) && (hero.getY() == 0)) {
				hero.spawnGenerator();
				while (getValidRespawnPosition((int) hero.getX(), (int) hero.getY(), hero.getImageWidth(),
						hero.getImageHeight())) {
					hero.spawnGenerator();
				}
			}

			for (int i = 0; i < 9; i++) {
				updateMissiles();
				checkBulletWallCollision();
			}
			repaint();
		}

		for (int ind = 0; ind < stage; ind++) {
			(computer.get(ind)).setDestX(hero.getX());
			(computer.get(ind)).setDestY(hero.getY());
		}
	}

	private void gameStartThread() {
		timer2 = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (names_set) {
					countdown--;
					if (countdown == 0) {
						start = true;
						initThread();
					}
				}
			}
		});

	}

	private void initThread() {

		Thread powerupRemoveThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (int i = 0; i < powerupArray.size(); i++) {
						if (!(powerupArray.get(i)).isAlive()) {
							powerupArray.remove(i);
						}
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException ignore) {
					}
				}
			}
		});

		Thread powerupAddThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					Random rand = new Random();
					int powerupTime1 = rand.nextInt((10000 - 0) + 1) + 0;
					Powerup newPowerup = new Powerup(mode);
					newPowerup.spawnGenerator();
					while (getValidRespawnPosition((int) newPowerup.getX(), (int) newPowerup.getY(),
							newPowerup.getImageWidth(), newPowerup.getImageHeight())) {
						newPowerup.spawnGenerator();
					}
					powerupArray.add(newPowerup);
					try {
						Thread.sleep(powerupTime1);
					} catch (InterruptedException ignore) {
					}
				}
			}
		});
		timer3 = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					
			
					explosiontime--;
					if (explosiontime == 0) {
						explodet1 = false;
						explodet2 = false;
						explosiontime = 1;
						timer3.restart();
						timer3.stop();
					}
			}
		});

		powerupRemoveThread.start();
		powerupAddThread.start();

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (start) {
			counttime--;
			if(counttime == 60){
				orange = true;
			}
			if(counttime == 20){
				red = true;
				orange = false;
			}
			if (counttime == 0) {
				gameOver = true;
				start = false;
			}
			if (red) {
				dispTime = !dispTime;
			}
		}
	}
}
