//ID: 322374240
//NAME: OFEK KAHARIZI
package Geometry;

/**
 * Represents a 2D point with x and y coordinates.
 * Provides methods for getting and setting coordinates,
 * calculating distance to another point, and checking equality.
 */
public class Point {
    private double x;
    private double y;

    /**
     * Constructor to create a point with specified x and y coordinates.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates and returns the distance between this point and another point.
     * The distance is calculated using the distance formula:
     * sqrt((x2 - x1)^2 + (y2 - y1)^2)
     *
     * @param other The other point to which the distance is calculated.
     * @return The distance between this point and the other point.
     */
    public double distance(Point other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    /**
     * Checks if this point is equal to another point.
     * Two points are considered equal if their x and y coordinates are the same.
     *
     * @param other The other point to compare with.
     * @return True if the points are equal, false otherwise.
     */

    public boolean equal(Point other) {
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Returns the x-coordinate of this point.
     *
     * @return The x-coordinate of the point.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate of this point.
     *
     * @return The y-coordinate of the point.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Sets the x-coordinate of this point to a new value.
     *
     * @param x The new x-coordinate of the point.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of this point to a new value.
     *
     * @param y The new y-coordinate of the point.
     */
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
