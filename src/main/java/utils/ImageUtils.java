package utils;

import java.awt.image.BufferedImage;
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

    public static JLabel getPieceLabelForKoma(BufferedImage image, int i, int j, long xOffset, long yOffset, long centerX, long centerY) {
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

    public static BufferedImage loadImageFromResources(String imageName) {
        try {
            return ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream(imageName));
        } catch (IOException ex) {
            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static void drawImage(Board board, JPanel boardPanel, String imageName, long xCoord, long yCoord, long width, long height, long centerX, long centerY) {
        ImageCache imageCache = board.getImageCache();
        BufferedImage imageFile = imageCache.getImage(imageName);
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
