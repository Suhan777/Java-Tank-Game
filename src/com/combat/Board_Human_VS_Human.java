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
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board_Human_VS_Human extends JPanel implements ActionListener {
	// Global variables initialised at the start, and used later
	private Timer timer;
	private Timer timer2;
	public Hero hero;
	public Hero_2 hero_2;
	Image image;
	private int countdown = 3;
	private int p1score = 0;
	private String Player1;
	private String Player2;
	private int p2score = 0;
	private int counttime = 120;
	private boolean pause = false;
	private boolean gameOver = false;
	private boolean start = false;
	private ArrayList<Powerup> powerupArray = new ArrayList<Powerup>();
	private ArrayList<Wall> wallArray = new ArrayList<Wall>();
	private boolean names_set = false;
	private boolean orange;
	private boolean red;
	public static int mapChoice = 0;
	public static int modeSelect = 0;
	private boolean dispTime = true;
	private double posyt1;
	private double posxt1;
	private Timer timer3;
	protected int explosiontime = 1;
	private boolean explodet2 = false;
	private boolean explodet1 = false;
	private double posxt2;
	private double posyt2;
	private srcImage imageSet;

	// constructor
	public Board_Human_VS_Human() {
		init();
		setBackground(Color.darkGray);
		setDoubleBuffered(true);
		setFocusable(true);
	}

	// start, counttime, gameOver are variables used to start and pause the
	// games, and stop everything from running once the 120 seconds has passed
	public boolean isStart() {
		return start;
	}

	public void ResetTime() {
		counttime = 120;
	}

	public void setGameOver() {
		gameOver = true;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	// used to pause
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	// this play function is used to play sounds when collisions happen, you
	// input parameter is the filename of the music file
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

		getWalls(); // get walls function is called, the walls are stores in an
					// array called wallArray

		imageSet = new srcImage(modeSelect); // secImage is a class which
												// contains all the different
												// images, modeSelect defines
												// which set tanks, bullets and
												// walls you want

		// these are the inilialization of the new tanks into the board.
		// getValidRespawnPosition checks for a valid place to spawn
		hero = new Hero(900, 350, 180, modeSelect);
		hero.spawnGenerator();
		while (getValidRespawnPosition((int) hero.getX(), (int) hero.getY(), hero.getImageWidth(),
				hero.getImageHeight())) {
			hero.spawnGenerator();
		}

		hero_2 = new Hero_2(50, 350, 0, modeSelect);
		hero_2.spawnGenerator();
		while (getValidRespawnPosition((int) hero_2.getX(), (int) hero_2.getY(), hero_2.getImageWidth(),
				hero_2.getImageHeight())) {
			hero_2.spawnGenerator();
		}

		// call the function which contains all the different threads. Timer is
		// used for the count down of the time the (120 second) countdown.
		gameStartThread();
		timer = new Timer(1000, this);
		timer.start();

	}

	public void setNames() {
		// askes the user for any custom names
		Player1 = JOptionPane.showInputDialog(null, "Enter your Name:");
		Player2 = JOptionPane.showInputDialog(null, "Enter your Name:");
		names_set = true; // Draw game countdown after names are set.
	}

	// all these timer function is used to start/pasue the 120 second count down
	// timer
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

	// either files are read, or if random is picked, the random map generator
	// class is called
	public void getWalls() {
		if (mapChoice == 0 || mapChoice == 1 || mapChoice == 2) {
			Scanner scanner = null;
			try {
				if (mapChoice == 0) {
					scanner = new Scanner(new File("Map1.txt"));
				} else if (mapChoice == 1) {
					scanner = new Scanner(new File("Map2.txt"));
				} else {
					scanner = new Scanner(new File("Map3.txt"));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while (scanner.hasNextInt()) {
				wallArray.add(new Wall(scanner.nextInt(), scanner.nextInt(), modeSelect));
			}
		} else if (mapChoice == 3) {
			RandomMapGenerator rmg = new RandomMapGenerator();

			boolean[][] cm = rmg.cellmap;

			for (int x = 0; x < rmg.width; x++) {
				for (int y = 0; y < rmg.height; y++) {
					if (cm[x][y]) {
						wallArray.add(new Wall(x * 12, y * 12, modeSelect));
					}
				}
			}
		}
	}

	// paint method is used to paint the different objects to the JPanel
	// other functions are called within it
	public void paint(Graphics g) {
		super.paint(g);
		checkCollision(g);
		if (gameOver) {
			drawGameOver(g);
		} else {
			drawObjects(g);
			drawScores(g);
			if (dispTime) {
				drawCountdown(g);
			}
			if (hero.isPowerup()) {
				drawPowerupHero1(g);
			}

			if (hero_2.isPowerup()) {
				drawPowerupHero2(g);
			}
			if (explodet1) {
				timer3.start();
				drawExplosion(g, posxt1, posyt1);

			}
			if (explodet2) {
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

	// firstly what the checkButtetWallCollision does it that it find if the
	// bullet is in the left, right, top or bottom side of the wall.
	// depending on the side of the wall the bullet is in, it reflects it at its
	// composite angle
	// veriable isReflected is set to true, so the own bullet can now kill the
	// tank, becaose only waay a bullet can come and hit your tank back is if it
	// has been reflected
	// plays the sound accordingly
	private void checkBulletWallCollision() {
		ArrayList<Missile> heroMissiles = hero.getMissiles();
		ArrayList<Missile> hero2Missile = hero_2.getMissiles();

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

	// getValidRespawn position finds a void place a tank can respawn, so it
	// cannot respawn on a wall or a powerup
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
		for (Powerup p : powerupArray) { // error here
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

		// collision detection for the tanks
		Graphics2D g2d = (Graphics2D) g.create();
		Rectangle heroRectangle = hero.getBounds();
		Rectangle hero2Rectangle = hero_2.getBounds();
		ArrayList<Missile> heroMissiles = hero.getMissiles();
		ArrayList<Missile> hero2Missile = hero_2.getMissiles();

		// hero1 --------------------
		AffineTransform at = g2d.getTransform();
		g2d.setTransform(at);
		at.rotate(Math.toRadians(hero.getAngle()), hero.getX() + hero.getImageWidth() / 2,
				hero.getY() + hero.getImageHeight() / 2);
		GeneralPath heroPath = new GeneralPath();
		heroPath.append(heroRectangle.getPathIterator(at), true);
		Area heroArea = new Area(heroPath);

		// hero2 ------------------------------
		at = new AffineTransform();
		g2d.setTransform(at);
		at.rotate(Math.toRadians(hero_2.getAngle()), hero_2.getX() + hero_2.getImageWidth() / 2,
				hero_2.getY() + hero_2.getImageHeight() / 2);
		GeneralPath hero2Path = new GeneralPath();
		hero2Path.append(hero2Rectangle.getPathIterator(at), true);
		Area hero2Area = new Area(hero2Path);

		// tank1 -- tank2 collision
		hero2Area.intersect(heroArea);
		if (!hero2Area.isEmpty()) {
			play("tank-tank.wav");
			posxt2 = hero_2.getX();
			posyt2 = hero_2.getY();
			posyt1 = hero.getY();
			posxt1 = hero.getX();

			explodet2 = true;
			explodet1 = true;
			g2d.setColor(Color.RED);
			g2d.fill(heroArea);
			hero_2.spawnGenerator();
			while (getValidRespawnPosition((int) hero_2.getX(), (int) hero_2.getY(), hero_2.getImageWidth(),
					hero_2.getImageHeight())) {
				hero_2.spawnGenerator();
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
				posxt2 = hero_2.getX();
				posyt2 = hero_2.getY();

				explodet2 = true;
				g2d.setColor(Color.RED);
				g2d.fill(heroArea);
				if (hero_2.getCurrentPowerup() == 5) {
				} else {
					p1score++;
					hero_2.spawnGenerator();
					while (getValidRespawnPosition((int) hero_2.getX(), (int) hero_2.getY(), hero_2.getImageWidth(),
							hero_2.getImageHeight())) {
						hero_2.spawnGenerator();
					}
				}
				heroMissiles.remove(i);
				hero_2.setCurrentPowerup(0);
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
				posxt1 = hero.getX();
				posyt1 = hero.getY();

				explodet1 = true;
				g2d.setColor(Color.RED);
				g2d.fill(heroArea);
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

		// tank1 bullet -- tank2 bullet collision
		at = new AffineTransform();
		g2d.setTransform(at);
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
					hero2Missile.remove(i);
					heroMissiles.remove(j);
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
			hero2Area = new Area(hero2Path);
			Area tankBulletArea = new Area(tankBulletPath);
			heroArea.intersect(tankBulletArea);

			if (!heroArea.isEmpty() && m1.isReflected()) {
				posxt1 = hero.getX(); // where collides
				posyt1 = hero.getY();

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
				posxt2 = hero_2.getX();
				posyt2 = hero_2.getY();

				explodet2 = true;
				if (hero_2.getCurrentPowerup() == 5) {
				} else {
					p1score++;
					hero_2.spawnGenerator();
					while (getValidRespawnPosition((int) hero_2.getX(), (int) hero_2.getY(), hero_2.getImageWidth(),
							hero_2.getImageHeight())) {
						hero_2.spawnGenerator();
					}
				}
				hero2Missile.remove(i);
				hero_2.setCurrentPowerup(0);
			}
		}

		// tank1 -- powerup collision
		at = new AffineTransform();
		g2d.setTransform(at);
		for (int i = 0; i < powerupArray.size(); i++) {
			Rectangle powerupRectangle = (powerupArray.get(i)).getBounds();
			GeneralPath powerupPath = new GeneralPath();
			powerupPath.append(powerupRectangle.getPathIterator(at), true);
			heroArea = new Area(heroPath);
			hero2Area = new Area(hero2Path);
			Area powerupArea = new Area(powerupPath);
			heroArea.intersect(powerupArea);
			if (!heroArea.isEmpty()) {
				if ((powerupArray.get(i)).getPowerupType() == 5) {
					hero.setIndex(1);
				}
				g2d.setColor(Color.RED);
				g2d.fill(heroArea);
				int powerupType = (powerupArray.get(i)).getPowerupType();
				hero.setCurrentPowerup(powerupType);
				powerupArray.remove(i);
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
				if ((powerupArray.get(i)).getPowerupType() == 5) {
					hero_2.setIndex(1);
				}
				g2d.setColor(Color.RED);
				g2d.fill(heroArea);
				int powerupType = (powerupArray.get(i)).getPowerupType();
				hero_2.setCurrentPowerup(powerupType);
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
			hero2Area = new Area(hero2Path);
			Area wallArea = new Area(wallPath);
			heroArea.intersect(wallArea);
			if (!heroArea.isEmpty()) {
				g2d.setColor(Color.RED);
				g2d.fill(heroArea);
				hero.setX(hero.getPrevX());
				hero.setY(hero.getPrevY());
			}
		}
		// tank 2 and wall collision
		at = new AffineTransform();
		g2d.setTransform(at);
		for (Wall w : wallArray) {
			Rectangle wallRect = w.getBounds();
			GeneralPath wallPath = new GeneralPath();
			wallPath.append(wallRect.getPathIterator(at), true);
			heroArea = new Area(heroPath);
			hero2Area = new Area(hero2Path);
			Area wallArea = new Area(wallPath);
			hero2Area.intersect(wallArea);
			if (!hero2Area.isEmpty()) {
				g2d.setColor(Color.RED);
				g2d.fill(hero2Area);
				hero_2.setX(hero_2.getPrevX());
				hero_2.setY(hero_2.getPrevY());
			}
		}
	}

	// this will be displayed in the last screen, indicating who won
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
			msg = "Congratulations " + Player1 + "!";
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

	private void drawPowerupHero2(Graphics g) {

		switch (hero_2.getCurrentPowerup()) {
		case 0:

			break;
		case 1:
			g.drawImage(imageSet.getSpeedUpImage(), 974, 0, 50, 50, this); // speed
																			// =
																			// 4.5;
			break;
		case 2:

			g.drawImage(imageSet.getSpeedDownImage(), 974, 0, 50, 50, this);
			break;
		case 3:

			g.drawImage(imageSet.getHigherFireRateImage(), 949, 0, 75, 50, this);
			break;
		case 4:

			g.drawImage(imageSet.getLowerFireRateImage(), 949, 0, 75, 50, this);
			break;
		case 5:

			g.drawImage(imageSet.getShildImage(), 974, 0, 50, 50, this); // powerup
																			// =
																			// true;
			break;
		}

	}

	private void drawPowerupHero1(Graphics g) {

		switch (hero.getCurrentPowerup()) {
		case 0:

			break;
		case 1:
			g.drawImage(imageSet.getSpeedUpImage(), 0, 0, 50, 50, this); // speed
																			// =
																			// 4.5;
			// powerup = true;
			break;
		case 2:
			g.drawImage(imageSet.getSpeedDownImage(), 0, 0, 50, 50, this);
			break;
		case 3:
			g.drawImage(imageSet.getHigherFireRateImage(), 0, 0, 75, 50, this);
			// powerup = true;
			break;
		case 4:
			g.drawImage(imageSet.getLowerFireRateImage(), 0, 0, 75, 50, this);
			// fireRate = 1000;
			// powerup = true;
			break;
		case 5:
			g.drawImage(imageSet.getShildImage(), 0, 0, 50, 50, this); // powerup
																		// =
																		// true;
			// life++;
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
		if (orange) {
			g.setColor(Color.orange);
		}
		if (red) {
			g.setColor(Color.red);
		}
		g.setFont(small);
		g.drawString(count_down, (1024 - fm.stringWidth(count_down)) / 2, 25);

	}

	// here the explosions are drawn as a image icon
	private void drawExplosion(Graphics g, double x, double y) {
		g.drawImage(imageSet.getExplosionAnimation(), (int) x, (int) y, null);
	}

	// the player scores are drawn on the screen, to keep an indication of who
	// is winning
	// the strings are formatted to have the player scores on either side and
	// the timer in the middle
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

	// this is the main function which draws the diffent objects onto the
	// screen.
	// AffineTransform is used to rotate the object, the g2d.setTransform is
	// called so that we can set the coorinates back to how they were initially
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
			// hero2
			g2d.rotate(Math.toRadians(hero_2.getAngle()), hero_2.getX() + hero_2.getImageWidth() / 2,
					hero_2.getY() + hero_2.getImageHeight() / 2);
			g2d.drawImage(hero_2.getImage(), (int) hero_2.getX(), (int) hero_2.getY(), hero_2.getImageWidth(),
					hero_2.getImageHeight(), this);
			g2d.setTransform(old);
			// hero1 bullets
			ArrayList<Missile> ms = hero.getMissiles();
			for (Missile m : ms) {
				g2d.drawImage(m.getImage(), (int) m.getX(), (int) m.getY(), this);
			}
			// hero2 bullets
			ArrayList<Missile> ms2 = hero_2.getMissiles();
			for (Missile m2 : ms2) {
				g2d.drawImage(m2.getImage(), (int) m2.getX(), (int) m2.getY(), this);
			}
			// powerups
			for (Powerup m : powerupArray) {
				g2d.drawImage(m.getImage(), (int) m.getX(), (int) m.getY(), this);
			}
		}
	}

	// the missiles are updated here to their new values
	private void updateMissiles() {
		ArrayList<Missile> ms = hero.getMissiles();
		for (int i = 0; i < ms.size(); i++) {
			Missile m = (Missile) ms.get(i);
			if (m == null) {
				break;
			}
			m.move();
		}
		ArrayList<Missile> ms2 = hero_2.getMissiles();
		for (int i = 0; i < ms2.size(); i++) {
			Missile m1 = (Missile) ms2.get(i);
			if (m1 == null) {
				break;
			}
			m1.move();
		}
	}

	// here the tank classes are updated. Hero.play calls the class within the
	// hero class which calculates the new position of the tank using the
	// current angle
	public void update() {

		if (names_set) {
			timer2.start();
		}
		if (start) {
			timer2.stop();
			repaint();
			hero.play();
			hero_2.play();
			for (int i = 0; i < 9; i++) {
				updateMissiles(); // here the bullets are updated once, then
									// checked for collision, we cannot just
									// update the bullets (ie move them by 9
									// pixels at once) because the walls are
									// only 8 pixels, hence we could have a
									// instance where the bullet completely
									// passes through the wall.
				checkBulletWallCollision();
			}
		}
	}

	// here we set the background game thread to count down from 120 seconds
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
		// powerup remove thread removes the correspoind thead, once it has
		// timed out. ie isAlive goes to false. This removes the powerups once
		// 10 second has elapsed
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
		// powerup add timer on the other hand adds the powerups to the board.
		// It uses a random number generator to generate the powerups every x
		// seconds
		Thread powerupAddThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					Random rand = new Random();
					int powerupTime1 = rand.nextInt((10000 - 0) + 1) + 0;
					Powerup newPowerup = new Powerup(modeSelect);
					newPowerup.spawnGenerator(); // we must also find a valid
													// postion for the new
													// powerup, we dont want it
													// to spawn on a wall or
													// another powerup
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
		// We must remove the explosion once the tank is shot after a certain
		// time, because it is a gif, and we do not want it running in the
		// backgound.
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
	public void actionPerformed(ActionEvent arg0) {
		// This set of lines is used to change the colour of the timer after a
		// certain time.
		// so it initially starts off green, then goes to orange after 60
		// seconds. And lastly starts FLASHING in red once the time left goes
		// below 20 seconds.
		if (start) {
			counttime--;
			if (counttime == 60) {
				orange = true;
			}
			if (counttime == 20) {
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
