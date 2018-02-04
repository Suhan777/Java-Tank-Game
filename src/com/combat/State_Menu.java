package com.combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

public class State_Menu extends GameState {

	private Image image;
	private Image[] image1 = new Image[10];
	Clip music;

	private void loadImage() {
		ImageIcon ii = new ImageIcon("try.gif"); //taken from https://www.youtube.com/watch?v=wo02IR8fU6I and
		image = ii.getImage();					// adapted by http://www.gifntext.com/.
	}

	private void loadImages() {
		ImageIcon ii = new ImageIcon("Story2.png");
		ImageIcon iii = new ImageIcon("Practice2.png");
		ImageIcon iv = new ImageIcon("Offline2.png");
		ImageIcon v = new ImageIcon("Online2.png");
		ImageIcon vi = new ImageIcon("Exit2.png");
		ImageIcon vii = new ImageIcon("Story.png");
		ImageIcon viii = new ImageIcon("Practice.png");
		ImageIcon ix = new ImageIcon("Offline.png");
		ImageIcon x = new ImageIcon("Online.png");
		ImageIcon xi = new ImageIcon("Exit.png");
		image1[0] = ii.getImage();
		image1[1] = iii.getImage();
		image1[2] = iv.getImage();
		image1[3] = v.getImage();	//puts images into an array
		image1[4] = vi.getImage();
		image1[5] = vii.getImage();
		image1[6] = viii.getImage();
		image1[7] = ix.getImage();
		image1[8] = x.getImage();
		image1[9] = xi.getImage();
	}

	private int currentChoice = 0;

	public State_Menu(GameStateManager gsm) {
		this.gsm = gsm;

		try {
			File file = new File("music.wav");
			AudioInputStream audio = AudioSystem.getAudioInputStream(file);
			music = AudioSystem.getClip();
			music.open(audio);
			music.start();	//plays the music
			music.loop(-1); // music obtained as open source from http://www.bensound.com/royalty-free-music/track/epic
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void draw(Graphics g) {
		loadImages();
		loadImage();
		g.drawImage(image, 0, 0, 1024, 768, null);
		for (int i = 0; i < 5; i++) {

			g.drawImage(image1[i], 725, 300 + i * 100, 275, 75, null);
			if (i == currentChoice) {

				g.drawImage(image1[i + 5], 725, 300 + i * 100, 275, 75, null);
			}

		}
		//^Constantly draws and updates the images. if i is set to the image thats the
	}	// current choice, that image changes.

	public void keyPressed(KeyEvent k) {

		if (k.getKeyCode() == KeyEvent.VK_DOWN) {
			currentChoice++;
			if (currentChoice >= 5) {
				currentChoice = 0;

			} // we have 5 options in the menu so we need it to roll back to 0
				// once we hit option 5.
		} else if (k.getKeyCode() == KeyEvent.VK_UP) {
			currentChoice--;
			if (currentChoice < 0) {
				currentChoice = 5 - 1;
			} //or roll back to option 5 when we hit 0 going up.

		}

		if ((k.getKeyCode() == KeyEvent.VK_ENTER)) {
			if (currentChoice == 0) {
				
				GameStateManager.setState(new State_Story_Intermediate(gsm,1)); //Story
				//changes to story state

			} else if (currentChoice == 1) {
				
				GameStateManager.setState(new State_Practise(gsm)) ; //Practice
				
				//changes to practice state

			} else if (currentChoice == 2) {
				  GameStateManager.setState(new State_Menu2(gsm)); 
				  // goes into the offline menu state

			} else if (currentChoice == 3) {
				//online

			}else if (currentChoice == 4){
				
				System.exit(0); // exits game
			}
		}

	}

	public void keyReleased(KeyEvent k) {

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
