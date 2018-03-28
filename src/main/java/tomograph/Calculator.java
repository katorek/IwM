package tomograph;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.*;

public class Calculator {
    private Settings settings;
    private int[][] greyScaleArr;
    private int size, radius;
    double[][] sinogram;
    double[][] sinogramFiltered;
    Bresenham bresenham;

    double minVal = 255;

    int iterations, n;
    double a, l;
    private boolean filtering;

    public Calculator(int size) {
        this.size = size;
        radius = size / 2;
        bresenham = new Bresenham(size);
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

    void calculateSinograms(Settings settings){
        this.settings = settings;
        initVariables(settings);
        calculateSinogram();
        applyFilter2();
    }

    double[][] getSinogram() {
//        this.settings = settings;
//        initVariables(settings);
//        calculateSinogram();
//        applyFilter2();
        return sinogram;
    }

    public void applyFilter2() {
        sinogramFiltered = new double[sinogram.length][sinogram[0].length];
        for (int i = 0; i < sinogram.length; i++) {
            double temp[] = new double[sinogram[i].length];
            for (int j = 0; j < sinogram[i].length; j++) {
                for (int k = 0; k < sinogram[i].length; k++) {
                    temp[j] += fx(j, k) * sinogram[i][k];
                }
            }
            sinogramFiltered[i] = temp;
        }
    }

    private double fx(int id, int id2) {
        int temp = abs(id - id2);
        if (temp == 0) return 1;
        if (temp % 2 == 0) return 0;
        return (-4 / PI2) / (temp * temp);
    }

    private static final double PI2 = PI * PI;

    private void calculateSinogram() {
        Point p1 = new Point(0, size / 2), p2;

        double newA = 0;
        double dl = l / n;

        sinogram = new double[iterations][n];
        for (int i = 0; i < iterations; i++, newA = i * a) {
            List<Point> points = new ArrayList<>(n / 2);
            if (n % 2 == 0) {
                for (int j = 0; j < n / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA - dl / 2 - dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA + dl / 2 + dl * j);
                    sinogram[i][n / 2 - 1 - j] = average(bresenham.findLine(p1, p2));
                }
                for (int j = 0; j < n / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA + dl / 2 + dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA - dl / 2 - dl * j);
                    sinogram[i][n / 2 + j] = average(bresenham.findLine(p1, p2));
                }
            } else {
                if (newA != 0)
                    p1 = calculatePointOnCircleByAngle(newA);
                p2 = Point.mirrorPoint(p1, size);
                sinogram[i][n / 2] = average(bresenham.findLine(p1, p2));

                int emiters = n - 1;
                for (int j = 1; j <= emiters / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA - dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA + dl * j);
                    sinogram[i][n / 2 - j] = average(bresenham.findLine(p1, p2));
                }

                for (int j = 1; j <= emiters / 2; j++) {
                    p1 = calculatePointOnCircleByAngle(newA + dl * j);
                    p2 = calculatePointOnCircleByAngle(180 + newA - dl * j);
                    sinogram[i][n / 2 + j] = average(bresenham.findLine(p1, p2));
                }
            }
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
        return points.stream().mapToInt(p -> greyScaleArr[p.x][p.y]).average().orElse(255 / 2);
    }

    private void initVariables(Settings settings) {
        iterations = settings.getI();
        n = settings.getN();
        a = settings.getA();
        l = settings.getL();
    }

    public Image renderImgFromSinogram(int step, boolean doFilter) {
        double[][] sino = (doFilter) ? sinogramFiltered : sinogram;

        if (step == -1) step = sino.length;
        double[][][] doubleImgArr = new double[size][size][2];

        double angle = 0;
        double dl = l / n;

        Point p1, p2;
        for (int i = 0; i < step; i++, angle = i * a) {
            for (int j = 0; j < sino[i].length; j++) {
                double val = 0;
                if (n % 2 == 0) {
                    //calculatePointOnCircleByAngle
                    //calculatePointOnPerimeterByAngle
                    if (j < n / 2) {
                        val = sino[i][n / 2 - 1 - j];
                        p1 = calculatePointOnCircleByAngle(angle - dl / 2 - dl * j);
                        p2 = calculatePointOnCircleByAngle(180 + angle + dl / 2 + dl * j);
                    } else {
                        val = sino[i][j];
                        p1 = calculatePointOnCircleByAngle(angle + dl / 2 + dl * (j - n / 2));
                        p2 = calculatePointOnCircleByAngle(180 + angle - dl / 2 - dl * (j - n / 2));
                    }

                } else {
                    if (j == n / 2) {
                        val = sino[i][n / 2];
                        if (angle == 0) p1 = new Point(0, size / 2);
                        else p1 = calculatePointOnCircleByAngle(angle);
                        p2 = Point.mirrorPoint(p1, size);
                    } else if (j < 2) {
                        val = sino[i][n / 2 - j];
                        p1 = calculatePointOnCircleByAngle(angle - dl * (j + 1));
                        p2 = calculatePointOnCircleByAngle(180 + angle + dl * (j + 1));
                    } else {
                        val = sino[i][j];
                        p1 = calculatePointOnCircleByAngle(angle + dl * (n / 2 - j + 1));
                        p2 = calculatePointOnCircleByAngle(180 + angle - dl * (n / 2 - j + 1));
                    }
                }
                doubleImgArr = addLineValsToArr(doubleImgArr, bresenham.findLine(p1, p2), val);
            }
        }
        return getImage(averageArr(doubleImgArr, doFilter));
    }

    /**
     * @param angle angle in degrees
     * @return
     */
    public Point calculatePointOnPerimeterByAngle(double angle) {
        double radians = toRadians(angle);
        int hypotenuse = radius;

        int x = (int) (radius + cos(radians) * hypotenuse);
        int y = (int) (radius + sin(radians) * hypotenuse);
        while ((x > 0 && x < size - 1) && (y > 0 && y < size - 1)) {
//            hypotenuse += 0.5;
            x = (int) (radius + cos(radians) * ++hypotenuse);
            y = (int) (radius + sin(radians) * ++hypotenuse);
        }

        return new Point(x, y);
    }

    public Point calculatePointOnPerimeterByAngleV2(double angle) {
        double rad = toRadians(angle);
        int x, y;

//        if (angle == 0 || angle == 360) return new Point(0, size / 2);
//        if (angle == 45) return new Point(0, size);
//        if (angle == 90) return new Point(size /2 , size);
//        if (angle == 135) return new Point(0, size);
//        if (angle == 180) return new Point(size, size / 2);
//        if (angle == 270) return new Point(size / 2, 0);

        switch ((int) angle) {
            case 0:
            case 360:
                return new Point(0, size / 2);
            case 90:
                return new Point(size / 2, size);
            case 180:
                return new Point(size, size / 2);
            case 270:
                return new Point(size / 2, 0);
            case 45:
                return new Point(0, size);
            case 135:
                return new Point(size, size);
            case 225:
                return new Point(size, 0);
            case 315:
                return new Point(0, 0);
            default:
                break;
        }


        if (angle < 45 || angle > 315)

            return new Point(0, 0);
        return new Point(0, 0);
    }


    private void printArr(double[][][] arr) {
        for (double[][] doubles : arr) {
            for (double[] doubleInt : doubles) {
                System.out.format("%3.2f\t", doubleInt[0]);
            }
            System.out.println();
        }
    }

    private void printArr(int[][] arr) {
        for (int[] ints : arr) {
            for (int anInt : ints) {
                System.out.print(anInt + "\t");
            }
            System.out.println();
        }
    }

    private int[][] normalizeArr(double[][][] imageV2) {

        int[][] avgArr = averageArr(imageV2, false);
//        double maxVal = findMaxVal(avgArr);
//        double minVal = findMinVal(avgArr);
//        System.out.println("minVal: " + minVal + ",maxVal: " + maxVal);
//        double minVal = findMinVal(avgArr);
//        System.err.println("MinVAl: "+ minVal);

//        int[][] normalized = new int[size][size];

//        for (int i = 0; i < avgArr.length; i++) {
//            for (int j = 0; j < avgArr[i].length; j++) {
////                normalized[i][j] = (int) (avgArr[i][j] / maxVal * 255);
////                normalized[i][j] += 255 - (int) ((255 - avgArr[i][j]) * 255 / (255 + minVal));
////                normalized[i][j] /= 2;
////                if(minVal < 0)
////                    normalized[i][j] =  (int) ((avgArr[i][j] - minVal) * 255 / (maxVal - minVal));
////                else
////                normalized[i][j] = abs(normalized[i][j]);
////                normalized[i][j] = (int) ((avgArr[i][j] - minVal) * 255 / (maxVal - minVal));
//                normalized[i][j] = avgArr[i][j];
//            }
//        }

//        return normalized;
        return avgArr;
    }

    private double findMinVal(int[][] arr) {
        int min = arr[0][0];
//        for (int[] arr1 : arr) {
//            for (int arr2 : arr1) {
//                min = arr2 > min ? min : arr2;
//            }
//        }
        int r = radius;
        min = arr[0][0];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if ((i - r) * (i - r) + (j - r) * (j - r) < r * r) {
                    min = arr[i][j] > min ? min : arr[i][j];
                }
            }
        }
        return min;
    }

    private double findMaxVal(int[][] arr) {
        int max = arr[0][0];
        for (int[] arr1 : arr) {
            for (int arr2 : arr1) {
                max = arr2 < max ? max : arr2;
            }
        }
        int r = radius;
        max = arr[0][0];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if ((i - r) * (i - r) + (j - r) * (j - r) < r * r) {
                    max = arr[i][j] < max ? max : arr[i][j];
                }
            }
        }
        return max;
    }

    static Image getImage(int[][] arr) {
        WritableImage image = new WritableImage(arr.length, arr.length);
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                int color = arr[i][j] < 0 ? 0 : arr[i][j];
                image.getPixelWriter().setColor(i, j, Color.grayRgb(color));
            }
        }
