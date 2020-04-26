package com.entities;

// LibGDX imports

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

// Custom class import
import com.misc.Constants;
import com.misc.Arrow;
import com.misc.ResourceBar;
import com.misc.SaveControls.SavedFiretruck;
import com.screens.GameScreen;
import com.sprites.MovementSprite;

// Java util import
import java.util.ArrayList;

import static com.misc.Constants.*;

/**
 * The Firetruck implementation. A sprite capable of moving and colliding with other sprites.
 *
 * @author Archie
 * @since 16/12/2019
 */
public class Firetruck extends MovementSprite {

    // hose values
    private Boolean isSpraying;
    private int toggleDelay;
    private float hoseWidth, hoseHeight;
    private Polygon hoseRange;
    private ResourceBar waterBar;

    private final TruckType type;

    // texture slices to give 3D effect
    private final ArrayList<Texture> firetruckSlices;

    // water frames to give animation effect
    private final ArrayList<Texture> waterFrames;

    // arrow values
    private ETFortress nearestFortress;
    private final Arrow arrow;
    private boolean isArrowVisible;

    // car park values
    private final TiledMapTileLayer carparkLayer;
    private CarparkEntrances location;
    private boolean isBought;

    private boolean isAlive;

    private final Firestation fireStation;

    //Powerup values
    private int[] powerupTimer;
    private String powerupType;

    private boolean powerupActive;
    public boolean isInvisible;
    public boolean isImmune;


    // The amount of damage the firetruck can do
    private float damage;

    private int difficulty;


    /**
     * Creates a firetruck capable of moving and colliding with the tiledMap and other sprites.
     * It also requires an ID so that it can be focused with the camera. Drawn with the given
     * texture at the given position.
     *
     * @param textureSlices  The array of textures used to draw the firetruck with.
     * @param frames         The texture used to draw the water with.
     * @param type           The properties of the truck inherited from Constants.
     * @param collisionLayer The layer of the map the firetruck collides with.
     * @param carparkLayer   The layer of the map the carparks are on
     * @param fireStation    The fire station
     * @param isBought       <code>true</code> if truck is bought to start with
     *                       <code>false</code> if truck needs to still be bought
     * @param gameScreen     GameScreen to get difficulty and save controls
     */
    public Firetruck(ArrayList<Texture> textureSlices, ArrayList<Texture> frames, TruckType type, TiledMapTileLayer collisionLayer, TiledMapTileLayer carparkLayer, Firestation fireStation, boolean isBought, GameScreen gameScreen) {
        super(textureSlices.get(textureSlices.size() - 1), collisionLayer);
        this.waterFrames = frames;
        this.firetruckSlices = textureSlices;
        this.type = type;
        this.location = CarparkEntrances.Main1;
        this.setPosition(CarparkEntrances.Main1.getLocation().x, CarparkEntrances.Main1.getLocation().y);
        this.fireStation = fireStation;
        this.difficulty = gameScreen.getDifficulty();
        this.create(difficulty);
        this.arrow = new Arrow(15, 50, 100, 50);
        this.isArrowVisible = false;
        this.carparkLayer = carparkLayer;
        this.isBought = isBought;

        this.powerupTimer = new int[6];

        this.powerupActive = false;

        this.damage = this.getType().getProperties()[7];

        // If loading from a save file
        if (gameScreen.getSaveControls().getCurrentSaveNumber() != 0) {
            SavedFiretruck savedFiretruck = gameScreen.getSaveControls().getSavedFiretruck(type);
            this.getHealthBar().setCurrentAmount(savedFiretruck.health);
            this.waterBar.setCurrentAmount(savedFiretruck.water);
            this.isBought = savedFiretruck.isBought;
            this.location = savedFiretruck.respawnLocation;
            this.respawn();
            this.setPosition(savedFiretruck.x, savedFiretruck.y);
        }

    }

