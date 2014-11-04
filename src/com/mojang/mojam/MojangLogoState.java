package com.mojang.mojam;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.mojang.mojam.util.ColorTools;

/**
 * 
 * @author Johan
 */
public class MojangLogoState extends BasicGameState {
    public static final int ID = 1;
    Image logoImage;
    Animation anim;
    final long logoDuration = 4000;
    long startTime;
    Color colMult = new Color(Color.white);

    @Override
    public void init(GameContainer container, StateBasedGame arg1)
	    throws SlickException {
	container.setMouseCursor("res/pointer/Pizzamouse32.png", 0, 0);
	logoImage = new Image("res/cyborghippo.png");
	startTime = container.getTime();
	colMult.a = 0;
    }

    @Override
    public void render(GameContainer container, StateBasedGame sbg, Graphics g)
	    throws SlickException {
	g.setColor(Color.black);
	g.fillRect(0, 0, container.getWidth(), container.getHeight());
	logoImage.setColor(0, colMult.r, colMult.g, colMult.b, colMult.a);
	logoImage.setColor(1, colMult.r, colMult.g, colMult.b, colMult.a);
	logoImage.setColor(2, colMult.r, colMult.g, colMult.b, colMult.a);
	logoImage.setColor(3, colMult.r, colMult.g, colMult.b, colMult.a);
	logoImage.draw(container.getWidth() / 2 - logoImage.getWidth() / 2,
		container.getHeight() / 2 - logoImage.getHeight() / 2);
    }

    @Override
    public void update(GameContainer container, StateBasedGame sbg, int deltaMS)
	    throws SlickException {
	ColorTools.visualSeekAlpha(colMult, 1.0f, 0.02f);
	Input input = container.getInput();
	boolean skipToStart = input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
		|| input.isKeyDown(Input.KEY_SPACE)
		|| input.isKeyDown(Input.KEY_ESCAPE);
	boolean goToNextScreen = startTime + logoDuration < container.getTime();
	if (skipToStart || goToNextScreen) {
	    sbg.enterState(TeamTotalschadenLogoState.ID, new FadeOutTransition(
		    Color.black, 400), new FadeInTransition());
	}
    }

    @Override
    public int getID() {
	return ID;
    }
}
