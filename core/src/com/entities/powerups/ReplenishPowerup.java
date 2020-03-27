package com.entities.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.sprites.PowerupSprite;
import com.entities.Firetruck;

import java.util.ArrayList;

public class ReplenishPowerup extends PowerupSprite {

    private int activeTime = 600; //10 seconds

    public ReplenishPowerup(ArrayList<Texture> textureSlices, float x, float y) {
        super(textureSlices, x, y);
    }

    public void action(Firetruck activeFireTruck){
        activeFireTruck.setPowerup(activeTime, "replenish");
    }
}
