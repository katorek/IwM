package tomograph;

import org.junit.Test;

import static java.lang.Math.PI;

public class CustomTest {

    @Test
    public void customTest(){
        double[] t1 = new double[]{0,0,0,100,100,100,0,0,0};
        double[] t2 = new double[t1.length];
        for (int i = 0; i < t1.length; i++) {
            t2[i] = applyFilter(i,t1);
            System.out.format("t1[%d]:\t%5.2f\tt2[%d]:\t%5.2f\n",i,t1[i],i,t2[i]);
        }


    }

    private double applyFilter(int idx, double[] values) {
        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += h((idx - i)) * values[i];
        }
        return sum;
    }

    private double h(double k) {
        if (k == 0) return 1;
        else if (k % 2 == 0) return 0;
        else return (-4 / PI2) / (k * k);
    }

    private static final double PI2 = PI * PI;
}
