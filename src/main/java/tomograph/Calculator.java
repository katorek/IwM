package tomograph;

public class Calculator {
    int[][] greyScaleArr;
    int size;

    public Calculator(int size) {
        this.size = size;
    }

    public Calculator() {

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
}
