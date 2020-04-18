package com.sprites;

/* =================================================================
                       New class added for assessment 3
   ===============================================================*/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.misc.Constants;

import java.util.ArrayList;

/**
 * This sprite can be located around the map and when
 * a player drives over it, the mini game will begin
 */
public class MinigameSprite extends SimpleSprite {

    // using polygon as firetruck is a polygon, which is what it collides with
    private final Polygon hitBox;

    // fire frames to give animation effect
    private final ArrayList<Texture> fireFrames;
    /**
     * Constructor for minigame sprite
     *
     *
     *
     * @param x coordinate where the sprite spawns
     * @param y coordinate where the sprite spawns
     */
    public MinigameSprite(float x, float y, ArrayList<Texture> tex) {
        super(new Texture(Gdx.files.internal("empty.png")));
        this.setBounds(x*Constants.TILE_DIMS, y*Constants.TILE_DIMS, 0.5f * Constants.TILE_DIMS, 0.5f * Constants.TILE_DIMS);
        this.hitBox = new Polygon(new float[]{0,0,this.getWidth(),0,this.getWidth(),this.getHeight(),0,this.getHeight()});
        this.hitBox.setPosition(this.getX(), this.getY());
        this.fireFrames = tex;
    }

    /**
     * Draw the sprite
     *
     * @param batch to be drawn to
     */
    public void update(Batch batch) {
        super.update(batch);
        batch.draw(this.fireFrames.get(Math.round(this.getInternalTime() / 10) % 3), super.getX(), super.getY(), super.getWidth() * 0.5f, super.getHeight() * 0.5f);
    }


    public Polygon getHitBox() {
        return this.hitBox;
    }
}
