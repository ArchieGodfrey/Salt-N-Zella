package com.misc;

/* =================================================================
                   New class added for assessment 4
   ===============================================================*/

// Java IO imports
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

// Class imports
import com.entities.ETFortress;
import com.entities.Firestation;
import com.entities.Firetruck;
import com.google.gson.Gson;

/**
 * A class to store the functions required to save objects in the main game.
 *
 * @author Archie
 * @since 6/03/2020
 */
public class SaveControls {

    // The current save file in use
    // If no save files loaded, it will be 0
    private int currentSaveNumber;
    // An instance of Google's JSON module
    private Gson gson;
    // The current loaded save file
    private SaveFile currentSaveFile;

    /**
     * The format of a save file
     */
    public class SaveFile {
        // Whether the file is empty or not
        public boolean empty;
        // The firetrucks stored in the save file
        public ArrayList<SavedFiretruck> firetrucks;

        // Default constructor, creates an empty save file
        public SaveFile() {
            this.empty = true;
        };
        // Overloaded constructor, creates a full save file
        public SaveFile(ArrayList<SavedFiretruck> firetrucks) {
            this.firetrucks = firetrucks;
            this.empty = false;
        }
    }

    /** 
     * A 'barebones' Firetruck class to only store the
    *  properties needed to re-create the class
    **/
    public class SavedFiretruck {
        // The idetifier of the truck
        public Constants.TruckType type;
        // The saved health
        public int health;
        // The saved respawn location
        public float respawnLocation;

        // Default constructor, creates an empty save file
        public SavedFiretruck() {};
        // Overloaded constructor, creates a full firetruck save
        public SavedFiretruck(Constants.TruckType type, int health, float respawnLocation) {
            this.health = health;
            this.respawnLocation = respawnLocation;
            this.type = type;
        }
    }

    /**
     * Creates a new Gson instance and creates a fresh save file
     * setting the current save to 0
     */
    public SaveControls() {
        this.gson = new Gson();
        this.currentSaveFile = new SaveFile();
        this.currentSaveNumber = 0;
    }

    /**
     * The one function to save the entire game in a selected filed.
     * @param saveNumber    The number of the save file to store the JSON in
     * @param activeTruck   The active firetruck to be converted
     * @param firestation   The firestation to be converted
     */
    public void saveGame(int saveNumber, Firetruck activeTruck, Firestation firestation/* , ArrayList<ETFortress> ETFortresses */) {
        // Create an array to store all trucks to be saved
        ArrayList<SavedFiretruck> savedFiretrucks = new ArrayList<SavedFiretruck>();
        // Add the active truck to the list
        savedFiretrucks.add(new SavedFiretruck(activeTruck.getType(), 10, 1));
        // Add the remaining trucks to the list
        for (Firetruck firetruck : firestation.getParkedFireTrucks()) {
            savedFiretrucks.add(new SavedFiretruck(firetruck.getType(), 10, 1));
        }
        // Create a new save file
        SaveFile saveFile = new SaveFile(savedFiretrucks);
        // Write the save file object to a JSON file
        writeSaveToFile(saveNumber, saveFile);
    }

    /**
     * Converts the save file object to a JSON file.
     * @param saveNumber    The index of the save file to store the data in
     * @param saveFile      The save file object to be converted
     */
    private void writeSaveToFile(int saveNumber, SaveFile saveFile) {
        try (Writer writer = new FileWriter("Save" + saveNumber + ".json")) {
            this.gson.toJson(saveFile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a save file object from data stored in a JSON file
     * @param saveNumber    The index of the save file to retrieve the data from
     * @return              A new save file object created from the JSON file
     * @throws IOException  Thrown when failing to access a save file
     */
    private SaveFile readSaveFromFile(int saveNumber) throws IOException {
        // Try find the file
        Path paths = Paths.get("Save" + saveNumber + ".json");
        Reader reader = Files.newBufferedReader(paths);
        // convert JSON string to SaveFile object
        SaveFile savefile = this.gson.fromJson(reader,SaveFile.class);
        // Close reader
        reader.close();
        // Return save file
        return savefile;
    }

    /**
     * Attempts to load a save file. If it fails to open/find
     * a save file it defaults to a new save file
     * @param saveNumber The index of the save file to load
     */
    public void loadFromSave(int saveNumber) {
        try {
            this.currentSaveFile = readSaveFromFile(saveNumber);
            this.currentSaveNumber = saveNumber;
        } catch (IOException e) {
            this.currentSaveFile = new SaveFile();
            this.currentSaveNumber = 0;
        }
    }

    /**
     * Gets the index of the save file currently used. Will be 0
     * if no save is loaded.
     * @return The index of the save file currently loaded
     */
    public int getCurrentSaveNumber() {
        return this.currentSaveNumber;
    }

    /**
     * Checks if a save file contains any data
     * @param saveNumber The save number to check
     * @return Whether the save file is empty (true) or not (false)
     */
    public boolean checkIfSaveEmpty(int saveNumber) {
        SaveFile toBeChecked;
        try {
            toBeChecked = readSaveFromFile(saveNumber);
        } catch (IOException e) {
            toBeChecked = new SaveFile();
        }
        return toBeChecked.empty;
    }

    /**
     * Gets a saved firetruck given its type
     * @param type  The type of firetruck to retrieve
     * @return      The saved firetruck
     */
    public SavedFiretruck getSavedFiretruck(Constants.TruckType type) {
        for (SavedFiretruck firetruck : this.currentSaveFile.firetrucks) {
            if (firetruck.type == type) {
                return firetruck;
            }
        }
        return this.currentSaveFile.firetrucks.get(0);
    }
}