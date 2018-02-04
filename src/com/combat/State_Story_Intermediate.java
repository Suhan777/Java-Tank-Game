package com.combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Random;

public class State_Story_Intermediate extends GameState {

	private int level = 0;
	private boolean flash = true;
	private boolean showEnter = true;

	public State_Story_Intermediate(GameStateManager gsm, int level) {
		this.gsm = gsm;
		this.level = level;
		//this contructor takes in a level to change between the differing levels in story mode,
	}	// levels are incremented in the StoryMode state.

	@Override
	public void init() {
		Thread flashThread = new Thread(new Runnable() {
			public void run() {
				while (flash) {

					showEnter = !showEnter; //flashes the "press enter to continue" text on screen

					try {
						Thread.sleep(500);
					} catch (InterruptedException ignore) {
					}
				}
			}
		});

		flashThread.start(); //after completing each level you enter this state
	}

	@Override
	public void update() {

	}

	@Override
	public void draw(Graphics g) {
		//draws the necessary text on screen.
		g.setColor(new Color(0, 114, 208));
		g.fillRect(0, 0, 1024, 768);

		if (level == 1) {
			String Pause = "                  Your world is over run by a world of machines.";
			String two = "                 You are the last hope of this dystopian society.";
			String three = "                                  You are its last champion.";
			String four = "   It is up to you to overcome their tyrannical rule and set the future free!";
			String five = "           The people of your world have chosen you to be their saviour.";
			String six = "                                       Dont let your world down!";
			String Seven = "             PREPARE FOR BATTLE!!!";
			Font small = new Font("Helvetica", Font.BOLD, 30);
			g.setColor(Color.white);
			g.setFont(small);
			
			
			g.drawString(Pause, 0, 25);
			g.drawString(two, 0, 125);
			g.drawString(three, 0, 225);
			g.drawString(four, 0, 325);
			g.drawString(five, 0, 425);
			g.drawString(six, 0, 525);
			Font big = new Font("TimesRoman", Font.BOLD, 50);
			g.setColor(Color.red);
			g.setFont(big);
			g.drawString(Seven, 0, 750);
			
		} else {
			String Pause = "Level " + (level - 1) + " Completed";
			Font small = new Font("Helvetica", Font.BOLD, 75);
			g.setColor(Color.BLACK);
			g.setFont(small);

			g.drawString(Pause, 175, 768 / 2);
		}
		
		if (showEnter) {
			String Pause = "PRESS ENTER TO CONTINUE";
			Font small = new Font("Helvetica", Font.BOLD, 30);
			g.setColor(Color.BLACK);
			g.setFont(small);

			g.drawString(Pause, 1024 / 2 - 250, 650);
		}
	}

	@Override
	public void keyPressed(KeyEvent k) {

		if (k.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
		}

		if (k.getKeyCode() == KeyEvent.VK_ENTER) {
			flash = false;
			if (level != 6) {
				GameStateManager.setState(new State_StoryMode(gsm, level));
				// go back into the story mode with a different level.
			} else {
				GameStateManager.setState(new State_Story_End(gsm));
				// once you reach level 5, you finish story mode.
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent k) {

	}

}
