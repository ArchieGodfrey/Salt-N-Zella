package com.sprites;

// LibGDX imports
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

// Constants import
import static com.config.Constants.MAP_HEIGHT;
import static com.config.Constants.MAP_WIDTH;
import static com.config.Constants.COLLISION_TILE;
import static com.config.Constants.Direction;
import static com.config.Constants.DirectionToAngle;

/**
 * MovementSprite adds movement facilities to a sprite.
 * @author Archie
 * @since 08/12/2019
 */
public class MovementSprite extends SimpleSprite {

    // Private values to be used in this class only
    private Direction direction;
    private float accelerationRate = 20f, speedX = 0f, speedY = 0f, bounce = 1f;
    private TiledMapTileLayer collisionLayer;

    /**
     * Constructors for this class, gathers required information so that it can be drawn.
     * @param spriteBatch    The batch that the sprite should be drawn on.
     * @param spriteTexture  The texture the sprite should use.
     * @param collisionLayer The layer of the map the sprite will collide with.
     */
    public MovementSprite(Batch spriteBatch, Texture spriteTexture, TiledMapTileLayer collisionLayer) {
        super(spriteBatch, spriteTexture);
        this.direction = Direction.UP;
        this.collisionLayer = collisionLayer;
    }

    /**
     * Overload constructor for this class, taking a position to draw the sprite at.
     * 
     * @param spriteBatch    The batch that the sprite should be drawn on.
     * @param spriteTexture  The texture the sprite should use.
     * @param collisionLayer The layer of the map the sprite will collide with.
     * @param xPos           The x-coordinate for the sprite to be drawn.
     * @param yPos           The y-coordinate for the sprite to be drawn.
     */
    public MovementSprite(Batch spriteBatch, Texture spriteTexture, float xPos, float yPos, TiledMapTileLayer collisionLayer) {
        super(spriteBatch, spriteTexture, xPos, yPos);
        this.direction = Direction.UP;
        this.collisionLayer = collisionLayer;
    }

    /**
     * Update the sprite position and direction based on acceleration and
     * boundaries. This is called every game frame.
     */
    public void update() {
        // Calculate the acceleration on the sprite and apply it
        applyAcceleration();
        // Set the sprites direction based on its speed
        setDirection(getDirectionFromSpeed());
        // Rotate sprite to face the direction its moving in
        updateRotation();
        // Check the sprite is within the map boundaries then draw
        checkBoundaries();
        // Draw the sprite at the new location
        super.update();
    }

    /**
     * Calculate the sprite's rotation from its speed
     */
    private void updateRotation() {
        float currentRotation = this.getRotation(), desiredRotation = DirectionToAngle(this.direction);
        if (currentRotation != desiredRotation) {
            float difference = desiredRotation - currentRotation;
            // Choose the shortest angle
            float adjustment = difference >= 200 ? -(360 - difference): difference;
            this.rotate(adjustment * 3 * Gdx.graphics.getDeltaTime());
        }
        switch (this.direction) {
            case UP:
                this.setSize(this.getWidth(), this.getHeight());
            case DOWN:
                this.setSize(this.getWidth(), this.getHeight());
            default:
                this.setSize(this.getHeight(), this.getWidth());
        }
    }

    /**
     * Calculate the sprite's direction from its speed
     * @return The direction the sprite is travelling in
     */
    private Direction getDirectionFromSpeed() {
        boolean left = this.speedX < 0, right = this.speedX > 0, up = this.speedY > 0,down = this.speedY < 0;
        boolean vertical = up || down, horizontal = left || right;
        if (vertical) {
            if (up && horizontal) {
                return left ? Direction.UPLEFT : Direction.UPRIGHT;
            } else if (down && horizontal) {
                return left ? Direction.DOWNLEFT : Direction.DOWNRIGHT;
            } else if (up) {
                return Direction.UP;
            }
            return Direction.DOWN;
        } else if (horizontal) {
            return left ? Direction.LEFT : Direction.RIGHT;
        }
        // If stationary return last direction
        return this.direction;
    }

