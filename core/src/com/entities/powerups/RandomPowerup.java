package com.entities.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.sprites.PowerupSprite;
import com.entities.Firetruck;
import java.util.ArrayList;

public class RandomPowerup extends PowerupSprite {

    private int activeTime = 600; //10 seconds

    public RandomPowerup(ArrayList<Texture> textureSlices, float x, float y) {
        super(textureSlices, x, y);
    }

    public void action(Firetruck activeFireTruck){
        int random = (int)(Math.random() * 5);

        switch(random){
            case 0:
                activeFireTruck.setPowerup(activeTime, "invisible");
            break;
            case 1:
                activeFireTruck.setPowerup(activeTime, "replenish");
                break;
            case 2:
                activeFireTruck.setPowerup(activeTime, "speedUp");
                break;
            case 3:
                activeFireTruck.setPowerup(activeTime, "immunity");
                break;
            case 4:
                activeFireTruck.setPowerup(activeTime, "damageUp");
                break;
        }
    }
}
