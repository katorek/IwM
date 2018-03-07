package tomograph;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


public class Controller {
    private double angleL, angleA;
    private int iterations, emiters, currentIter;

    private Calculator calculator;

    @FXML
    private Tab sinogramTab, biggerImgTab, sinogramImgTab;

    @FXML
    private TextArea sinogramTextArea;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Slider slider, sliderCopy;

    @FXML
    private ProgressBar progressBar, progressBarCopy;

    @FXML
    private ImageView oryginalImage, outputImage, copyOutputImage, sinogramImg;

    @FXML
    private Button imagePickButton, analyzeButton;

    @FXML
    private TextField angleAlfaTextField, angleLtextField, countEmitersTextField, iterationsTextField;

    @FXML
    private Label sliderProgress, sliderProgressCopy;

    @FXML
    private CheckBox itersRequiredCheckBox;

    @FXML
    private void chooseImage() {

        File file = loadFile();

        if (file == null) return;

        MyImage myImage = new MyImage(file);

        calculator = new Calculator(myImage.getGreyScaleImgArr().length);

        countEmitersTextField.setText(String.valueOf(myImage.getGreyScaleImgArr().length));

        calculator.setGreyScaleArr(myImage.getGreyScaleImgArr());

//        oryginalImage.setImage(myImage.getFxImage());
        oryginalImage.setImage(myImage.greyScaledImg());

        int[][] arr = myImage.getGreyScaleImgArr();

        analyzeButton.setDisable(false);

        //co jaki kat wyzanczyc punkty !
//        double kAngle = Double.parseDouble(angleLtextField.getText()) / Double.parseDouble(countEmitersTextField.getText());
        //rownianie okregu to 1/4*x^2 + 1/4*y^2 - 1/4 = 0
        //punkt startowy na samej gorze (0,s/2)
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
    private void analyze() {
        parseVariables();
        Settings settings = new Settings(emiters, iterations, angleL, angleA);
        double[][] sinogram = calculator.getSinogram(settings);


        slider.setDisable(false);

        biggerImgTab.setDisable(false);
        sliderCopy.setDisable(false);
        Image image = calculator.renderImgFromSinogram(-1);

        sinogramTab.setDisable(false);
        sinogramTextArea.setText(printSinogram(sinogram, settings));

        sinogramImgTab.setDisable(false);
        sinogramImg.setImage(MyImage.printImg(sinogram));

        outputImage.setImage(image);
        copyOutputImage.setImage(image);
    }

    private String printSinogram(double[][] sinogram, Settings s) {
        StringBuilder sb = new StringBuilder();

        for (int angle = 0; angle < s.getI(); angle++) {
            sb.append(String.format("%5.1f  \t", s.getA() * angle));
            for (int detector = 0; detector < sinogram[angle].length; detector++) {
                sb.append(String.format("%6.2f\t", sinogram[angle][detector]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void parseVariables() {
        iterations = parseAndSetInteger(iterationsTextField);
        emiters = parseAndSetInteger(countEmitersTextField);
        angleL = parseAndSetDouble(angleLtextField);
        angleA = parseAndSetDouble(angleAlfaTextField);
    }

    private double parseAndSetDouble(TextField tf) {
        String s = tf.getText();
        s = s.replaceAll(",", ".");
        double out = s.length() == 0 ? 0 : Double.parseDouble(s);

        tf.setText(String.valueOf(out).replace(".", ","));
        return out;
    }

    private int parseAndSetInteger(TextField tf) {
        String s = tf.getText();
        int out = s.length() == 0 ? 0 : Integer.parseInt(s);

        tf.setText(String.valueOf(out));
        return out;
    }

    @FXML
    private void itersRequiredCheckBoxValueChange() {
        System.err.println("Val changed");
        if (itersRequiredCheckBox.isSelected()) {
            iterationsTextField.setDisable(true);
            int iRequired = (int) (180 / Double.parseDouble(angleAlfaTextField.getText().replace(",", ".")));
            iterationsTextField.setText(String.valueOf(iRequired));
        } else {
            iterationsTextField.setDisable(false);
        }
    }

    @FXML
    void initialize() {
        assert oryginalImage != null : "fx:id=\"oryginalImage\" was not injected: check your FXML file 'window.fxml'.";
        assert outputImage != null : "fx:id=\"outputImage\" was not injected: check your FXML file 'window.fxml'.";
        assert imagePickButton != null : "fx:id=\"imagePickButton\" was not injected: check your FXML file 'window.fxml'.";
        assert slider != null : "fx:id=\"slider\" was not injected: check your FXML file 'window.fxml'.";
        assert sliderCopy != null : "fx:id=\"sliderCopy\" was not injected: check your FXML file 'window.fxml'.";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'window.fxml'.";
        assert progressBarCopy != null : "fx:id=\"progressBarCopy\" was not injected: check your FXML file 'window.fxml'.";
        assert angleAlfaTextField != null : "fx:id=\"angleAlfaTextField\" was not injected: check your FXML file 'window.fxml'.";
        assert angleLtextField != null : "fx:id=\"angleLtextField\" was not injected: check your FXML file 'window.fxml'.";
        assert countEmitersTextField != null : "fx:id=\"countEmitersTextField\" was not injected: check your FXML file 'window.fxml'.";
        assert iterationsTextField != null : "fx:id=\"iterationsTextField\" was not injected: check your FXML file 'window.fxml'.";
        assert sliderProgress != null : "fx:id=\"sliderProgress\" was not injected: check your FXML file 'window.fxml'.";
        assert sliderProgressCopy != null : "fx:id=\"sliderProgressCopy\" was not injected: check your FXML file 'window.fxml'.";
        assert itersRequiredCheckBox != null : "fx:id=\"itersRequiredCheckBox\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramTextArea != null : "fx:id=\"sinogramTextArea\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramTab != null : "fx:id=\"sinogramTab\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramImgTab != null : "fx:id=\"sinogramImgTab\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramImg != null : "fx:id=\"sinogramImg\" was not injected: check your FXML file 'window.fxml'.";
        assert copyOutputImage != null : "fx:id=\"copyOutputImage\" was not injected: check your FXML file 'window.fxml'.";

        setUpTextFormatters();

        slider.valueProperty().addListener((ov, old_val, new_val) -> progressBar.setProgress(new_val.doubleValue() / 100));
        sliderCopy.valueProperty().addListener((ov, old_val, new_val) -> progressBarCopy.setProgress(new_val.doubleValue() / 100));

        setInitValues();
        setUpSlider();
        setUpTextFields();
    }

    private void setUpTextFields() {

        angleAlfaTextField.selectedTextProperty().addListener((o, old, new_val) -> {
            System.err.println("Changed");
            if (itersRequiredCheckBox.isSelected()) {
                iterationsTextField.setText(String.valueOf((int) (180 / Double.parseDouble(angleAlfaTextField.getText().replace(",", ".")))));
            }
        });
    }

//    private void setUpCheckBox() {
//        itersRequiredCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> itersRequiredCheckBoxValueChange());
//    }

    private void setUpSlider() {
        slider.valueProperty().addListener((ov, old_val, new_val) -> updateIter(new_val));

        sliderCopy.valueProperty().addListener((ov, old_val, new_val) -> updateIter(new_val));
    }

    private void updateIter(Number num) {

        int prev = currentIter;
        currentIter = (int) (num.doubleValue() * calculator.getIterations() / 100);
        if (prev != currentIter)
            updateOutputImage();
        sliderProgress.setText(String.format("Iteracja: %d", currentIter));
        sliderProgressCopy.setText(String.format("Iteracja: %d", currentIter));
    }

    private void updateOutputImage() {
        Platform.runLater(() -> {
            Image im = calculator.renderImgFromSinogram(currentIter);
            outputImage.setImage(im);
            copyOutputImage.setImage(im);
        });
    }

    private void setInitValues() {
        angleAlfaTextField.setText("1");
        angleLtextField.setText("180");
        countEmitersTextField.setText("90");
        iterationsTextField.setText("180");
    }

    private void setUpTextFormatters() {
        Pattern doublePattern = Pattern.compile("\\-?\\d*\\,?\\d*");
        Pattern intPattern = Pattern.compile("\\d*");

        angleAlfaTextField.setTextFormatter(getFormater(doublePattern));
        angleLtextField.setTextFormatter(getFormater(doublePattern));
        countEmitersTextField.setTextFormatter(getFormater(intPattern));
        iterationsTextField.setTextFormatter(getFormater(intPattern));
    }

    private TextFormatter getFormater(Pattern pattern) {
        return new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
    }
}
