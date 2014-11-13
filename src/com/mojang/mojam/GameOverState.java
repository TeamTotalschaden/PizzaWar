package com.mojang.mojam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.mojang.mojam.gui.Button;
import com.mojang.mojam.world.Starfield;

public class GameOverState extends BasicGameState {

    public static final int ID = 6;
    private Image failImage;
    private Image winImage;
    private Starfield starfield;
    private boolean isVictory;

    private static TextField nameInput;
    private static Button button;

    // DATENBANK BEGIN
    private static String[] Name = new String[10];
    private static int[] Score = new int[10];
    private static int loadedScores = 0;
    private static long scoreSides;
    private static long currentSide = 0;
    private static boolean loadingFinished = false;
    private static final String db = "jdbc:mysql://sql4.freesqldatabase.com:3306/sql457996";
    private static final String user = "sql457996";
    private static final String pw = "pD1*zG4*";

    // DATENBANK END

    public GameOverState() {
    }

    @Override
    public void init(GameContainer container, StateBasedGame sbg)
	    throws SlickException {
	button = new Button("OK");
	button.setPos(container.getWidth() / 2 - button.getWidth() / 2,
		container.getHeight() / 2 + 250);

	Graphics g = container.getGraphics();
	nameInput = new TextField(container, g.getFont(),
		container.getWidth() / 2 + 5, container.getHeight() / 2 + 165,
		141, g.getFont().getLineHeight());
	nameInput.setMaxLength(15);
	nameInput.setBorderColor(new Color(.0f, .0f, .0f, .5f));

	failImage = new Image("res/GUI/game_over.png");
	winImage = new Image("res/GUI/victory.png");
	starfield = new Starfield(container.getScreenWidth(),
		container.getScreenHeight());
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
	    throws SlickException {
	container.setMouseCursor("res/pointer/Pizzamouse32.png", 0, 0);

	// DATENBANK BEGIN
	Thread t = new Thread() {
	    public void run() {
		initScore();
		updateScore(0);
	    }
	};
	t.start();
	// DATENBANK END
    }

    public void initScore() {
	// DATENBANK BEGIN
	Connection cn = null;
	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    cn = DriverManager.getConnection(db, user, pw);

	    ResultSet result = cn.createStatement().executeQuery(
		    "SELECT count(1) FROM highscore");
	    while (result.next()) {
		scoreSides = (long) (result.getLong("count(1)") / 10);
	    }
	    cn.close();

	} catch (Exception e) {
	    e.printStackTrace();
	}
	// DATENBANK END
    }

