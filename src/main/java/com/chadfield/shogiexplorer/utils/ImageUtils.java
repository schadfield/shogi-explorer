package com.chadfield.shogiexplorer.utils;

import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Dimension;
import com.chadfield.shogiexplorer.objects.ImageCache;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class ImageUtils {

    private ImageUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static JLabel getPieceLabelForKoma(Image image, Coordinate boardCoord, Dimension offset, Coordinate imageLocation) {
        JLabel pieceLabel = new JLabel(new ImageIcon(image));
        pieceLabel.setBounds(
                imageLocation.getX() + (boardCoord.getX() * MathUtils.KOMA_X + offset.getWidth()),
                imageLocation.getY() + (boardCoord.getY() * MathUtils.KOMA_Y + offset.getHeight()),
                MathUtils.KOMA_X,
                MathUtils.KOMA_Y);
        return pieceLabel;
    }

    public static JLabel getTextLabelForBan(Coordinate boardCoord, Dimension offset, Coordinate imageLocation, String text) {
        JLabel numberLabel = new JLabel(text);
        numberLabel.setBounds(
                imageLocation.getX() + (boardCoord.getX() * MathUtils.KOMA_X + offset.getWidth()),
                imageLocation.getY() + (boardCoord.getY() * MathUtils.KOMA_Y + offset.getHeight()),
                MathUtils.KOMA_X,
                MathUtils.KOMA_Y);
        return numberLabel;
    }

    public static Image loadSVGImageFromResources(String imageName, Dimension imageDimension) {
        Image image1 = null;
        Image image2 = null;
        Image image3 = null;
        Image image4 = null;

        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(imageName + ".svg");
            image1 = transcodeSVGToBufferedImage(inputStream, imageDimension.getWidth(), imageDimension.getHeight());
            inputStream.close();
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(imageName + ".svg");
            image2 = transcodeSVGToBufferedImage(inputStream, imageDimension.getWidth() * 1.25, imageDimension.getHeight() * 1.25);
            inputStream.close();
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(imageName + ".svg");
            image3 = transcodeSVGToBufferedImage(inputStream, imageDimension.getWidth() * 1.5, imageDimension.getHeight() * 1.5);
            inputStream.close();
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(imageName + ".svg");
            image4 = transcodeSVGToBufferedImage(inputStream, imageDimension.getWidth() * 2.0, imageDimension.getHeight() * 2.0);
            inputStream.close();
        } catch (TranscoderException | IOException ex) {
            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        BaseMultiResolutionImage mri = new BaseMultiResolutionImage(image1, image2, image3, image4);
        return (mri);
    }

    public static BufferedImage transcodeSVGToBufferedImage(InputStream inputStream, double width, double height) throws TranscoderException {
        // Create a PNG transcoder.
        PNGTranscoder t = new PNGTranscoder() {
            @Override
            protected ImageRenderer createRenderer() {
                ImageRenderer r = super.createRenderer();
                RenderingHints rh = r.getRenderingHints();
                rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                rh.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE));
                rh.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
                rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
                rh.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
                rh.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));
                return r;
            }
        };

        // Set the transcoding hints.
        t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) width);
        t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) height);
        t.addTranscodingHint(PNGTranscoder.KEY_GAMMA, (float) 2.2);

        try {
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

        } catch (IOException | TranscoderException ex) {
            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Image loadIconImageFromResources(String imageName) {
        Image image1 = null;
        Image image2 = null;
        try {
            image1 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_128x128.png"));
            image2 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_256x256.png"));
        } catch (IOException ex) {
            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        BaseMultiResolutionImage mri = new BaseMultiResolutionImage(image1, image2);
        return (mri);
    }

    public static Image loadTaskbarImageFromResources(String imageName) {
        Image image1 = null;
        Image image2 = null;
        Image image3 = null;
        Image image4 = null;
        Image image5 = null;
        Image image6 = null;
        try {
            image1 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_16x16.png"));
            image2 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_32x32.png"));
            image3 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_64x64.png"));
            image4 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_128x128.png"));
            image5 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_256x256.png"));
            image6 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "_512x512.png"));

        } catch (IOException ex) {
            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        BaseMultiResolutionImage mri = new BaseMultiResolutionImage(image1, image2, image3, image4, image5, image6);
        return (mri);
    }

    public static void drawImage(ImageCache imageCache, JPanel boardPanel, String imageName, Coordinate imageCoordinate, Dimension imageDimension, Coordinate offset) {
        Image imageFile = imageCache.getImage(imageName);
        if (imageFile == null) {
            imageFile = loadSVGImageFromResources(imageName, imageDimension);
            imageCache.putImage(imageName, imageFile);
        }
        JLabel imageLable = new JLabel(new ImageIcon(imageFile));
        imageLable.setBounds(
                offset.getX() + imageCoordinate.getX(),
                offset.getY() + imageCoordinate.getY(),
                imageDimension.getWidth(),
                imageDimension.getHeight());
        boardPanel.add(imageLable);
    }
}
