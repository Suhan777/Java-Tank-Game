package com.combat;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

public class Hero_2 extends General implements Runnable {

	private Thread loop;
	private double speed = 3;
	private ArrayList<Missile> missiles = new ArrayList<Missile>();
	long lastShotTime = System.currentTimeMillis();
	long lastPowerupTime = System.currentTimeMillis();
	private int currentPowerup = 0;
	private int fireRate = 500;
	private boolean moveForward, moveBackward;
	public boolean started = false;
	private double prevX = 0, prevY = 0, prevAngle = 0;
	private int mapWidth = 950, mapHeight = 670; // fix later
	
	private int index = 0;

	private boolean powerup = false;

	public boolean isPowerup() {
		return powerup;
	}

	public Hero_2(double x, double y, double angle, int modeSel) {

		this.modeSel = modeSel;
		imageSet = new srcImage(modeSel);
		
		this.x = x;
		this.y = y;
		this.angle = angle;

		image = imageSet.getTank2Image();
		width = image.getWidth(null);
		height = image.getHeight(null);

		moveForward = moveBackward = false;

		loop = new Thread(this);
		loop.start();

	}

	// move toward the angle
	// forward
	public void moveForward() {
		prevX = x;
		prevY = y;
		x += speed * Math.cos(Math.toRadians(angle));
		y += speed * Math.sin(Math.toRadians(angle));
		if (x < 0 || x > (1024-48) || y < 0 || y > (768-48)) {
			x = prevX;
			y = prevY;
		}
	}

	// backward
	public void moveBackword() {
		prevX = x;
		prevY = y;
		x -= speed * Math.cos(Math.toRadians(angle));
		y -= speed * Math.sin(Math.toRadians(angle));
		if (x < 0 || x > (1024-48) || y < 0 || y > (768-48)) {
			x = prevX;
			y = prevY;
		}
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public double getPrevX() {
		return prevX;
	}

	public double getPrevY() {
		return prevY;
	}

	public double getPrevAngle() { // do i even need this?
		return prevAngle;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public ArrayList<Missile> getMissiles() {
		return missiles;
	}

	public void setCurrentPowerup(int currentPowerup) {
		this.currentPowerup = currentPowerup;
		lastPowerupTime = System.currentTimeMillis();
	}

	public int getCurrentPowerup() {
		return currentPowerup;
	}

	public void play() {

		// this is just to keep the angle between 0 and 360
		if (angle > 360) {
			angle = 0;
		} else if (angle < 0) {
			angle = 360;
		}

		prevAngle = angle;// do i need this?

		// setting the hero angle
		setAngle(angle);

		// moving the hero
		if (moveForward) {
			moveForward();
			// System.out.println("HI"); // check this?
		} else if (moveBackward) {
			moveBackword();
		}
	}
	
	public static void play(String filename)
	{
	    try
	    {
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(new File(filename)));
	        clip.start();
	    }
	    catch (Exception exc)
	    {
	        exc.printStackTrace(System.out);
	    }
	}

	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_Q) {
			fire();
		}
		if (e.getKeyCode() == KeyEvent.VK_W) {
			moveForward = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			moveBackward = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			angle -= 22.5;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			angle += 22.5;
		}

	}

	private void fire() {
		long timeDiff = System.currentTimeMillis() - lastShotTime;
		if (missiles.size() < 6 && timeDiff > fireRate) {
			missiles.add(new Missile(x + (width / 2), y + (height / 2), angle, modeSel));
			play("fire.wav");
			lastShotTime = System.currentTimeMillis();
		}
	}

	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_W) {
			moveForward = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			moveBackward = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			angle -= 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			angle += 0;
		}
	}

	@Override
	public void run() {

		while (true) {
			for (int i = 0; i < missiles.size(); i++) {
				if (!(missiles.get(i)).isAlive()) {
					missiles.remove(i);
				}
			}

			long timeDiff = System.currentTimeMillis() - lastPowerupTime;
			if (timeDiff > 10000) {
				currentPowerup = 0;
				powerup = false;
				index = 0;
			}

			switch (currentPowerup) {
			case 0:
				speed = 9;
				fireRate = 500;
				powerup = false;
				// life = 0;
				break;
			case 1:
				speed = 4.5;
				powerup = true;
				break;
			case 2:
				speed = 1.5;
				powerup = true;
;
				break;
			case 3:
				fireRate = 333;
				powerup = true;

				break;
			case 4:
				fireRate = 1000;
				powerup = true;

				break;
			case 5:
				// life++;
				powerup = true;
				break;
			}

			if (index == 0) {
				image = imageSet.getTank2Image();
			} else if (index == 1) {
				image = imageSet.getTank2ShieldImage();
			}


			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) {
			}
		}

	}

}