    /**
     * Sets the health of the firetruck and its size provided in CONSTANTS.
     * Also initialises any properties needed by the firetruck.
     */
    private void create(int difficulty) {
        super.setMovementHitBox(-90);
        this.isSpraying = true;
        this.setSize(FIRETRUCK_WIDTH, FIRETRUCK_HEIGHT);
        this.getHealthBar().setMaxResource((int) this.getType().getProperties()[0] / difficulty);
        this.setAccelerationRate(this.getType().getProperties()[1]);
        this.setDecelerationRate(this.getType().getProperties()[1] * 0.6f);
        this.setMaxSpeed(this.getType().getProperties()[2]);
        this.createWaterHose();
        this.isAlive = true;

        // Start the firetruck facing left
        this.rotate(-90);
        this.resetSprite();
    }

    /**
     * Update the position and direction of the firetruck every frame.
     *
     * @param batch  The batch to draw onto.
     * @param camera Used to get the centre of the screen.
     */
    public void update(Batch batch, Camera camera) {
        super.update(batch);
        checkCarparkCollision();

        // Look for key press input, then accelerate the firetruck in that direction
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            super.applyAcceleration(Direction.LEFT);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            super.applyAcceleration(Direction.RIGHT);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
            super.applyAcceleration(Direction.DOWN);
        }
        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
            super.applyAcceleration(Direction.UP);
        }

        // Deplete water if spraying, toggle off when depleted
        if (this.isSpraying && this.waterBar.getCurrentAmount() > 0) {
            this.waterBar.subtractResourceAmount(1);
        } else if (this.isSpraying) {
            this.toggleHose();
        }

        // Update the water bar position
        this.waterBar.setPosition(this.getX(), this.getCentreY());
        this.waterBar.update(batch);

        // Get the mouse input and get the angle from the truck to it. Get vector, normalise then get angle
        Vector2 hoseVector = new Vector2((this.getCentreX() - (camera.viewportWidth / 2) + Gdx.input.getX()), (this.getCentreY() + (camera.viewportHeight / 2) - Gdx.input.getY()));
        Vector2 centreVector = new Vector2(this.getCentreX(), this.getCentreY());

        // Work out the vector between them
        hoseVector = hoseVector.sub(centreVector);
        hoseVector.nor();

        // Update the hose size and position. Angle it towards the mouse
        float scale = this.isSpraying && this.hoseRange.getScaleX() < this.getType().getProperties()[4] ?
                0.05f : !this.isSpraying && this.hoseRange.getScaleX() > 0 ? -0.05f : 0;
        this.hoseRange.setScale(this.hoseRange.getScaleX() + scale, this.hoseRange.getScaleY() + scale);
        this.hoseRange.setPosition(this.getCentreX(), this.getCentreY());
        this.hoseRange.setRotation(hoseVector.angle());

        // Change batch alpha to match bar to fade hose in and out
        batch.setColor(1.0f, 1.0f, 1.0f, this.waterBar.getFade() * 0.9f);
        batch.draw(new TextureRegion(this.waterFrames.get(Math.round(this.getInternalTime() / 10) % 3)), this.hoseRange.getX(), this.hoseRange.getY() - this.hoseHeight / 2,
                0, this.hoseHeight / 2, this.hoseWidth, this.hoseHeight, this.hoseRange.getScaleX(), this.hoseRange.getScaleY(), hoseVector.angle(), true);

        // Return the batch to its original colours
        batch.setColor(1.0f, 1.0f, 1.0f, 1f);

        // Decrease timeout, used for keeping track of time between toggle presses
        if (this.toggleDelay > 0) this.toggleDelay -= 1;

        /*
         *  =======================================================================
         *                          Added for Assessment 4
         *  =======================================================================
         */
        //Run powerup methods for set amount of time:
        if(powerupActive){
            //Countdown all timers
            this.powerupTimer[0]--;
            this.powerupTimer[1]--;
            this.powerupTimer[2]--;
            this.powerupTimer[3]--;
            this.powerupTimer[4]--;
            this.powerupTimer[5]--;

            //Allow multiple powerups to be active at a time
            if(this.powerupTimer[0]>0){this.ghost(batch);}
            if(this.powerupTimer[1]>0){this.immunity();}
            if(this.powerupTimer[2]>0){this.replenish();}
            if(this.powerupTimer[3]>0){this.speedUp();}
            if(this.powerupTimer[4]>0){this.damageUp();}
            if(this.powerupTimer[5]>0){this.infiniteWater();}

            //If all powerups have ended then the powerups are stopped from running
            if(this.powerupTimer[0]<0 && this.powerupTimer[1]<0 && this.powerupTimer[2]<0 && this.powerupTimer[3]<0 && this.powerupTimer[4]<0  && this.powerupTimer[5]<0){
                this.powerupActive = false;
            }
        }
        //Reset default values that may have changed by power up effects. Ensures firetruck properties aren't
        //permanently changed
        //Speed up
        if(this.powerupTimer[3]<0) {
            this.setAccelerationRate(this.getType().getProperties()[1]);
        }
        //Invisible
        if(this.powerupTimer[0]<0) {
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1.0f);
            this.isInvisible = false;
        }
        //Immunity
        if(this.powerupTimer[1]<0) {
            this.isImmune = false;
        }
        //DamageUp
        if(this.powerupTimer[4]<0) {
            this.damage = this.getType().getProperties()[7] / difficulty;
        }

        //Draw voxel image at the end so that invisible powerup takes effect
        drawVoxelImage(batch);
    }

    /**
     * Checks if the firetruck enters a car park, set the respawn location
     * of the fire truck to that car park and sets the menu to be opened
     */
    public void checkCarparkCollision() {
        if (carparkLayer != null) {
            for (Vector2 vertex : getPolygonVertices(super.getMovementHitBox())) {
                if (carparkLayer.getCell((int) (vertex.x / TILE_DIMS), (int) (vertex.y / TILE_DIMS)) != null) {
                    if (carparkLayer.getCell((int) (vertex.x / TILE_DIMS), (int) (vertex.y / TILE_DIMS)).getTile().getProperties().get("carpark") != null) {
                        int carparkNum = ((int) carparkLayer.getCell(((int) (vertex.x / TILE_DIMS)), ((int) (vertex.y / TILE_DIMS))).getTile().getProperties().get("carpark"));
                        this.setRespawnLocation(carparkNum);
                        this.fireStation.toggleMenu(true);
                    }
                }
            }
        }
    }

    /**
     * ======================================================================
     * Added for assessment 3
     * ======================================================================
     * Sets the respawn location which contains the
     * name, x coord, y coord and name of car park
     *
     * @param number from tile map custom property
     */
    public void setRespawnLocation(int number) {
        switch (number) {
            case 0:
                this.location = CarparkEntrances.Main1;
                break;
            case 1:
                this.location = CarparkEntrances.Main2;
                break;
            case 2:
                this.location = CarparkEntrances.Lower;
                break;
            case 3:
                this.location = CarparkEntrances.Upper1;
                break;
            case 4:
                this.location = CarparkEntrances.Upper2;
                break;
            case 5:
                this.location = CarparkEntrances.TopLeft;
                break;
            case 6:
                this.location = CarparkEntrances.TopRight;
        }
    }

    /**
     * ==================================================================================
     * Added for assessment 3
     * ==================================================================================
     * <p>
     * Updates the arrow to point at the nearest
     * fortress to help the user know where to go
     *
     * @param shapeRenderer to draw the arrow
     * @param fortresses    list of fortresses
     */
    public void updateArrow(ShapeRenderer shapeRenderer, ArrayList<ETFortress> fortresses) {
        setNearestFortress(fortresses);
        if (isArrowVisible && this.nearestFortress != null) {
            arrow.setPosition(this.getCentreX(), this.getCentreY());
            arrow.aimAtTarget(this.nearestFortress.getCentre());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.polygon(arrow.getTransformedVertices());
            shapeRenderer.end();
        }
    }

    /**
     * Draws the voxel representation of the firetruck. Incrementally builds the firetruck
     * from layers of images with each image slightly higher than the last
     */
    private void drawVoxelImage(Batch batch) {
        // Length of array containing image slices
        int slicesLength = this.firetruckSlices.size() - 1;
        float x = getX(), y = getY(), angle = this.getRotation();
        float width = this.getWidth(), height = this.getHeight();
        for (int i = 0; i < slicesLength; i++) {
            Texture texture = animateLights(i);
            batch.draw(new TextureRegion(texture), x, (y - slicesLength / 3f) + i, width / 2, height / 2, width, height, 1, 1, angle, true);
        }
    }

    /**
     * Alternates between showing the red and blue light on the truck.
     * Returns the texture at the given index offset to the correct index.
     *
     * @param index The index of the next texture to draw the sprite with.
     * @return The next texture to draw the sprite with.
     */
    private Texture animateLights(int index) {
        if (index == 14) { // The index of the texture containing the first light colour
            return this.getInternalTime() / 5 > 15 ? this.firetruckSlices.get(index + 1) : this.firetruckSlices.get(index);
        } else if (index > 14) { // Offset remaining in order to not repeat a texture
            return this.firetruckSlices.get(index + 1);
        }
        return this.firetruckSlices.get(index);
    }

    /**
     * Resets rotation and hit box when fire truck is spawned
     */
    protected void resetSprite() {
        super.resetRotation(0 + this.location.getRotation());
        super.setMovementHitBox(180 + this.location.getRotation());
    }

    /*
     *  =======================================================================
     *                          Added for Assessment 3
     *  =======================================================================
     */
    /**
     * Finds the nearest alive fortress
     *
     * @param fortresses list of fortresses
     */
    public void setNearestFortress(ArrayList<ETFortress> fortresses) {
        ETFortress nearest = null;
        for (ETFortress fortress : fortresses) {
            if (!fortress.isFlooded()) {
                if (nearest == null) {
                    nearest = fortress;
                } else if (fortress.getCentre().dst(this.getCentre()) < nearest.getCentre().dst(this.getCentre())) {
                    nearest = fortress;
                }
            }
        }
        this.nearestFortress = nearest;
    }

    /**
     * Creates the polygon for the hose and the water bar to store the firetruck's
     * water level.
     */
    private void createWaterHose() {
        // Get the scale of the hose and create its shape
        float rangeScale = this.getType().getProperties()[4];
        this.hoseWidth = this.getHeight() * 4.5f * rangeScale;
        this.hoseHeight = this.getWidth() * 0.65f * rangeScale;
        float[] hoseVertices = { // Starts facing right
                0, 0,
                (hoseWidth * 0.5f), (hoseHeight / 2),
                (hoseWidth * 0.9f), (hoseHeight / 2),
                (hoseWidth), (hoseHeight / 2.25f),
                (hoseWidth), -(hoseHeight / 2.25f),
                (hoseWidth * 0.9f), -(hoseHeight / 2),
                (hoseWidth * 0.5f), -(hoseHeight / 2)
        };
        this.hoseRange = new Polygon(hoseVertices);
        // Create the water bar
        this.waterBar = new ResourceBar(Math.max(this.getWidth(), this.getHeight()), Math.min(this.getWidth(), this.getHeight()));
        this.waterBar.setColourRange(new Color[]{Color.BLUE});
        this.waterBar.setMaxResource((int) this.getType().getProperties()[5]);
        // Start with the hose off
        this.toggleHose();
    }

    /**
     * Checks if a polygon is within the range of the firetrucks hose.
     *
     * @param polygon The polygon that needs to be checked.
     * @return Whether the polygon is in the hose's range
     */
    public boolean isInHoseRange(Polygon polygon) {
        if (this.getInternalTime() % 10 != 0) return false;
        return Intersector.overlapConvexPolygons(polygon, this.hoseRange);
    }

    /**
     * Gets whether the firetruck is damaged.
     *
     * @return Whether the firetruck is damaged.
     */
    public boolean isDamaged() {
        return this.getHealthBar().getCurrentAmount() < this.getType().getProperties()[0];
    }

    /**
     * Gets whether the firetruck has used any water.
     *
     * @return Whether the firetruck has used any water.
     */
    public boolean isLowOnWater() {
        return this.waterBar.getCurrentAmount() < this.getType().getProperties()[5];
    }

    /**
     * Toggles the firetruck's hose to spray if off and stop if on.
     */
    public void toggleHose() {
        if (this.toggleDelay <= 0) {
            this.toggleDelay = 20;
            this.isSpraying = !this.isSpraying && !this.isTankEmpty();
            this.waterBar.setFade(false, !this.isSpraying);
        }
    }

    /**
     * Set the hose on or off
     *
     * @param bool  <code>true</code> to turn hose on
     *              <code>false</code> to turn hose off
     */
    public void setHose(boolean bool) {
        this.isSpraying = bool;
        this.waterBar.setFade(false, !this.isSpraying);
    }

    /**
     * Overloaded method for drawing debug information. Draws the hitbox as well
     * as the hose range indicator.
     *
     * @param renderer The renderer used to draw the hitbox and range indicator with.
     */
    @Override
    public void drawDebug(ShapeRenderer renderer) {
        super.drawDebug(renderer);
        renderer.polygon(this.hoseRange.getTransformedVertices());
    }

    /**
     * Gets the firetruck's water bar so it can be manipulated.
     *
     * @return The firetruck's water bar.
     */
    public ResourceBar getWaterBar() {
        return this.waterBar;
    }

    /**
     * Gets whether the firetruck is spraying water.
     *
     * @return Whether the firetruck is spraying water.
     */
    public boolean isSpraying() {
        return this.isSpraying;
    }

    /**
     * Gets if the water tank of the truck is empty
     * @return  <code>true</code> if tank is empty
     *          <code>false</code> otherwise
     */
    public boolean isTankEmpty() {
        return this.waterBar.getCurrentAmount() <= 0;
    }

    /*
     *  =======================================================================
     *                          Added for Assessment 3
     *  =======================================================================
     */
    /**
     * Calls methods to reset the fire truck when it is to be respawned
     */
    public void respawn() {
        this.setPosition(this.location.getLocation().x, this.location.getLocation().y);
        this.resetSprite();
        this.setSpeed(new Vector2(0, 0));
    }

    /**
     * Checks if the fire truck should be destroyed
     * @return  <code>true</code> if fire truck is destroyed
     *          <code>false</code> otherwise
     */
    public boolean checkDestroyed() {
        this.isAlive = (getHealthBar().getCurrentAmount() > 0);
        return !this.isAlive;
    }

    public Constants.CarparkEntrances getCarpark() {
        return this.location;
    }

    public Texture getFireTruckTexture() {
        return new Texture(Gdx.files.internal("FireTrucks/" + type.getColourString() + "/FiretruckFull.png"));
    }

    public Image getFireTruckImage() {
        return new Image(getFireTruckTexture());
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public float getRange() {
        return this.getType().getProperties()[4];
    }

    public TruckType getType() {
        return type;
    }

    public float getPrice() {
        return this.getType().getProperties()[6];
    }

    public float getDamage() {
        return this.damage;
    }

    public void buy() {
        this.isBought = true;
    }

    public boolean isBought() {
        return this.isBought;
    }

    public void setToggleDelay(int delay) {this.toggleDelay = delay;}

    public ETFortress getNearestFortress() {
        return this.nearestFortress;
    }

    public void setArrow(boolean b) {this.isArrowVisible = b;}


    /*
     *  =======================================================================
     *                          Added for Assessment 4
     *  =======================================================================
     */
    //Getter for invisible variable
    public boolean getInvisible(){ return this.isInvisible; }

    /*
     *  =======================================================================
     *                          Added for Assessment 4
     *  =======================================================================
     */
    //Getter for immune variable
    public boolean getImmune(){ return this.isImmune; }

    /*
     *  =======================================================================
     *                          Added for Assessment 4
     *  =======================================================================
     */
    //Getter for power up type
    public String getPowerupType(){return this.powerupType;}

    /*
     *  =======================================================================
     *                          Added for Assessment 4
     *  =======================================================================
     */
    //Getter for finding out which power ups are currently active and returning an array of strings containing active powerups
    public ArrayList<String> getPowerupDisplay(){
        ArrayList<String> activePowerups = new ArrayList<String>();
        ArrayList<Integer> active = getActivePowerups();
        for(int i : active){
            switch(i){
                case 0:
                    activePowerups.add("Invisible " + (int) Math.floor(this.powerupTimer[0] / 100)); break;
                case 1:
                    activePowerups.add("Immune " + (int) Math.floor(this.powerupTimer[1] / 100)); break;
                case 2:
                    activePowerups.add("Replenishing " + (int) Math.floor(this.powerupTimer[2] / 100)); break;
                case 3:
                    activePowerups.add("Speed Up " + (int) Math.floor(this.powerupTimer[3] / 100)); break;
                case 4:
                    activePowerups.add("Damage Up " + (int) Math.floor(this.powerupTimer[4] / 100)); break;
                case 5:
                    activePowerups.add("Infinite Water " + (int) Math.floor(this.powerupTimer[5] / 100)); break;
            }
        }
        return activePowerups;
    }

    public ArrayList<Integer> getActivePowerups(){
        ArrayList<Integer> activePowerups = new ArrayList<Integer>();
        if(this.powerupTimer[0]>0){activePowerups.add(0);}
        if(this.powerupTimer[1]>0){activePowerups.add(1);}
        if(this.powerupTimer[2]>0){activePowerups.add(2);}
        if(this.powerupTimer[3]>0){activePowerups.add(3);}
        if(this.powerupTimer[4]>0){activePowerups.add(4);}
        if(this.powerupTimer[5]>0){activePowerups.add(5);}
        return activePowerups;
    }
    /*
     *  =======================================================================
     *                          Added for Assessment 4
     *  =======================================================================
     */
    //Sets the powerup type and whether a powerup is active. It also sets the individual timers for each powerup so that
    //each power up can be timed independantly.
    public void setPowerup(int time, String type){
            this.powerupType = type;
            this.powerupActive = true;
            switch(type){
                case "ghost":
                    this.powerupTimer[0] = time;
                    break;
                case "immunity":
                    this.powerupTimer[1] = time;
                    break;
                case "replenish":
                    this.powerupTimer[2] = time;
                    break;
                case "speedUp":
                    this.powerupTimer[3] = time;
                    break;
                case "damageUp":
                    this.powerupTimer[4] = time;
                    break;
                case "infiniteWater":
                    this.powerupTimer[5] = time;
                    break;
            }
    }

    /*
     *  =======================================================================
     *                          Added for Assessment 4
     *  =======================================================================
     *
     * Powerup effects
     */
    //Sets the isInvisible flag to true and turns the fire truck transparent by temporarily changing the draw colour
    private void ghost(Batch batch){
        this.isInvisible = true;
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0.05f);
    }
    //Sets the immune flag to true so that the collision calculations stops damage.
    private void immunity(){
        this.isImmune = true;
    }

    //Increases health and water over the duration of the power up
    private void replenish(){
        //If statement slows down replenish rate by only adding on factors of 4,
        //reducing replenish speed by 4
        if(this.powerupTimer[2]%4 == 0) {
            this.getHealthBar().addResourceAmount(1);
            this.getWaterBar().addResourceAmount(1);
        }
    }
    //Sets the acceleration to 1.5 times the active trucks original.
    private void speedUp(){
        this.setAccelerationRate(this.getType().getProperties()[1] * 1.5f);
    }
    //Increases damage by a multiplying factor
    private void damageUp(){
        int damageMul = 2;
        //If statement stops the trucks damage from exponentially increasing over time, assures it will only be
        //multiplied once in the power ups lifetime.
        if(this.damage == (this.getType().getProperties()[7] / difficulty)){
            this.damage *= damageMul;
        }
    }
    //Sets the water amount to maximum so no water gets used when power up is active
    private void infiniteWater(){
        this.getWaterBar().setCurrentAmount((int)this.getWaterBar().getMaxAmount());
    }




    /**
     * Dispose of all textures used by this class and its parents.
     */
    @Override
    public void dispose() {
        super.dispose();
        for (Texture texture : this.firetruckSlices) {
            texture.dispose();
        }
        for (Texture texture : this.waterFrames) {
            texture.dispose();
        }
    }
}