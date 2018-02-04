package com.combat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

public class Hero_AI extends General implements Runnable {

	private Thread loop;
	private double speed = 3;
	private ArrayList<Missile> missiles = new ArrayList<Missile>();
	long lastShotTime = System.currentTimeMillis();
	long lastPowerupTime = System.currentTimeMillis();
	private int currentPowerup = 0;
	private int fireRate = 500;
	public boolean started = false;
	private double prevX = 0, prevY = 0, prevAngle = 0;
	private List<List<Integer>> obstacleArray = new ArrayList<List<Integer>>();
	private List<List<Integer>> pathArray = new ArrayList<List<Integer>>();
	private ArrayList<Powerup> powerupArray = new ArrayList<Powerup>();
	private int pathArrayIndex;
	private double destX, destY;
	private int index = 0;
	private int counter = 0;
	private int reRouteRate = 10;
	private AStar aStar = new AStar();
	private int changeSpeed = 0;

	private boolean powerup = false;

	public boolean isPowerup() {
		return powerup;
	}

	public Hero_AI(double x, double y, double angle, int modeSel, ArrayList<Wall> wallArray,
			ArrayList<Powerup> powerupArray) {

		this.modeSel = modeSel;
		imageSet = new srcImage(modeSel);

		this.x = x;
		this.y = y;
		this.angle = angle;
		this.powerupArray = powerupArray;

		image = imageSet.getTank2Image();
		width = image.getWidth(null);
		height = image.getHeight(null);

		for (int i = 0; i < wallArray.size(); i++) {

			for (int sidex = -48; sidex < 12; sidex++) {
				for (int sidey = -48; sidey < 12; sidey++) {

					if ((((int) ((wallArray.get(i)).getX() + sidex)) > 0)
							&& (((int) ((wallArray.get(i)).getX() + sidex)) < 768)
							&& (((int) ((wallArray.get(i)).getY()) + sidey) > 0)
							&& (((int) ((wallArray.get(i)).getY()) + sidey) < 1024)) {
						List<Integer> row = new ArrayList<Integer>();
						row.add((int) ((wallArray.get(i)).getX() + sidex));
						row.add((int) ((wallArray.get(i)).getY()) + sidey);
						obstacleArray.add(row);
					}
				}
			}
		}

		loop = new Thread(this);
		loop.start();

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

	public void setDestX(double destX) {
		this.destX = destX;
	}

	public void setDestY(double destY) {
		this.destY = destY;
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

	public void setIndex(int index) {
		this.index = index;
	}

	public void spawnGenerator() {
		x = Math.random() * 924;
		y = Math.random() * 668;
		counter = 0;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public static void play(String filename) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(filename)));
			clip.start();
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
	}

	public void play() {

		long start = System.currentTimeMillis();

		boolean goingToPowerup = false;

		if (counter == 0) {

			if (changeSpeed == 0) {
				speed = 3;
			} else if (changeSpeed == 1) {
				speed = 2;
			} else if (changeSpeed == 2) {
				speed = 5;
			}

			for (int i = 0; i < powerupArray.size(); i++) {
				Powerup p = powerupArray.get(i);
				double powerupDist = Math.sqrt(((x - p.getX()) * (x - p.getX())) + ((y - p.getY()) * (y - p.getY())));
				if (powerupDist < 100) {
					aStar.route((int) speed, (int) (768 / speed), (int) (1024 / speed), (int) (x / speed),
							(int) (y / speed), (int) (p.getX() / speed), (int) (p.getY() / speed), obstacleArray);
					goingToPowerup = true;
					break;
				}
			}

			if (!goingToPowerup) {
				aStar.route((int) speed, (int) (768 / speed), (int) (1024 / speed), (int) (x / speed),
						(int) (y / speed), (int) (destX / speed), (int) (destY / speed), obstacleArray);
			}

			pathArray = aStar.getPathArray();
			pathArrayIndex = ((pathArray.size()) - 1);

			goingToPowerup = false;

			if (pathArrayIndex < reRouteRate) {
				counter = pathArrayIndex;
			} else {
				counter = reRouteRate;
			}
		}

		if (pathArrayIndex != 0) {
			double nX = (((pathArray.get(pathArrayIndex - 1)).get(0)) * speed);
			double nY = (((pathArray.get(pathArrayIndex - 1)).get(1)) * speed);

			if ((nX > x) && (nY == y)) {
				angle = 0;
			} else if ((nX > x) && (nY > y)) {
				angle = 45;
			} else if ((nX == x) && (nY > y)) {
				angle = 90;
			} else if ((nX < x) && (nY > y)) {
				angle = 135;
			} else if ((nX < x) && (nY == y)) {
				angle = 180;
			} else if ((nX < x) && (nY < y)) {
				angle = 225;
			} else if ((nX == x) && (nY < y)) {
				angle = 270;
			} else if ((nX > x) && (nY < y)) {
				angle = 315;
			}

			double f = Math.sqrt(((destX - x) * (destX - x)) + ((destY - y) * (destY - y)));

			if (f < 300) {
				fire();
			}

			x = ((pathArray.get(pathArrayIndex)).get(0)) * speed;
			y = ((pathArray.get(pathArrayIndex)).get(1)) * speed;

			counter--;
			pathArrayIndex--;
		}

		long elapsed = System.currentTimeMillis() - start;

	}

	private void fire() {
		long timeDiff = System.currentTimeMillis() - lastShotTime;
		if (missiles.size() < 6 && timeDiff > fireRate) {
			missiles.add(new Missile(x + (width / 2), y + (height / 2), angle, modeSel));
			play("fire.wav");
			lastShotTime = System.currentTimeMillis();
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
				changeSpeed = 0;
				powerup = false;
				index = 0;
			}

			switch (currentPowerup) {
			case 0:
				// speed = 9;
				fireRate = 500;
				// life = 0;
				powerup = false;
				break;
			case 1:
				// speed = 5;
				changeSpeed = 1;
				powerup = true;
				break;
			case 2:
				// speed = 10;
				changeSpeed = 2;
				powerup = true;

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
