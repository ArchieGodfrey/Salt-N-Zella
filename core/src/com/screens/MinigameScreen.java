package com.screens;

/*
 *  =======================================================================
 *       	Modified for Assessment 4		@author Archie Godfrey
 *  =======================================================================
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.entities.Alien;
import com.sprites.MinigameSprite;
import com.Kroy;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import static com.misc.Constants.*;

/**
 * MinigameScreen, as suggested, a Mini game contained within
 * the game, which allows the player to play a different style
 * of game ("whack-a-mole"), but keeping the same theme (aliens
 * and fire trucks). Players can earn score which is transferred
 * into the main game after 30 seconds
 */
public class MinigameScreen implements Screen {

    // constants to store game and game screen
    private final Kroy game;
    private final GameScreen gameScreen;

    //Declare images
    private final Texture waterImage;
    private final Texture background;

    // score and time
    private int score;
    private int time;

    //declare camera items
    private final OrthographicCamera camera;
    private int screenWidth;
    private int screenHeight;

    // values to control spawning and despawning ETs
    private long timeSpawn;
    private final Random random;
    private double currentRowSpawn;
    private final ArrayList<Vector2> spawnPositions;
    private final ArrayList<Alien> onScreenETs;
    private final TreeMap<Double, AlienType> map;

    // water values
    private final Rectangle water;
    private boolean canSpray;
    private Vector2 clicked;

    private final MiniGameInputHandler miniGameInputHandler;

