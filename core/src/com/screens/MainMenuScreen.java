package com.screens;

// LibGDX imports
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
// Class imports
import com.Kroy;
import com.misc.SFX;

import static com.misc.Constants.DEBUG_ENABLED;

import java.util.ArrayList;

/**
 * Displays the main menu screen with selection buttons.
 * 
 * @author Archie
 * @author Josh
 * @since 23/11/2019
 */
public class MainMenuScreen implements Screen {
	
	// A constant variable to store the game
	final Kroy game;

	// objects used for visuals
	private final OrthographicCamera camera;
	private final Stage stage;
	private final Skin skin;
	private final Viewport viewport;

	/**
	 * The constructor for the main menu screen. All game logic for the main
	 * menu screen is contained.
	 *
	 * @param game The game object.
	 */
	public MainMenuScreen(final Kroy game) {
		this.game = game;

		skin = game.getSkin();
		
		// Create new sprite batch

		// Create an orthographic camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		/* tell the SpriteBatch to render in the
		   coordinate system specified by the camera. */
		game.spriteBatch.setProjectionMatrix(camera.combined);

		// Create a viewport
		viewport = new ScreenViewport(camera);
		viewport.apply(true);

		// Set camera to centre of viewport
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();

		// Create a stage for buttons
		stage = new Stage(viewport, game.spriteBatch);
		stage.setDebugAll(DEBUG_ENABLED);

		SFX.sfx_soundtrack_1.setLooping(true);
		SFX.playMenuMusic();
	}

	/**
	 * Render function to display all elements in the main menu.
	 * 
	 * @param delta The delta time of the game, updated every second rather than frame.
	 */
	@Override
	public void render(float delta) {
		// MUST BE FIRST: Clear the screen each frame to stop textures blurring
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw the button stage
		stage.draw();
	}

	// Below are all required methods of the screen class
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
        camera.update();
	}

	/**
	 * Create the button stage.
	 */
	@Override
	public void show() {
		// Allow stage to control screen inputs.
		Gdx.input.setInputProcessor(stage);

		// Create table to arrange buttons.
		Table buttonTable = new Table();
		buttonTable.center();

		Image bcg = new Image(new Texture(Gdx.files.internal("menu_bg_2.png")));
		Stack bcgstack = new Stack();
		bcgstack.setFillParent(true);
		bcgstack.add(bcg);
		bcgstack.add(buttonTable);

		// Create buttons
		Label heading = new Label("Kroy", new Label.LabelStyle(game.coolFont, Color.WHITE));
		heading.setFontScale(2);
		Label subHeading = new Label("Destroy the Fortresses and Save the City", new Label.LabelStyle(game.coolFont, Color.WHITE));
		TextButton playButton = new TextButton("Play", skin);
		TextButton howToPlayButton = new TextButton("How to Play", skin);
		TextButton quitButton = new TextButton("Quit", skin);

		/*
		*  =======================================================================
		*       	Modified for Assessment 4		@author Archie Godfrey
		*  =======================================================================
		*		Added save file load buttons to menu and listeners for each
		*/
		TextButton loadButton = new TextButton("Load", skin);
		loadButton.center().pad(10, 7, 10, 7).setTouchable(Touchable.disabled);

		// Create load vertical group
		HorizontalGroup loadGroup = new HorizontalGroup();
		loadGroup.center().addActor(loadButton);

		// Create array to store save buttons
		ArrayList<TextButton> saveButtons = new ArrayList<TextButton>();
		for (int i = 0; i < 3; i++) {
			// Create 3 save buttons
			TextButton saveButton = new TextButton(String.valueOf(i + 1), skin);
			boolean saveEmpty = game.getSaveControls().checkIfSaveEmpty(i + 1);
			saveButton.center().pad(10, (14 - (2 - i)), 10, (16 - i)).setTouchable(saveEmpty ? Touchable.disabled : Touchable.enabled);
			saveButton.setColor(saveEmpty ? Color.DARK_GRAY : Color.GREEN);
			// Add button to group and array
			loadGroup.space(10);
			loadGroup.addActor(saveButton);
			saveButtons.add(saveButton);
		}

		// Add buttons to table and style them
		buttonTable.add(heading).padBottom(10);
		buttonTable.row();
		buttonTable.add(subHeading).padBottom(15);
		buttonTable.row();
		buttonTable.add(playButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(loadGroup).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(howToPlayButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(quitButton).width(200).height(40);

		// Add listeners
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				game.getSaveControls().setCurrentSaveNumber(0);
				game.setScreen(new StoryScreen(game));
				dispose();
			}
		});

		// Add load functionality to buttons
		for (int i = 0; i < saveButtons.size(); i++) {
			final int index = i;
			saveButtons.get(index).addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SFX.sfx_button_click.play();
					game.loadGameFromSave(index + 1);
					dispose();
				}
			});
		}

		howToPlayButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				game.setScreen(new HowToPlayScreen(game, getThis()));
			}
		});

		quitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
				System.exit(1);
			}
		});

		// Add table to stage
		stage.addActor(bcgstack);
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
		stage.dispose();
	}

	/**
	 * Used to pass the main menu screen into the controls screen
	 *
	 * @return  main menu screen
	 */
	public Screen getThis() {
		return this;
	}
}