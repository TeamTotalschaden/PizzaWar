/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.world;

import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.entity.AlienType;

/**
 *
 * @author Johan
 */
public class EnemyGroup {
	public List<Object[]> enemyTypeInfo = new ArrayList<Object[]>();
	public int timoutSinceLastWave;

	public EnemyGroup(float timeOutSinceLastGroup) {
		this.timoutSinceLastWave = (int) (timeOutSinceLastGroup * 1000);
	}

	public EnemyGroup addEnemy(int count, AlienType type, int level) {
		enemyTypeInfo.add(new Object[] { count, type, level });
		return this;
	}
}
