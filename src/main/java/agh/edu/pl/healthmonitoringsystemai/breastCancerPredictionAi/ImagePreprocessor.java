package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystemai.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;


@Component
public class ImagePreprocessor {

    private static final int TARGET_WIDTH = 224;
    private static final int TARGET_HEIGHT = 224;
    private static final int RGB_CHANNELS_NUMBER = 3;
    private final ImageDecoder imageDecoder;

    @Autowired
    public ImagePreprocessor(ImageDecoder imageDecoder) {
        this.imageDecoder = imageDecoder;
    }

    public float[][][][] preprocessImage(String base64Image) {
        try {
            BufferedImage decodedImage = imageDecoder.decodeBase64Image(base64Image);
            BufferedImage resizedImage = resizeImage(decodedImage);
            return normalizeImage(resizedImage);
        } catch (Exception e) {
            throw new InvalidImageException("Error during image preprocessing: " + e.getMessage());
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage.getScaledInstance(TARGET_WIDTH, TARGET_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();
        return resizedImage;
    }

    private float[][][][] normalizeImage(BufferedImage image) {
        float[][][][] imageArray = new float[1][TARGET_HEIGHT][TARGET_WIDTH][RGB_CHANNELS_NUMBER];
        for (int y = 0; y < TARGET_HEIGHT; y++) {
            for (int x = 0; x < TARGET_WIDTH; x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                imageArray[0][y][x][0] = pixelColor.getRed() / 255.0f;   // Red channel
                imageArray[0][y][x][1] = pixelColor.getGreen() / 255.0f; // Green channel
                imageArray[0][y][x][2] = pixelColor.getBlue() / 255.0f;  // Blue channel
            }
        }
        return imageArray;
    }
}
