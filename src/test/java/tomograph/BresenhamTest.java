package tomograph;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

public class BresenhamTest {
    private int[][] arr;
    int size;
    Bresenham bresenham;
    Random r;
    @Before
    public void init() {
        r = new Random();
        bresenham = new Bresenham(size);
    }

    @Test
    public void verticalLine() {
        size = 50;

        Point p1 = new Point(r.nextInt(size), r.nextInt(size));
        Point p2 = new Point(r.nextInt(size), r.nextInt(size));


        arr = new int[size][size];
        List<Point> line = bresenham.findLine(p1, p2);

//        bresenham.findLine(arr, p1 , p2);
        bresenham.plot(arr,line);


    }
}
