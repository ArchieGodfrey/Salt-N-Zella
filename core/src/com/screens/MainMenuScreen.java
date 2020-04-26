package com.screens;

// LibGDX imports
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
// Class imports
import com.Kroy;
import com.misc.SFX;

import static com.misc.Constants.*;

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

	// Objects used for visuals
	private final OrthographicCamera camera;
	private final Stage stage;
	private final Skin skin;
	private final Viewport viewport;

	// Whether the menu or difficulty is showing
	private boolean showMenu;

	/**
	 * The constructor for the main menu screen. All game logic for the main
	 * menu screen is contained.
	 *
	 * @param game The game object.
	 */
	public MainMenuScreen(final Kroy game) {
		this.game = game;

		skin = game.getSkin();
		
		// Show the menu options
		showMenu = true;

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
		camera.setToOrtho(false, width, height);
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

		// Add buttons to table and style them
		buttonTable.add(heading).padBottom(10);
		buttonTable.row();
		buttonTable.add(subHeading).padBottom(15);
		buttonTable.row();
		toggleMenuButtons(this.showMenu, buttonTable);

		// Add table to stage
		stage.addActor(bcgstack);
	}

	/*
	*  =======================================================================
	*       	Modified for Assessment 4		@author Archie Godfrey
	*  =======================================================================
	*			Toggles the menu buttons to first show all options
	*					and then the difficulty options
	*/
	private void toggleMenuButtons(boolean show, Table buttonTable) {
		if (show) {
			createMenuOptions(buttonTable);
		} else {
			createDifficultyOptions(buttonTable);
		}
	}

	/**
	 * =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
	 *		Creates the menu options "Play", "Load", "How To Play"
	 *				and "Quit" and displays them in a table
	 *
	 * @param buttonTable	The table to display the buttons in
	 */
	private void createMenuOptions(Table buttonTable) {
		// Create menu buttons
		TextButton playButton = new TextButton("Play", skin);
		TextButton howToPlayButton = new TextButton("How To Play", skin);
		TextButton quitButton = new TextButton("Quit", skin);

		// Create load buttons
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

		// Add buttons to table
		buttonTable.add(playButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(loadGroup).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(howToPlayButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(quitButton).width(200).height(40);

		// Show difficulty options on press
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				showMenu = false;
				show();
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

		// Show controls on press
		howToPlayButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				game.setScreen(new HowToPlayScreen(game, getThis()));
			}
		});

		// Quit game on press
		quitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
				System.exit(1);
			}
		});

	}

	/**
	 * =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
	 *			Creates the menu options "Easy", "Medium", "Hard"
	 *				and "Back" and displays them in a table
	 *
	 * @param buttonTable	The table to display the buttons in
	 */
	private void createDifficultyOptions(Table buttonTable) {
		// Create difficulty buttons
		TextButton easyButton = new TextButton("Easy", skin);
		TextButton mediumButton = new TextButton("Medium", skin);
		TextButton hardButton = new TextButton("Hard", skin);
		TextButton backButton = new TextButton("Back", skin);

		// Add buttons to table
		buttonTable.add(easyButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(mediumButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(hardButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(backButton).width(200).height(40);

		// Play easy game on press
		easyButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				game.getSaveControls().setCurrentSaveNumber(0);
				game.setDifficulty(1);
				game.setScreen(new StoryScreen(game));
				dispose();
			}
		});

		// Play medium game on press
		mediumButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				game.getSaveControls().setCurrentSaveNumber(0);
				game.setDifficulty(2);
				game.setScreen(new StoryScreen(game));
				dispose();
			}
		});

		// Play hard game on press
		hardButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				game.getSaveControls().setCurrentSaveNumber(0);
				game.setDifficulty(3);
				game.setScreen(new StoryScreen(game));
				dispose();
			}
		});

		// Go back to other options on press
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				showMenu = true;
				show();
			}
		});
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