//ID: 322374240
//NAME: OFEK KAHARIZI
package GameCore;

import Collision.CollisionInfo;
import Geometry.Point;
import Interfaces.Collidable;
import biuoop.DrawSurface;
import Geometry.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * The GameCore.GameEnvironment class represents the environment of the game.
 * It holds a collection of collidable objects and manages collision detection.
 */
public class GameEnvironment {
    private List<Collidable> collidables = new ArrayList<>(); // List of collidable objects

    /**
     * Adds the given collidable object to the environment.
     *
     * @param c The collidable object to add.
     */
    public void addCollidable(Collidable c) {
        collidables.add(c);
    }

    /**
     * Removes the given collidable object to the environment.
     *
     * @param c The collidable object to remove.
     */
    public void removeCollidable(Collidable c) {
        collidables.remove(c);
    }

    /**
     * Checks for the closest collision along a given trajectory.
     * If no collisions occur, returns null. Otherwise, returns information
     * about the closest collision.
     *
     * @param trajectory The line representing the object's movement path.
     * @return The information about the closest collision, or null if no collision occurs.
     */
    public CollisionInfo getClosestCollision(Line trajectory) {
        double minDistance = Double.MAX_VALUE;
        CollisionInfo closestCollisionInfo = new CollisionInfo();

        for (Collidable collidable : collidables) {
            // Check if the trajectory intersects with the collidable
            if (collidable.getCollisionRectangle().intersectionPoints(trajectory).isEmpty()) {
                continue; // No intersection, skip to the next collidable
            } else {
                // Find the closest intersection point
                Point closestPoint = trajectory.closestIntersectionToStartOfLine(collidable.getCollisionRectangle());
                if (closestPoint.distance(trajectory.start()) < minDistance) {
                    minDistance = closestPoint.distance(trajectory.start());
                    closestCollisionInfo.setCollisionPoint(closestPoint);
                    closestCollisionInfo.setCollidableObject(collidable);
                }
            }
        }

        // If no collision object or point is found, return null
        if (closestCollisionInfo.collisionObject() == null && closestCollisionInfo.collisionPoint() == null) {
            return null;
        }

        return closestCollisionInfo; // Return the closest collision info
    }

    /**
     * Draws all the collidable objects in the environment on the given surface.
     *
     * @param d The surface to draw on.
     */
    public void drawAllOn(DrawSurface d) {
        List<Collidable> collidableList = new ArrayList<Collidable>(this.collidables);
        for (Collidable collidable : collidableList) {
            collidable.drawOn(d);
        }
    }
}
