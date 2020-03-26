package com.entities.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.sprites.PowerupSprite;
import com.entities.Firetruck;

import java.util.ArrayList;

public class GhostPowerup extends PowerupSprite {

    private int activeTime = 600; //10 seconds

    public GhostPowerup(ArrayList<Texture> textureSlices, float x, float y) {
        super(textureSlices, x, y);
    }

    public void action(Firetruck activeFireTruck){

        System.out.print("Powerup!");

        activeFireTruck.setPowerup(activeTime, "invisible");
    }
}
