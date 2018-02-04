package com.combat;

import java.awt.Image;

import javax.swing.ImageIcon;

public class srcImage {

	Image speedUpImage;
	Image higherFireRateImage;
	Image shildImage;
	Image speedDownImage;
	Image lowerFireRateImage;
	Image backgroundImage;
	Image explosionAnimation;
	Image tank1Image;
	Image tank2Image;
	Image tank1ShieldImage;
	Image tank2ShieldImage;
	Image powerupImage;
	Image wallImage;
	Image bulletImage;

	private int modeSelect = 0;

	public srcImage(int modeSelect) {
		this.modeSelect = modeSelect;
		initImages();

	}

	public void initImages() {

		if (modeSelect == 0) {

			ImageIcon backgrnd = new ImageIcon("background1.png");
			backgroundImage = backgrnd.getImage();

			ImageIcon t1 = new ImageIcon("tank11.png");
			tank1Image = t1.getImage();

			ImageIcon t2 = new ImageIcon("tank12.png");
			tank2Image = t2.getImage();
			
			ImageIcon t1p = new ImageIcon("tank11p.png");
			tank1ShieldImage = t1p.getImage();

			ImageIcon t2p = new ImageIcon("tank12p.png");
			tank2ShieldImage = t2p.getImage();

			ImageIcon powerup = new ImageIcon("powerup1.png");
			powerupImage = powerup.getImage();

			ImageIcon wall = new ImageIcon("wall1.png");
			wallImage = wall.getImage();

			ImageIcon bullet = new ImageIcon("bullet1.png");
			bulletImage = bullet.getImage();

		} else if (modeSelect == 1) {

			ImageIcon backgrnd = new ImageIcon("background2.png");
			backgroundImage = backgrnd.getImage();

			ImageIcon t1 = new ImageIcon("tank21.png");
			tank1Image = t1.getImage();

			ImageIcon t2 = new ImageIcon("tank22.png");
			tank2Image = t2.getImage();
			
			ImageIcon t1p = new ImageIcon("tank21p.png");
			tank1ShieldImage = t1p.getImage();

			ImageIcon t2p = new ImageIcon("tank22p.png");
			tank2ShieldImage = t2p.getImage();

			ImageIcon powerup = new ImageIcon("powerup2.png");
			powerupImage = powerup.getImage();

			ImageIcon wall = new ImageIcon("wall2.png");
			wallImage = wall.getImage();

			ImageIcon bullet = new ImageIcon("bullet2.png");
			bulletImage = bullet.getImage();

		} else if (modeSelect == 2) {

			ImageIcon backgrnd = new ImageIcon("background3.png");
			backgroundImage = backgrnd.getImage();

			ImageIcon t1 = new ImageIcon("tank31.png");
			tank1Image = t1.getImage();

			ImageIcon t2 = new ImageIcon("tank32.png");
			tank2Image = t2.getImage();
			
			ImageIcon t1p = new ImageIcon("tank31p.png");
			tank1ShieldImage = t1p.getImage();

			ImageIcon t2p = new ImageIcon("tank13p.png");
			tank2ShieldImage = t2p.getImage();

			ImageIcon powerup = new ImageIcon("powerup3.png");
			powerupImage = powerup.getImage();

			ImageIcon wall = new ImageIcon("wall3.png");
			wallImage = wall.getImage();

			ImageIcon bullet = new ImageIcon("bullet3.png");
			bulletImage = bullet.getImage();

		} else if (modeSelect == 3) {

			ImageIcon backgrnd = new ImageIcon("background1.png");
			backgroundImage = backgrnd.getImage();

			ImageIcon t1 = new ImageIcon("tank11.png");
			tank1Image = t1.getImage();

			ImageIcon t2 = new ImageIcon("tank12.png");
			tank2Image = t2.getImage();
			
			ImageIcon t1p = new ImageIcon("tank11p.png");
			tank1ShieldImage = t1p.getImage();

			ImageIcon t2p = new ImageIcon("tank12p.png");
			tank2ShieldImage = t2p.getImage();

			ImageIcon powerup = new ImageIcon("powerup1.png");
			powerupImage = powerup.getImage();

			ImageIcon wall = new ImageIcon("wall1.png");
			wallImage = wall.getImage();

			ImageIcon bullet = new ImageIcon("bullet1.png");
			bulletImage = bullet.getImage();

		} else if (modeSelect == 4) {

			ImageIcon backgrnd = new ImageIcon("background4.png");
			backgroundImage = backgrnd.getImage();

			ImageIcon t1 = new ImageIcon("tank11.png");
			tank1Image = t1.getImage();

			ImageIcon t2 = new ImageIcon("tank12.png");
			tank2Image = t2.getImage();
			
			ImageIcon t1p = new ImageIcon("tank11p.png");
			tank1ShieldImage = t1p.getImage();

			ImageIcon t2p = new ImageIcon("tank12p.png");
			tank2ShieldImage = t2p.getImage();

			ImageIcon powerup = new ImageIcon("powerup1.png");
			powerupImage = powerup.getImage();

			ImageIcon wall = new ImageIcon("wall1.png");
			wallImage = wall.getImage();

			ImageIcon bullet = new ImageIcon("bullet1.png");
			bulletImage = bullet.getImage();

		}

		ImageIcon ii = new ImageIcon("speed.png");
		speedUpImage = ii.getImage();
		ImageIcon v = new ImageIcon("1.png");
		speedDownImage = v.getImage();
		ImageIcon iii = new ImageIcon("firerate.png");
		higherFireRateImage = iii.getImage();
		ImageIcon vi = new ImageIcon("2.png");
		lowerFireRateImage = vi.getImage();
		ImageIcon iv = new ImageIcon("shield.png");
		shildImage = iv.getImage();
		ImageIcon vii = new ImageIcon("explosion.gif");
		explosionAnimation = vii.getImage();

	}

	public Image getExplosionAnimation() {
		return explosionAnimation;
	}

	public Image getSpeedUpImage() {
		return speedUpImage;
	}

	public Image getHigherFireRateImage() {
		return higherFireRateImage;
	}

	public Image getShildImage() {
		return shildImage;
	}

	public Image getSpeedDownImage() {
		return speedDownImage;
	}

	public Image getLowerFireRateImage() {
		return lowerFireRateImage;
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public Image getTank1Image() {
		return tank1Image;
	}

	public Image getTank2Image() {
		return tank2Image;
	}

	public Image getPowerupImage() {
		return powerupImage;
	}

	public Image getWallImage() {
		return wallImage;
	}

	public Image getBulletImage() {
		return bulletImage;
	}
	
	public Image getTank1ShieldImage() {
		return tank1ShieldImage;
	}

	public Image getTank2ShieldImage() {
		return tank2ShieldImage;
	}
}
