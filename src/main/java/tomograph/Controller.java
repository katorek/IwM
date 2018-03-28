package tomograph;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


public class Controller {
    private double angleL, angleA;
    private int iterations, emiters, currentIter;
    private Storage storage;

    private Calculator calculator;

    @FXML
    private Tab sinogramTab, biggerImgTab, sinogramImgTab, sinogramFilteredTab;

    @FXML
    private TextArea sinogramTextArea, sinogramFilteredTextArea;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Slider slider, sliderCopy;

    @FXML
    private ProgressBar progressBar, progressBarCopy;

    @FXML
    private ImageView oryginalImgView, outputImageView, copyOutputImageView, sinogramImgView, sinogramFilteredImgView;

    @FXML
    private Button imagePickButton, analyzeButton, sinogramButton;

    @FXML
    private TextField angleAlfaTextField, angleLtextField, countEmitersTextField, iterationsTextField;

    @FXML
    private Label sliderProgressLabel, sliderProgressCopyLabel;

    @FXML
    private CheckBox itersRequiredCheckBox, filterCheckBox;

    @FXML
    private void chooseImage() {

        slider.setDisable(true);
        sliderCopy.setDisable(true);

        File file = loadFile();

        if (file == null) return;

        myImg = new MyImage(file);

        calculator = new Calculator(myImg.getGreyScaleImgArr().length);

        countEmitersTextField.setText(String.valueOf(myImg.getGreyScaleImgArr().length));

        calculator.setGreyScaleArr(myImg.getGreyScaleImgArr());

        oryginalImgView.setImage(myImg.greyScaledImg());

        analyzeButton.setDisable(true);
        sinogramButton.setDisable(false);

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
    private void calcSinogram() {
        parseVariables();
        Settings settings = new Settings(emiters, iterations + 1, angleL, angleA);

        calculator.calculateSinograms(settings);

        double[][] sinogram = calculator.getSinogram();

        sinogramTab.setDisable(false);
        sinogramTextArea.setText(printSinogram(sinogram, settings));

        sinogramFilteredTab.setDisable(false);
        sinogramFilteredTextArea.setText(printSinogram(calculator.getSinogramFiltered(), settings));

        sinogramImgTab.setDisable(false);
        sinogramImgView.setImage(MyImage.printImg(sinogram));


        analyzeButton.setDisable(false);
        storage = new Storage(calculator);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Label lab = new Label("Przetwarzanie");
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                storage.renderImages();
                int cur = storage.getRenderedImagesNumber();
                int max = calculator.getIterations() * 2;
                while (cur != max) {
//                    Thread.sleep(100);
//                    double processed = cur / max;
//                    lab.setText(String.format("%5.2f%",processed));
                    cur = storage.getRenderedImagesNumber();
                    updateProgress(cur, max);
                }
//                lab.setText("Zakonczono !");
                return null;
            }
        };

