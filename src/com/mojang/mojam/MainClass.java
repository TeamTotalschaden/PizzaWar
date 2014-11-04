package com.mojang.mojam;

import org.newdawn.slick.AppGameContainer;

public class MainClass {
	
    public static final String Version = "Version 14";

    public static void main(String[] args) throws Exception {
        System.out.println("I'm a little teapot. That is all.");
        AppGameContainer app = new AppGameContainer(new GameStateController());
        for (String s: args){
        	System.out.println("Arg: " + s);
        }
        app.setDisplayMode(app.getScreenWidth(), app.getScreenHeight(), true);
        app.start();
    }
}
