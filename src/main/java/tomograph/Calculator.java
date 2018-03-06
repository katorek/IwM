package tomograph;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Calculator {
    private int[][] greyScaleArr;
    private int size, radius;
    double[][] sinogram;
    Bresenham bresenham;

    int iterations, n;
    double a, l;

    public Calculator(int size) {
        this.size = size;
        radius = size / 2;
        bresenham = new Bresenham();
    }

    public int[] f(int x) {
        return f(x, size);
    }

    public int[] f(int x, int size) {
        return new int[]{y1(x, size), y2(x, size)};
    }

    private int y1(int x, int s) {
        return (int) (1 / 2 * (s - Math.sqrt(-s * s + 4 * s * x + s - 4 * x * x)));
    }

    private int y2(int x, int s) {
        return (int) (1 / 2 * (s + Math.sqrt(-s * s + 4 * s * x + s - 4 * x * x)));
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Point calculateMiddleEndPoint(Point sPoint) {
        return Point.mirrorPoint(sPoint, size);
    }

    public int[][] getGreyScaleArr() {
        return greyScaleArr;
    }

    public void setGreyScaleArr(int[][] greyScaleArr) {
        this.greyScaleArr = greyScaleArr;
    }

    double[][] getSinogram(Settings settings) {
        initVariables(settings);
        calculateSinogram();

        return sinogram;
    }

    private void calculateSinogram() {
        Point start = new Point(0, size / 2), current = (Point) start.clone(), opposite = null;

        double newA = 0;
        double dl = l / n;

        sinogram = new double[iterations][n];

        List<Point> points = new ArrayList<>(size);

        for (int i = 0; i < iterations && newA < 180; i++, newA = i * a) {
//            System.out.format("i:%d, newA:%.2f\n", i, newA);
            if (newA == 0) {

            } else {
                current = calculatePointOnCircleByAngle(newA);
            }
            opposite = Point.mirrorPoint(current, size);

            Point p1, p2;
            if (n % 2 == 0) {

                for (int j = 0; j < n / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA - dl / 2 - dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA + dl / 2 + dl * j);

                    List<Point> line = bresenham.findLine(p1, p2);

                    sinogram[i][n/2-1-j] = average(line);
//                    points.addAll(line);
                }

                for (int j = 0; j < n / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA + dl / 2 + dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA - dl / 2 - dl * j);

                    List<Point> line = bresenham.findLine(p1, p2);

                    sinogram[i][n/2+j] = average(line);
//                    points.addAll(line);
                }

            } else {
                List<Point> line = bresenham.findLine(current, opposite);
                sinogram[i][n/2] = average(line);

                int emiters = n - 1;
                for (int j = 1; j <= emiters / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA - dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA + dl * j);

//                    List<Point> line = bresenham.findLine(p1, p2);
//                    double avg = average(line);
//                    points.addAll(line);
                    sinogram[i][n/2-j]=  average(bresenham.findLine(p1, p2));
                    //todo dodanie averaga do sinogramu
                }

                for (int j = 1; j <= emiters / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA + dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA - dl * j);

                    sinogram[i][n/2+j] = average(bresenham.findLine(p1, p2));
//                    List<Point> line = bresenham.findLine(p1, p2);
//                    points.addAll(line);
                }


                points.addAll(line);
            }
//            bresenham.plot(size, points);
            points.clear();
            //todo zamiast dodawac punkty do points dodawac sumy po elementach do sinogramu
        }


    }


    private Point calculatePointOnCircleByAngle(double angle) {
        double radians = toRadians(angle);
        int x, y;
        x = (int) (radius + cos(radians) * radius);
        y = (int) (radius + sin(radians) * radius);

        return new Point(x, y);
    }

    private double average(List<Point> points) {
        return points.stream().mapToInt(p -> greyScaleArr[p.x][p.y]).average().orElse(0);
    }

    private void initVariables(Settings settings) {
        iterations = settings.getI();
        n = settings.getN();
        a = settings.getA();
        l = settings.getL();
    }

}