        alert.setWidth(400);
        alert.setHeight(100);
        alert.setHeaderText(null);
        ProgressBar pb = new ProgressBar();
//        pb.setMaxWidth(pb.getParent().getScene().getWidth());
        pb.progressProperty().bind(task.progressProperty());
        alert.getDialogPane().setContent(new VBox(lab,pb));
        alert.show();
        new Thread(task).start();

    }

    @FXML
    private void analyze() {
        calculator.setFiltering(filterCheckBox.isSelected());

        slider.setDisable(false);

        biggerImgTab.setDisable(false);
        sliderCopy.setDisable(false);

        parseVariables();

        //check if img is available

        Image image = getImage(iterations - 1, filterCheckBox.isSelected());
//        Image image = calculator.renderImgFromSinogram(iterations - 1, filterCheckBox.isSelected());

        outputImageView.setImage(image);
        copyOutputImageView.setImage(image);


//        resize();
    }

    private Image getImage(int i, boolean b) {
        if (storage.exists(i, b)) {
            return storage.getImage(i, b);

        } else {
            return calculator.renderImgFromSinogram(i, b);
        }
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
        assert oryginalImgView != null : "fx:id=\"oryginalImgView\" was not injected: check your FXML file 'window.fxml'.";
        assert outputImageView != null : "fx:id=\"outputImageView\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramImgView != null : "fx:id=\"sinogramImgView\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramFilteredImgView != null : "fx:id=\"sinogramFilteredImgView\" was not injected: check your FXML file 'window.fxml'.";
        assert copyOutputImageView != null : "fx:id=\"copyOutputImageView\" was not injected: check your FXML file 'window.fxml'.";

        assert imagePickButton != null : "fx:id=\"imagePickButton\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramButton != null : "fx:id=\"sinogramButton\" was not injected: check your FXML file 'window.fxml'.";

        assert slider != null : "fx:id=\"slider\" was not injected: check your FXML file 'window.fxml'.";
        assert sliderCopy != null : "fx:id=\"sliderCopy\" was not injected: check your FXML file 'window.fxml'.";

        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'window.fxml'.";
        assert progressBarCopy != null : "fx:id=\"progressBarCopy\" was not injected: check your FXML file 'window.fxml'.";

        assert angleAlfaTextField != null : "fx:id=\"angleAlfaTextField\" was not injected: check your FXML file 'window.fxml'.";
        assert angleLtextField != null : "fx:id=\"angleLtextField\" was not injected: check your FXML file 'window.fxml'.";
        assert countEmitersTextField != null : "fx:id=\"countEmitersTextField\" was not injected: check your FXML file 'window.fxml'.";
        assert iterationsTextField != null : "fx:id=\"iterationsTextField\" was not injected: check your FXML file 'window.fxml'.";

        assert sliderProgressLabel != null : "fx:id=\"sliderProgressLabel\" was not injected: check your FXML file 'window.fxml'.";
        assert sliderProgressCopyLabel != null : "fx:id=\"sliderProgressCopyLabel\" was not injected: check your FXML file 'window.fxml'.";

        assert itersRequiredCheckBox != null : "fx:id=\"itersRequiredCheckBox\" was not injected: check your FXML file 'window.fxml'.";
        assert filterCheckBox != null : "fx:id=\"filterCheckBox\" was not injected: check your FXML file 'window.fxml'.";

        assert sinogramTextArea != null : "fx:id=\"sinogramTextArea\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramFilteredTextArea != null : "fx:id=\"sinogramFilteredTextArea\" was not injected: check your FXML file 'window.fxml'.";

        assert sinogramTab != null : "fx:id=\"sinogramTab\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramFilteredTab != null : "fx:id=\"sinogramFilteredTab\" was not injected: check your FXML file 'window.fxml'.";
        assert sinogramImgTab != null : "fx:id=\"sinogramImgTab\" was not injected: check your FXML file 'window.fxml'.";

        setUpTextFormatters();

        slider.valueProperty().addListener((ov, old_val, new_val) -> progressBar.setProgress(new_val.doubleValue() / 100));
        sliderCopy.valueProperty().addListener((ov, old_val, new_val) -> progressBarCopy.setProgress(new_val.doubleValue() / 100));

        setInitValues();
        setUpSlider();
        setUpTextFields();

//        setUpImgViews();
    }


    private void setUpTextFields() {

        angleAlfaTextField.selectedTextProperty().addListener((o, old, new_val) -> {
//            System.err.println("Changed");
            if (itersRequiredCheckBox.isSelected()) {
                iterationsTextField.setText(String.valueOf((int) (180 / Double.parseDouble(angleAlfaTextField.getText().replace(",", "."))) - 1));
            }
        });
    }

    private void setUpSlider() {
        slider.valueProperty().addListener((ov, old_val, new_val) -> updateIter(new_val));

        sliderCopy.valueProperty().addListener((ov, old_val, new_val) -> updateIter(new_val));
    }

    private MyImage myImg;

    private Thread getCalcualtingThread() {
        return new Thread(() -> myImg.mediumSquaredError(oryginalImgView, outputImageView));
    }

    private Thread getUpdatingThread() {
        return new Thread(this::updateOutputImage);
    }


    private void updateIter(Number num) {
        int prev = currentIter;
        currentIter = (int) (num.doubleValue() * calculator.getIterations() / 100);
        if (prev != currentIter) {
            getCalcualtingThread().start();
            getUpdatingThread().start();
        }
        sliderProgressLabel.setText(String.format("Iteracja: %d\tBłąd średniokwadratowy: %5.2f", currentIter, myImg.getError()));
        sliderProgressCopyLabel.setText(String.format("Iteracja: %d\tBłąd średniokwadratowy: %5.2f", currentIter, myImg.getError()));
    }


    private void updateOutputImage() {
        Platform.runLater(() -> {
            Image im = getImage(currentIter, filterCheckBox.isSelected());
//            Image im = calculator.renderImgFromSinogram(currentIter, filterCheckBox.isSelected());
            outputImageView.setImage(im);
            copyOutputImageView.setImage(im);
        });
    }

    private void setInitValues() {
        angleAlfaTextField.setText("1");
        angleLtextField.setText("179");
        countEmitersTextField.setText("90");
        iterationsTextField.setText("179");
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
