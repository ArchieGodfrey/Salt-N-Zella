package com.misc;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

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
    private SaveControls saveControls;

    /**
     * The contructor of the input listener, gathers information that it
     * can be called when the player has finished entering a string
     * @param saveControls  The save controls needed to change the save file name
     * @param button        The button's text with the player's input
     * @param saveNumber    The save number that is being modified
     */
    public StringInputListener(SaveControls saveControls, TextButton button, int saveNumber) {
        this.button = button;
        this.saveNumber = saveNumber;
        this.saveControls = saveControls;
    }

    /**
     * Called when the player finishes editing
     * @param text  The string the player wrote
     */
    @Override
    public void input(String text) {
        if (text.length() > 0) {
            button.setText(text);
            saveControls.setSaveName(saveNumber, text);
        }
    }

    /**
     * Called if the player cancel's editing
     */
    @Override
    public void canceled() {
        System.out.println("Canceled");
    }
}