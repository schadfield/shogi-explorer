package main;

import static main.ImageUtils.transcodeSVGToBufferedImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static main.MathUtils.calculateScaleFactor;
import org.apache.batik.transcoder.TranscoderException;

public class LoadBoard {

    public static void loadBoard(Board board, javax.swing.JPanel boardPanel) {
        float scale;

        if (boardPanel.getWidth() == 0) {
            return;
        }

        scale = calculateScaleFactor(boardPanel);

        // If the scale of the board has changed we need to create a new image cache.
        if (board.getScaledImageCache() == null || scale != board.getScaledImageCache().getScale()) {
            board.setScaledImageCache(new ScaledImageCache(scale));
        }

        // Start with a clean slate.
        boardPanel.removeAll();

        drawPieces(board, boardPanel, scale);

        // drawPiecesInHand();
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
                    BufferedImage pieceImage = board.getScaledImageCache().getImage(koma.getType().toString());
                    if (pieceImage == null) {
                        pieceImage = ImageUtils.getScaledKomaImage(koma.getType(), scale);
                        board.getScaledImageCache().putImage(koma.getType().toString(), pieceImage);
                    }
                    boardPanel.add(ImageUtils.getPieceLabelForKoma(board, pieceImage, i, j, scale));
                }
            }
        }
    }

    public static void drawGrid(Board board, float scale, JPanel boardPanel) {
        drawImage(
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
        drawImage(
                board,
                scale,
                boardPanel,
                "ban.svg",
                Math.round((MathUtils.KOMA_X * (MathUtils.BOARD_XY + 2) + MathUtils.COORD_XY * 4) * scale),
                Math.round((MathUtils.COORD_XY * 2 + MathUtils.KOMA_Y * 2) * scale),
                Math.round((MathUtils.KOMA_X * 2) * scale),
                Math.round((MathUtils.KOMA_Y * 7) * scale));

        drawImage(
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
        drawImage(
                board,
                scale,
                boardPanel,
                "background.svg",
                Math.round(scale * (MathUtils.KOMA_X * 2 + MathUtils.COORD_XY)),
                0,
                Math.round((MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale),
                Math.round((MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale));
    }

    public static void drawImage(Board board, float scale, JPanel boardPanel, String imageName, int xCoord, int yCoord, int width, int height) {
        BufferedImage imageFile = board.getScaledImageCache().getImage(imageName);
        if (imageFile == null) {
            File sourceFile = new File(KomaResources.RESOURCE_PATH + imageName);
            try {
                imageFile = transcodeSVGToBufferedImage(sourceFile, width, height);
                board.getScaledImageCache().putImage(imageName, imageFile);
            } catch (TranscoderException ex) {
                Logger.getLogger(LoadBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JLabel imageLable = new JLabel(new ImageIcon(imageFile));
        imageLable.setBounds(xCoord, yCoord, width, height);
        boardPanel.add(imageLable);
    }

}
