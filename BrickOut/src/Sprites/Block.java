//ID: 322374240
//NAME: OFEK KAHARIZI
package Sprites;

import Collision.Velocity;
import GameCore.Game;
import Geometry.Point;
import Geometry.Rectangle;
import Interfaces.Collidable;
import Interfaces.HitListener;
import Interfaces.HitNotifier;
import Interfaces.Sprite;
import biuoop.DrawSurface;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The Sprites.Block class represents a block in the game.
 * It can detect collisions, respond to them, and draw itself on the screen.
 */
public class Block implements Sprite, Collidable, HitNotifier {
    private Rectangle bounds; // The rectangle representing the block
    private Color color;
    private final List<HitListener> hitListeners = new ArrayList<>();

    /**
     * Constructor to create a block with the given rectangle.
     *
     * @param bounds The rectangle representing the block.
     */
    public Block(Rectangle bounds) {
        this.bounds = bounds;
    }

    private void notifyHit(Ball hitter) {
        // Make a copy of the hitListeners before iterating over them.
        List<HitListener> listeners = new ArrayList<HitListener>(this.hitListeners);
        // Notify all listeners about a hit event:
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    /**
     * Gets the rectangle representing the block.
     *
     * @return The block's rectangle.
     */
    public Rectangle getCollisionRectangle() {
        return bounds;
    }

    /**
     * Sets the block color.
     *
     * @param color the color the function sets.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the block color.
     *
     * @return The block's color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * checks if the color of this block and the color of a ball match.
     *
     * @param ball the ball that needs to be checked.
     * @return is the ball color and the block color match.
     */
    public Boolean ballColorMatch(Ball ball) {
        return this.getColor().equals(ball.getColor());
    }

    @Override
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    @Override
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * Handles a hit event when the ball collides with the block.
     * Adjusts the velocity based on the collision point.
     * removes the ball that has a different color from the block
     *
     * @param collisionPoint  The point where the collision occurred.
     * @param currentVelocity The current velocity of the ball.
     * @param hitter          the ball which the color needs to be checked and removed.
     * @return The new velocity after the collision.
     */
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        double threshold = 0.0000001; // Small value to handle floating-point precision
        Point startingPoint = this.bounds.getUpperLeft();
        double leftX = startingPoint.getX();
        double rightX = startingPoint.getX() + this.bounds.getWidth();
        double topY = startingPoint.getY();
        double bottomY = startingPoint.getY() + this.bounds.getHeight();

        // Check for collisions with corners or sides and adjust velocity
        if (Math.abs(collisionPoint.getX() - leftX) < threshold
                && Math.abs(collisionPoint.getY() - topY) < threshold) {
            currentVelocity.setDX(-Math.abs(currentVelocity.getDX()));
            currentVelocity.setDY(-Math.abs(currentVelocity.getDY()));

        } else if (Math.abs(collisionPoint.getX() - rightX) < threshold
                && Math.abs(collisionPoint.getY() - topY) < threshold) {
            currentVelocity.setDX(Math.abs(currentVelocity.getDX()));
            currentVelocity.setDY(-Math.abs(currentVelocity.getDY()));

        } else if (Math.abs(collisionPoint.getX() - leftX) < threshold
                && Math.abs(collisionPoint.getY() - bottomY) < threshold) {
            currentVelocity.setDX(-Math.abs(currentVelocity.getDX()));
            currentVelocity.setDY(Math.abs(currentVelocity.getDY()));

        } else if (Math.abs(collisionPoint.getX() - rightX) < threshold
                && Math.abs(collisionPoint.getY() - bottomY) < threshold) {
            currentVelocity.setDX(Math.abs(currentVelocity.getDX()));
            currentVelocity.setDY(Math.abs(currentVelocity.getDY()));

        } else if (Math.abs(collisionPoint.getX() - leftX) < threshold) {
            currentVelocity.setDX(-Math.abs(currentVelocity.getDX()));

        } else if (Math.abs(collisionPoint.getX() - rightX) < threshold) {
            currentVelocity.setDX(Math.abs(currentVelocity.getDX()));

        } else if (Math.abs(collisionPoint.getY() - topY) < threshold) {
            currentVelocity.setDY(-Math.abs(currentVelocity.getDY()));

        } else if (Math.abs(collisionPoint.getY() - bottomY) < threshold) {
            currentVelocity.setDY(Math.abs(currentVelocity.getDY()));

        }
        if (!ballColorMatch(hitter)) {
            this.notifyHit(hitter);
        }
        this.setColor(hitter.getColor());
        return currentVelocity;
    }

    /**
     * Adds this block to the game as a sprite and a collidable object.
     *
     * @param game The game to add the block to.
     */
    public void addToGame(Game game) {
        game.addCollidable(this);
        game.addSprite(this);
    }

    /**
     * Removes this block from the game as a sprite and a collidable object.
     *
     * @param game The game to remove the block from.
     */
    public void removeFromGame(Game game) {
        game.removeCollidable(this);
        game.removeSprite(this);
    }

    /**
     * Draws the block on the given surface.
     * The block's color depends on its position.
     *
     * @param d The surface to draw on.
     */
    public void drawOn(DrawSurface d) {
        if (this.bounds.getUpperLeft().getY() == 30) {
            this.setColor(Color.YELLOW);
            d.setColor(this.getColor());
        } else if (this.bounds.getUpperLeft().getY() == 55) {
            this.setColor(Color.RED);
            d.setColor(this.getColor());
        } else if (this.bounds.getUpperLeft().getY() == 80) {
            this.setColor(Color.GREEN);
            d.setColor(this.getColor());
        } else if (this.bounds.getUpperLeft().getY() == 105) {
            this.setColor(Color.ORANGE);
            d.setColor(this.getColor());
        } else if (this.bounds.getUpperLeft().getY() == 130) {
            this.setColor(Color.BLUE);
            d.setColor(this.getColor());
        } else {
            this.setColor(Color.GRAY);
            d.setColor(this.getColor());
        }
        d.setColor(color);
        Rectangle block = this.bounds;
        d.fillRectangle((int) block.getUpperLeft().getX(), (int) block.getUpperLeft().getY(),
                (int) block.getWidth(), (int) block.getHeight());
        d.setColor(Color.BLACK);
        d.drawRectangle((int) block.getUpperLeft().getX(), (int) block.getUpperLeft().getY(),
                (int) block.getWidth(), (int) block.getHeight());
    }

    /**
     * Called every frame to update the block.
     * (No behavior for blocks in this implementation.)
     */
    @Override
    public void timePassed() {
        // No updates needed for a block.
    }
}
