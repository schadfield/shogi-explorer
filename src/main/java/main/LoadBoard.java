package main;

import objects.ScaledImageCache;
import objects.Koma;
import objects.Board;
import utils.MathUtils;
import utils.ImageUtils;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import static utils.MathUtils.calculateScaleFactor;
import static utils.StringUtils.substituteKomaName;

public class LoadBoard {

    public static void loadBoard(Board board, javax.swing.JPanel boardPanel) {
        // TODO: why is loadBoard() being called when the boardPanel has no width?
        if (boardPanel.getWidth() == 0) {
            return;
        }

        float scale = calculateScaleFactor(boardPanel);

        // If the scale of the board has changed we need to create a new image cache.
        if (board.getScaledImageCache() == null || Float.compare(scale, board.getScaledImageCache().getScale()) != 0) {
            board.setScaledImageCache(new ScaledImageCache(scale));
        }

        // Start with a clean slate.
        boardPanel.removeAll();

        drawPieces(board, boardPanel, scale);

        // TODO: drawPiecesInHand();
        // TODO: drawCoordinates();
        drawGrid(board, scale, boardPanel);
        drawBans(board, scale, boardPanel);
        drawBackground(board, scale, boardPanel);

        boardPanel.setVisible(false);
        boardPanel.setVisible(true);
    }

    public static void drawPieces(Board board, JPanel boardPanel, float scale) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.masu[i][j] != null) {
                    Koma koma = board.masu[i][j];
                    BufferedImage pieceImage = ImageUtils.getScaledImage(
                            board.getScaledImageCache(),
                            substituteKomaName(koma.getType().toString()) + ".svg",
                            Math.round(scale * MathUtils.KOMA_X), Math.round(scale * MathUtils.KOMA_Y)
                    );
                    boardPanel.add(ImageUtils.getPieceLabelForKoma(board, pieceImage, i, j, scale));
                }
            }
        }
    }

    public static void drawGrid(Board board, float scale, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                scale,
                boardPanel,
                "grid.svg",
                Math.round(scale * (MathUtils.KOMA_X * 2 + MathUtils.COORD_XY)),
                0,
                Math.round((MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY) * scale),
                Math.round((MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY) * scale));
    }

    public static void drawBans(Board board, float scale, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                scale,
                boardPanel,
                "ban.svg",
                Math.round((MathUtils.KOMA_X * (MathUtils.BOARD_XY + 2) + MathUtils.COORD_XY * 4) * scale),
                Math.round((MathUtils.COORD_XY * 2 + MathUtils.KOMA_Y * 2) * scale),
                Math.round((MathUtils.KOMA_X * 2) * scale),
                Math.round((MathUtils.KOMA_Y * 7) * scale));

        ImageUtils.drawImage(
                board,
                scale,
                boardPanel,
                "ban.svg",
                0,
                0,
                Math.round((MathUtils.KOMA_X * 2) * scale),
                Math.round((MathUtils.KOMA_Y * 7) * scale));
    }

    public static void drawBackground(Board board, float scale, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                scale,
                boardPanel,
                "background.svg",
                Math.round(scale * (MathUtils.KOMA_X * 2 + MathUtils.COORD_XY)),
                0,
                Math.round((MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale),
                Math.round((MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale));
    }

}
