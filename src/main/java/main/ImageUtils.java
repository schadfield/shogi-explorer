package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class ImageUtils {

    public static BufferedImage transcodeSVGToBufferedImage(File file, int width, int height) throws TranscoderException {
        // Create a PNG transcoder.
        Transcoder t = new PNGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
        t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);
        t.addTranscodingHint(PNGTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 3.543f);
        try ( FileInputStream inputStream = new FileInputStream(file)) {
            // Create the transcoder input.
            TranscoderInput input = new TranscoderInput(inputStream);

            // Create the transcoder output.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);

            // Save the image.
            t.transcode(input, output);

            // Flush and close the stream.
            outputStream.flush();
            outputStream.close();

            // Convert the byte stream into an image.
            byte[] imgData = outputStream.toByteArray();
            return ImageIO.read(new ByteArrayInputStream(imgData));

        } catch (IOException | TranscoderException e) {
            //log.error("Conversion error", e);
        }
        return null;
    }

    public static JLabel getPieceLabelForKoma(Board board, BufferedImage image, int i, int j, float scale) {
        JLabel pieceLabel = new JLabel(new ImageIcon(image));
        pieceLabel.setBounds(Math.round(scale * ((i + 2) * MathUtils.KOMA_X + MathUtils.COORD_XY * 2)), Math.round(scale * (j * MathUtils.KOMA_Y + MathUtils.COORD_XY)), Math.round(scale * MathUtils.KOMA_X), Math.round(scale * MathUtils.KOMA_Y));
        return pieceLabel;
    }

    public static BufferedImage getScaledKomaImage(Koma.Type komaType, float scale) {
        File imageFile = KomaResources.getKomaImageFile(komaType);
        try {
            return transcodeSVGToBufferedImage(imageFile, Math.round(scale * MathUtils.KOMA_X), Math.round(scale * MathUtils.KOMA_Y));
        } catch (TranscoderException ex) {
            Logger.getLogger(KomaResources.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static BufferedImage getScaledImage(ScaledImageCache imageCache, String imageName, int width, int height) {
        BufferedImage imageFile = imageCache.getImage(imageName);
        if (imageFile == null) {
            File sourceFile = new File(KomaResources.RESOURCE_PATH + imageName);
            try {
                imageFile = transcodeSVGToBufferedImage(sourceFile, width, height);
                imageCache.putImage(imageName, imageFile);
            } catch (TranscoderException ex) {
                Logger.getLogger(LoadBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return imageFile;
    }

    public static void drawImage(Board board, float scale, JPanel boardPanel, String imageName, int xCoord, int yCoord, int width, int height) {
        BufferedImage imageFile = board.getScaledImageCache().getImage(imageName);
        if (imageFile == null) {
            File sourceFile = new File(KomaResources.RESOURCE_PATH + imageName);
            try {
                imageFile = transcodeSVGToBufferedImage(sourceFile, width, height);
                board.getScaledImageCache().putImage(imageName, imageFile);
            } catch (TranscoderException ex) {
                Logger.getLogger(LoadBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JLabel imageLable = new JLabel(new ImageIcon(imageFile));
        imageLable.setBounds(xCoord, yCoord, width, height);
        boardPanel.add(imageLable);
    }

}
