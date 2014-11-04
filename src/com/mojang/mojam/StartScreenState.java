package com.mojang.mojam;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.mojang.mojam.world.Starfield;

/**
 * 
 * @author Johan
 */
public class StartScreenState extends BasicGameState {
	public static final int ID = 3;
	private Starfield starfield;
	Image splashImage;
	Image startButtonImage;
	Animation anim;

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		splashImage = new Image("res/GUI/splash.png");
		startButtonImage = new Image("res/GUI/button_start.png");
		anim = new Animation(new SpriteSheet("res/actors/artichoke.png", 128, 128), 200);
		starfield = new Starfield(container.getScreenWidth(),
				container.getScreenHeight());
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs)
			throws SlickException {
		Camera cam = new Camera();
		starfield.render(gc, grphcs, cam);
		grphcs.drawImage(splashImage,
				(gc.getScreenWidth() - splashImage.getWidth()) / 2,
				(gc.getScreenHeight() - splashImage.getHeight()) / 2);
		Rectangle buttonRect = getStartGameButtonRect(gc);
		grphcs.drawImage(startButtonImage, buttonRect.getX(), buttonRect.getY() + 25);
		anim.draw(gc.getWidth() / 2 - anim.getWidth() / 2, gc.getHeight() / 2 + 100);
		grphcs.setColor(new org.newdawn.slick.Color(0xFFFFFFFF));
		grphcs.drawString(MainClass.Version, gc.getWidth() - 100, gc.getHeight() - 25);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int deltaMS)
			throws SlickException {
		Input input = gc.getInput();
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			Rectangle buttonRect = getStartGameButtonRect(gc);
			if (buttonRect.contains(input.getMouseX(), input.getMouseY() - 25)) {
				sbg.enterState(DifficultyState.ID, new FadeOutTransition(),
						new FadeInTransition());
			}
		}
		starfield.update(gc, deltaMS);
	}

	private Rectangle getStartGameButtonRect(GameContainer slickContainer) {
		int boxWidth = 194;
		int boxHeight = 59;
		return new Rectangle(slickContainer.getWidth() / 2 - boxWidth / 2,
				slickContainer.getHeight() / 2 + 250, boxWidth, boxHeight);
	}
}
