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

public class MyImage {

    private javafx.scene.image.Image fxImage;
    private BufferedImage awtImage;
    private int[][] greyScaleImgArr;

    public MyImage(File file){
        try {
            fxImage = new javafx.scene.image.Image(file.toURI().toString());
            //resizowanie ewentualne
            awtImage = ImageIO.read(file);
            int pSize = prefferedSize();

            greyScaleImgArr = new int[pSize][pSize];

            Raster raster = awtImage.getData();

            for (int i = 0; i < pSize; i++) {
                for (int j = 0; j < pSize; j++) {
                    greyScaleImgArr[i][j] = raster.getSample(i,j,0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int prefferedSize(){
        return (int) ((fxImage.getHeight()>fxImage.getWidth())?fxImage.getWidth():fxImage.getHeight());
    }

    public int[][] getGreyScaleImgArr(){
        return greyScaleImgArr;
    }

    public Image greyScaledImg(){
        return Calculator.getImage(greyScaleImgArr);
    }

    private static Image printImg(int[][] arr){
        WritableImage img = new WritableImage(arr.length, arr[0].length);
        PixelWriter px = img.getPixelWriter();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                px.setColor(i, j, Color.grayRgb(arr[i][j]));
            }
        }
        return img;
    }

    public static Image printImg(double[][] arr){
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
}
