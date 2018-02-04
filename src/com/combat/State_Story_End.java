package com.combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class State_Story_End extends GameState {
	
	private boolean flash = true;
	private boolean showEnter = true;

	public State_Story_End(GameStateManager gsm) {
		this.gsm = gsm;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		Thread flashThread = new Thread(new Runnable() {
			public void run() {
				while (flash) {

					showEnter = !showEnter;

					try {
						Thread.sleep(500);
					} catch (InterruptedException ignore) {
					}
				}
			}
		});

		flashThread.start(); //starts the flashing thread which is called when this state is set initially
	}

	@Override
	public void update() {

	}

	@Override
	public void draw(Graphics g) {

		g.setColor(new Color(0, 114, 208));
		g.fillRect(0, 0, 1024, 768);

		String Pause = "YOU WIN!";
		Font small = new Font("Helvetica", Font.BOLD, 75);
		g.setColor(Color.BLACK);
		g.setFont(small);

		g.drawString(Pause, 1024 / 2 - 250, 768 / 2);

		if (showEnter) {
			Pause = "PRESS ENTER TO GO BACK TO MAIN MENU";
			small = new Font("Helvetica", Font.BOLD, 30);
			g.setColor(Color.BLACK);
			g.setFont(small);

			g.drawString(Pause, 1024 / 2 - 250, 650);
		} // draws the graphics constantly of this
		
	}

	@Override
	public void keyPressed(KeyEvent k) {

		if (k.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
		}

		if (k.getKeyCode() == KeyEvent.VK_ENTER) {
			flash = false;
			GameStateManager.setState(new State_Menu(gsm));
		}	//returns you back to the main menu after finishing story mode

	}

	@Override
	public void keyReleased(KeyEvent k) {

	}

}
