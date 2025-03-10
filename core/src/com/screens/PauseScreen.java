package com.screens;

/* =================================================================
                   New class added for assessment 3
   ===============================================================*/

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.misc.BackgroundBox;
import com.misc.SFX;
import com.misc.StringInputListener;
import com.Kroy;

import static com.misc.Constants.*;

import java.util.ArrayList;

/**
 * Screen that appears when the user pauses the game.
 * Whilst the game is paused, the timer will stop
 * counting down. From this screen, the user can exit
 * to the main menu or resume to game
 */
public class PauseScreen implements Screen {

    // A constant variable to store the game
    private final Kroy game;
    private final GameScreen gameScreen;

    // visuals and rendering
    private final Skin skin;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage stage;

    // Whether the menu or save options are showing
    private boolean showMenu;

    // Controls warning message
    private String warning;
    private Runnable continueCallback;
    
    private VerticalGroup saveGroup;
    private ArrayList<TextButton> saveTextButtons;
    private ArrayList<TextButton> saveOptionButtons;
    private ArrayList<TextButton> saveDeleteButtons;

    /**
     * The constructor for the pause screen
     *
     * @param game          the game object to change between screens
     * @param gameScreen    game screen to go back to
     */
    public PauseScreen(Kroy game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

		skin = game.getSkin();
		
		// Show the menu options
        showMenu = true;
        
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
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table buttonTable = new Table();
        buttonTable.center();

        Image bcg = new Image(new Texture(Gdx.files.internal("Minigame/minigame_bg_blue.png")));
		Stack bcgstack = new Stack();
        bcgstack.setFillParent(true);
		bcgstack.add(bcg);
        bcgstack.add(buttonTable);

        Label label = new Label("Game Paused", new Label.LabelStyle(game.coolFont, Color.WHITE));
        label.setFontScale(2);

        HorizontalGroup labels = new HorizontalGroup();
        Label scoreLabel = new Label("Score: " + gameScreen.getScore(), new Label.LabelStyle(game.coolFont, Color.WHITE));
        labels.addActor(scoreLabel);
        labels.space(200);
        Label timeLabel = new Label("Time: " + gameScreen.getFireStationTime(), new Label.LabelStyle(game.coolFont, Color.WHITE));
        labels.addActor(timeLabel);

        buttonTable.add(label).padBottom(20);
        buttonTable.row();
        toggleMenuButtons(this.showMenu, buttonTable);
        buttonTable.row();
        buttonTable.add(labels);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    SFX.sfx_button_click.play();
                    game.setScreen(gameScreen);
                    gameScreen.resume();
                    dispose();
                }
                return true;
            }
        });

        stage.addActor(bcgstack);
    }

    /*
	*  =======================================================================
	*       	Modified for Assessment 4		@author Archie Godfrey
	*  =======================================================================
	*			Toggles the menu buttons to first show all options
	*					and then the save options
	*/
	private void toggleMenuButtons(boolean show, Table buttonTable) {
		if (show && warning.length() == 0) {
			createMenuOptions(buttonTable);
		} else if (warning.length() == 0) {
			createSaveOptions(buttonTable);
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
        TextButton resumeButton = new TextButton("Resume Game", skin);
        TextButton saveButton = new TextButton("Save Options", skin);
        TextButton howToPlayButton = new TextButton("How to Play", skin);
        TextButton quitButton = new TextButton("Return to Main Menu", skin);

		// Add buttons to table
		buttonTable.add(resumeButton).width(200).height(40).padBottom(20);
        buttonTable.row();
        buttonTable.add(saveButton).width(200).height(40).padBottom(20);
        buttonTable.row();
        buttonTable.add(howToPlayButton).width(200).height(40).padBottom(20);
        buttonTable.row();
        buttonTable.add(quitButton).width(200).height(40).padBottom(20);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFX.sfx_button_click.play();
                game.setScreen(gameScreen);
                gameScreen.resume();
                dispose();
            }
        });

        // Show save options on press
		saveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SFX.sfx_button_click.play();
				showMenu = false;
				show();
			}
		});

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HowToPlayScreen(game,  getThis()));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFX.sfx_button_click.play();
                warning = "Are you sure you want to return to the main menu? \n\n Unsaved progress will be lost";
                continueCallback = new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MainMenuScreen(game));
                        gameScreen.dispose();
                        dispose();
                    }
                };
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
        saveOptionButtons = new ArrayList<>();
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
            saveOptionButtons.get(i).addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!gameScreen.getSaveControls().checkIfSaveEmpty(index)) {
                        warning = "You are about to overwrite an\n existing save file? \n\n This cannot be undone";
                        continueCallback = new Runnable() {
                            @Override
                            public void run() {
                                gameScreen.saveGame(index);
                                if (gameScreen.getSaveControls().getCurrentSaveNumber() != index) {
                                    game.getSaveControls().setCurrentSaveNumber(index);
                                }
                                show();
                            }
                        };
                    }
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
        saveOptionButtons.clear();
        saveTextButtons.clear();
        for (int i=1; i <= 3; i++) {
            boolean emptySave = this.gameScreen.getSaveControls().checkIfSaveEmpty(i);

            TextButton textButton = new TextButton("", skin);
            TextButton optionButton = new TextButton("  Save Game  ", skin);
            TextButton deleteButton = new TextButton(" Delete Save ", skin);
            deleteButton.setColor(Color.RED);
            optionButton.setColor(Color.GREEN);

            if (emptySave) {
                textButton.setText("  Empty Save  ");
                textButton.setColor(Color.DARK_GRAY);
            } else {
                if (this.gameScreen.getSaveControls().getCurrentSaveNumber() == i) {
                    textButton.setText("Current Save");
                    textButton.setColor(Color.LIGHT_GRAY);
                    textButton.setTouchable(Touchable.disabled);
                } else {
                    textButton.setText("   Load Save   ");
                    optionButton.setText("   Overwrite   ");
                    optionButton.setColor(Color.ORANGE);
                }
            }
            optionButton.pad(10).padTop(5).padBottom(5);
            textButton.pad(10).padTop(5).padBottom(5);
            deleteButton.pad(10).padTop(5).padBottom(5);
            saveOptionButtons.add(optionButton);
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

            TextButton nameButton = new TextButton(this.gameScreen.getSaveControls().getSaveName(index), skin);
            nameButton.pad(10).padTop(5).padBottom(5);
            nameButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    StringInputListener listener = new StringInputListener(gameScreen.getSaveControls(), nameButton, index);
                    Gdx.input.getTextInput(listener, "Enter Save Name", "", "Save " + index);
                }
            });
            
            HorizontalGroup hgSave = new HorizontalGroup();
            hgSave.center();
            hgSave.pad(10);
            hgSave.addActor(saveTextButtons.get(i));
            hgSave.space(15);
            hgSave.addActor(saveOptionButtons.get(i));
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

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /**
     * @param width of window
     * @param height of window
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.setToOrtho(false, width, height);
    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Used to pass the pause screen into the controls screen
     *
     * @return  pause screen
     */
    public Screen getThis() {
        return this;
    }
}
