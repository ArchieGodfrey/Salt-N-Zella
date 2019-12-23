package com.config;

/**
 * Game constants for use by Kroy.
 */
public final class Constants {

    private Constants() {
        // Any constants that need instantiation go here
    }

     // Enums
     public static enum Direction {
        UP,
        UPLEFT,
        UPRIGHT,
        DOWN,
        DOWNLEFT,
        DOWNRIGHT,
        LEFT,
        RIGHT
    }

    public static float DirectionToAngle(Direction dir) {
        switch (dir) {
            case UP:
                return 270;
            case DOWN:
                return 90;
            case LEFT:
                return 0;
            case RIGHT:
                return 180;
            case UPLEFT:
                return 315;
            case UPRIGHT:
                return 225;
            case DOWNLEFT:
                return 45;
            case DOWNRIGHT:
                return 135;
            default:
                return 0;
        }
    }

    // Difficulty settings
    public static final int ETFORTRESS_HEALTH = 1000;
    public static final int FIRESTATION_HEALTH = 1000;
    public static final int FIRETRUCK_HEALTH = 100;

    // Game settings
    public static final String GAME_NAME = "Kroy";
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int MAP_WIDTH = 10000;
    public static final int MAP_HEIGHT = 10000;
    public static final String COLLISION_TILE = "Blocked";

    // Camera settings
    public static final float LERP = 1.1f;

    // Class sizing
    public static final double SCREEN_CENTRE_X = SCREEN_WIDTH * 0.5;
    public static final double SCREEN_CENTRE_Y = SCREEN_HEIGHT * 0.5;
    public static final double SCORE_Y = SCREEN_HEIGHT * 0.95;
    public static final double SCORE_X = SCREEN_WIDTH * 0.05;

    // Sprite sizing
    public static final int SPRITE_WIDTH = 6*32;
    public static final int SPRITE_HEIGHT = 3*32;
    public static final int FIRESTATION_WIDTH = 5*32;
    public static final int FIRESTATION_HEIGHT = 5*32;
    public static final int ETFORTRESS_WIDTH = 5*32;
    public static final int ETFORTRESS_HEIGHT = 5*32;

}