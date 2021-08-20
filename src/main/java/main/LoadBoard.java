package main;

import objects.ScaledImageCache;
import objects.Koma;
import objects.Board;
import utils.MathUtils;
import utils.ImageUtils;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JPanel;
import static utils.MathUtils.calculateScaleFactor;
import static utils.StringUtils.substituteKomaName;

public class LoadBoard {

    public static final HashMap<Koma.Type, Integer> xOffsetMap = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> yOffsetMap = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> xCoordMap = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> yCoordMap = new HashMap<>();

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

        drawPieces(board, boardPanel);
        drawPiecesInHand(board, boardPanel);
        // TODO: drawCoordinates();
        drawGrid(board, boardPanel);
        drawBans(board, boardPanel);
        drawBackground(board, boardPanel);

        boardPanel.setVisible(false);
        boardPanel.setVisible(true);
    }

    public static void drawPiecesInHand(Board board, JPanel boardPanel) {
        final int SBAN_XOFFSET = (MathUtils.BOARD_XY + 2) * MathUtils.KOMA_X + MathUtils.COORD_XY*4;
        final int SBAN_YOFFSET = MathUtils.KOMA_Y * 2 + MathUtils.COORD_XY*2;
        
        xOffsetMap.put(Koma.Type.GFU, 0);
        yOffsetMap.put(Koma.Type.GFU, 0);
        xCoordMap.put( Koma.Type.GFU, 0);
        yCoordMap.put( Koma.Type.GFU, 0);
        xOffsetMap.put(Koma.Type.GKY, 0);
        yOffsetMap.put(Koma.Type.GKY, 0);
        xCoordMap.put( Koma.Type.GKY, 0);
        yCoordMap.put( Koma.Type.GKY, 1);
        xOffsetMap.put(Koma.Type.GKE, 0);
        yOffsetMap.put(Koma.Type.GKE, 0);
        xCoordMap.put( Koma.Type.GKE, 0);
        yCoordMap.put( Koma.Type.GKE, 2);
        xOffsetMap.put(Koma.Type.GGI, 0);
        yOffsetMap.put(Koma.Type.GGI, 0);
        xCoordMap.put( Koma.Type.GGI, 0);
        yCoordMap.put( Koma.Type.GGI, 3);
        xOffsetMap.put(Koma.Type.GKI, 0);
        yOffsetMap.put(Koma.Type.GKI, 0);
        xCoordMap.put( Koma.Type.GKI, 0);
        yCoordMap.put( Koma.Type.GKI, 4);
        xOffsetMap.put(Koma.Type.GKA, 0);
        yOffsetMap.put(Koma.Type.GKA, 0);
        xCoordMap.put( Koma.Type.GKA, 0);
        yCoordMap.put( Koma.Type.GKA, 5);
        xOffsetMap.put(Koma.Type.GHI, 0);
        yOffsetMap.put(Koma.Type.GHI, 0);
        xCoordMap.put( Koma.Type.GHI, 0);
        yCoordMap.put( Koma.Type.GHI, 6);
        
        xOffsetMap.put(Koma.Type.SFU, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SFU, SBAN_YOFFSET);
        xCoordMap.put( Koma.Type.SFU, 0);
        yCoordMap.put( Koma.Type.SFU, 6);
        xOffsetMap.put(Koma.Type.SKY, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKY, SBAN_YOFFSET);
        xCoordMap.put( Koma.Type.SKY, 0);
        yCoordMap.put( Koma.Type.SKY, 5);
        xOffsetMap.put(Koma.Type.SKE, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKE, SBAN_YOFFSET);
        xCoordMap.put( Koma.Type.SKE, 0);
        yCoordMap.put( Koma.Type.SKE, 4);
        xOffsetMap.put(Koma.Type.SGI, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SGI, SBAN_YOFFSET);
        xCoordMap.put( Koma.Type.SGI, 0);
        yCoordMap.put( Koma.Type.SGI, 3);
        xOffsetMap.put(Koma.Type.SKI, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKI, SBAN_YOFFSET);
        xCoordMap.put( Koma.Type.SKI, 0);
        yCoordMap.put( Koma.Type.SKI, 2);
        xOffsetMap.put(Koma.Type.SKA, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKA, SBAN_YOFFSET);
        xCoordMap.put( Koma.Type.SKA, 0);
        yCoordMap.put( Koma.Type.SKA, 1);
        xOffsetMap.put(Koma.Type.SHI, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SHI, SBAN_YOFFSET);
        xCoordMap.put( Koma.Type.SHI, 0);
        yCoordMap.put( Koma.Type.SHI, 0);

        ScaledImageCache scaledImageCache = board.scaledImageCache;

        for (Koma.Type komaType : Koma.Type.values()) {
            Integer numberHeld = board.inHandKomaMap.get(komaType);
            if (numberHeld != null && numberHeld > 0) {
                BufferedImage pieceImage = ImageUtils.getScaledImage(
                        scaledImageCache,
                        substituteKomaName(komaType.toString()) + ".svg",
                        MathUtils.KOMA_X, MathUtils.KOMA_Y
                );
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(
                                scaledImageCache.getScale(),
                                pieceImage,
                                xCoordMap.get(komaType),
                                yCoordMap.get(komaType),
                                xOffsetMap.get(komaType),
                                yOffsetMap.get(komaType)
                        ));
   
                      boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                scaledImageCache.getScale(),
                                pieceImage,
                                xCoordMap.get(komaType)+1,
                                yCoordMap.get(komaType),
                                xOffsetMap.get(komaType),
                                yOffsetMap.get(komaType),
                                numberHeld
                        ));          
            }
        }

    }

    public static void drawPieces(Board board, JPanel boardPanel) {
        ScaledImageCache scaledImageCache = board.scaledImageCache;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.masu[i][j] != null) {
                    Koma koma = board.masu[i][j];
                    BufferedImage pieceImage = ImageUtils.getScaledImage(
                            scaledImageCache,
                            substituteKomaName(koma.getType().toString()) + ".svg",
                            MathUtils.KOMA_X,
                            MathUtils.KOMA_Y
                    );

                    boardPanel.add(ImageUtils.getPieceLabelForKoma(
                            scaledImageCache.getScale(),
                            pieceImage,
                            i,
                            j,
                            2 * MathUtils.KOMA_X + 2 * MathUtils.COORD_XY,
                            MathUtils.COORD_XY
                    ));
                }
            }
        }
    }

    public static void drawGrid(Board board, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                boardPanel,
                "grid.svg",
                MathUtils.KOMA_X * 2 + MathUtils.COORD_XY,
                0,
                MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY,
                MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY);
    }

    public static void drawBans(Board board, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                boardPanel,
                "ban.svg",
                MathUtils.KOMA_X * (MathUtils.BOARD_XY + 2) + MathUtils.COORD_XY * 4,
                MathUtils.COORD_XY * 2 + MathUtils.KOMA_Y * 2,
                MathUtils.KOMA_X + MathUtils.COORD_XY,
                MathUtils.KOMA_Y * 7);

        ImageUtils.drawImage(
                board,
                boardPanel,
                "ban.svg",
                0,
                0,
                MathUtils.KOMA_X + MathUtils.COORD_XY,
                MathUtils.KOMA_Y * 7);
    }

    public static void drawBackground(Board board, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                boardPanel,
                "background.svg",
                MathUtils.KOMA_X * 2 + MathUtils.COORD_XY,
                0,
                MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2,
                MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2);
    }

}
