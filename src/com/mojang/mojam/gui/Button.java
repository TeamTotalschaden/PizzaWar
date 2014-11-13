package com.mojang.mojam.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Button extends Image {
    private int xPos = 0;
    private int yPos = 0;
    private String buttonText;

    private Color textColor = new Color(240, 200, 50);
    private Color buttonColor = new Color(175, 155, 180);
    private Image button = new Image("GUI/button.png");
    //private UnicodeFont font = new UnicodeFont("fonts/Franchise-Bold.ttf", 48, false, false);
    private UnicodeFont font = new UnicodeFont("fonts/MeltdownMF.ttf", 32, false, false);

    public Button(Image button, UnicodeFont font, String buttonText,
	    Color textColor, Color buttonColor, int xPos, int yPos)
	    throws SlickException {
	this.setPos(xPos, yPos);
	this.button = button;
	this.buttonText = buttonText;
	this.textColor = textColor;
	this.buttonColor = buttonColor;
	this.font = font;
	this.initFont();
    }
    
    public Button(String buttonText)
	    throws SlickException {
	this.buttonText = buttonText;
	this.initFont();
    }

    public Button(String buttonText, Color buttonColor)
	    throws SlickException {
	this.setPos(xPos, yPos);
	this.buttonText = buttonText;
	this.buttonColor = buttonColor;
	this.initFont();
    }

    @SuppressWarnings("unchecked")
    public void initFont() throws SlickException {
	this.font.addAsciiGlyphs();
	this.font.getEffects().add(new ColorEffect());
	this.font.loadGlyphs();
    }

    public void render(GameContainer gc, Graphics g) {
	button.draw(xPos, yPos, buttonColor);
	Color temp = g.getColor();
	g.setColor(textColor);
	g.setFont(font);
	g.drawString(buttonText,
		xPos + button.getWidth() / 2 - font.getWidth(buttonText) / 2,
		yPos + button.getHeight() / 2 - font.getHeight(buttonText) / 2);
	g.resetFont();
	g.setColor(temp);
    }

    public boolean isClicked(int mouseX, int mouseY) {
	if (mouseX >= xPos && mouseX <= xPos + button.getWidth()) {
	    if (mouseY >= yPos && mouseY <= yPos + button.getHeight()) {
		return true;
	    }
	}
	return false;
    }

    public void setPos(int xPos, int yPos) {
	this.xPos = xPos;
	this.yPos = yPos;
    }

    public void setTextColor(Color textColor) {
	this.textColor = textColor;
    }

    public void setButtonColor(Color buttonColor) {
	this.buttonColor = buttonColor;
    }

    public int getHeight() {
	return button.getHeight();
    }

    public int getWidth() {
	return button.getWidth();
    }
}
