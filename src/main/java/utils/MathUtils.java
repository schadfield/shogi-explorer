package utils;

import javax.swing.JPanel;
import objects.ScaleItem;

public class MathUtils {

    public static final int KOMA_X = 43;
    public static final int KOMA_Y = 48;
    public static final int BOARD_XY = 9;
    public static final int COORD_XY = 20;

    public static ScaleItem calculateScaleFactor(JPanel board) {
        ScaleItem scaleItem = new ScaleItem();
        double xScale = ((double) board.getWidth()-10.0f) / ((double) KOMA_X * (double) (BOARD_XY + 2) + (double) COORD_XY * 6);
        double yScale = ((double) board.getHeight()-10.0f) / ((double) KOMA_Y * (double) BOARD_XY + (double) COORD_XY * 2);
        if (xScale < yScale) {
            scaleItem.setScale(xScale);
            scaleItem.setCenterX(5);
            scaleItem.setCenterY(Math.round(((double) board.getHeight() - xScale * ((double) KOMA_Y * (double) BOARD_XY + (double) COORD_XY * 2.0f)) / 2.0f));
        } else {
            scaleItem.setScale(yScale);
            scaleItem.setCenterX(Math.round(((double) board.getWidth() - yScale * ((double) KOMA_X * (double) (BOARD_XY + 2) + (double) COORD_XY * 6)) / 2.0f));
            scaleItem.setCenterY(5);
        }
        return scaleItem;
    }
}
