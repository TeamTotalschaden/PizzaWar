package com.mojang.mojam.entity;

public enum AlienType {
	NULL(-1), ATTACK_PLAYER(0), ATTACK_NUKE(1), SHOOTER(2), BIGGUS(3), EYE_RED(
			4), EYE_BLUE(5);
	private final int id;

	AlienType(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}
}