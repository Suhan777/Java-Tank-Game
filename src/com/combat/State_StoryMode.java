package com.combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

public class State_StoryMode extends GameState {

	Board_StoryMode board;
	private boolean pause = false;
	private int currentLevel = 0;
	public static String username = "";

	public State_StoryMode(GameStateManager gsm, int level) {
		this.gsm = gsm;
		currentLevel = level;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		board = new Board_StoryMode(currentLevel);

		if (currentLevel == 1) {
			Board_StoryMode.Player1 = "COMPUTER";
			Board_StoryMode.Player2 = JOptionPane.showInputDialog(null, "Enter your Name:");
		}
		// we set the names here using static variables to stop constantly asking for names
		// after each new level.
		board.setNames_set(true);
	}

	@Override
	public void update() {
		System.out.println(board.level);
		if (!pause) {	//only plays when pause isnt set.
			board.update();
		}
		board.updateTimer();

	}

	@Override
	public void draw(Graphics g) {
		g.setColor(new Color(0, 114, 208));
		g.fillRect(0, 0, 1024, 768);

		board.paint(g);

		String Pause = "Game Paused";
		Font small = new Font("Helvetica", Font.BOLD, 75);
		g.setColor(Color.BLACK);
		g.setFont(small);

		if (pause) {
			g.drawString(Pause, 1024 / 2 - 250, 768 / 2);
		}

		if (board.levelComplete) {
			board.levelComplete = false; //must be set off to go to next level
			(board.level)++; //increments the level when you finish a previous level
			GameStateManager.setState(new State_Story_Intermediate(gsm, board.level));
			board.start = true; //restarts board.
		}

	}

	@Override
	public void keyPressed(KeyEvent k) {
		if (k.getKeyCode() == KeyEvent.VK_P) {
			pause = !pause; //toggles pause
			board.setPause(pause);
		}

		if (k.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			board.setGameOver();
		}
		if(k.getKeyCode() == KeyEvent.VK_ESCAPE){
			GameStateManager.setState(new State_Menu(gsm));
			//esc key returns you back to main menu
		}

		if (k.getKeyCode() == KeyEvent.VK_ENTER) {
			if (board.isGameOver()) {
				GameStateManager.currentGameState = new State_Menu(gsm);
				//enter return you to main menu if game is over
			}
		}
		if (!pause && (board.isStart() == true)) {
			board.hero.keyPressed(k); //only move tanks when game starts
		}

	}

	@Override
	public void keyReleased(KeyEvent k) {
		if (!pause && (board.isStart() == true)) {
			board.hero.keyReleased(k);
		}

	}

}
