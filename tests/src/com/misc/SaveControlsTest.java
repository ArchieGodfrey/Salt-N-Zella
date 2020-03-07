package com.misc;

import com.misc.SaveControls.SavedFiretruck;
import com.testrunner.GdxTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import com.entities.*;
import com.misc.*;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@RunWith(GdxTestRunner.class)
public class SaveControlsTest {

    private SaveControls saveControls;

    @Mock
    Firestation firestation;

    @Mock
    Firetruck firetruck;

    @Before
    public void setUp() {
        this.saveControls = new SaveControls();
    }

    @Test
    public void saveGameTest() {
        this.saveControls.saveGame(1, firetruck, firestation);
        try {
            Path paths = Paths.get("Save1.json");
            Reader reader = Files.newBufferedReader(paths);
            // TODO
            //assertEquals(reader, new String(`{"empty":false,"firetrucks":[{"type":"BLUE","health":10,"respawnLocation":1.0},{"type":"RED","health":10,"respawnLocation":1.0},{"type":"YELLOW","health":10,"respawnLocation":1.0},{"type":"GREEN","health":10,"respawnLocation":1.0}]}`));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Test
    public void getCurrentSaveNumberTest() {
        assertEquals(this.saveControls.getSavedFiretruck(Constants.TruckType.RED), 0);
    }

    @Test
    public void checkIfSaveEmptyTest() {
        this.saveControls.saveGame(4, firetruck, firestation);
        assertEquals(this.saveControls.checkIfSaveEmpty(4), false);
    }
}
