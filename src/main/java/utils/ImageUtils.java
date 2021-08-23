package utils;

import java.awt.RenderingHints;
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
import objects.Board;
import objects.Koma;
import main.RenderBoard;
import objects.ScaledImageCache;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class ImageUtils {

    public static BufferedImage transcodeSVGToBufferedImage(File file, long width, long height) throws TranscoderException {
        Transcoder transcoder = new PNGTranscoder() {
            @Override
            protected ImageRenderer createRenderer() {
                ImageRenderer r = super.createRenderer();

                RenderingHints rh = r.getRenderingHints();

                //TODO: Can we improve this?
                rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC));
                rh.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON));
                rh.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_DITHERING,
                        RenderingHints.VALUE_DITHER_ENABLE));
                rh.add(new RenderingHints(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL,
                        RenderingHints.VALUE_STROKE_PURE));
                rh.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS,
                        RenderingHints.VALUE_FRACTIONALMETRICS_ON));
                rh.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT));
                r.setRenderingHints(rh);
                return r;
            }
        };

        //int RESOLUTION_DPI = 226;
        //float SCALE_BY_RESOLUTION = RESOLUTION_DPI / 72f;
        //float scaledWidth = width * SCALE_BY_RESOLUTION;
        //float scaledHeight = height * SCALE_BY_RESOLUTION;
        //float pixelUnitToMM = new Float(25.4f / RESOLUTION_DPI);

        // Set the transcoding hints.
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);
        //transcoder.addTranscodingHint(PNGTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, pixelUnitToMM);

        try ( FileInputStream inputStream = new FileInputStream(file)) {
            // Create the transcoder input.
            TranscoderInput input = new TranscoderInput(inputStream);

            // Create the transcoder output.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);

            // Save the image.
            transcoder.transcode(input, output);

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

    public static JLabel getPieceLabelForKoma(double scale, BufferedImage image, int i, int j, long xOffset, long yOffset, long centerX, long centerY) {
        JLabel pieceLabel = new JLabel(new ImageIcon(image));
        pieceLabel.setBounds(
                (int) (centerX + Math.round(scale * (i * MathUtils.KOMA_X + xOffset))),
                (int) (centerY + Math.round(scale * (j * MathUtils.KOMA_Y + yOffset))),
                (int) Math.round(scale * MathUtils.KOMA_X),
                (int) Math.round(scale * MathUtils.KOMA_Y));
        return pieceLabel;
    }

    public static JLabel getTextLabelForBan(double scale, int i, int j, double xOffset, double yOffset, long centerX, long centerY, String text) {
        JLabel numberLabel = new JLabel(text);
        numberLabel.setBounds(
                (int) (centerX + Math.round(scale * (i * MathUtils.KOMA_X + xOffset))),
                (int) (centerY + Math.round(scale * (j * MathUtils.KOMA_Y + yOffset))),
                (int) Math.round(scale * MathUtils.KOMA_X),
                (int) Math.round(scale * MathUtils.KOMA_Y));
        return numberLabel;
    }

    public static BufferedImage getScaledKomaImage(Koma.Type komaType, double scale) {
        File imageFile = FileUtils.getKomaImageFile(komaType);
        try {
            return transcodeSVGToBufferedImage(imageFile, Math.round(scale * MathUtils.KOMA_X), Math.round(scale * MathUtils.KOMA_Y));
        } catch (TranscoderException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static BufferedImage getScaledImage(ScaledImageCache imageCache, String imageName, long width, long height) {
        BufferedImage imageFile = imageCache.getImage(imageName);
        if (imageFile == null) {
            File sourceFile = new File(FileUtils.RESOURCE_PATH + imageName);
            try {
                double scale = imageCache.getScale();
                imageFile = transcodeSVGToBufferedImage(sourceFile, Math.round(scale * width), Math.round(scale * height));
                imageCache.putImage(imageName, imageFile);
            } catch (TranscoderException ex) {
                Logger.getLogger(RenderBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return imageFile;
    }

    public static void drawImage(Board board, JPanel boardPanel, String imageName, long xCoord, long yCoord, long width, long height, long centerX, long centerY) {
        ScaledImageCache scaledImageCache = board.getScaledImageCache();
        double scale = scaledImageCache.getScale();
        BufferedImage imageFile = scaledImageCache.getImage(imageName);
        if (imageFile == null) {
            File sourceFile = new File(FileUtils.RESOURCE_PATH + imageName);
            try {
                imageFile = transcodeSVGToBufferedImage(sourceFile, Math.round(scale * width), Math.round(scale * height));
                scaledImageCache.putImage(imageName, imageFile);
            } catch (TranscoderException ex) {
                Logger.getLogger(RenderBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JLabel imageLable = new JLabel(new ImageIcon(imageFile));
        imageLable.setBounds(
                (int) (centerX + Math.round(scale * xCoord)),
                (int) (centerY + Math.round(scale * yCoord)),
                (int) (Math.round(scale * width)),
                (int) (Math.round(scale * height)));
        boardPanel.add(imageLable);
    }

}
