package tomograph;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


public class Controller {
    private double angleL, angleA;
    private int iterations, emiters;

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
    private Button analyzeButton;

    @FXML
    private TextField angleAlfaTextField;

    @FXML
    private TextField angleLtextField;

    @FXML
    private TextField countEmitersTextField;

    @FXML
    private TextField iterationsTextField;

    @FXML
    private void chooseImage() {

        File file = loadFile();

        if (file == null) return;

        MyImage myImage = new MyImage(file);

        calculator = new Calculator(myImage.getGreyScaleImgArr().length);

        calculator.setGreyScaleArr(myImage.getGreyScaleImgArr());

        oryginalImage.setImage(myImage.getFxImage());

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
        double[][] sinogram = calculator.getSinogram(new Settings(emiters, iterations, angleL, angleA));
        for (int i = 0; i < sinogram.length; i++) {
            System.out.format("%3.2f:\t",i*angleA);
            for (int j = 0; j < sinogram[0].length; j++) {
                System.out.format("%3.2f\t", sinogram[i][j]);

            }
            System.out.println();

        }
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
    void initialize() {
        assert oryginalImage != null : "fx:id=\"oryginalImage\" was not injected: check your FXML file 'window.fxml'.";
        assert outputImage != null : "fx:id=\"outputImage\" was not injected: check your FXML file 'window.fxml'.";
        assert imagePickButton != null : "fx:id=\"imagePickButton\" was not injected: check your FXML file 'window.fxml'.";
        assert slider != null : "fx:id=\"slider\" was not injected: check your FXML file 'window.fxml'.";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'window.fxml'.";
        assert angleAlfaTextField != null : "fx:id=\"angleAlfaTextField\" was not injected: check your FXML file 'window.fxml'.";
        assert angleLtextField != null : "fx:id=\"angleLtextField\" was not injected: check your FXML file 'window.fxml'.";
        assert countEmitersTextField != null : "fx:id=\"countEmitersTextField\" was not injected: check your FXML file 'window.fxml'.";
        assert iterationsTextField != null : "fx:id=\"iterationsTextField\" was not injected: check your FXML file 'window.fxml'.";


        setUpTextFormatters();

        slider.valueProperty().addListener((ov, old_val, new_val) -> progressBar.setProgress(new_val.doubleValue() / 100));

        setInitValues();
    }

    private void setInitValues() {
        angleAlfaTextField.setText("1");
        angleLtextField.setText("90");
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
