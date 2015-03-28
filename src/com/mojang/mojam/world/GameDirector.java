/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.mojang.mojam.DifficultyState;
import com.mojang.mojam.entity.AlienType;
import com.mojang.mojam.entity.FlyingSlice;
import com.mojang.mojam.entity.PizzaBubble;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gui.Announcer;

/**
 * 
 * @author Johan
 */
public class GameDirector {
	private PizzaWorld gameWorld;
	Random random = new Random();
	final long levelPause = 10000;
	List<EnemyWave> enemyWaves = new ArrayList<EnemyWave>();
	int currentEnemyWave = 0;
	private int timeUntilNextRandomPickup;
	private Announcer announcer;
	private Announcer warningAnnouncer;
	boolean announcedLevel = false;
	long levelClearedTime = 0;

	public GameDirector(PizzaWorld gameWorld) {
		this.gameWorld = gameWorld;
		switch (DifficultyState.difficulty) {
		case 0:
			DifficultyState.diffMulti = 0.50f;
			break;
		case 1:
			DifficultyState.diffMulti = 0.75f;
			break;
		case 2:
			DifficultyState.diffMulti = 1.00f;
			break;
		case 3:
			DifficultyState.diffMulti = 1.25f;
			break;
		case 4:
			DifficultyState.diffMulti = 1.50f;
			break;
		case 5:
			DifficultyState.diffMulti = 1.00f;
			break;
		default:
			DifficultyState.diffMulti = 1.00f;
			break;
		}

		if (DifficultyState.difficulty != 5)
			createEnemyWaves(DifficultyState.diffMulti);
		else
			createTestWaves();

		int tmpCurLevel = 1;
		for (EnemyWave wave : enemyWaves) {
			wave.setCurLevel(tmpCurLevel);
			tmpCurLevel++;
		}
	}

	public void init(GameContainer container) {
		announcer = new Announcer(container, container.getHeight() / 2 + 140,
				Color.white);
		announcer.postMessage(container, "Prepare for Battle!");

		warningAnnouncer = new Announcer(container,
				container.getHeight() / 2 - 180, Color.red);
	}

	public void update(GameContainer container, int deltaMS) {
		EnemyWave currentWave = enemyWaves.get(currentEnemyWave);
		if (currentWave.isWaveDone() && announcedLevel
				&& gameWorld.getGameTime() > levelClearedTime + levelPause) {
			if (currentEnemyWave + 1 < enemyWaves.size()) {
				currentEnemyWave++;
				currentWave = enemyWaves.get(currentEnemyWave);
				announcer.postMessage(container, currentWave.getName());
				announcedLevel = false;

				for (int i = 0; i < 1 + (currentEnemyWave / 5); i++) {
					gameWorld.addParticle(new FlyingSlice(gameWorld));
				}
			}
		}
		if (!announcedLevel && currentWave.isWaveDone()
				&& gameWorld.getNumberOfEnimies() == 0) {
			levelClearedTime = gameWorld.getGameTime();
			announcedLevel = true;
			announcer.postMessage(container, "Level Cleared!");
		}
		enemyWaves.get(currentEnemyWave).update(container, deltaMS);

		timeUntilNextRandomPickup -= deltaMS;
		if (timeUntilNextRandomPickup < 0) {
			Vector2f pos = gameWorld.getDoodadSafePizzaPosition(random);
			gameWorld.addEntity(new PizzaBubble(gameWorld, pos.x, pos.y));

			timeUntilNextRandomPickup = 4000 + random.nextInt(11) * 1000;
		}

		// check warnings
		Player player = gameWorld.getPlayer();
		if (player.getHealth() < player.getMaxHealth() * .25f) {
			warningAnnouncer.updateMessage(container, "Warning: Low Health!");
		}

		announcer.update(container, deltaMS);
		warningAnnouncer.update(container, deltaMS);
	}

