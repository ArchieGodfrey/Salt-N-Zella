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
        // The name of the save file
        public String name;
        // Whether the file is empty or not
        public boolean empty;
        // The saved game score
        public int score;
        // The saved time
        public int time;
        // The saved game difficulty
        public int difficulty;
        // The saved firestation health
        public int firestationHealth;
        // The saved active firetruck type
        public Constants.TruckType activeFireTruckType;
        // The firetrucks stored in the save file
        public ArrayList<SavedFiretruck> firetrucks;
        // The ETFortresses stored in the save file
        public ArrayList<SavedFortress> ETFortresses;

        // Default constructor, creates an empty save file
        public SaveFile() {
            this.empty = true;
        };
        // Overloaded constructor, creates a full save file
        public SaveFile(String name, int score, int time, int difficulty, int firestationHealth, Constants.TruckType activeFireTruckType, ArrayList<SavedFiretruck> firetrucks, ArrayList<SavedFortress> ETFortresses) {
            this.firetrucks = firetrucks;
            this.empty = false;
            this.name = name;
            this.score = score;
            this.time = time;
            this.difficulty = difficulty;
            this.activeFireTruckType = activeFireTruckType;
            this.ETFortresses = ETFortresses;
            this.firestationHealth = firestationHealth;
        }
    }

    /** 
     * A 'barebones' Firetruck class to only store the
     * properties needed to re-create the class
    **/
    public class SavedFiretruck {
        // The idetifier of the truck
        public Constants.TruckType type;
        // The saved health
        public int health;
        // The saved water
        public int water;
        // The saved respawn location
        public Constants.CarparkEntrances respawnLocation;
        // The saved x position
        public float x;
        // The saved y position
        public float y;
        // If the truck has been purchased
        public boolean isBought;

        // Default constructor, creates an empty firetruck save
        public SavedFiretruck() {};
        // Overloaded constructor, creates a full firetruck save
        public SavedFiretruck(Constants.TruckType type, int health, int water, Constants.CarparkEntrances respawnLocation, float x, float y, boolean isBought) {
            this.health = health;
            this.water = water;
            this.respawnLocation = respawnLocation;
            this.type = type;
            this.x = x;
            this.y = y;
            this.isBought = isBought;
        }
    }

    /** 
     * A 'barebones' ETFortress class to only store the
     * properties needed to re-create the class
    **/
    public class SavedFortress {
        // The identifier of the fortress
        public Constants.FortressType type;
        // The saved health
        public int health;
        // If the fortress is flooded
        public boolean flooded;

        // Default constructor, creates an empty fortress save
        public SavedFortress() {};
        // Overloaded constructor, creates a full fortress save
        public SavedFortress(Constants.FortressType type, int health, boolean flooded) {
            this.health = health;
            this.type = type;
            this.flooded = flooded;
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
     * The one function to save the entire game in a selected file.
     * @param saveNumber    The number of the save file to store the JSON in
     * @param score         The game score to be converted
     * @param time          The game time to be converted
     * @param difficulty    The game difficulty to be converted
     * @param activeTruck   The active firetruck to be converted
     * @param firestation   The firestation to be converted
     * @param ETFortresses  The ETFortresses to be converted
     */
    public void saveGame(int saveNumber, int score, int time, int difficulty, Firestation firestation, ArrayList<ETFortress> ETFortresses) {
        // Create an array to store all trucks to be saved
        ArrayList<SavedFiretruck> savedFiretrucks = new ArrayList<SavedFiretruck>();
        // Gets the active truck
        Firetruck activeTruck = firestation.getActiveFireTruck();
        // Add the active truck to the list
        savedFiretrucks.add(new SavedFiretruck(
            activeTruck.getType(),
            activeTruck.getHealthBar().getCurrentAmount(),
            activeTruck.getWaterBar().getCurrentAmount(),
            activeTruck.getCarpark(),
            activeTruck.getX(), activeTruck.getY(),
            activeTruck.isBought()
        ));
        // Add the remaining trucks to the list
        for (Firetruck firetruck : firestation.getParkedFireTrucks()) {
            savedFiretrucks.add(new SavedFiretruck(
                firetruck.getType(),
                firetruck.getHealthBar().getCurrentAmount(),
                firetruck.getWaterBar().getCurrentAmount(),
                firetruck.getCarpark(),
                firetruck.getX(), firetruck.getY(), 
                firetruck.isBought()
            ));
        }
        // Create an array to store all ETFortresses to be saved
        ArrayList<SavedFortress> savedFortresses = new ArrayList<SavedFortress>();
        // Add the fortresses to the list
        for (ETFortress ETFortress : ETFortresses) {
            savedFortresses.add(new SavedFortress(
                ETFortress.getType(),
                ETFortress.getHealthBar().getCurrentAmount(),
                ETFortress.isFlooded()
            ));
        }
        // Create a new save file
        SaveFile saveFile = new SaveFile(
            getSaveName(saveNumber), score, time, difficulty,
            firestation.getHealthBar().getCurrentAmount(),
            activeTruck.getType(),
            savedFiretrucks, savedFortresses
        );
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
     * Attempts to delete a save file. If deleting the save file in use,
     * set the currentSaveNumber to 0 to start a new game on reload
     * @param saveNumber The index of the save file to delete
     */
    public void deleteSave(int saveNumber) {
        if (saveNumber == this.currentSaveNumber) {
            this.currentSaveNumber = 0;
        }
        Path paths = Paths.get("Save" + saveNumber + ".json");
        try {
            Files.deleteIfExists(paths);
        } catch (IOException e) {
            System.out.println("Unable to delete file");
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
     * Sets the index of the save file currently used.
     * @param saveNumber The index of the save file currently loaded
     */
    public void setCurrentSaveNumber(int saveNumber) {
        this.currentSaveNumber = saveNumber;
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
     * Gets the name of a save file
     * @param saveNumber The save number to check
     * @return           The name of the savefile
     */
    public String getSaveName(int saveNumber) {
        try {
            SaveFile save = readSaveFromFile(saveNumber);
            return save.name;
        } catch (IOException e) {
            System.out.println("Unable to read save name");
            return "Save " + saveNumber;
        }
    }

    /**
     * Sets the name of a save file
     * @param saveNumber The save number to change
     * @param name       The name of the savefile
     */
    public void setSaveName(int saveNumber, String name) {
        try {
            SaveFile saveFile = readSaveFromFile(saveNumber);
            System.out.println(saveFile.name);
            saveFile.name = name;
            writeSaveToFile(saveNumber, saveFile);
        } catch (IOException e) {
            System.out.println("Unable to change save name, creating empty file");
            SaveFile saveFile = new SaveFile();
            saveFile.name = name;
            writeSaveToFile(saveNumber, saveFile);
        }
    }

    /**
     * Gets the current loaded save file
     * @return      The current savefile
     */
    public SaveFile getSaveFile() {
        return this.currentSaveFile;
    }

    /**
     * Gets the saved game difficulty
     * @return      The saved difficulty
     */
    public int getSavedDifficulty() {
        return this.currentSaveFile.difficulty;
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

    /**
     * Gets a saved fortress given its type
     * @param type  The type of fortress to retrieve
     * @return      The saved fortress
     */
    public SavedFortress getSavedFortress(Constants.FortressType type) {
        for (SavedFortress ETFortress : this.currentSaveFile.ETFortresses) {
            if (ETFortress.type == type) {
                return ETFortress;
            }
        }
        return this.currentSaveFile.ETFortresses.get(0);
    }
}