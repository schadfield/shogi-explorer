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
                    BufferedImage pieceImage = board.getScaledImageCache().getKomaImage(koma.getType());
                    if (pieceImage == null) {
                        pieceImage = ImageUtils.getScaledKomaImage(koma.getType(), scale);
                        board.getScaledImageCache().putKomaImage(koma.getType(), pieceImage);
                    }
                    boardPanel.add(ImageUtils.getPieceLabelForKoma(board, pieceImage, i, j, scale));
                }
            }
        }
    }

    public static void drawGrid(Board board, float scale, JPanel boardPanel) {
        BufferedImage masuImage = board.getScaledImageCache().getMasu();
        if (masuImage == null) {
            File gridFile = new File(KomaResources.RESOURCE_PATH + "grid.svg");
            try {
                masuImage = transcodeSVGToBufferedImage(gridFile, Math.round((MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY) * scale), Math.round((MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY) * scale));
                board.getScaledImageCache().setMasu(masuImage);
            } catch (TranscoderException ex) {
                Logger.getLogger(LoadBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JLabel gridLabel = new JLabel(new ImageIcon(masuImage));
        gridLabel.setBounds(Math.round(scale * (MathUtils.KOMA_X * 2 + MathUtils.COORD_XY)), 0, Math.round((MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY) * scale), Math.round((MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY) * scale));
        boardPanel.add(gridLabel);
    }

    public static void drawBans(Board board, float scale, JPanel boardPanel) {
        BufferedImage banImage = board.getScaledImageCache().getBan();
        if (banImage == null) {
            File bgFile = new File(KomaResources.RESOURCE_PATH + "ban.svg");
            try {
                banImage = transcodeSVGToBufferedImage(bgFile, Math.round((MathUtils.KOMA_X * 2) * scale), Math.round((MathUtils.KOMA_Y * 7) * scale));
                board.getScaledImageCache().setBan(banImage);
            } catch (TranscoderException ex) {
                Logger.getLogger(LoadBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JLabel banLabel = new JLabel(new ImageIcon(banImage));
        banLabel.setBounds(0, 0, Math.round((MathUtils.KOMA_X * 2) * scale), Math.round((MathUtils.KOMA_Y * 7) * scale));
        boardPanel.add(banLabel);
        JLabel banLabel2 = new JLabel(new ImageIcon(banImage));
        banLabel2.setBounds(Math.round((MathUtils.KOMA_X * (MathUtils.BOARD_XY + 2) + MathUtils.COORD_XY * 4) * scale), Math.round((MathUtils.COORD_XY * 2 + MathUtils.KOMA_Y * 2) * scale), Math.round((MathUtils.KOMA_X * 2) * scale), Math.round((MathUtils.KOMA_Y * 7) * scale));
        boardPanel.add(banLabel2);
    }

    public static void drawBackground(Board board, float scale, JPanel boardPanel) {
        BufferedImage masuBGImage = board.getScaledImageCache().getMasuBG();
        if (masuBGImage == null) {
            File bgFile = new File(KomaResources.RESOURCE_PATH + "background.svg");
            try {
                masuBGImage = transcodeSVGToBufferedImage(bgFile, Math.round((MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale), Math.round((MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale));
                board.getScaledImageCache().setMasuBG(masuBGImage);
            } catch (TranscoderException ex) {
                Logger.getLogger(LoadBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JLabel bgLabel = new JLabel(new ImageIcon(masuBGImage));
        bgLabel.setBounds(Math.round(scale * (MathUtils.KOMA_X * 2 + MathUtils.COORD_XY)), 0, Math.round((MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale), Math.round((MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2) * scale));
        boardPanel.add(bgLabel);
    }
}
