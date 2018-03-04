package tomograph;

import javafx.scene.image.ImageView;

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

    public java.awt.Image getAwtImage() {
        return awtImage;
    }

    public javafx.scene.image.Image getFxImage() {
        return fxImage;
    }
}
