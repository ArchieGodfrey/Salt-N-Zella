package com.sprites;

/* =================================================================
                       New class added for assessment 4
   ===============================================================*/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.entities.Firetruck;
import com.misc.Constants;

import java.util.ArrayList;

/**
 * This sprite can be located around the map and when
 * a player drives over it, the power up will be applied
 */
public class PowerupSprite extends Sprite {

    // using polygon as powerup is a polygon, which is what it collides with
    private final Polygon hitBox;

    // texture slices to give 3D effect
    private final ArrayList<Texture> powerupSlices;

    // Effect duration
    private int activeTime;
    
    // Effect type
    private String type;

    /**
     * Constructor for powerup sprite
     * @param type the type of powerup that will be spawned
     * @param textureSlices the built array of textures that will be used to draw the 3d sprite
     * @param x coordinate where the sprite spawns
     * @param y coordinate where the sprite spawns
     */
    public PowerupSprite(String type, ArrayList<Texture> textureSlices, float x, float y,int difficulty) {
        super(new Texture(Gdx.files.internal("powerup.png")));
        this.setBounds(x*Constants.TILE_DIMS, y*Constants.TILE_DIMS, 1.5f * 4, 1.5f * Constants.TILE_DIMS);
        this.hitBox = new Polygon(new float[]{0,0,this.getWidth(),0,this.getWidth(),this.getHeight(),0,this.getHeight()});
        this.hitBox.setPosition(this.getX(), this.getY());
        this.type = type;
        this.powerupSlices = textureSlices;
        this.activeTime = 1200 / difficulty;
    }

    /**
     * Draw the sprite
     *
     * @param batch to be drawn to
     */
    public void update(Batch batch) {

        //Rotate the sprite by factor
        int rotateSpd = 1;
        rotate(rotateSpd);

        drawVoxelImage(batch);
    }

    /**
     * Applies a powerup effect to the active truck
     * @param activeFireTruck   The active truck to apply the effect to
     */
    public void action(Firetruck activeFireTruck){
        if (type == "random") {
            int random = (int)(Math.random() * 4.4);
            switch(random){
                case 0:
                    activeFireTruck.setPowerup(activeTime, "ghost");
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
        } else {
            activeFireTruck.setPowerup(activeTime, type);
        }
    }

    /**
     * Draws the voxel representation of the powerup. Incrementally builds the powerup
     * from layers of images with each image slightly higher than the last
     * As the sprite rotates, the y will move up and down to make it more noticable as a pickup item
     */
    private void drawVoxelImage(Batch batch) {
        // Length of array containing image slices
        int slicesLength = this.powerupSlices.size() - 1;
        float x = getX(), angle = this.getRotation();
        float y = getY() + (float)(Math.sin(Math.toRadians(angle*3))) * 10;

        float width = this.getWidth(), height = this.getHeight();
        for (int i = 0; i < slicesLength; i++) {
            Texture texture = this.powerupSlices.get(i);
            batch.draw(new TextureRegion(texture), x, (y - slicesLength / 3f) + i, width / 2, height / 2, width, height, 1, 1, angle, true);
        }
    }

    public Polygon getHitBox() {
        return this.hitBox;
    }

}
