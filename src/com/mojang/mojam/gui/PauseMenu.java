package com.mojang.mojam.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.mojang.mojam.DifficultyState;

public class PauseMenu {

	private boolean visible;

	private static final Color shade = new Color(0, 0, 0, .5f);

	private static final Button[] BUTTON = new Button[3];
	private static final String BUTTON_TEXT[] = { "RESUME", "RESTART", "EXIT", };

	private static Image symbol;

	public PauseMenu() throws SlickException {
		symbol = new Image("GUI/radioactive.png");
		for (int i = 0; i < BUTTON_TEXT.length; i++) {
			BUTTON[i] = new Button(BUTTON_TEXT[i], new Color(235, 185, 5));
			BUTTON[i].setTextColor(Color.black);
		}
	}

	public void update(GameContainer container, StateBasedGame sbg, int deltaMS) {

		Input input = container.getInput();

		if (input.isMousePressed(0)) {
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			if (BUTTON[0].isClicked(mouseX, mouseY)) {
				this.setVisible(!this.isVisible());
			}

			if (BUTTON[1].isClicked(mouseX, mouseY)) {
				this.setVisible(!this.isVisible());
				sbg.enterState(DifficultyState.ID, new FadeOutTransition(),
						new FadeInTransition());
			}

			if (BUTTON[2].isClicked(mouseX, mouseY)) {
				System.exit(1);
			}
		}
	}

	public void render(GameContainer container, Graphics g) {
		if (!isVisible()) {
			return;
		}

		g.setColor(shade);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());

		int yPos = container.getHeight() / 2;
		int xPos = container.getWidth() / 2;

		symbol.draw(xPos - symbol.getWidth() / 2, yPos - symbol.getHeight() / 2);

		BUTTON[0].setPos(xPos - BUTTON[0].getWidth() - 125,
				yPos - symbol.getHeight() / 4);
		BUTTON[1].setPos(xPos + 125, yPos - symbol.getHeight() / 4);
		BUTTON[2].setPos(xPos - BUTTON[2].getWidth() / 2,
				yPos + symbol.getHeight() / 2 + 25);

		for (int i = 0; i < BUTTON_TEXT.length; i++) {
			BUTTON[i].render(container, g);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
