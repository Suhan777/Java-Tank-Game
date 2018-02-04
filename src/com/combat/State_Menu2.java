package com.combat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

public class State_Menu2 extends GameState {
	private Image image;
	private Image[] image1 = new Image[8];
	private Image[] image2 = new Image[6];
	private Image[] image3 = new Image[4];

	private void loadImage() {
		ImageIcon ii = new ImageIcon("temp.jpg");
		image = ii.getImage();
	}

	private void loadImages() {
		ImageIcon ii = new ImageIcon("Map1.png");
		ImageIcon iii = new ImageIcon("Map4.png");
		ImageIcon iv = new ImageIcon("Map8.png");
		ImageIcon v = new ImageIcon("Map5.png");
		ImageIcon vi = new ImageIcon("Map2.png");
		ImageIcon vii = new ImageIcon("Map3.png");
		ImageIcon viii = new ImageIcon("Map7.png");
		ImageIcon ix = new ImageIcon("Map6.png");

		image1[0] = ii.getImage();
		image1[1] = iii.getImage();
		image1[2] = iv.getImage();
		image1[3] = v.getImage();
		image1[4] = vi.getImage();
		image1[5] = vii.getImage();
		image1[6] = viii.getImage();
		image1[7] = ix.getImage();

		ImageIcon sp = new ImageIcon("SinglePlayer2.png");
		ImageIcon mp = new ImageIcon("Multiplayer.png");
		ImageIcon ai = new ImageIcon("AIvsAI2.png");
		ImageIcon sp2 = new ImageIcon("SinglePlayer.png");
		ImageIcon mp2 = new ImageIcon("Multiplayer2.png");
		ImageIcon ai2 = new ImageIcon("AIvsAI.png");

		image2[0] = sp.getImage();
		image2[1] = mp.getImage();
		image2[2] = ai.getImage();
		image2[3] = sp2.getImage();
		image2[4] = mp2.getImage();
		image2[5] = ai2.getImage();

		ImageIcon forest = new ImageIcon("1.jpg");
		ImageIcon dungeon = new ImageIcon("2.jpg");
		ImageIcon ice = new ImageIcon("3.jpg");
		ImageIcon rand = new ImageIcon("4.jpg");

		image3[0] = forest.getImage();
		image3[1] = dungeon.getImage();
		image3[2] = ice.getImage();
		image3[3] = rand.getImage();

	}

	private int currentChoice = 0;
	private int currentChoice2 = 0;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	public State_Menu2(GameStateManager gsm) {
		this.gsm = gsm;
	}

	@Override
	public void draw(Graphics g) {
		loadImages();
		loadImage();
		g.drawImage(image, 0, 0, 1024, 768, null);
		// g.setColor(Color.black);
		for (int i = 0; i < 4; i++) {

			g.drawImage(image1[i], 190 + i * 210, 675, 200, 60, null);
			if (i == currentChoice) {

				g.drawImage(image1[i + 4], 190 + i * 210, 675, 200, 60, null);
				g.drawImage(image3[i], 300, 50, 700, 600, null);
			}//^Constantly draws and updates the images. if i is set to the image thats the
			// current choice, that image changes.

		}

		// if(mapSelected){

		for (int i = 0; i < 3; i++) {

			g.drawImage(image2[i], 25, 100 + i * 100, 250, 75, null);
			if (i == currentChoice2) {

				g.drawImage(image2[i + 3], 25, 100 + i * 100, 250, 75, null);
			}
			//^Constantly draws and updates the images. if i is set to the image thats the
			// current choice, that image changes for the game modes.
		}

		// }

	}

	@Override
	public void keyPressed(KeyEvent k) {
		if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
			currentChoice++;
			if (currentChoice >= 4) {
				currentChoice = 0;

			} // we have 4 options in the map selection menu so we need it to roll back to 0
			// once we hit option 4.
		} else if (k.getKeyCode() == KeyEvent.VK_LEFT) {
			currentChoice--;
			if (currentChoice < 0) {
				currentChoice = 4 - 1;
			} //or roll back to option 4 when we hit 0 going up.

		}
		if (k.getKeyCode() == KeyEvent.VK_DOWN) {
			currentChoice2++;
			if (currentChoice2 >= 3) {
				currentChoice2 = 0;

			}// we have 3 options in the game mode selection menu so we need it to roll back to 0
			// once we hit option 3.
		} else if (k.getKeyCode() == KeyEvent.VK_UP) {
			currentChoice2--;
			if (currentChoice2 < 0) {
				currentChoice2 = 3 - 1;
			} //or roll back to option 3 when we hit 0 going up.

		}
		if ((k.getKeyCode() == KeyEvent.VK_ESCAPE)) {
			GameStateManager.currentGameState = new State_Menu(gsm);
		}

