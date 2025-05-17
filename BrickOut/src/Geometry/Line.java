//ID: 322374240
//NAME: OFEK KAHARIZI
package Geometry;

import java.util.List;

/**
 * The Geometry.Line class defined by two points (start and end).
 * It provides various methods for calculating the length, midpoint, slope, and checking for intersections
 * with other lines or line segments.
 */
public class Line {
    private Point start;
    private Point end;

    // Constructors

    /**
     * Creates a line with two points.
     *
     * @param start The starting point of the line.
     * @param end   The ending point of the line.
     */
    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Creates a line using the coordinates of the start and end points.
     *
     * @param x1 The x-coordinate of the start point.
     * @param y1 The y-coordinate of the start point.
     * @param x2 The x-coordinate of the end point.
     * @param y2 The y-coordinate of the end point.
     */
    public Line(double x1, double y1, double x2, double y2) {
        this.start = new Point(x1, y1);
        this.end = new Point(x2, y2);
    }

    /**
     * Calculates and returns the length of the line.
     *
     * @return The length of the line.
     */
    public double length() {
        return Math.sqrt(Math.pow(this.start.getX() - this.end.getX(), 2)
                + Math.pow(this.start.getY() - this.end.getY(), 2));
    }

    /**
     * Returns the middle point of the line.
     * The middle point is calculated by averaging the x and y coordinates of the start and end points.
     *
     * @return The middle point of the line.
     */
    public Point middle() {
        double x = (this.start.getX() + this.end.getX()) / 2;
        double y = (this.start.getY() + this.end.getY()) / 2;
        return new Point(x, y);
    }

    /**
     * Returns the start point of the line.
     *
     * @return The start point of the line.
     */
    public Point start() {
        return new Point(this.start.getX(), this.start.getY());
    }

    /**
     * Returns the end point of the line.
     *
     * @return The end point of the line.
     */
    public Point end() {
        return new Point(this.end.getX(), this.end.getY());
    }

    /**
     * Calculates and returns the slope of the line.
     * The slope is calculated as (y2 - y1) / (x2 - x1).
     *
     * @return The slope of the line.
     */
    public double slope() {
        if (this.start.getX() == this.end.getX()) {
            return Double.POSITIVE_INFINITY; // Indicates a vertical line with undefined slope
        }
        return (this.end.getY() - this.start.getY()) / (this.end.getX() - this.start.getX());
    }

    /**
     * Checks if this line intersects with another line.
     *
     * @param other The other line to check for intersection.
     * @return True if the lines intersect, false otherwise.
     */
    public boolean isIntersecting(Line other) {
        if (this.equals(other)) {
            return true;
        }
// If the slopes are the same, the lines are parallel and do not intersect
        if (this.slope() == other.slope()) {
            return false;
        }
        // Special case: One line is horizontal, the other is vertical
        if (this.slope() == 0 && other.slope() == Double.POSITIVE_INFINITY) {
            // Geometry.Line this is horizontal and other is vertical
            double x = other.start.getX(); // x-coordinate of the vertical line
            double y = this.start.getY(); // y-coordinate of the horizontal line
            return isPointOnLineSegment(this, x, y) && isPointOnLineSegment(other, x, y);
        }
        if (this.slope() == Double.POSITIVE_INFINITY && other.slope() == 0) {
            // Geometry.Line this is vertical and other is horizontal
            double x = this.start.getX(); // x-coordinate of the vertical line
            double y = other.start.getY(); // y-coordinate of the horizontal line
            return isPointOnLineSegment(this, x, y) && isPointOnLineSegment(other, x, y);
        }
        if (other.slope() == Double.POSITIVE_INFINITY) {
            // The other line is vertical, calculate the intersection y-coordinate
            double x = other.start.getX();
            double y = this.slope() * x + (this.start.getY() - this.slope() * this.start.getX());
            return isPointOnLineSegment(this, x, y) && isPointOnLineSegment(other, x, y);
        }
        // If one of the lines is vertical, we need to handle it specially
        if (this.slope() == Double.POSITIVE_INFINITY) {
            // This line is vertical, calculate the intersection y-coordinate
            double x = this.start.getX();
            double y = other.slope() * x + (other.start.getY() - other.slope() * other.start.getX());
            return isPointOnLineSegment(this, x, y) && isPointOnLineSegment(other, x, y);
        }

        // Calculate the y-intercepts (b) for each line
        double b1 = this.start.getY() - this.slope() * this.start.getX();
        double b2 = other.start.getY() - other.slope() * other.start.getX();

        // Solve for the intersection point (x, y)
        double x = (b2 - b1) / (this.slope() - other.slope());
        double y = this.slope() * x + b1;

        // Check if the intersection point is within the line segments
        return isPointOnLineSegment(this, x, y) && isPointOnLineSegment(other, x, y);
    }

