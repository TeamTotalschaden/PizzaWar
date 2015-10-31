package com.mojang.mojam;

import java.rmi.Naming;
import java.util.List;

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

import com.mojang.mojam.data.DatabaseInterface;
import com.mojang.mojam.data.ScoreEntry;
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

	private static String[] Name = new String[10];
	private static long[] Score = new long[10];
	private static int loadedScores = 0;
	private static long scorePages;
	private static long currentPage = 0;
	private static boolean loadingFinished = false;
	private static DatabaseInterface db;

	public GameOverState() {
	}

	@Override
	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		try {
			db = (DatabaseInterface) Naming.lookup("rmi://cyborg.no-ip.org:1099/ScoreDatabase");
		} catch (Exception e) {
			System.out.println("DatabaseInterface failed, caught exception " + e.getMessage());
		}
		button = new Button("OK");
		button.setPos(container.getWidth() / 2 - button.getWidth() / 2, container.getHeight() / 2 + 250);

		Graphics g = container.getGraphics();
		nameInput = new TextField(container, g.getFont(), container.getWidth() / 2 + 5, container.getHeight() / 2 + 165,
				141, g.getFont().getLineHeight());
		nameInput.setMaxLength(15);
		nameInput.setBorderColor(new Color(.0f, .0f, .0f, .5f));

		failImage = new Image("GUI/game_over.png");
		winImage = new Image("GUI/victory.png");
		starfield = new Starfield(container.getScreenWidth(), container.getScreenHeight());
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		container.setMouseCursor("GUI/pointer/Pizzamouse32.png", 0, 0);

		Thread t = new Thread() {
			public void run() {
				try {
					scorePages = db.getPageCount(10);
				} catch (Exception e) {
					e.printStackTrace();
				};
				updateScore(0);
			}
		};
		t.start();
	}

	public void updateScore(long start) {
		try {
			loadingFinished = false;

			List<ScoreEntry> result = db.getScore(start);

			int i = 0;
			for (ScoreEntry entry : result) {
				Name[i] = entry.name;
				Score[i] = entry.score;
				i++;
			}
			loadedScores = i;
			loadingFinished = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame sbg, Graphics g) throws SlickException {
		Camera cam = new Camera();
		starfield.render(container, g, cam);
		if (isVictory) {
			winImage.draw((container.getScreenWidth() - winImage.getWidth()) / 2,
					(container.getScreenHeight() - winImage.getHeight()) / 2);
		} else {
			failImage.draw((container.getScreenWidth() - failImage.getWidth()) / 2,
					(container.getScreenHeight() - failImage.getHeight()) / 2);
		}

		// DATENBANK BEGIN
		g.setColor(new Color(0, 0, 0, .5f));
		g.fillRect(container.getScreenWidth() / 2 - 175, container.getHeight() / 2 - 125, 175 * 2,
				g.getFont().getLineHeight() * Name.length + 125);
		g.setColor(Color.white);
		if (loadingFinished) {
			for (int i = 0; i < (scorePages == currentPage ? loadedScores : 10); i++) {
				g.drawString((i + 1 + currentPage * 10) + ".", container.getScreenWidth() / 2 - 160,
						container.getScreenHeight() / 2 - 105 + g.getFont().getLineHeight() * i);
				g.drawString(Name[i], container.getScreenWidth() / 2 - g.getFont().getWidth(Name[i]) - 5,
						container.getScreenHeight() / 2 - 105 + g.getFont().getLineHeight() * i);
				g.drawString("" + Score[i], container.getScreenWidth() / 2 + 5,
						container.getScreenHeight() / 2 - 105 + g.getFont().getLineHeight() * i);
			}
		} else {
			String msg = "Loading...";
			g.drawString(msg, container.getScreenWidth() / 2 - g.getFont().getWidth(msg) / 2,
					container.getScreenHeight() / 2 - g.getFont().getLineHeight() / 2);
		}

		String msg = "Press <-- or --> to browse.";
		g.drawString(msg, container.getScreenWidth() / 2 - g.getFont().getWidth(msg) / 2,
				container.getScreenHeight() / 2 + 105);
		// DATENBANK END

		g.setColor(Color.white);
		String score = "Your Score: " + GameState.getFinalScore();
		g.drawString(score, container.getScreenWidth() / 2 - g.getFont().getWidth(score) / 2,
				container.getScreenHeight() / 2 + 165 - g.getFont().getLineHeight());

		score = "Enter your Name:";
		g.drawString(score, container.getScreenWidth() / 2 - g.getFont().getWidth(score) - 5,
				container.getScreenHeight() / 2 + 165);
		nameInput.render(container, g);

		button.render(container, g);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int deltaMS) throws SlickException {

		Input input = gc.getInput();

		if (input.isKeyPressed(Input.KEY_LEFT)) {
			if (currentPage > 0) {
				currentPage--;
				Thread t = new Thread() {
					public void run() {
						updateScore(10 * currentPage);
					}
				};
				t.start();

			}
		}
		if (input.isKeyPressed(Input.KEY_RIGHT)) {
			if (currentPage < scorePages) {
				currentPage++;
				Thread t = new Thread() {

					public void run() {
						updateScore(10 * currentPage);
					}
				};
				t.start();
			}
		}

		if (input.isMousePressed(0)) {

			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();

			if (button.isClicked(mouseX, mouseY) || input.isKeyPressed(Input.KEY_ENTER)) {

				if (nameInput.getText().trim().equals(""))
					nameInput.setText("Anonymous");

				Thread t = new Thread() {
					public void run() {
						try {
							db.submitScore(GameState.getFinalScore(), nameInput.getText());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				t.start();

				sbg.enterState(StartScreenState.ID, new FadeOutTransition(), new FadeInTransition());

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
