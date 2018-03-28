package tomograph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {
    Point p1, p2;
    Calculator calc;

    @Before
    public void init() {
        calc = new Calculator(100);
    }

    @Test
    public void calculatePointOnPerimeterByAngleTest() {
        int[] vals = new int[]{0, 45, 90, 135, 180, 225, 270, 315, 360};
        for (int val : vals) {
            System.out.format("Angle: %d\t%s\n",val,calc.calculatePointOnPerimeterByAngle(val));
        }
//        p1 = calc.calculatePointOnPerimeterByAngle(45);
//        System.out.println("p1 = " + p1);
//        p2 = new Point()
//        Assert.assertEquals(p1);
    }
}