    public void updateScore(long start) {
	// DATENBANK BEGIN
	Connection cn = null;
	try {
	    loadingFinished = false;
	    Class.forName("com.mysql.jdbc.Driver");
	    cn = DriverManager.getConnection(db, user, pw);

	    // @formatter:off
	    ResultSet result = cn.createStatement().executeQuery(
		    "SELECT Name, Score " +
		    "FROM `highscore` " +
		    "ORDER BY Score DESC " +
		    "LIMIT "+ start +" , 10");
	    // @formatter:on

	    int i = 0;
	    while (result.next()) {
		Name[i] = result.getString("Name");
		Score[i] = result.getInt("Score");
		i++;
	    }
	    loadedScores = i;
	    loadingFinished = true;
	    cn.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	// DATENBANK END
    }

    protected void saveScore(String text, int finalScore) {
	// DATENBANK BEGINN
	Connection cn = null;
	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    cn = DriverManager.getConnection(db, user, pw);
	    cn.createStatement().execute(
		    "insert into highscore (Name, Score) values ('"
			    + nameInput.getText() + "', "
			    + GameState.getFinalScore() + ");");
	    cn.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	// DATENBANK END
    }

    @Override
    public void render(GameContainer container, StateBasedGame sbg, Graphics g)
	    throws SlickException {
	Camera cam = new Camera();
	starfield.render(container, g, cam);
	if (isVictory) {
	    winImage.draw(
		    (container.getScreenWidth() - winImage.getWidth()) / 2,
		    (container.getScreenHeight() - winImage.getHeight()) / 2);
	} else {
	    failImage.draw(
		    (container.getScreenWidth() - failImage.getWidth()) / 2,
		    (container.getScreenHeight() - failImage.getHeight()) / 2);
	}

	// DATENBANK BEGIN
	g.setColor(new Color(0, 0, 0, .5f));
	g.fillRect(container.getScreenWidth() / 2 - 175,
		container.getHeight() / 2 - 125, 175 * 2, g.getFont()
			.getLineHeight() * Name.length + 125);
	g.setColor(Color.white);
	if (loadingFinished) {
	    for (int i = 0; i < (scoreSides == currentSide ? loadedScores : 10); i++) {
		g.drawString((i + 1 + currentSide * 10) + ".",
			container.getScreenWidth() / 2 - 160,
			container.getScreenHeight() / 2 - 105
				+ g.getFont().getLineHeight() * i);
		g.drawString(Name[i], container.getScreenWidth() / 2
			- g.getFont().getWidth(Name[i]) - 5,
			container.getScreenHeight() / 2 - 105
				+ g.getFont().getLineHeight() * i);
		g.drawString("" + Score[i], container.getScreenWidth() / 2 + 5,
			container.getScreenHeight() / 2 - 105
				+ g.getFont().getLineHeight() * i);
	    }
	} else {
	    String msg = "Loading...";
	    g.drawString(msg, container.getScreenWidth() / 2
		    - g.getFont().getWidth(msg) / 2,
		    container.getScreenHeight() / 2
			    - g.getFont().getLineHeight() / 2);
	}

	String msg = "Press <-- or --> to browse.";
	g.drawString(msg, container.getScreenWidth() / 2
		- g.getFont().getWidth(msg) / 2,
		container.getScreenHeight() / 2 + 105);
	// DATENBANK END

	g.setColor(Color.white);
	String score = "Your Score: " + GameState.getFinalScore();
	g.drawString(score, container.getScreenWidth() / 2
		- g.getFont().getWidth(score) / 2, container.getScreenHeight()
		/ 2 + 165 - g.getFont().getLineHeight());

	score = "Enter your Name:";
	g.drawString(score, container.getScreenWidth() / 2
		- g.getFont().getWidth(score) - 5,
		container.getScreenHeight() / 2 + 165);
	nameInput.render(container, g);

	button.render(container, g);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int deltaMS)
	    throws SlickException {

	Input input = gc.getInput();

	if (input.isKeyPressed(Input.KEY_LEFT)) {
	    if (currentSide > 0) {
		currentSide--;
		Thread t = new Thread() {
		    public void run() {
			updateScore(10 * currentSide);
		    }
		};
		t.start();

	    }
	}
	if (input.isKeyPressed(Input.KEY_RIGHT)) {
	    if (currentSide < scoreSides) {
		currentSide++;
		Thread t = new Thread() {
		    public void run() {
			updateScore(10 * currentSide);
		    }
		};
		t.start();
	    }
	}

	if (input.isMousePressed(0)) {
	    int mouseX = input.getMouseX();
	    int mouseY = input.getMouseY();

	    if (button.isClicked(mouseX, mouseY)
		    || input.isKeyPressed(Input.KEY_ENTER)) {

		if (nameInput.getText().trim().equals(""))
		    nameInput.setText("Anonymous");

		// DATENBANK BEGINConnection cn = null;
		Thread t = new Thread() {
		    public void run() {
			saveScore(nameInput.getText(),
				GameState.getFinalScore());
		    }
		};
		t.start();
		// DATENBANK END

		sbg.enterState(StartScreenState.ID, new FadeOutTransition(),
			new FadeInTransition());
	    }
	}
	starfield.update(gc, deltaMS);
    }

    @Override
    public int getID() {
	return ID;
    }

    public void setVictory(boolean victory) {
	isVictory = victory;
    }

}
