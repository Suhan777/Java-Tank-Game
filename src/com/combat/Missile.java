package com.combat;

import javax.swing.ImageIcon;

public class Missile extends General{

	public int speed = 1;
	long startTime = System.currentTimeMillis();
	private long estimatedTime = System.currentTimeMillis() - startTime;
	private boolean reflected = false;

	public Missile(double d, double e, double angle, int modeSel) {

		this.modeSel = modeSel;
		imageSet = new srcImage(modeSel);
		
		image = imageSet.getBulletImage();
		width = image.getWidth(null);
		height = image.getHeight(null);
		this.angle = angle;
		x =  d;
		y =  e;
	}

	public void move() {
		x += speed*Math.cos(Math.toRadians(angle));
		y += speed*Math.sin(Math.toRadians(angle));
	}
	
	public long getTimeAlive(){
		return estimatedTime = System.currentTimeMillis() - startTime;
	}
	
	public boolean isAlive(){
		estimatedTime = System.currentTimeMillis() - startTime;
		if (estimatedTime < 4500) {
			return true;
		}
		return false;
	}

	public boolean isReflected() {
		return reflected;
	}

	public void setReflected(boolean reflected) {
		this.reflected = reflected;
	}
}
