package tomograph;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


public class Controller {

    private Calculator calculator;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Slider slider;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView oryginalImage;

    @FXML
    private ImageView outputImage;

    @FXML
    private Button imagePickButton;

    @FXML
    private TextField angleAlfa;

    @FXML
    private TextField angleL;

    @FXML
    private TextField countEmiters;

    @FXML
    void chooseImage(ActionEvent event) {

        File file = loadFile();

        if (file == null) return;

        MyImage myImage = new MyImage(file);

        calculator = new Calculator(myImage.getGreyScaleImgArr().length);


        oryginalImage.setImage(myImage.getFxImage());

        int[][] arr = myImage.getGreyScaleImgArr();
        //rownianie okregu to 1/4*x^2 + 1/4*y^2 - 1/4 = 0

    }



    private File loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do analizy");
        fileChooser.setInitialDirectory(new File("E:\\Projekty\\studia\\si\\2podejscie\\IwM\\src\\main\\resources"));

        FileChooser.ExtensionFilter extFilterGraphic = new FileChooser.ExtensionFilter("Pliki graficzne", "*.png", "*.jpg");
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("Pliki JPG (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("Pliki PNG (*.png)", "*.png");

        fileChooser.getExtensionFilters().addAll(extFilterGraphic, extFilterJPG, extFilterPNG);

        return fileChooser.showOpenDialog(imagePickButton.getScene().getWindow());
    }


    @FXML
    void initialize() {
        assert oryginalImage != null : "fx:id=\"oryginalImage\" was not injected: check your FXML file 'sample.fxml'.";
        assert outputImage != null : "fx:id=\"outputImage\" was not injected: check your FXML file 'sample.fxml'.";
        assert imagePickButton != null : "fx:id=\"imagePickButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert slider != null : "fx:id=\"slider\" was not injected: check your FXML file 'sample.fxml'.";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'sample.fxml'.";
        assert angleAlfa != null : "fx:id=\"angleAlfa\" was not injected: check your FXML file 'sample.fxml'.";
        assert angleL != null : "fx:id=\"angleL\" was not injected: check your FXML file 'sample.fxml'.";
        assert countEmiters != null : "fx:id=\"countEmiters\" was not injected: check your FXML file 'sample.fxml'.";


//        final ProgressIndicator pi = new ProgressIndicator(0);

        slider.valueProperty().addListener((ov, old_val, new_val) -> progressBar.setProgress(new_val.doubleValue() / 100));


    }
}
