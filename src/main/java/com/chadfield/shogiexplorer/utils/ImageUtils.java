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
import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Dimension;
import com.chadfield.shogiexplorer.objects.ImageCache;

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

    public static Image loadImageFromResources(String imageName) {
        Image image1 = null;
        Image image2 = null;
        Image image3 = null;
        try {
            image1 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + ".png"));
            image2 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "@1.25x.png"));
            image3 = ImageIO.read(ClassLoader.getSystemClassLoader().getResource(imageName + "@2x.png"));
        } catch (IOException ex) {
            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        BaseMultiResolutionImage mri = new BaseMultiResolutionImage(image1, image2, image3);
        return (mri);
    }

    public static Image loadIconImageFromResources(String imageName) {
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

    public static void drawImage(Board board, JPanel boardPanel, String imageName, Coordinate imageCoordinate, Dimension imageDimension, Coordinate offset) {
        ImageCache imageCache = board.getImageCache();
        Image imageFile = imageCache.getImage(imageName);
        if (imageFile == null) {
            imageFile = loadImageFromResources(imageName);
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
