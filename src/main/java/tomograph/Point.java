package tomograph;

public class Point extends java.awt.Point {

    public Point(int x, int y) {
        super(x, y);
    }

    public Point() {
        this(0, 0);
    }

    /**
     * Sets new x.
     *
     * @param x New value of x.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets new y.
     *
     * @param y New value of y.
     */
    public void setY(int y) {
        this.y = y;
    }

    public static Point mirrorPoint(Point point, int imgSize) {
        int x = imgSize - (int) point.getX();
        int y = imgSize - (int) point.getY();
        return new Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public static Point validatePoint(Point p, int size) {
        if (p.x < 0) p.x = 0;
        if (p.y < 0) p.y = 0;

        if (p.x >= size) p.x = size - 1;
        if (p.y >= size) p.y = size - 1;

        return p;
    }
}
