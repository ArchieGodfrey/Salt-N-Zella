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
 * a player drives over it, the mini game will begin
 */
public class PowerupSprite extends Sprite {

    // using polygon as firetruck is a polygon, which is what it collides with
    private final Polygon hitBox;

    // texture slices to give 3D effect
    private final ArrayList<Texture> powerupSlices;

    /**
     * Constructor for minigame sprite
     *
     *
     *
     * @param x coordinate where the sprite spawns
     * @param y coordinate where the sprite spawns
     */
    public PowerupSprite(ArrayList<Texture> textureSlices, float x, float y) {
        super(new Texture(Gdx.files.internal("powerup.png")));
        this.setBounds(x*Constants.TILE_DIMS, y*Constants.TILE_DIMS, 1.5f * 4, 1.5f * Constants.TILE_DIMS);
        this.hitBox = new Polygon(new float[]{0,0,this.getWidth(),0,this.getWidth(),this.getHeight(),0,this.getHeight()});
        this.hitBox.setPosition(this.getX(), this.getY());

        this.powerupSlices = textureSlices;
    }

    /**
     * Draw the sprite
     *
     * @param batch to be drawn to
     */
    public void update(Batch batch) {
        //batch.draw(super.getTexture(), super.getX(), super.getY(), super.getWidth(), super.getHeight());

        rotate(1);
        drawVoxelImage(batch);
    }

    /**
     * Draws the voxel representation of the firetruck. Incrementally builds the firetruck
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

    public void action(Firetruck activeFireTruck){}
}
