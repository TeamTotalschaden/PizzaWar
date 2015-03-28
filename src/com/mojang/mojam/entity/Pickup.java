package com.mojang.mojam.entity;

import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import com.mojang.mojam.Camera;
import com.mojang.mojam.sound.Sounds;
import com.mojang.mojam.world.PizzaResource;
import com.mojang.mojam.world.PizzaWorld;

public class Pickup extends Entity {

	private Animation anim;
	private int life;
	private final int resourceType;

	public Pickup(PizzaWorld world, float x, float z, int type) {
		super(world, x, z);
		this.resourceType = type;
		try {
			if (type == PizzaResource.TYPE_COIN_SILVER
					|| type == PizzaResource.TYPE_COIN_GOLD) {
				anim = new Animation(new SpriteSheet(
						PizzaResource.iconNames[type], 16, 16), 200);
			} else {
				anim = new Animation(new SpriteSheet(
						PizzaResource.iconNames[type], 32, 32), 200);
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
		this.life = 30000;
	}

	@Override
	public boolean update(GameContainer slickContainer, int deltaMS) {

		Player player = world.getPlayer();
		float dx = player.x - x;
		float dz = player.z - z;
		if (dx * dx + dz * dz < 128.0f * 128.0f) {
			float dist = (float) Math.sqrt(dx * dx + dz * dz);
			dx /= dist;
			dz /= dist;
			velocity.x += dx * deltaMS * .001f * 120;
			velocity.z += dz * deltaMS * .001f * 120;
		}
		velocity.x -= velocity.x * deltaMS * .001f * 2;
		velocity.z -= velocity.z * deltaMS * .001f * 2;
		x += velocity.x * deltaMS * .001f;
		z += velocity.z * deltaMS * .001f;

		y = 5 + (float) Math.cos(life * .002) * 5.f;
		life -= deltaMS;
		return life > 0;
	}

	@Override
	public void render(GameContainer slickContainer, Graphics g, Camera camera) {
		if (resourceType == PizzaResource.TYPE_COIN_SILVER
				|| resourceType == PizzaResource.TYPE_COIN_GOLD) {
			anim.draw(x - camera.getX() - 16, z - camera.getY() - 32 - y);
		}

		Image current = anim.getCurrentFrame();
		if (life < 5000) {
			float alpha = life / 5000.0f;
			current.setColor(0, 1, 1, 1, alpha);
			current.setColor(1, 1, 1, 1, alpha);
			current.setColor(2, 1, 1, 1, alpha);
			current.setColor(3, 1, 1, 1, alpha);
		} else {
			current.setColor(0, 1, 1, 1, 1);
			current.setColor(1, 1, 1, 1, 1);
			current.setColor(2, 1, 1, 1, 1);
			current.setColor(3, 1, 1, 1, 1);
		}
		current.draw(x - camera.getX() - 16, z - camera.getY() - 32 - y);
	}

	@Override
	public void renderGroundLayer(GameContainer slickContainer, Graphics g,
			Camera camera) {
		if (y < 0) {
			return;
		}

		float shadowScale = (float) Math.pow(1 - Math.min(y, 400) / 400, 2);
		int offset = 0;
		if (resourceType == PizzaResource.TYPE_COIN_SILVER
				|| resourceType == PizzaResource.TYPE_COIN_GOLD) {
			shadowScale /= 2;
			offset = 8;
		}
		int halfShadowX = (int) (((float) shadowImage.getWidth() / 2.0f) * shadowScale);
		int halfShadowY = (int) (((float) shadowImage.getHeight() / 2.0f) * shadowScale);

		g.drawImage(shadowImage, x - camera.getX() - halfShadowX - offset, z
				- camera.getY() - halfShadowY - offset, x - camera.getX()
				+ halfShadowX - offset, z - camera.getY() + halfShadowY
				- offset, 0, 0, 32, 32, shadowColorMult);
	}

	@Override
	public boolean collidesWith(Entity other) {
		return other instanceof Player;
	}

	@Override
	protected void onCollide(Entity entity) {
		String[] coinSound = { Sounds.COIN_1, Sounds.COIN_1, Sounds.COIN_2,
				Sounds.COIN_3 };
		if (entity instanceof Player) {
			Player player = (Player) entity;
			switch (resourceType) {
			case PizzaResource.TYPE_FETA:
			case PizzaResource.TYPE_BASIL:
			case PizzaResource.TYPE_PEPPERONI_GREEN:
				Sounds.getInstance().playSound(Sounds.PICKUP_HEALTH, x, y, z);
				player.addResource(resourceType);
				break;
			case PizzaResource.TYPE_PEPPERONI_RED:
				player.heal(player.getMaxHealth() * 0.4f);
				break;
			case PizzaResource.TYPE_OIL_FLASK_FULL:
				Sounds.getInstance().playSound(Sounds.PICKUP_HEALTH, x, y, z);
				player.setBeamTime(player.getMaxBeamTime());
				break;
			case PizzaResource.TYPE_OIL_FLASK_HALF:
				Sounds.getInstance().playSound(Sounds.PICKUP_HEALTH, x, y, z);
				player.addBeamTime(player.getMaxBeamTime() / 2);
				break;
			case PizzaResource.TYPE_COIN_SILVER:
				Sounds.getInstance().playSound(coinSound[random.nextInt(4)], x,
						y, z);
				player.addCash(10);
				break;
			case PizzaResource.TYPE_COIN_GOLD:
				Sounds.getInstance().playSound(coinSound[random.nextInt(4)], x,
						y, z);
				player.addCash(50);
				break;
			default:
			}
			setRemoved();
		}
	}

	public static int resourceType(Random random) {
		int selectRoll = random.nextInt(14);
		if (selectRoll <= 2) {
			return PizzaResource.TYPE_PEPPERONI_GREEN; // 21,5%
		} else if (selectRoll <= 6) {
			return PizzaResource.TYPE_BASIL; // 28,5%
		}
		return PizzaResource.TYPE_FETA; // 50%
	}

	public static int oilFlaskDrop(Random random) {
		int selectRoll = random.nextInt(100);
		if (selectRoll < 5) {
			return PizzaResource.TYPE_OIL_FLASK_FULL; // 5 %
		} else if (selectRoll < 15) {
			return PizzaResource.TYPE_OIL_FLASK_HALF; // 10 %
		}
		return -1;
	}

	/**
	 * @author Johannes
	 * @return Int ID of the dropped Entity
	 * */
	public static int randomType(Random random) {
		int drop = oilFlaskDrop(random);
		if (drop == -1) {
			drop = resourceType(random);
		}
		return drop;
	}

	public static int pepperoniGreenDrop() {
		return PizzaResource.TYPE_PEPPERONI_GREEN;
	}

	public static int pepperoniRedDrop() {
		return PizzaResource.TYPE_PEPPERONI_RED;
	}

	public static int basilDrop() {
		return PizzaResource.TYPE_BASIL;
	}

	public static int fetaDrop() {
		return PizzaResource.TYPE_FETA;
	}

	public static int oilFlaskFullDrop() {
		return PizzaResource.TYPE_OIL_FLASK_FULL;
	}

	public static int oilFlaskHalfDrop() {
		return PizzaResource.TYPE_OIL_FLASK_HALF;
	}

	public static int coinSilverDrop() {
		return PizzaResource.TYPE_COIN_SILVER;
	}

	public static int coinGoldDrop() {
		return PizzaResource.TYPE_COIN_GOLD;
	}

	public static int coinDrop(Entity entity, Random random) {
		int selectRoll = random.nextInt(100);
		if (entity instanceof Alien) {
			switch (((Alien) entity).getAlienType()) {
			case ATTACK_PLAYER:
				if (selectRoll < 30) {
					return PizzaResource.TYPE_COIN_SILVER;
				} else if (selectRoll < 35) {
					return PizzaResource.TYPE_COIN_GOLD;
				}
				break;
			case ATTACK_NUKE:
				return -1;
			case SHOOTER:
				if (selectRoll < 50) {
					return PizzaResource.TYPE_COIN_SILVER;
				} else if (selectRoll < 65) {
					return PizzaResource.TYPE_COIN_GOLD;
				}
				break;
			case BIGGUS:
				if (selectRoll < 10) {
					return PizzaResource.TYPE_COIN_SILVER;
				} else if (selectRoll < 50) {
					return PizzaResource.TYPE_COIN_GOLD;
				}
				break;
			case EYE_RED:
				if (selectRoll < 60) {
					return PizzaResource.TYPE_COIN_SILVER;
				} else if (selectRoll < 70) {
					return PizzaResource.TYPE_COIN_GOLD;
				}
				break;
			case EYE_BLUE:
				if (selectRoll < 50) {
					return PizzaResource.TYPE_COIN_SILVER;
				} else if (selectRoll < 60) {
					return PizzaResource.TYPE_COIN_GOLD;
				}
				break;
			default:
				return -1;
			}
		}
		return -1;
	}

}