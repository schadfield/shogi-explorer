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

    public static JLabel getTextLabelForBan(Coordinate boardCoord, Dimension offset, Coordinate ImageLocation, String text) {
        JLabel numberLabel = new JLabel(text);
        numberLabel.setBounds(
                ImageLocation.getX() + (boardCoord.getX() * MathUtils.KOMA_X + offset.getWidth()),
                ImageLocation.getY() + (boardCoord.getY() * MathUtils.KOMA_Y + offset.getHeight()),
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
        BaseMultiResolutionImage mri = new BaseMultiResolutionImage(new Image[]{image1, image2, image3});
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
