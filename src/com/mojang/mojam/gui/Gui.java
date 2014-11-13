package com.mojang.mojam.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.mojang.mojam.world.PizzaResource;

public class Gui {

	private static Image fortificationImage;

	public static void init() throws SlickException {
		fortificationImage = new Image("GUI/fortification_points.png");
	}

	public static void renderCenterString(Graphics g, String message, int cx,
			int cy) {
		int width = g.getFont().getWidth(message);
		g.drawString(message, cx - width / 2, cy);
	}

	public static void renderResourceList(Graphics g, int[] resources, float x,
			float y, String title, int maxWidth) {

		int titleWidth = g.getFont().getWidth(title);
		if (titleWidth > 0) {
			g.setColor(Color.white);
			g.drawString(title, x, y);
			titleWidth += 10;
		}

		if (resources == null) {
			return;
		}

		float rx = x + titleWidth - 16;
		float ry = y - 5;
		int totalWidth = 0;
		for (int r = 0; r < PizzaResource.NUM_BUY_RESOURCES; r++) {
			int count = resources[r];
			int spacing = 14 - Math.min(12, (count / 5));
			totalWidth += spacing * count;
			if (count > 0 && r < PizzaResource.NUM_BUY_RESOURCES - 1) {
				totalWidth += 14;
			}
		}

		boolean smallFit = (rx + totalWidth - x >= maxWidth);
		for (int r = 0; r < PizzaResource.NUM_BUY_RESOURCES; r++) {
			int count = resources[r];
			if (smallFit) {
				if (count > 0) {
					g.drawImage(PizzaResource.icons[r].getCurrentFrame(), rx,
							ry);
					rx += 28;
					String msg = " " + count;
					g.drawString(msg, rx, ry + 7);
					rx += 10 + g.getFont().getWidth(msg);
				}
			} else {
				int spacing = 14 - Math.min(12, (count / 5));
				{
					for (int i = 0; i < resources[r]; i++) {
						g.drawImage(PizzaResource.icons[r].getCurrentFrame(),
								rx, ry);
						rx += spacing;
					}
				}
				if (resources[r] > 0) {
					rx += 14;
				}
			}
		}
	}

	public static void renderFortificationList(Graphics g, int fortification,
			float x, float y, String title, int maxWidth) {
		int titleWidth = g.getFont().getWidth(title);
		if (titleWidth > 0) {
			g.setColor(Color.white);
			g.drawString(title, x, y);
			titleWidth += 10;
		}

		if (fortification == 0) {
			return;
		}

		float rx = x + titleWidth - 16;
		float ry = y - 5;
		int totalWidth = 0;
		int count = fortification;
		int spacing = 14 - Math.min(12, (count / 10));
		totalWidth += spacing * count;

		boolean smallFit = (rx + totalWidth - x >= maxWidth);
		if (smallFit) {
			if (count > 0) {
				g.drawImage(fortificationImage, rx, ry);
				rx += 28;
				String msg = " " + count;
				g.drawString(msg, rx, ry + 7);
				rx += 10 + g.getFont().getWidth(msg);
			}
		} else {
			for (int i = 0; i < fortification; i++) {
				g.drawImage(fortificationImage, rx, ry);
				rx += spacing;
			}
			if (fortification > 0) {
				rx += 14;
			}
		}
	}
}
