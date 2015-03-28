package com.mojang.mojam.entity;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import com.mojang.mojam.Camera;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.PizzaWorld;

public class PizzaBubble extends HealthEntity {

	private static final Color bubbleHurtColor = new Color(1, .7f, .5f, 1.0f);
	private Animation bubbleAnimation;

	public PizzaBubble(PizzaWorld world, float x, float z) {
		super(world, x, z);

		try {
			bubbleAnimation = new Animation(new SpriteSheet(
					"actors/bubble.png", 64, 64), 200);
			bubbleAnimation.setLooping(false);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFixedPosition() {
		return true;
	}

	@Override
	public boolean update(GameContainer slickContainer, int deltaMS) {
		bubbleAnimation.update(deltaMS);
		if (!super.update(slickContainer, deltaMS)) {
			// create a resource here
			if (random.nextInt(100) <= 5) {
				// drop Health
				world.addEntity(new Pickup(world, x, z, Pickup
						.pepperoniRedDrop()));
			} else {
				// drop something else
				world.addEntity(new Pickup(world, x, z, Pickup
						.randomType(random)));
			}
			Sounds.getInstance().playSound(Sounds.PIZZA_PLOP, x, 0, z);
			world.addParticle(new AnimationParticle(world, x, 0, z,
					AnimationParticle.pizzaPop));
			return false;
		}
		return true;
	}

	@Override
	public void render(GameContainer slickContainer, Graphics g, Camera camera) {
		Image alienImage = bubbleAnimation.getCurrentFrame();
		g.drawImage(alienImage, x - camera.getX() - 31, z - camera.getY() - 45,
				getHurtColor());
		super.render(slickContainer, g, camera);
	}

	@Override
	public void renderGroundLayer(GameContainer slickContainer, Graphics g,
			Camera camera) {
		// do nothing, no shadow
	}

	@Override
	public float getMaxHealth() {
		return 125.0f;
	}

	@Override
	public boolean collidesWith(Entity other) {
		return other instanceof Projectile || other instanceof Alien
				|| other instanceof Player;
	}

	@Override
	public float getCollisionRadius() {
		return 32;
	}

	@Override
	protected Color getHurtColor() {
		if (isRecentlyHurt()) {
			return bubbleHurtColor;
		}
		return Color.white;
	}

}
