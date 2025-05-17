//ID: 322374240
//NAME: OFEK KAHARIZI
package Sprites;

import Collision.Velocity;
import GameCore.Game;
import Geometry.Point;
import Geometry.Rectangle;
import Interfaces.Collidable;
import Interfaces.Sprite;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;

import java.awt.Color;

/**
 * A paddle controlled by the player, which can move left and right.
 * It also interacts with the ball during the game.
 */
public class Paddle implements Sprite, Collidable {
    private Rectangle bounds;
    private KeyboardSensor keyboard;

    /**
     * Creates a paddle with the given position and keyboard sensor.
     *
     * @param bounds   The rectangle defining the paddle's position and size.
     * @param keyboard The keyboard sensor to control the paddle.
     */
    public Paddle(Rectangle bounds, KeyboardSensor keyboard) {
        this.bounds = bounds;
        this.keyboard = keyboard;
    }

    /**
     * Moves the paddle left if it's not at the screen edge.
     */
    public void moveLeft() {
        if (this.bounds.getUpperLeft().getX() < 30) {
            this.bounds = new Rectangle(new Point(670, 580), 100, 10);
        }
        this.bounds.getUpperLeft().setX(bounds.getUpperLeft().getX() - 5);
    }

    /**
     * Moves the paddle right if it's not at the screen edge.
     */
    public void moveRight() {
        if (this.bounds.getUpperLeft().getX() + bounds.getWidth() > 770) {
            this.bounds = new Rectangle(new Point(30, 580), 100, 10);
        }
        this.bounds.getUpperLeft().setX(bounds.getUpperLeft().getX() + 5);
    }

    /**
     * Updates the paddle based on the keys pressed.
     */
    public void timePassed() {
        if (keyboard.isPressed(KeyboardSensor.LEFT_KEY)) {
            moveLeft();
        } else if (keyboard.isPressed(KeyboardSensor.RIGHT_KEY)) {
            moveRight();
        }
    }

    /**
     * Draws the paddle on the given surface.
     *
     * @param d The surface to draw on.
     */
    public void drawOn(DrawSurface d) {
        d.setColor(Color.YELLOW);
        Rectangle block = this.bounds;
        d.fillRectangle((int) block.getUpperLeft().getX(), (int) block.getUpperLeft().getY(),
                (int) block.getWidth(), (int) block.getHeight());
        d.setColor(Color.BLACK);
        d.drawRectangle((int) block.getUpperLeft().getX(), (int) block.getUpperLeft().getY(),
                (int) block.getWidth(), (int) block.getHeight());
    }

    /**
     * Returns the rectangle defining the paddle's position and size.
     *
     * @return The collision rectangle.
     */
    public Rectangle getCollisionRectangle() {
        return this.bounds;
    }

    /**
     * Checks if a value is between two bounds.
     *
     * @param bound1 The lower bound.
     * @param num    The value to check.
     * @param bound2 The upper bound.
     * @return True if the value is between the bounds, false otherwise.
     */
    public boolean isBetween(double bound1, double num, double bound2) {
        return num >= bound1 && num < bound2;
    }

    /**
     * Changes the ball's velocity based on where it hits the paddle.
     *
     * @param collisionPoint  The point where the ball hit.
     * @param currentVelocity The current velocity of the ball.
     * @param hitter the ball which the color needs to be checked and removed.
     * @return The new velocity after the hit.
     */
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity) {
        double threshold = 0.0000001; // Small value to handle floating-point precision
        Point startingPoint = this.bounds.getUpperLeft();
        double leftX = startingPoint.getX();
        double rightX = startingPoint.getX() + this.bounds.getWidth();
        double topY = startingPoint.getY();
        double bottomY = startingPoint.getY() + this.bounds.getHeight();
        double width = this.bounds.getWidth();
        if (isBetween(leftX, collisionPoint.getX(), leftX + width / 5)
                && collisionPoint.getY() == topY) {
            return Velocity.fromAngleAndSpeed(210, 5);
        } else if (isBetween(leftX + width / 5, collisionPoint.getX(), leftX + width * 2 / 5)
                && collisionPoint.getY() == topY) {
            return Velocity.fromAngleAndSpeed(240, 5);
        } else if (isBetween(leftX + width * 3 / 5, collisionPoint.getX(), leftX + width * 4 / 5)
                && collisionPoint.getY() == topY) {
            return Velocity.fromAngleAndSpeed(300, 5);
        } else if (isBetween(leftX + width * 4 / 5, collisionPoint.getX(), leftX + width)
                && collisionPoint.getY() == topY) {
            return Velocity.fromAngleAndSpeed(330, 5);
        } else if (Math.abs(collisionPoint.getX() - leftX) < threshold) {
            currentVelocity.setDX(-Math.abs(currentVelocity.getDX()));

        } else if (Math.abs(collisionPoint.getX() - rightX) < threshold) {
            currentVelocity.setDX(Math.abs(currentVelocity.getDX()));

        } else if (Math.abs(collisionPoint.getY() - topY) < threshold) {
            currentVelocity.setDY(-Math.abs(currentVelocity.getDY()));

        } else if (Math.abs(collisionPoint.getY() - bottomY) < threshold) {
            currentVelocity.setDY(Math.abs(currentVelocity.getDY()));
        }
        return currentVelocity;
    }

    /**
     * Adds the paddle to the game as both a sprite and a collidable.
     *
     * @param g The game to add the paddle to.
     */
    public void addToGame(Game g) {
        g.addSprite(this);
        g.addCollidable(this);
    }
}