	public void renderGUI(GameContainer container, Graphics g) {
		String message = "";
		if (gameWorld.getNumberOfEnimies() == 0
				&& enemyWaves.get(currentEnemyWave).isWaveDone()) {
			if (currentEnemyWave + 1 < enemyWaves.size()) {
				long secondsUntilNextLevel = ((levelClearedTime + levelPause) - gameWorld
						.getGameTime()) / 1000;
				message = String.format("%s starts in %d seconds", enemyWaves
						.get(currentEnemyWave + 1).getName(),
						secondsUntilNextLevel);
			} else {
				message = "You won, I guess...";
			}
		} else {
			// message = enemyWaves.get(currentEnemyWave).getName();
		}
		int width = g.getFont().getWidth(message);
		int height = g.getFont().getHeight(message);
		g.setColor(Color.white);
		g.drawString(message, container.getWidth() / 2 - width / 2,
				container.getHeight() / 6 - height / 2);
		announcer.render(container, g);
		warningAnnouncer.render(container, g);
	}

	private EnemyWave createEnemyWave(String name) {
		EnemyWave wave = new EnemyWave(name, gameWorld);
		enemyWaves.add(wave);
		return wave;
	}

	public boolean isWon() {
		return currentEnemyWave >= (enemyWaves.size() - 1)
				&& gameWorld.getNumberOfEnimies() == 0
				&& enemyWaves.get(currentEnemyWave).isWaveDone();
	}