    /**
     * Apply acceleration to the sprite, based on collision boundaries and
     * existing acceleration.
     */
    private void applyAcceleration() {
        // Calculate whether it hits any boundaries
        // Do this once here rather than multiple times in code
        boolean hitLeft = collidesLeft();
        boolean hitRight = collidesRight();
        boolean hitTop = collidesTop();
        boolean hitBottom = collidesBottom();
        // Apply acceleration and check if it collides with any tiles
        if (!hitLeft && this.speedX < 0) {
            setX(getX() + this.speedX * Gdx.graphics.getDeltaTime());
        } else if (hitLeft) {
            collisionOccurred();
        }
        if (!hitRight && this.speedX > 0) {
            setX(getX() + this.speedX * Gdx.graphics.getDeltaTime());
        } else if (hitRight) {
            collisionOccurred();
        }
        if (!hitTop && this.speedY > 0) {
            setY(getY() + this.speedY * Gdx.graphics.getDeltaTime());
        } else if (hitTop) {
            collisionOccurred();
        }
        if (!hitBottom && this.speedY < 0) {
            setY(getY() + this.speedY * Gdx.graphics.getDeltaTime());
        } else if (hitBottom) {
            collisionOccurred();
        }
        if (this.speedY != 0f || this.speedX != 0f) {
            decelerate();
        }
    }

    /**
     * Checks what direction the sprite is facing and bounces it the opposite way
     */
    public void collisionOccurred() {
        int knockback = 2;
        switch (this.direction) {
            case UP:
                this.setY(this.getY() - knockback);
                this.speedY *= -this.bounce;
            case DOWN:
                this.setY(this.getY() + knockback);
                this.speedY *= this.bounce;
            case LEFT:
                this.setX(this.getX() + knockback);
                this.speedX *= this.bounce;
            case RIGHT:
                this.setX(this.getX() - knockback);
                this.speedX *= -this.bounce;
            case UPLEFT:
                this.setY(this.getY() - knockback);
                this.speedY *= -this.bounce;
                this.setX(this.getX() + knockback);
                this.speedX *= this.bounce;
            case UPRIGHT:
                this.setY(this.getY() - knockback);
                this.speedY *= -this.bounce;
                this.setX(this.getX() - knockback);
                this.speedX *= -this.bounce;
            case DOWNLEFT:
                this.setY(this.getY() + knockback);
                this.speedY *= this.bounce;
                this.setX(this.getX() + knockback);
                this.speedX *= this.bounce;
            case DOWNRIGHT:
                this.setY(this.getY() + knockback);
                this.speedY *= this.bounce;
                this.setX(this.getX() - knockback);
                this.speedX *= -this.bounce;
        }
    }

    /**
     * Checks if the tile at a location is a "blocked" tile or not.
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return Whether the sprite can enter the tile (true) or not (false).
     */
    private boolean isCellBlocked(float x, float y) {
        Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
		return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey(COLLISION_TILE);
	}

