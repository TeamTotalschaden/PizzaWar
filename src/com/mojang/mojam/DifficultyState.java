package com.mojang.mojam;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.mojang.mojam.gui.Button;
import com.mojang.mojam.world.Starfield;

/**
 * 
 * @author Johan
 */
public class DifficultyState extends BasicGameState {
    public static final int ID = 4;
    public static int difficulty;
    public static float diffMulti;
    private static Image difficultyImage;
    private static UnicodeFont font;
    private Starfield starfield;

    private static final String BUTTON_TEXT[] = { "Easiest", "Easy", "Normal",
	    "Hard", "Hardest", "Test" };
    private static final Button[] BUTTON = new Button[BUTTON_TEXT.length];
    
    @Override
    public int getID() {
	return ID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(GameContainer container, StateBasedGame game)
	    throws SlickException {
	starfield = new Starfield(container.getScreenWidth(),
		container.getScreenHeight());
	difficultyImage = new Image("pickups/pepperoni.png");
	font = new UnicodeFont("fonts/Franchise-Bold.ttf", 72, false, false);
	font.addAsciiGlyphs();
	font.getEffects().add(new ColorEffect());
	font.loadGlyphs();

	for (int i = 0; i < BUTTON_TEXT.length; i++) {
	    BUTTON[i] = new Button(BUTTON_TEXT[i]);
	}
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
	    throws SlickException {
	container.setMouseCursor("pointer/Pizzamouse32.png", 0, 0);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int deltaMS)
	    throws SlickException {
	Input input = gc.getInput();

	if (input.isMousePressed(0)) {

	    int mouseX = input.getMouseX();
	    int mouseY = input.getMouseY();

	    for (int i = 0; i < BUTTON_TEXT.length; i++) {
		if (BUTTON[i].isClicked(mouseX, mouseY)) {
		    difficulty = i;
		    sbg.enterState(GameState.ID, new FadeOutTransition(),
			    new FadeInTransition());
		}
	    }
	}

	starfield.update(gc, deltaMS);
    }

    @Override
    public void render(GameContainer container, StateBasedGame sbg, Graphics g)
	    throws SlickException {
	Camera cam = new Camera();
	starfield.render(container, g, cam);

	int yPos = container.getHeight() / 3 + BUTTON[0].getHeight();
	int xPos = container.getWidth() / 2 - BUTTON[0].getWidth() / 2;

	for (int i = 0; i < BUTTON_TEXT.length; i++) {
	    BUTTON[i].setPos(xPos, yPos);
	    BUTTON[i].render(container, g);

	    g.drawImage(difficultyImage, xPos + 16, yPos + 16);
	    g.drawImage(difficultyImage, xPos + BUTTON[i].getWidth()
		    - difficultyImage.getWidth() - 16, yPos + 16);

	    yPos += BUTTON[i].getHeight();
	}

	renderString(container, g, "Select Difficulty:");
    }

    public void renderString(GameContainer gc, Graphics grphcs, String message) {
	int width = font.getWidth(message);
	int x = gc.getWidth() / 2 - width / 2;
	int y = gc.getHeight() / 3 - 16;
	grphcs.setFont(font);
	grphcs.setColor(new Color(Color.white));
	grphcs.drawString(message, x, y);
	grphcs.resetFont();
    }

}