		if ((k.getKeyCode() == KeyEvent.VK_ENTER)) {

			if (currentChoice2 == 0 && currentChoice == 0) {

				Board_Human_VS_AI.mapChoice = currentChoice;
				Board_Human_VS_AI.modeSelect = currentChoice;
				//changes the map based on current choice, either map 1,2,3 or random will
				// be set. Gamemode is based on currentChoice2.
				// if either currentChoice2 is either 0,1,2 , SP, MP or AIvsAI will be selected.
				GameStateManager.setState(new State_Human_VS_AI(gsm));

			} else if (currentChoice2 == 0 && currentChoice == 1) {

				Board_Human_VS_AI.mapChoice = currentChoice;
				Board_Human_VS_AI.modeSelect = currentChoice;
				GameStateManager.setState(new State_Human_VS_AI(gsm));

			} else if (currentChoice2 == 0 && currentChoice == 2) {

				Board_Human_VS_AI.mapChoice = currentChoice;
				Board_Human_VS_AI.modeSelect = currentChoice;
				GameStateManager.setState(new State_Human_VS_AI(gsm));

			} else if (currentChoice2 == 0 && currentChoice == 3) {

				Board_Human_VS_AI.mapChoice = currentChoice;
				Board_Human_VS_AI.modeSelect = currentChoice;
				GameStateManager.setState(new State_Human_VS_AI(gsm));

			} else if (currentChoice2 == 1 && currentChoice == 0) {

				Board_Human_VS_Human.mapChoice = currentChoice;
				Board_Human_VS_Human.modeSelect = currentChoice;
				GameStateManager.setState(new State_Human_VS_Human(gsm));

			} else if (currentChoice2 == 1 && currentChoice == 1) {

				Board_Human_VS_Human.mapChoice = currentChoice;
				Board_Human_VS_Human.modeSelect = currentChoice;
				GameStateManager.setState(new State_Human_VS_Human(gsm));
			} else if (currentChoice2 == 1 && currentChoice == 2) {

				Board_Human_VS_Human.mapChoice = currentChoice;
				Board_Human_VS_Human.modeSelect = currentChoice;
				GameStateManager.setState(new State_Human_VS_Human(gsm));
			} else if (currentChoice2 == 1 && currentChoice == 3) {

				Board_Human_VS_Human.mapChoice = currentChoice;
				Board_Human_VS_Human.modeSelect = currentChoice;
				GameStateManager.setState(new State_Human_VS_Human(gsm));

			} else if (currentChoice2 == 2 && currentChoice == 0) {

				Board_AI_VS_AI.mapChoice = currentChoice;
				Board_AI_VS_AI.modeSelect = currentChoice;
				GameStateManager.setState(new State_AI_VS_AI(gsm));

			} else if (currentChoice2 == 2 && currentChoice == 1) {

				Board_AI_VS_AI.mapChoice = currentChoice;
				Board_AI_VS_AI.modeSelect = currentChoice;
				GameStateManager.setState(new State_AI_VS_AI(gsm));
			} else if (currentChoice2 == 2 && currentChoice == 2) {

				Board_AI_VS_AI.mapChoice = currentChoice;
				Board_AI_VS_AI.modeSelect = currentChoice;
				GameStateManager.setState(new State_AI_VS_AI(gsm));

			} else if (currentChoice2 == 2 && currentChoice == 3) {

				Board_AI_VS_AI.mapChoice = currentChoice;
				Board_AI_VS_AI.modeSelect = currentChoice;
				GameStateManager.setState(new State_AI_VS_AI(gsm));

			}
		}
	}

	@Override
	public void keyReleased(KeyEvent k) {
		// TODO Auto-generated method stub

	}

}
