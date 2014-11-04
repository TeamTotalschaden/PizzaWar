package com.mojang.mojam;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.mojang.mojam.world.PizzaWorld;

public class GameState extends BasicGameState implements MusicListener {
    Random random = new Random();
    public static final int ID = 5;
    private static int finalScore;
    private final int updatesPerSecond = 40;
    private final int msPerUpdate = 1000 / updatesPerSecond;
    private final float musicVolume = 0.6f;
    private PizzaWorld pizzaWorld;
    private Camera camera;
    Music music;
    public String[] soundPaths = new String[] { "res/music/mus_fast02.ogg",
    // "res/music/recording2.ogg",
    // "res/music/recording3.ogg",
    // "res/music/recording4.ogg",
    // "res/music/recording5.ogg"
    };

    public GameState() {
    }

    void startGame(GameContainer container) throws SlickException {
	container.setMouseCursor("res/pointer/crosshair5.png", 16, 16);
	camera = new Camera();
	pizzaWorld = new PizzaWorld(camera);
	pizzaWorld.init(container);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
	    throws SlickException {
	if (pizzaWorld != null) {
	    pizzaWorld.render(container, g);
	}
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
	    throws SlickException {
	super.enter(container, game);
	startGame(container);
	music = new Music(soundPaths[random.nextInt(soundPaths.length)]);
	music.play(1.0f, musicVolume);
	music.addListener(this);
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) {
	music.fade(500, 0.0f, true);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game)
	    throws SlickException {
	container.setMinimumLogicUpdateInterval(msPerUpdate);
	container.setMaximumLogicUpdateInterval(msPerUpdate);
    }

    // We are using a fixed update rate
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
	    throws SlickException {
	if (pizzaWorld != null) {
	    camera.update(delta);
	    pizzaWorld.update(container, game, delta);

	    if (pizzaWorld.isGameOver()) {
		setFinalScore(pizzaWorld.getPlayer().getScore());
		GameOverState state = (GameOverState) game
			.getState(GameOverState.ID);
		state.setVictory(pizzaWorld.isVictory());
		game.enterState(GameOverState.ID, new FadeOutTransition(
			Color.black, 1600), new FadeInTransition());
	    }
	}
    }

    @Override
    public int getID() {
	return ID;
    }

    @Override
    public void musicEnded(Music music) {
	try {
	    music = new Music(soundPaths[random.nextInt(soundPaths.length)]);
	    music.play(1.0f, musicVolume);
	    music.addListener(this);
	} catch (Exception e) {

	}
    }

    @Override
    public void musicSwapped(Music music, Music music1) {

    }

    public static int getFinalScore() {
	return finalScore;
    }

    public static void setFinalScore(int finalScore) {
	GameState.finalScore = finalScore;
    }
}
