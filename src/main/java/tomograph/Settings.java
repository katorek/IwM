package tomograph;

public class Settings {
    private int n, i;
    private double l, a;

    public Settings(int emitersCount, int iterations, double angleL, double angleAlfa) {
        this.n = emitersCount;
        this.i = iterations;
        this.l = angleL;
        this.a = angleAlfa;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

}
