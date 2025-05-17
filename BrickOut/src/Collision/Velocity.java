//ID: 322374240
//NAME: OFEK KAHARIZI
package Collision;

import Geometry.Point;

/**
 * Collision.Velocity represents the change in position on the x and y axes.
 * It is used to describe the movement of an object in a 2D space.
 */
public class Velocity {
    private double dx;
    private double dy;

    /**
     * Constructor to initialize a Collision.Velocity object with specified changes in the x and y directions.
     *
     * @param dx The change in position along the x-axis.
     * @param dy The change in position along the y-axis.
     */
    public Velocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Applies this velocity to a given point by updating its position.
     * The new point is created by adding the velocity's dx and dy to the point's current x and y coordinates.
     *
     * @param p The point to which the velocity is applied.
     * @return A new point with the updated position.
     */
    public Point applyToPoint(Point p) {
        return new Point(p.getX() + dx, p.getY() + dy);
    }

    /**
     * Returns the change in the x direction (dx).
     *
     * @return The change in the x direction as an integer.
     */
    public double getDX() {
        return this.dx;
    }

    /**
     * Returns the change in the y direction (dy).
     *
     * @return The change in the y direction as an integer.
     */
    public double getDY() {
        return this.dy;
    }

    /**
     * Sets the change in the x direction (dx).
     *
     * @param dx The new value for the change in the x direction.
     */
    public void setDX(double dx) {
        this.dx = dx;
    }

    /**
     * Sets the change in the y direction (dy).
     *
     * @param dy The new value for the change in the y direction.
     */
    public void setDY(double dy) {
        this.dy = dy;
    }

    /**
     * Checks if this velocity is equal to another velocity.
     * Two velocities are considered equal if their dx and dy are the same.
     *
     * @param other The other velocity to compare with.
     * @return True if the velocities are equal, false otherwise.
     */
    public boolean equal(Velocity other) {
        return this.dx == other.dx && this.dy == other.dy;
    }

    /**
     * Creates a Collision.Velocity object from a specified angle and speed.
     * The angle is provided in degrees, and the speed is the magnitude of the velocity.
     *
     * @param angle The angle (in degrees) of the velocity.
     * @param speed The magnitude of the velocity (speed).
     * @return A new Collision.Velocity object that represents the velocity based on the given angle and speed.
     */
    public static Velocity fromAngleAndSpeed(double angle, double speed) {
        double radians = Math.toRadians(angle); // Convert angle to radians
        double dx = speed * Math.cos(radians);  // Calculate the change in x
        double dy = speed * Math.sin(radians);  // Calculate the change in y
        return new Velocity(dx, dy); // Return a new Collision.Velocity object
    }
}
