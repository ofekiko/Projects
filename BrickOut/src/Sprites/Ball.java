//ID: 322374240
//NAME: OFEK KAHARIZI
package Sprites;

import Collision.CollisionInfo;
import Collision.Velocity;
import GameCore.Game;
import GameCore.GameEnvironment;
import Geometry.Line;
import Geometry.Point;
import Interfaces.Sprite;
import biuoop.DrawSurface;

import java.awt.Color;

/**
 * A class that represents a ball in the game.
 * The ball has a center, radius, color, and velocity.
 */
public class Ball implements Sprite {
    private Point center;
    private int r;
    private Color color;
    private Velocity v;
    private GameEnvironment game;

    /**
     * Creates a ball with a given center, radius, and color.
     *
     * @param center The center point of the ball.
     * @param r      The radius of the ball.
     * @param color  The color of the ball.
     * @param game   The game environment of the ball.
     */
    public Ball(Point center, int r, Color color, GameEnvironment game) {
        this.center = center;
        this.r = r;
        this.color = color;
        this.game = game;
    }

    /**
     * Creates a trajectory for the ball based on its velocity.
     *
     * @return A line representing the ball's trajectory.
     */
    public Line trajectory() {
        Point startLine = new Point(this.center.getX(), this.center.getY());
        Point endLine = new Point(this.center.getX() + this.getVelocity().getDX(),
                this.center.getY() + this.getVelocity().getDY());
        return new Line(startLine, endLine);
    }

    /**
     * @return The x-coordinate of the ball's center.
     */
    public int getX() {
        return (int) this.center.getX();
    }

    /**
     * @return The y-coordinate of the ball's center.
     */
    public int getY() {
        return (int) this.center.getY();
    }

    /**
     * @return The radius of the ball.
     */
    public int getSize() {
        return this.r;
    }

    /**
     * @return The color of the ball.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the color of the ball.
     *
     * @param color The color to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Draws the ball on the given surface.
     *
     * @param surface The surface to draw the ball on.
     */
    public void drawOn(DrawSurface surface) {
        surface.setColor(color);
        surface.fillCircle(getX(), getY(), getSize());
        surface.setColor(Color.BLACK);
        surface.drawCircle(getX(), getY(), getSize());
    }

    /**
     * Sets the velocity of the ball.
     *
     * @param v The velocity to set.
     */
    public void setVelocity(Velocity v) {
        this.v = v;
    }

    /**
     * Sets the velocity of the ball using dx and dy.
     *
     * @param dx Horizontal velocity.
     * @param dy Vertical velocity.
     */
    public void setVelocity(double dx, double dy) {
        this.v = new Velocity(dx, dy);
    }

    /**
     * @return The current velocity of the ball.
     */
    public Velocity getVelocity() {
        return this.v;
    }

    /**
     * Moves the ball one step based on its velocity.
     * Checks for collisions and updates position and velocity accordingly.
     */
    public void moveOneStep() {
        CollisionInfo closestCollision = game.getClosestCollision(this.trajectory());
        if (closestCollision == null) {
            this.center = this.getVelocity().applyToPoint(this.center);
        } else {
            this.center = new Point(
                    closestCollision.collisionPoint().getX() - 0.1 * this.getVelocity().getDX(),
                    closestCollision.collisionPoint().getY() - 0.1 * this.getVelocity().getDY());
            this.setVelocity(closestCollision.collisionObject().hit(this, closestCollision.collisionPoint(),
                    this.getVelocity()));
        }
    }

    /**
     * Adds the ball to the game as a sprite.
     *
     * @param game The game to add the ball to.
     */
    public void addToGame(Game game) {
        game.addSprite(this);
    }

    /**
     * removes the ball from the game as a sprite.
     *
     * @param game The game to remove the ball from.
     */
    public void removeFromGame(Game game) {
        game.removeSprite(this);
    }

    /**
     * Updates the ball's behavior over time.
     * Called on every game tick.
     */
    @Override
    public void timePassed() {
        this.moveOneStep();
    }
}
