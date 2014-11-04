package com.mojang.mojam;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * 
 * @author Johan
 */
public class GameStateController extends StateBasedGame {
    public GameStateController() {
        super("Nuclear Pizza War - Jojo/Krasch Edition");
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        addState(new MojangLogoState());
        addState(new TeamTotalschadenLogoState());
        addState(new StartScreenState());
        addState(new DifficultyState());
        addState(new GameState());
        addState(new GameOverState());
    }

}
