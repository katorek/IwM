package tomograph;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;


public class MyImage {

    private Image fxImage;
    private BufferedImage awtImage;
    private int[][] greyScaleImgArr;
    private double error;

    public MyImage(File file) {
        error = 0.0;
        try {
            fxImage = new javafx.scene.image.Image(file.toURI().toString());
            //resizowanie ewentualne
            awtImage = ImageIO.read(file);
            int pSize = prefferedSize();

            greyScaleImgArr = new int[pSize][pSize];

            Raster raster = awtImage.getData();

            for (int i = 0; i < pSize; i++) {
                for (int j = 0; j < pSize; j++) {
                    greyScaleImgArr[i][j] = raster.getSample(i, j, 0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int prefferedSize() {
        return (int) ((fxImage.getHeight() > fxImage.getWidth()) ? fxImage.getWidth() : fxImage.getHeight());
    }

    public int[][] getGreyScaleImgArr() {
        return greyScaleImgArr;
    }

    public Image greyScaledImg() {
        return Calculator.getImage(greyScaleImgArr);
    }

    private static Image printImg(int[][] arr) {
        WritableImage img = new WritableImage(arr[0].length, arr.length);
        PixelWriter px = img.getPixelWriter();
//        int color;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
//                if(arr[i][j] < 0) color = Math.abs(arr[i][j]);
//                else if(arr[i][j]>255) color = 255;
//                else color = arr[i][j];
                px.setColor(j, i, Color.grayRgb(arr[i][j]));
            }
        }
        return img;
    }

    public static Image printImg(double[][] arr) {
        int[][] intArr = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                intArr[i][j] = (int) arr[i][j];
            }
        }
        return printImg(intArr);
    }

    public java.awt.Image getAwtImage() {
        return awtImage;
    }

    public Image getFxImage() {
        return fxImage;
    }

    public static double mediumSquaredError(Image im1, Image im2) {
        int x = (int) im1.getHeight();
        int y = (int) im1.getWidth();

        int[][] i1 = imgArr(im1);
        int[][] i2 = imgArr(im2);

        double error = 0;

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if ((i - x / 2) * (i - x / 2) + (j - y / 2) * (j - y / 2) < (x / 2) * (y / 2))
                    error += Math.sqrt(Math.pow(i1[i][j] -
                            i2[i][j], 2));
            }
        }
        error /= (x * y);
        return error;
    }

    private static int[][] imgArr(Image img) {
        int arr[][] = new int[(int) img.getWidth()][(int) img.getHeight()];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = (int) (img.getPixelReader().getColor(i, j).getBlue() * 255.0);
            }
        }
        return arr;
    }

    public void mediumSquaredError(ImageView iv1, ImageView iv2) {
        if (iv2.getImage().isError() || iv1.getImage().isError()) return;
        error = mediumSquaredError(iv1.getImage(), iv2.getImage());
    }

    public double getError() {
        return error;
    }
}
