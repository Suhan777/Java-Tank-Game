package com.combat;

import javax.swing.ImageIcon;

public class Wall extends General{

	public Wall(int x, int y, int modeSel) {

		this.modeSel = modeSel;
		imageSet = new srcImage(modeSel);
		
		this.x = x;
		this.y = y;
		
		image = imageSet.getWallImage();
		width = image.getWidth(null);
		height = image.getHeight(null);
	}

	public void respawn() {
		x = Math.random() * 924;
		y = Math.random() * 668;
	}
}