    /**
     *  =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
     *  Changed the background image to keep the minigame more inline with the
     *  main game. Aliens now spawn in the top right window and make their way
     *  to the bottom. The player must survive for 30 seconds in order to gain
     *     points, if the aliens reach the bottom the player loses and their
     *                  score is set to 0 and the minigame ends.
     * 
     * Constructor for minigame screen which is called when
     * the player drives over {@link MinigameSprite} in
     * {@link GameScreen}
     *
     * @param game          to change screen and access shared batch
     * @param gameScreen    to return back to after minigame completion
     */
    public MinigameScreen(Kroy game, GameScreen gameScreen) {

        this.game = game;
        this.gameScreen = gameScreen;

        // Set screen dimensions
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        //load images for sprites
        waterImage = new Texture(Gdx.files.internal("Minigame/splashcircle.png"));
        background = new Texture(Gdx.files.internal("Minigame/minigame_bg_new.png"));

        //alien creation
        onScreenETs = new ArrayList<Alien>();

        // Create spawn locations that are in windows
        spawnPositions = new ArrayList<Vector2>();
        for (int y = 4; y >= 0; y --) {
            for (int x = 2; x >= 0; x --) {
                if (x != 1 || y != 0) {
                    spawnPositions.add(new Vector2(screenWidth * 0.253f + (screenWidth * 0.211f * x), screenHeight * 0.16f + (screenHeight * 0.165f * y)));
                }
            }
        }

        ArrayList<AlienType> typeOfAliens = new ArrayList<>();
        typeOfAliens.add(AlienType.blue);
        typeOfAliens.add(AlienType.green);
        typeOfAliens.add(AlienType.red);

        ArrayList<Double> chanceOfSelectingAlien = new ArrayList<>();
        for (AlienType type : typeOfAliens){
            chanceOfSelectingAlien.add(type.getChance());
        }

        random = new Random();

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                time--;
            }
        }, 1, 1);

        time = MINIGAME_DURATION;

        // Creates a map of types of aliens and their chance of being selected
        map = new TreeMap<>();
        double total = 0.0d;
        for (int i = 0; i < typeOfAliens.size(); i++) {
            map.put(total += chanceOfSelectingAlien.get(i), typeOfAliens.get(i));
        }

        //initialise score to 0
        score = 0;

        //create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);



        // set InputHandler
        miniGameInputHandler = new MiniGameInputHandler(this);

        //create water rectangle to allow collision detection
        water = new Rectangle(0, 0, 150, 150);

        canSpray = false;

        clicked = new Vector2();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(miniGameInputHandler);

        // Start aliens spawning in top row
        this.currentRowSpawn = 2;
    }

    @Override
    public void render(float delta) {
        //render screen
        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        camera.update();

        //render sprites as batch inc. score
        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();
        
        //draw aliens on screen
        for (Alien alien : onScreenETs) {
            game.spriteBatch.draw(alien.getTexture(), alien.getX(), alien.getY(), 100, 100);
        }

        game.spriteBatch.draw(background, 0, 0, screenWidth, screenHeight);

        drawWater();

        game.coolFont.draw(game.spriteBatch, "Minigame Score: " + score, 25, 100);
        game.coolFont.draw(game.spriteBatch, "Time Remaining: " + time, screenWidth - 250, 100);

        game.spriteBatch.end();

        checkAlienDespawn();

        if (TimeUtils.millis() > timeSpawn + MINIGAME_SPAWN_RATE) spawnAlien();

        if (time <= 0) toGameScreen(true);

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        setScreenDimensions(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.dispose();
        waterImage.dispose();
    }

    /**
     *  =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
     *    Added a clause to end the game when an alien reaches the end, also
     *          decreases the furthest position an alien can spawn by 1
     * Check if an alien on the screen is ready to be
     * despawned by being clicked on or by the timer
     */
    public void checkAlienDespawn() {
        for (int i = 0; i < onScreenETs.size(); i++) {
            Alien alien = onScreenETs.get(i);

            if (TimeUtils.millis() > alien.getSpawnTime() + alien.type.getAliveTime()) {
                onScreenETs.remove(alien);
                // If alien reaches the last position, end game
                if (new Vector2(alien.getX(), alien.getY()).equals(this.spawnPositions.get(this.spawnPositions.size() - 1))) {
                    toGameScreen(false);
                }
            }

            if (alien.getBoundingRectangle().contains(clicked)) {
                score += alien.getScore();
                if (this.currentRowSpawn > 1) {
                    this.currentRowSpawn -= 1;
                }
                onScreenETs.remove(alien);
            }
        }
    }

    /**
     * Draws the water image if the player has clicked
     */
    private void drawWater() {
        if (canSpray) game.spriteBatch.draw(waterImage, water.x, water.y);
    }

    /**
     *  =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
     *   Now increments the current row by 2 each call and spawns an alien at
     *                      the front of the queue
     * 
     * Spawns an alien and resets the spawn timer
     */
    public void spawnAlien() {
        // Spawn 2 aliens anywhere on the building
        for (int i = 0; i < 3; i++) {
            onScreenETs.add(new Alien(generateType(), generateLocation()));
        }
        // and have a leading alien
        onScreenETs.add(new Alien(generateType(), this.spawnPositions.get((int) this.currentRowSpawn)));
        
        // Increase the spawn speed over the game
        timeSpawn = TimeUtils.millis() - (600 - (time * 20));

        // Increase the leading aliens row by 2 (so it has two random positions to choose)
        if (this.currentRowSpawn < this.spawnPositions.size() - 2) {
            this.currentRowSpawn += 2;
            // Until the end is almost reached then add 1 (odd number of positions)
        } else if (this.currentRowSpawn < this.spawnPositions.size() - 1) {
            this.currentRowSpawn += 1;
        }
    }

    /**
     * Generates a random number between 0.0 and 1.0 then rounds up
     * to the nearest value in the map - this allows a random patrol
     * type to be selected but has a higher probability of choosing
     * lower scoring patrols over higher scoring patrols
     *
     * @return  type of alien
     */
    public AlienType generateType() {
        double randomIndex = random.nextDouble();
        return map.ceilingEntry(randomIndex).getValue();
    }

    /**
     *  =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
     * Game now uses set spawn positions to show aliens in windows. Generates
     * a random window to show the alien at. Aliens start at the top and move
     * down row by row.
     *
     * @return  vector of random location
     */
    private Vector2 generateLocation(){
        int row = (int) Math.ceil(this.currentRowSpawn / 3);
        return this.spawnPositions.get(random.nextInt((row * 3) - 1));
    }

    /**
     *  =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
     *      Added ability to lose the game, score affected by win parameter
     * 
     * Goes back to the game screen once the user presses escape,
     * when the time runs out, or if the player loses
     * 
     * @param win Whether the player won the minigame (true) or not (false)
     */
    public void toGameScreen(boolean win) {
        if (win) {
            gameScreen.setScore(gameScreen.getScore() + score);
        } else {
            gameScreen.setScore(0);
        }
        this.game.setScreen(this.gameScreen);
        dispose();
    }

    /**
     * Sets the position of the water object and set
     * where the player clicked
     *
     * @param x position of water
     * @param y position of water
     */
    public void setTouch(int x, int y) {
        water.setPosition(x - (waterImage.getWidth()/2f), y - (waterImage.getHeight()/2f));
        clicked.set(x, y);
    }

    /**
     * Means that the water can spray and destroy
     * aliens in that location
     * @param b <code>true</code> if water can spray
     *          <code>false</code> otherwise
     */
    public void setCanSpray(boolean b) {
        this.canSpray = b;
    }

    public OrthographicCamera getCamera() { return camera; }

    public ArrayList<Alien> getOnScreenETs() {
        return this.onScreenETs;
    }

    public void setScreenDimensions(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public int getScore() {
        return this.score;
    }
}
