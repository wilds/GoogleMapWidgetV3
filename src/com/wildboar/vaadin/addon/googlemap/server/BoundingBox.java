package com.wildboar.vaadin.addon.googlemap.server;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Wilds
 *
 *         This class takes in points and determines a bounding box that will surround the points.
 *
 */
public class BoundingBox {
    /** distance less than EPSILON is considered zero */
    public static double EPSILON = 1e-6;

    Point2D.Double max, min, total;
    int numPoints;

    public BoundingBox(Point2D.Double startingPoint) {
        max = new Point2D.Double(startingPoint.x, startingPoint.y);
        min = new Point2D.Double(startingPoint.x, startingPoint.y);
        total = new Point2D.Double(startingPoint.x, startingPoint.y);
        numPoints = 1;
    }

    public void addPoint(Point2D.Double toAdd) {
        updateMax(toAdd);
        updateMin(toAdd);
        updateTotal(toAdd);
        numPoints++;
    }

    private void updateMax(Point2D.Double toAdd) {
        if (toAdd.x > max.x) {
            max.x = toAdd.x;
        }
        if (toAdd.y > max.y) {
            max.y = toAdd.y;
        }
    }

    private void updateMin(Point2D.Double toAdd) {
        if (toAdd.x < min.x) {
            min.x = toAdd.x;
        }
        if (toAdd.y < min.y) {
            min.y = toAdd.y;
        }
    }

    private void updateTotal(Point2D.Double toAdd) {
        total.x += toAdd.x;
        total.y += toAdd.y;
    }

    public double getPerimeter() {
        return 2 * (max.x - min.x) + 2 * (max.y - min.y);
    }

    public double getArea() {
        return (max.x - min.x) * (max.y - min.y);
    }

    public Rectangle2D.Double getBounds2D() {
        return new Rectangle2D.Double(min.x, min.y, max.x - min.x, max.y - min.y);
    }

    public Pair<Point2D.Double, Point2D.Double> getBounds() {
        return new Pair<Point2D.Double, Point2D.Double>(new Point2D.Double(min.x, min.y), new Point2D.Double(max.x, max.y));
    }

    /** returns the average point */
    public Point2D.Double getCenterOfMass() {
        return new Point2D.Double(total.x / numPoints, total.y / numPoints);
    }

    /** determines if two BoundingBoxes are equal */
    @Override
    public boolean equals(Object object) {
        if (object instanceof BoundingBox) {
            BoundingBox box = (BoundingBox) object;
            if (max.distance(box.max) < EPSILON && min.distance(box.min) < EPSILON && getCenterOfMass().distance(box.getCenterOfMass()) < EPSILON && total == box.total)
                return true;
            else
                return false;
        }
        return false;
    }

    public boolean contains(Point2D.Double point) {
        if (min.x <= point.x && point.x <= max.x && min.y <= point.y && point.y <= max.y)
            return true;
        else
            return false;
    }

    public boolean intersects(BoundingBox box) {
        Rectangle2D.Double thisOne;
        Rectangle2D.Double thatOne;
        thisOne = getBounds2D();
        thatOne = box.getBounds2D();
        return thisOne.intersects(thatOne);
    }

    /** readable text of the instance variables */
    @Override
    public String toString() {
        return "[box=" + getBounds2D() + ", total=" + total + ", numPoints=" + numPoints + "]";
    }
}