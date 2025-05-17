//ID: 322374240
//NAME: OFEK KAHARIZI
package Collision;

import Geometry.Point;
import Interfaces.Collidable;

/**
 * The Collision.CollisionInfo class represents information about a collision.
 * It includes the collision point and the collidable object involved.
 */
public class CollisionInfo {
    private Point point;         // The point of collision
    private Collidable collidable; // The object involved in the collision

    /**
     * Gets the point where the collision occurs.
     *
     * @return The collision point.
     */
    public Point collisionPoint() {
        return this.point;
    }

    /**
     * Gets the collidable object involved in the collision.
     *
     * @return The collidable object.
     */
    public Collidable collisionObject() {
        return this.collidable;
    }

    /**
     * Sets the collision point.
     *
     * @param point The collision point to set.
     */
    public void setCollisionPoint(Point point) {
        this.point = point;
    }

    /**
     * Sets the collidable object involved in the collision.
     *
     * @param collidable The collidable object to set.
     */
    public void setCollidableObject(Collidable collidable) {
        this.collidable = collidable;
    }
}
