package com.mojang.mojam.data;

import java.io.Serializable;

public class ScoreEntry implements Serializable {
	private static final long serialVersionUID = -5671139032424618246L;

	public String name;
	public long score;

	public ScoreEntry(String name, long score) {
		super();
		this.name = name;
		this.score = score;
	}
}
