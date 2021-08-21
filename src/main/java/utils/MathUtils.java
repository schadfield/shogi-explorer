package utils;

import javax.swing.JPanel;

public class MathUtils {
    
    public static final int KOMA_X = 43;
    public static final int KOMA_Y = 48;
    public static final int BOARD_XY = 9;
    public static final int COORD_XY = 20;
    
    public static float calculateScaleFactor(JPanel board) {
        float xScale = ((float) board.getWidth()-10.0f) / ((float) KOMA_X * (float) (BOARD_XY + 2) + (float) COORD_XY * 6);
        float yScale = ((float) board.getHeight()-10.0f) / ((float) KOMA_Y * (float) BOARD_XY + (float) COORD_XY * 2);
        if (xScale < yScale) {
            return xScale;
        } else {
            return yScale;
        }
    }
    
    public static int calculateCenterX(JPanel board) {
        float xScale = (float) board.getWidth() / ((float) KOMA_X * (float) (BOARD_XY + 2) + (float) COORD_XY * 6);
        float yScale = (float) board.getHeight() / ((float) KOMA_Y * (float) BOARD_XY + (float) COORD_XY * 2);
        if (xScale < yScale) {
            return 5;
        } else {
            System.out.println("CenterX");
            return Math.round(((float) board.getWidth() - yScale * ((float) KOMA_X * (float) (BOARD_XY + 2) + (float) COORD_XY * 6)) / 2.0f);
        }
    }    
    
    public static int calculateCenterY(JPanel board) {
        float xScale = (float) board.getWidth() / ((float) KOMA_X * (float) (BOARD_XY + 2) + (float) COORD_XY * 6);
        float yScale = (float) board.getHeight() / ((float) KOMA_Y * (float) BOARD_XY + (float) COORD_XY * 2);
        if (xScale < yScale) {
            System.out.println("CenterY");
            return Math.round(((float) board.getHeight() - xScale * ((float) KOMA_Y * (float) BOARD_XY + (float) COORD_XY * 2.0f)) / 2.0f);
        } else {
            return 5;
        }
    }
    
}