	public void createEnemyWaves(float mult) {
		// @formatter:off
	createEnemyWave("Level 1")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(1*mult), AlienType.ATTACK_PLAYER, 0))
		.addGroup(new EnemyGroup(5)
			.addEnemy((int)(5*mult), AlienType.ATTACK_PLAYER, 0));

	createEnemyWave("Level 2")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(5*mult), AlienType.ATTACK_PLAYER, 0)
			.addEnemy((int)(5*mult), AlienType.ATTACK_PLAYER, 0))
		.addGroup(new EnemyGroup(15)
			.addEnemy((int)(5*mult), AlienType.ATTACK_PLAYER, 0)
			.addEnemy((int)(5*mult), AlienType.ATTACK_PLAYER, 0));

	createEnemyWave("Level 3")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 0))
		.addGroup(new EnemyGroup(15)
			.addEnemy((int)(8*mult), AlienType.ATTACK_PLAYER, 0)
			.addEnemy((int)(8*mult), AlienType.ATTACK_PLAYER, 0));

	createEnemyWave("Level 4")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(5*mult), AlienType.ATTACK_NUKE, 0));

	createEnemyWave("Level 5")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(5*mult), AlienType.ATTACK_NUKE, 0)
			.addEnemy((int)(5*mult), AlienType.ATTACK_NUKE, 0));

	createEnemyWave("Level 6")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 0));

	createEnemyWave("Level 7")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(5*mult), AlienType.ATTACK_NUKE, 0)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 0));

	createEnemyWave("Level 8 - LARGE INVASION")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 0)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 0)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 0))
		.addGroup(new EnemyGroup(5)
			.addEnemy((int)(8*mult), AlienType.ATTACK_NUKE, 0));

	createEnemyWave("Level 9")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(20*mult), AlienType.ATTACK_PLAYER, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 1)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 1))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(5*mult), AlienType.ATTACK_NUKE, 1)
			.addEnemy((int)(5*mult), AlienType.ATTACK_PLAYER, 1))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(3*mult), AlienType.SHOOTER, 1)
			.addEnemy((int)(7*mult), AlienType.SHOOTER, 1))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(20*mult), AlienType.ATTACK_PLAYER, 1));

	createEnemyWave("Level 10 - BEWARE")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 0)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 0)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 0)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 0));

	createEnemyWave("Level 11")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(20*mult), AlienType.ATTACK_NUKE, 1));

	createEnemyWave("Level 12")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 0))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(3*mult), AlienType.ATTACK_NUKE, 0)
			.addEnemy((int)(3*mult), AlienType.ATTACK_NUKE, 0)
			.addEnemy((int)(3*mult), AlienType.ATTACK_NUKE, 0));

	createEnemyWave("Level 13")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(20*mult), AlienType.ATTACK_PLAYER, 1))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 2)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(5*mult), AlienType.ATTACK_NUKE, 2)
			.addEnemy((int)(5*mult), AlienType.ATTACK_PLAYER, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(3*mult), AlienType.SHOOTER, 2)
			.addEnemy((int)(7*mult), AlienType.SHOOTER, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(20*mult), AlienType.ATTACK_PLAYER, 1));

	createEnemyWave("Level 14")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(12*mult), AlienType.SHOOTER, 1)
			.addEnemy((int)(12*mult), AlienType.SHOOTER, 1));

	createEnemyWave("Level 15 - PATIENCE")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 2))
		.addGroup(new EnemyGroup(25)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 1)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 1)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 1));

	createEnemyWave("Level 16")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(10*mult), AlienType.ATTACK_NUKE, 2)
			.addEnemy((int)(10*mult), AlienType.SHOOTER, 2))
		.addGroup(new EnemyGroup(20)
			.addEnemy((int)(15*mult), AlienType.SHOOTER, 2)
			.addEnemy((int)(10*mult), AlienType.ATTACK_NUKE, 2));

	createEnemyWave("Level 17")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 2)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 2))
		.addGroup(new EnemyGroup(20)
			.addEnemy((int)(5*mult), AlienType.ATTACK_NUKE, 3));

	createEnemyWave("Level 18 - THE ELITE")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_PLAYER, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_PLAYER, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_PLAYER, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(3)
			.addEnemy((int)(1*mult), AlienType.BIGGUS, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(20*mult), AlienType.ATTACK_NUKE, 3));

	createEnemyWave("Level 19")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(4*mult), AlienType.ATTACK_PLAYER, 2)
			.addEnemy((int)(4*mult), AlienType.ATTACK_NUKE, 2)
			.addEnemy((int)(4*mult), AlienType.SHOOTER, 2)
			.addEnemy((int)(4*mult), AlienType.BIGGUS, 1));

	createEnemyWave("Level 20 - NUKES")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(30*mult), AlienType.ATTACK_NUKE, 2));

	createEnemyWave("Level 21")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(8*mult), AlienType.SHOOTER, 2))
		.addGroup(new EnemyGroup(15)
			.addEnemy((int)(3*mult), AlienType.BIGGUS, 3));

	createEnemyWave("Level 22 - THE HORDE")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(30*mult), AlienType.ATTACK_PLAYER, 2))
		.addGroup(new EnemyGroup(30)
			.addEnemy((int)(10*mult), AlienType.ATTACK_NUKE, 3));

	createEnemyWave("Level 23")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(15*mult), AlienType.ATTACK_NUKE, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(20*mult), AlienType.ATTACK_PLAYER, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(20*mult), AlienType.SHOOTER, 2))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 3)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 3)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 3)
			.addEnemy((int)(5*mult), AlienType.SHOOTER, 3));

	createEnemyWave("Level 24 - DEATH")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 2)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 2)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 2)
			.addEnemy((int)(2*mult), AlienType.BIGGUS, 2))
		.addGroup(new EnemyGroup(20)
			.addEnemy((int)(30*mult), AlienType.ATTACK_NUKE, 3));

	createEnemyWave("Level 25 - AFTERLIFE")
		.addGroup(new EnemyGroup(0)
			.addEnemy((int)(10*mult), AlienType.ATTACK_PLAYER, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.SHOOTER, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.ATTACK_NUKE, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(20*mult), AlienType.ATTACK_PLAYER, 3))
		.addGroup(new EnemyGroup(10)
			.addEnemy((int)(10*mult), AlienType.BIGGUS, 3));
	// @formatter:on
	}

	public void createTestWaves() {
		// @formatter:off
	createEnemyWave("TEST Alien5 Difficulty 0")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 0))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 0))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 0));
	createEnemyWave("TEST Alien5 Difficulty 1")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 1))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 1))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 1));
	createEnemyWave("TEST Alien5 Difficulty 2")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 2))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 2))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 2));
	createEnemyWave("TEST Alien5 Difficulty 3")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 3))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 3))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_RED, 3));
	createEnemyWave("TEST Alien6 Difficulty 0")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 0))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 0))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 0));
	createEnemyWave("TEST Alien6 Difficulty 1")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 1))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 1))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 1));
	createEnemyWave("TEST Alien6 Difficulty 2")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 2))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 2))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 2));
	createEnemyWave("TEST Alien6 Difficulty 3")
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 3))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 3))
		.addGroup(new EnemyGroup(0).addEnemy(1, AlienType.EYE_BLUE, 3));
	// @formatter:on
	}
}
