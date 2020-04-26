package com.misc;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.screens.GameScreen;

/**
 * A class to produce a popup allowing the player to enter
 * a string for the save name.
 *
 * @author Archie
 * @since 24/04/2020
 */
public class StringInputListener implements TextInputListener {

    private TextButton button;
    private int saveNumber;
    private GameScreen gameScreen;

    /**
     * The contructor of the input listener, gathers information that it
     * can be called when the player has finished entering a string
     * @param gameScreen    The current gamescreen containing the save controls
     * @param button        The button's text with the player's input
     * @param saveNumber    The save number that is being modified
     */
    public StringInputListener(GameScreen gameScreen, TextButton button, int saveNumber) {
        this.button = button;
        this.saveNumber = saveNumber;
        this.gameScreen = gameScreen;
    }

    /**
     * Called when the player finishes editing
     * @param text  The string the player wrote
     */
    @Override
    public void input(String text) {
        button.setText(text);
        gameScreen.getSaveControls().setSaveName(saveNumber, text);
    }

    /**
     * Called if the player cancel's editing
     */
    @Override
    public void canceled() {
        System.out.println("Canceled");
    }
}