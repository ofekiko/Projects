//ID: 322374240
//NAME: OFEK KAHARIZI
package Interfaces;

import Collision.Velocity;
import Geometry.Point;
import Geometry.Rectangle;
import Sprites.Ball;
import biuoop.DrawSurface;

/**
 * The Interfaces.Collidable interface represents objects that can be collided with.
 * It provides methods to retrieve the object's collision shape, respond to collisions,
 * and render the object on a given drawing surface.
 */
public interface Collidable {

    /**
     * Returns the "collision shape" of the object.
     * The collision shape is represented as a rectangle that defines the boundaries of the object.
     *
     * @return A Geometry.Rectangle representing the collision shape of the object.
     */
    Rectangle getCollisionRectangle();

    /**
     * Notifies the object that a collision has occurred at a specific point
     * with a given velocity. The object can calculate and return the new velocity
     * based on the collision forces it applies.
     *
     * @param collisionPoint  The point where the collision occurred.
     * @param currentVelocity The velocity of the object before the collision.
     * @param hitter the ball that needs to change the color
     * @return The new Collision.Velocity of the object after the collision.
     */
    Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity);

    /**
     * Draws the object on the given drawing surface.
     * This method is responsible for rendering the object visually.
     *
     * @param d The DrawSurface to draw the object on.
     */
    void drawOn(DrawSurface d);
}
