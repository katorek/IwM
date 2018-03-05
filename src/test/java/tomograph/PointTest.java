package tomograph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PointTest {
    Point p1, p2;

    @Before
    public void init(){
        p1 = new Point();
        p2 = new Point();
    }

    @Test
    public void mirrorPointTest(){

        p1.setX(90);
        p1.setY(40);

        p2.setX(10);
        p2.setY(60);

        int size = 100;

        Assert.assertEquals(p1, Point.mirrorPoint(p2, size));

        p1.setX(0); //
        p1.setY(33); //66

        p2.setX(66);
        p2.setY(33);
        size = 66;

        Assert.assertEquals(p1, Point.mirrorPoint(p2, size));
    }
}
