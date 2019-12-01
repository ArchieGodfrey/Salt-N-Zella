package com.screens;

// LibGDX imports
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

// Tiled map imports fro LibGDX
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

// Class imports
import com.kroy.Kroy;
import com.sprites.MovementSprite;

// Constants import
import static com.config.Constants.SCREEN_HEIGHT;
import static com.config.Constants.SCREEN_WIDTH;
import static com.config.Constants.SCORE_Y;
import static com.config.Constants.SCORE_X;

// Class to display the main game
public class GameScreen implements Screen {
	  
	// A constant variable to store the game
	final Kroy game;

	// Private values to be used in this class only
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private int score;
	private MovementSprite testSprite;
	private Texture texture;
	private Batch batch;

	// The constructor for the main game screen
	// All of the logic for the game will go here
	// Params:
	// Kroy gam - the game object
	public GameScreen(final Kroy gam) {
		this.game = gam;

		// Create an orthographic camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		game.init(camera);

		// Load the map, set the unit scale
		map = new TmxMapLoader().load("MapAssets/KroyMap.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1f / 1f);

		// Initalise textures and batch and then create a sprite
		batch = renderer.getBatch();
		texture = new Texture("badlogic.jpg");
		testSprite = new MovementSprite(batch, texture, 2000, 500, (TiledMapTileLayer) map.getLayers().get("River"));
	}

	// Render function to display all elements in the main game
	// Params:
	// float delta - the delta time of the game, updated every game second rather than frame
	@Override
	public void render(float delta) {
		// clear the screen with a dark blue color. The arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1] of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// set the TiledMapRenderer view based on what the camera sees, and render the map
		renderer.setView(camera);
		renderer.render();

		// Draw score to the screen at given co-ordinates
		game.drawFont("Score: " + score, SCORE_X, SCORE_Y);

		// Draw FPS to the screen at given co-ordinates
		game.drawFont("FPS: " + Gdx.graphics.getFramesPerSecond(), SCREEN_WIDTH - SCORE_X * 2, SCORE_Y);

		// Tell the camera to update to the sprites position with a delay based on lerp and game time
		float lerp = 1.1f;
		Vector3 position = camera.position;
		position.x += (testSprite.getCentreX() - position.x) * lerp * delta;
		position.y += (testSprite.getCentreY() - position.y) * lerp * delta;
		camera.update();

		// Call the update function of the sprite to draw and update it
		testSprite.update();
	}

	// Below are all required methods of the screen class
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		texture.dispose();
		testSprite.dispose();
	}

}