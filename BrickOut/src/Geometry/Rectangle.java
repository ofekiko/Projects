//ID: 322374240
//NAME: OFEK KAHARIZI
package Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * The Geometry.Rectangle class represents a rectangle defined by its upper-left corner,
 * width, and height. It provides methods to calculate intersections with a line.
 */
public class Rectangle {
    private Point upperLeft; // The upper-left corner of the rectangle
    private double width;    // The width of the rectangle
    private double height;   // The height of the rectangle

    /**
     * Constructs a new rectangle with the given upper-left corner, width, and height.
     *
     * @param upperLeft The upper-left corner of the rectangle.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     */
    public Rectangle(Point upperLeft, double width, double height) {
        this.upperLeft = upperLeft;
        this.width = width;
        this.height = height;
    }

    /**
     * Finds and returns a list of intersection points between this rectangle
     * and the given line. If there are no intersections, the list will be empty.
     *
     * @param line The line to check for intersections.
     * @return A list of intersection points between the rectangle and the line.
     */
    public List<Point> intersectionPoints(Line line) {
        double x = upperLeft.getX(); // X-coordinate of the upper-left corner
        double y = upperLeft.getY(); // Y-coordinate of the upper-left corner

        List<Point> intersectionPoints = new ArrayList<>();
        // Define the four sides (walls) of the rectangle as lines
        Line topWall = new Line(x, y, x + getWidth(), y);
        Line leftWall = new Line(x, y, x, y + getHeight());
        Line bottomWall = new Line(x, y + getHeight(), x + getWidth(), y + getHeight());
        Line rightWall = new Line(x + getWidth(), y, x + getWidth(), y + getHeight());

        Line[] walls = {topWall, leftWall, bottomWall, rightWall};
        for (Line wall : walls) {
            if (line.isIntersecting(wall)) {
                Point intersection = wall.intersectionWith(line);
                // Check if an intersection exists and add it to the list
                if (intersection != null) {
                    intersectionPoints.add(intersection);
                }
            }
        }
        return intersectionPoints;
    }

    /**
     * Returns the width of the rectangle.
     *
     * @return The width of the rectangle.
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Returns the height of the rectangle.
     *
     * @return The height of the rectangle.
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Returns the upper-left point of the rectangle.
     *
     * @return The upper-left point of the rectangle.
     */
    public Point getUpperLeft() {
        return this.upperLeft;
    }
}