    /**
     * Checks all tiles the sprite will cover in the rightward direction to see
     * if they are "blocked". Steps through each tile, with step length
     * determined by the size of the sprite.
     * @return Whether any tiles on route are blocked (true) or no blockages (false).
     */
	private boolean collidesRight() {
		for(float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
			if(isCellBlocked(getX() + getWidth(), getY() + step))
				return true;
		return false;
	}

    /**
     * Checks all tiles the sprite will cover in the leftward direction to see if
     * they are "blocked". Steps through each tile, with step length determined by
     * the size of the sprite.
     * 
     * @return Whether any tiles on route are blocked (true) or no blockages
     *         (false).
     */
	private boolean collidesLeft() {
		for(float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
			if(isCellBlocked(getX(), getY() + step))
				return true;
		return false;
	}

    /**
     * Checks all tiles the sprite will cover in the upward direction to see if
     * they are "blocked". Steps through each tile, with step length determined by
     * the size of the sprite.
     * 
     * @return Whether any tiles on route are blocked (true) or no blockages
     *         (false).
     */
	private boolean collidesTop() {
		for(float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2)
			if(isCellBlocked(getX() + step, getY() + getHeight()))
				return true;
		return false;

	}

    /**
     * Checks all tiles the sprite will cover in the downward direction to see if
     * they are "blocked". Steps through each tile, with step length determined by
     * the size of the sprite.
     * 
     * @return Whether any tiles on route are blocked (true) or no blockages
     *         (false).
     */
	private boolean collidesBottom() {
		for(float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2)
			if(isCellBlocked(getX() + step, getY()))
				return true;
		return false;
	}
    
    /**
     * Ensures the sprite stays within the bounds set by the map.
     */
    private void checkBoundaries() {
        if (getY() < 0)
            setY(0);
        if (getY() > MAP_HEIGHT - this.getHeight())
            setY(MAP_HEIGHT - this.getHeight());
        if (getX() < 0)
            setX(0);
        if (getX() > MAP_WIDTH - this.getWidth())
            setX(MAP_WIDTH - this.getWidth());
    }

    /**
     * Increases the speed of the sprite in the given direction.
     * @param direction The direction to accelerate in.
     */
    public void accelerate(Direction direction) {
        float maxSpeed = 300f;
        if (this.speedY < maxSpeed && direction == Direction.UP) {
            this.speedY += this.accelerationRate;
        }
        if (this.speedY > -maxSpeed && direction == Direction.DOWN) {
            this.speedY -= this.accelerationRate;
        }
        if (this.speedX < maxSpeed && direction == Direction.RIGHT) {
            this.speedX += this.accelerationRate;
        }
        if (this.speedX > -maxSpeed && direction == Direction.LEFT) {
            this.speedX -= this.accelerationRate;
        }
    }

    /**
     * Decreases the speed of the sprite (direction irrelevant). Deceleration rate
     * is based upon the sprite's properties.
     */
    private void decelerate() {
        float decelerationRate = this.accelerationRate * 0.5f;
        float restThreshold = this.accelerationRate;
        // Check the direction the sprite is moving based on its velocity
        if (this.speedY > 0) {
            // If within a threshold stop the spirte
            // Stops it bouncing from decelerating in one direction and then another etc..
            if (this.speedY < restThreshold) {
                this.speedY = 0f;
            } else {
                this.speedY -= decelerationRate;
            }
        } else {
            if (this.speedY > -restThreshold) {
                this.speedY = 0f;
            } else {
                this.speedY += decelerationRate;
            }
        }
        // Repeat for the x axis
        if (this.speedX > 0) {
            if (this.speedX < restThreshold) {
                this.speedX = 0f;
            } else {
                this.speedX -= decelerationRate;
            }
        } else {
            if (this.speedX > -restThreshold) {
                this.speedX = 0f;
            } else {
                this.speedX += decelerationRate;
            }
        }
    }

    /**
     * Sets the current direction of the sprite.
     * @param dir The direction for the sprite.
     */
    public void setDirection(Direction dir) {
        this.direction = dir;
    }

    /**
     * Gets the current direction of the sprite.
     * @return The direction of the sprite.
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Sets the current speed of the sprite in the X axis.
     * @param speed The speed the sprite should travel.
     */
    public void setSpeedX(Float speed) {
        this.speedX = speed;
    }

    /**
     * Gets the current speed of the sprite in the X axis.
     * @return The current speed of the sprite in the X axis.
     */
    public float getSpeedX() {
        return this.speedX;
    }

    /**
     * Sets the current speed of the sprite in the Y axis.
     * @param speed The speed the sprite should travel.
     */
    public void setSpeedY(Float speed) {
        this.speedY = speed;
    }

    /**
     * Gets the current speed of the sprite in the Y axis.
     * @return The current speed of the sprite in the Y axis.
     */
    public float getSpeedY() {
        return this.speedY;
    }
}