package agh.edu.pl.healthmonitoringsystemai.breastCancerPredictionAi;

import agh.edu.pl.healthmonitoringsystemai.exception.InvalidImageException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;


@Component
public class ImageDecoder {

    public BufferedImage decodeBase64Image(String base64Data) {
        try {
//            String base64Data = extractBase64Data(base64Str);
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (Exception ex) {
            throw new InvalidImageException("Failed to decode base64 image: " + ex.getMessage());
        }
    }

    private String extractBase64Data(String base64Str) {
        // Input should be in the format "data:image/png;base64,XXXX"
        String[] parts = base64Str.split(",");
        if (parts.length != 2) {
            throw new InvalidImageException("Invalid base64 image format.");
        }
        return parts[1];
    }
}
