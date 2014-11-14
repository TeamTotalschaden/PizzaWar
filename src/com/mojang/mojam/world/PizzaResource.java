package com.mojang.mojam.world;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class PizzaResource {

    public static final int TYPE_FETA = 0;
    public static final int TYPE_BASIL = 1;
    public static final int TYPE_PEPPERONI = 2;
    public static final int TYPE_OIL_FLASK_FULL = 3;
    public static final int TYPE_OIL_FLASK_HALF = 4;
   
    public static final int TYPE_COIN_SILVER = 5;
    public static final int TYPE_COIN_GOLD = 6;

    
    public static final int NUM_RESOURCES = 7;
    public static final int NUM_BUY_RESOURCES = 3;

    public static final String[] iconNames = {
            "pickups/feta.png", 
            "pickups/basil.png", 
            "pickups/pepperoni.png", 
            "pickups/oil_flask_full.png", 
            "pickups/oil_flask_half.png", 
            "pickups/coin_silver_16.png", 
            "pickups/coin_gold_16.png",
    };
    public static final Animation[] icons = new Animation[NUM_RESOURCES];

    public static void init() throws SlickException {
	for (int i = 0; i < NUM_RESOURCES; i++) {
	    if (i == PizzaResource.TYPE_COIN_SILVER || i == PizzaResource.TYPE_COIN_GOLD) {
		icons[i] = new Animation(new SpriteSheet(iconNames[i], 16, 16), 200);
	    } else {
		icons[i] = new Animation(new SpriteSheet(iconNames[i], 32, 32), 200);
	    }
        }
    }
}
