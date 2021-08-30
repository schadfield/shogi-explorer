package utils;

import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import objects.Board;
import objects.ImageCache;

public class ImageUtils {

    public static JLabel getPieceLabelForKoma(Image image, int i, int j, long xOffset, long yOffset, long centerX, long centerY) {
        JLabel pieceLabel = new JLabel(new ImageIcon(image));
        pieceLabel.setBounds(
                (int) (centerX + (i * MathUtils.KOMA_X + xOffset)),
                (int) (centerY + (j * MathUtils.KOMA_Y + yOffset)),
                (int) MathUtils.KOMA_X,
                (int) MathUtils.KOMA_Y);
        return pieceLabel;
    }

    public static JLabel getTextLabelForBan(int i, int j, double xOffset, double yOffset, long centerX, long centerY, String text) {
        JLabel numberLabel = new JLabel(text);
        numberLabel.setBounds(
                (int) (centerX + (i * MathUtils.KOMA_X + xOffset)),
                (int) (centerY + (j * MathUtils.KOMA_Y + yOffset)),
                (int) MathUtils.KOMA_X,
                (int) MathUtils.KOMA_Y);
        return numberLabel;
    }

    public static Image loadImageFromResources(String imageName) {
        Image image1 = null;
        Image image2 =null;
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

    public static void drawImage(Board board, JPanel boardPanel, String imageName, long xCoord, long yCoord, long width, long height, long centerX, long centerY) {
        ImageCache imageCache = board.getImageCache();
        Image imageFile = imageCache.getImage(imageName);
        if (imageFile == null) {
            imageFile = loadImageFromResources(imageName);
            imageCache.putImage(imageName, imageFile);
        }
        JLabel imageLable = new JLabel(new ImageIcon(imageFile));
        imageLable.setBounds(
                (int) (centerX + xCoord),
                (int) (centerY + yCoord),
                (int) (width),
                (int) (height));
        boardPanel.add(imageLable);
    }
}
