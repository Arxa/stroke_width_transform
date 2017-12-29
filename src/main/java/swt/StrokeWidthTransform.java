package swt;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.text.extraction.swt.LineCandidate;
import org.openimaj.image.text.extraction.swt.SWTTextDetector;
import org.openimaj.image.text.extraction.swt.WordCandidate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stroke Width Transform
 */
public class StrokeWidthTransform {

    /**
     * Checks whether the Mat input image contains text by checking the results of SWTTextDetector
     * @param mat the input Mat image
     * @return true if image contains text - false otherwise
     */
    public static boolean containsText(Mat mat)
    {
        SWTTextDetector detector = new SWTTextDetector();
        detector.getOptions().direction = SWTTextDetector.Direction.LightOnDark;
        detector.getOptions().minHeight = 0;
        detector.getOptions().minArea = 0;
        detector.getOptions().minLettersPerLine = 1;
        MBFImage image;
        try {
            image = ImageUtilities.readMBF(matToInputStream(mat));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        detector.analyseImage(image.flatten());
        if (detector.getLines().size() > 0){
            // some lines/text was detected
            // add size filtering - are the lines/text detected big enough to count them as text?
            for (LineCandidate line : detector.getLines()){
                for (WordCandidate word : line.getWords()){
                    if (word.getRegularBoundingBox().height * word.getRegularBoundingBox().width > mat.height() * mat.width() * 0.005){
                        return true;
                    }
                }
            }
            return false;
        }
        else {
            SWTTextDetector detector2 = new SWTTextDetector();
            // try with DarkOnLight in case the background was white
            detector2.getOptions().direction = SWTTextDetector.Direction.DarkOnLight;
            detector2.getOptions().minHeight = 0;
            detector2.getOptions().minArea = 0;
            detector2.getOptions().minLettersPerLine = 1;
            detector2.analyseImage(image.flatten());
            if (detector2.getLines().size() > 0){
                // some lines/text was detected
                // add size filtering - are the lines/text detected big enough to count them as text?
                for (LineCandidate line : detector2.getLines()){
                    for (WordCandidate word : line.getWords()){
                        if (word.getRegularBoundingBox().height * word.getRegularBoundingBox().width > mat.height() * mat.width() * 0.005){
                            return true;
                        }
                    }
                }
                return false;
            } else return false;
        }
    }

    /**
     * Automatically detects the text on the image and draws the bounding boxes, using the SWTTextDetector class.
     */
    public static void detectText(File imageFile){
        final SWTTextDetector detector = new SWTTextDetector();
        detector.getOptions().direction = SWTTextDetector.Direction.LightOnDark;
        detector.getOptions().minHeight = 0;
        detector.getOptions().minArea = 0;
        detector.getOptions().minLettersPerLine = 1;
        final MBFImage image;
        try {
            image = ImageUtilities.readMBF(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        detector.analyseImage(image.flatten());
        for (final LineCandidate line : detector.getLines()) {
            image.drawShape(line.getRegularBoundingBox(), 3, RGBColour.RED);
        }
        DisplayUtilities.display(image, "Filtered candidate letters, lines and words.");
    }

    private static InputStream matToInputStream(Mat mat){
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".png", mat, bytemat);
        byte[] bytes = bytemat.toArray();
        return new ByteArrayInputStream(bytes);
    }

}
