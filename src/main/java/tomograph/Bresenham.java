package tomograph;

/**
 * * Java Program to Implement Bresenham Line Algorithm
 **/

import java.util.ArrayList;
import java.util.List;
//import java.awt.Point;
//import

/** Class Bresenham
 * from: http://www.sanfoundry.com/java-program-bresenham-line-algorithm/
 * **/
public class Bresenham {
    private int size;

    public Bresenham(int size){
        this.size = size;
    }
    /** function findLine() - to find that belong to line connecting the two points **/
    public List<Point> findLine(int x0, int y0, int x1, int y1) {
        List<Point> line = new ArrayList<Point>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        int e2;

//        System.err.format("dx:%d, dy:%d, sx:%d, sy:%d, err:%d\n", dx,dy,sx,sy,err);
//        System.err.println("dx: "+dx + ", dy: "+dy +", sx: "+sx+", sy");

        while (true) {
            line.add(new Point(x0, y0));

            if (x0 == x1 && y0 == y1)
                break;

            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }

            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        return line;
    }

    /** function plot() - to visualize grid **/
    public void plot(int[][] grid, List<Point> line) {
        int rows = grid.length;
        int cols = grid.length;
//        System.err.println("length: " + rows + ", " + cols);

        System.out.println("\nPlot : \n");
        Point p = new Point();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                p.setLocation(i, j);
                if (line.contains(p))
                    System.out.print("*");
                else
                    System.out.print("X");
            }
            System.out.println();
        }
    }

    public void plot(int size, List<Point> line){
        plot(new int[size][size], line);
    }

    public List<Point> findLine(Point p1, Point p2) {
        p1 = Point.validatePoint(p1, size);
        p2 = Point.validatePoint(p2, size);
//        System.err.println("p1: "+ p1.toString()+", p2: "+p2.toString());
        return findLine(p1.x, p1.y, p2.x, p2.y);
    }

}
