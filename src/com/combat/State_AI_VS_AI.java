package com.combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class State_AI_VS_AI extends GameState {

	public Board_AI_VS_AI board;
	private boolean pause = false;

	public State_AI_VS_AI(GameStateManager gsm) {
		this.gsm = gsm;
	}

	@Override
	public void init() {

		board = new Board_AI_VS_AI(); //Initialises a new AIvsAi board.
	}

	@Override
	public void update() {
		if (!pause) {
			board.update();
		}//only play (or update) the game when the pause is off
		board.updateTimer();
		// keep updating the timer no matter what to check for pauses.
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(0, 0, 1024, 768);

		board.paint(g);

		String Pause = "Game Paused";
		Font small = new Font("Helvetica", Font.BOLD, 75);
		g.setColor(Color.BLACK);
		g.setFont(small); //draws the pause screen and updates the draw method in board AI vs AI.

		if (pause) {
			g.drawString(Pause, 1024 / 2 - 250, 768 / 2);
		}

	}

	@Override
	public void keyPressed(KeyEvent k) {

		if (k.getKeyCode() == KeyEvent.VK_P) {
			pause = !pause;	//toggles pause
			board.setPause(pause);
		}

		if (k.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
//			board.setGame?Over();
		}
		if(k.getKeyCode() == KeyEvent.VK_ESCAPE){
			GameStateManager.setState(new State_Menu(gsm));
			//sets the state back to the main menu after escape is pushed.
		}

		if (k.getKeyCode() == KeyEvent.VK_ENTER) {
			if (board.isGameOver()) {
				GameStateManager.setState(new State_Menu(gsm));
			}
		}//when the game is over and enter is pressed, you're taken back to the main
		// menu
	}

	@Override
	public void keyReleased(KeyEvent k) {

	}

}
