package utils;

import javax.swing.JPanel;

public class MathUtils {

    public static final int KOMA_X = 43;
    public static final int KOMA_Y = 48;
    public static final int BOARD_XY = 9;
    public static final int COORD_XY = 20;

    public static float calculateScaleFactor(JPanel board) {
        float xScale = (float) board.getWidth() / ((float) KOMA_X * (float) (BOARD_XY + 2) + (float) COORD_XY * 6);
        float yScale = (float) board.getHeight() / ((float) KOMA_Y * (float) BOARD_XY + (float) COORD_XY * 2);
        if (xScale < yScale) {
            return xScale;
        } else {
            return yScale;
        }
    }

}