//        Color.rgb()
        return image;
    }

    private double[][][] addLineValsToArr(double[][][] arr, List<Point> points, double val) {
        for (Point point : points) {
            arr = addValToArr(arr, point, val);
        }
        return arr;
    }

    private double[][][] addValToArr(double[][][] arr, Point p, double val) {
        arr[p.x][p.y][0] += val;
        arr[p.x][p.y][1]++;
        return arr;
    }

    private static boolean tak = false;

    private int[][] averageArr(double[][][] arr, boolean applyFilter) {
        int[][] output = new int[arr.length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (!applyFilter) {
                    if (arr[i][j][1] == 0) output[i][j] = 255;
                    else {
                        output[i][j] = (int) (arr[i][j][0] / arr[i][j][1]);
                    }

                } else {
                    if (arr[i][j][1] == 0) output[i][j] = 255;
                    else output[i][j] = (int) arr[i][j][0];
                    if (output[i][j] < 0) output[i][j] = 0;
                    if (output[i][j] > 255) output[i][j] = 255;
                }

            }
        }
        return output;
    }

    public double[][] getSinogramFiltered() {
        return sinogramFiltered;
    }

    public int getIterations() {
        return iterations;
    }

    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
    }

    public boolean isFiltering() {
        return filtering;
    }
}
