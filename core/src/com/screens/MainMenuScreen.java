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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
// Class imports
import com.Kroy;
import com.misc.BackgroundBox;
import com.misc.SFX;
import com.misc.StringInputListener;

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

	// Whether the menu or difficulty or load options are showing
	private boolean showMenu;
	private boolean showLoad;

	// Controls warning message
	private String warning;
	private Runnable continueCallback;

	private VerticalGroup saveGroup;
    private ArrayList<TextButton> saveTextButtons;
    private ArrayList<TextButton> saveDeleteButtons;

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
		showLoad = false;

		// Initialise warning message
        warning = "";

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
		if (show && warning.length() == 0) {
			createMenuOptions(buttonTable);
		} else if (showLoad && warning.length() == 0) {
			createSaveOptions(buttonTable);
		} else if (!showLoad && warning.length() == 0) {
			createDifficultyOptions(buttonTable);
		} else {
            createWarning(buttonTable);
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
		TextButton loadButton = new TextButton("Load", skin);
		TextButton howToPlayButton = new TextButton("How To Play", skin);
		TextButton quitButton = new TextButton("Quit", skin);

		// Add buttons to table
		buttonTable.add(playButton).padBottom(20).width(200).height(40);
		buttonTable.row();
		buttonTable.add(loadButton).padBottom(20).width(200).height(40);
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

		// Show difficulty options on press
		loadButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				showMenu = false;
				showLoad = true;
				show();
			}
		});

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

	/**
	 * =======================================================================
	 *       	Modified for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
     *			Creates the menu options save file rows
     *                and displays them in a table
	 *
	 * @param buttonTable	The table to display the buttons in
	 */
	private void createSaveOptions(Table buttonTable) {
        // Create back button
        TextButton backButton = new TextButton("Back", skin);

        // The vertical group to store the save buttons
        Stack saveStack = new Stack();
        saveGroup = new VerticalGroup();
        saveTextButtons = new ArrayList<>();
        saveDeleteButtons = new ArrayList<>();
        generateSaveButtons();
        saveStack.add(saveGroup);
        buttonTable.add(saveStack).padBottom(20);
        buttonTable.row();
		buttonTable.add(backButton).width(200).height(40);

        // Create save buttons
        generateSaveSelector();

        // Go back to other options on press
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				showMenu = true;
				showLoad = false;
				show();
			}
        });

        // Add save and load save functionality
        for (int i = 0; i < 3; i++) {
            int index = i + 1;
            saveTextButtons.get(i).addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    warning = "Are you sure you want to load a new game? \n\n Unsaved progress will be lost";
                    continueCallback = new Runnable() {
                        @Override
                        public void run() {
                            game.loadGameFromSave(index);
                        }
                    };
                    show();  
                }
            });
            saveDeleteButtons.get(i).addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    warning = "Are you sure you want to delete this save? \n\n This cannot be undone";
                    continueCallback = new Runnable() {
                        @Override
                        public void run() {
                            game.getSaveControls().deleteSave(index);
                            show();
                        }
                    };
                    show();
                }
            });
        }
    }

    /*
	 *  =======================================================================
	 *       	Added for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
	 */
    /**
     * Builds each save file item which contains:
     * - save option button (save or overwrite)
     * - text button (empty or load)
     */
    private void generateSaveButtons() {
        saveTextButtons.clear();
        for (int i=1; i <= 3; i++) {
            boolean emptySave = this.game.getSaveControls().checkIfSaveEmpty(i);

            TextButton textButton = new TextButton("", skin);
            TextButton optionButton = new TextButton("  Save Game  ", skin);
            TextButton deleteButton = new TextButton(" Delete Save ", skin);
            deleteButton.setColor(Color.RED);
            optionButton.setColor(Color.GREEN);

            if (emptySave) {
                textButton.setText("  Empty Save  ");
                textButton.setColor(Color.DARK_GRAY);
            } else {
                textButton.setText("   Load Save   ");
            }
            optionButton.pad(10).padTop(5).padBottom(5);
            textButton.pad(10).padTop(5).padBottom(5);
			deleteButton.pad(10).padTop(5).padBottom(5);
            saveTextButtons.add(textButton);
            saveDeleteButtons.add(deleteButton);
        }
    }

    /*
	 *  =======================================================================
	 *       	Added for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
	 */
    /**
     * Builds the save selector section of the screen
     * it is called once the screen is opened
     */
    private void generateSaveSelector() {
        saveGroup.clear();
        saveGroup.expand();
        saveGroup.center();
        
        for (int i=0; i <= 2; i++) {
            saveGroup.space(10);
            final int index = i + 1;

            TextButton nameButton = new TextButton(this.game.getSaveControls().getSaveName(index), skin);
            nameButton.pad(10).padTop(5).padBottom(5);
            nameButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    StringInputListener listener = new StringInputListener(game.getSaveControls(), nameButton, index);
                    Gdx.input.getTextInput(listener, "Enter Save Name", "", "Save " + index);
                }
            });
            
            HorizontalGroup hgSave = new HorizontalGroup();
            hgSave.center();
            hgSave.pad(10);
            hgSave.addActor(saveTextButtons.get(i));
            hgSave.space(15);
            hgSave.addActor(saveDeleteButtons.get(i));

            VerticalGroup vgSave = new VerticalGroup();
            vgSave.padTop(10);
            vgSave.addActor(nameButton);
            vgSave.addActor(hgSave);

            Stack stack = new Stack();
			stack.addActor(new BackgroundBox(100, 100, Color.GRAY, 20));
            stack.addActor(vgSave);
            saveGroup.addActor(stack);
        }
    }

	/*
	 *  =======================================================================
	 *       	Added for Assessment 4		@author Archie Godfrey
	 *  =======================================================================
	 */
    /**
     * Renders a warning message with a cancel and continue button
     * @param buttonTable   The table to draw the message to
     */
    private void createWarning(Table buttonTable) {
        // Create warning message
        Label message = new Label(warning, new Label.LabelStyle(game.coolFont, Color.WHITE));
        message.setFontScale(1.1f);
        message.setAlignment(1);

        // Create menu buttons
        HorizontalGroup buttons = new HorizontalGroup();
        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.pad(15).padTop(8).padBottom(8);
        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.pad(15).padTop(8).padBottom(8);
        buttons.addActor(cancelButton);
        buttons.space(200);
        buttons.addActor(continueButton);
        buttons.setPosition(0, 0);

        // Add buttons to table
        buttonTable.center();
        buttonTable.add(message).padBottom(40);
        buttonTable.row();
        buttonTable.add(buttons).padBottom(40);

        // Add listeners that run callbacks on press
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFX.sfx_button_click.play();
                warning = "";
                show();
            }
        });
		continueButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
                SFX.sfx_button_click.play();
                warning = "";
				continueCallback.run();
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