import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import swt.StrokeWidthTransform;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        System.load("src\\main\\resources\\natives\\opencv_320_64.dll");
        File imageFile = new File("path\\to\\imageToTest.png");
        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        boolean result = StrokeWidthTransform.containsText(image);
        System.out.println(result);

        // OR

        StrokeWidthTransform.detectText(imageFile);
    }
}