    /**
     * Helper function to check if a point (x, y) lies on a line segment.
     *
     * @param line The line segment to check.
     * @param x    The x-coordinate of the point to check.
     * @param y    The y-coordinate of the point to check.
     * @return True if the point lies on the line segment, false otherwise.
     */
    private boolean isPointOnLineSegment(Line line, double x, double y) {
        Point p = new Point(x, y);
        double ep = 0.0000001;
        return Math.abs(line.start.distance(p) + line.end.distance(p) - line.length()) < ep;
    }

    /**
     * Checks if this line intersects with two other lines.
     * The lines must intersect with both other lines to return true.
     *
     * @param other1 The first line to check for intersection.
     * @param other2 The second line to check for intersection.
     * @return True if this line intersects with both other lines, false otherwise.
     */
    public boolean isIntersecting(Line other1, Line other2) {
        return this.isIntersecting(other1) && this.isIntersecting(other2);
    }

    /**
     * Returns the intersection point of this line and another line.
     * If the lines do not intersect, null is returned.
     *
     * @param other The other line to check for intersection.
     * @return The intersection point, or null if the lines do not intersect.
     */
    public Point intersectionWith(Line other) {
        // Check if the lines are equal or have the same slope (parallel)
        if (this.equals(other) || this.slope() == other.slope()) {
            return null;
        }

        double x, y;
        // For vertical lines
        if (this.start.getX() == this.end.getX()) { // This line is vertical
            x = this.start.getX();
            y = other.slope() * x + (other.start.getY() - other.slope() * other.start.getX());
        } else if (other.start.getX() == other.end.getX()) { // The other line is vertical
            x = other.start.getX();
            y = this.slope() * x + (this.start.getY() - this.slope() * this.start.getX());
        } else {
            // If both lines are not vertical, solve for x and y using the slope-intercept form
            double b1 = this.start.getY() - this.slope() * this.start.getX();
            double b2 = other.start.getY() - other.slope() * other.start.getX();

            // Solve for x: (b2 - b1) / (m1 - m2)
            x = (b2 - b1) / (this.slope() - other.slope());
            y = this.slope() * x + b1;
        }


        // Check if the intersection point (x, y) lies within both line segments
        if (isPointOnLineSegment(this, x, y) && isPointOnLineSegment(other, x, y)) {
            return new Point(x, y); // Return the intersection point
        } else {
            return null; // No intersection
        }
    }

    /**
     * Checks if two lines are equal.
     * Two lines are considered equal if their start and end points are the same (order does not matter).
     *
     * @param other The other line to compare with.
     * @return True if the lines are equal, false otherwise.
     */
    public boolean equals(Line other) {
        return (this.end == other.end && this.start == other.start)
                || ((this.start == other.end) && (other.start == this.end));
    }

    /**
     * Finds the closest intersection point between this line and a rectangle
     * that is closest to the start of the line.
     *
     * @param rect The rectangle to find intersection points with.
     * @return The closest intersection point, or null if there are no intersections.
     */
    public Point closestIntersectionToStartOfLine(Rectangle rect) {
        List<Point> intersections = rect.intersectionPoints(this);
        if (intersections.isEmpty()) {
            return null;
        }

        Point closestPoint = null;
        double minDistance = Double.MAX_VALUE;

        for (Point point : intersections) {
            double distance = point.distance(this.start);
            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = point;
            }
        }
        return closestPoint;
    }
}
