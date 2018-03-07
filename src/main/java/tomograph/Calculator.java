package tomograph;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

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

    double[][] getSinogram(Settings settings) {
        initVariables(settings);
        calculateSinogram();

        return sinogram;
    }

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
        return points.stream().mapToInt(p -> greyScaleArr[p.x][p.y]).average().orElse(255);
    }

    private void initVariables(Settings settings) {
        iterations = settings.getI();
        n = settings.getN();
        a = settings.getA();
        l = settings.getL();
    }

    public Image renderImgFromSinogram(int step) {
        if (step == -1) step = sinogram.length;
        double[][][] imageV2 = new double[size][size][2];

        double angle = 0;
        double dl = l / n;

        Point p1, p2;
        for (int i = 0; i < step; i++, angle = i * a) {
            for (int j = 0; j < sinogram[i].length; j++) {
                double val = 0;
                if (n % 2 == 0) {
                    if (j < n / 2) {
                        val = sinogram[i][n / 2 - 1 - j];
                        p1 = calculatePointOnCircleByAngle(angle - dl / 2 - dl * j);
                        p2 = calculatePointOnCircleByAngle(180 + angle + dl / 2 + dl * j);
                    } else {
                        val = sinogram[i][j];
                        p1 = calculatePointOnCircleByAngle(angle + dl / 2 + dl * (j - n / 2));
                        p2 = calculatePointOnCircleByAngle(180 + angle - dl / 2 - dl * (j - n / 2));
                    }

                } else {
                    if (j == n / 2) {
                        val = sinogram[i][n / 2];
                        if (angle == 0) p1 = new Point(0, size / 2);
                        else p1 = calculatePointOnCircleByAngle(angle);
                        p2 = Point.mirrorPoint(p1, size);
                    } else if (j < 2) {
                        val = sinogram[i][n / 2 - j];
                        p1 = calculatePointOnCircleByAngle(angle - dl * (j + 1));
                        p2 = calculatePointOnCircleByAngle(180 + angle + dl * (j + 1));
                    } else {
                        val = sinogram[i][j];
                        p1 = calculatePointOnCircleByAngle(angle + dl * (n / 2 - j + 1));
                        p2 = calculatePointOnCircleByAngle(180 + angle - dl * (n / 2 - j + 1));
                    }
                }
                imageV2 = addLineValsToArr(imageV2, bresenham.findLine(p1, p2), val);
            }
        }
        return getImage(normalizeArr(imageV2));
    }

    private void printArr(double[][][] arr) {
        for (double[][] doubles : arr) {
            for (double[] doubleInt : doubles) {
                System.out.format("%3.2f\t",doubleInt[0]);
            }
            System.out.println();
        }
    }

    private void printArr(int[][] arr) {
        for (int[] ints : arr) {
            for (int anInt : ints) {
                System.out.print(anInt+"\t");
            }
            System.out.println();
        }
    }

    private int[][] normalizeArr(double[][][] imageV2) {
        int[][] avgArr = averageArr(imageV2);
        double minVal = findMinVal(avgArr);

        int[][] normalized = new int[size][size];

        for (int i = 0; i < avgArr.length; i++) {
            for (int j = 0; j < avgArr[i].length; j++) {
                normalized[i][j] = 255 - (int) ((255 - avgArr[i][j]) * 255 / (255 - minVal));
            }
        }

        return normalized;
    }

    private double findMinVal(int[][] arr) {
        int min = arr[0][0];
        for (int[] arr1 : arr) {
            for (int arr2 : arr1) {
                min = arr2 > min ? min : arr2;
            }
        }
        return min;
    }

    static Image getImage(int[][] arr) {
        WritableImage image = new WritableImage(arr.length, arr.length);
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                image.getPixelWriter().setColor(i, j, Color.grayRgb(arr[i][j]));
            }
        }
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

    private int[][] averageArr(double[][][] arr) {
        int[][] output = new int[arr.length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j][1] == 0) output[i][j] = 255;
                else output[i][j] = (int) (arr[i][j][0] / arr[i][j][1]);
            }
        }
        return output;
    }

    public int getIterations() {
        return iterations;
    }
}
