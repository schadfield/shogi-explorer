package main;

import objects.ScaledImageCache;
import objects.Koma;
import objects.Board;
import utils.MathUtils;
import utils.ImageUtils;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JPanel;
import objects.Board.Turn;
import objects.Coordinate;
import objects.ScaleItem;
import static utils.MathUtils.calculateScaleFactor;
import static utils.StringUtils.substituteKomaName;
import static utils.StringUtils.substituteKomaNameRotated;

public class RenderBoard {

    public static final HashMap<Koma.Type, Integer> xOffsetMap = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> yOffsetMap = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> xOffsetMapRotated = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> yOffsetMapRotated = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> xCoordMap = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> yCoordMap = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> xCoordMapRotated = new HashMap<>();
    public static final HashMap<Koma.Type, Integer> yCoordMapRotated = new HashMap<>();
    final static int SBAN_XOFFSET = (MathUtils.BOARD_XY + 1) * MathUtils.KOMA_X + MathUtils.COORD_XY * 5;
    final static int SBAN_YOFFSET = MathUtils.KOMA_Y * 2 + MathUtils.COORD_XY * 2;
    private static long centerX;
    private static long centerY;

    public static void loadBoard(Board board, javax.swing.JPanel boardPanel, boolean rotatedView) {
        // TODO: why is loadBoard() being called when the boardPanel has no width?
        if (boardPanel.getWidth() == 0) {
            return;
        }

        ScaleItem scaleItem = calculateScaleFactor(boardPanel);
        double scale = scaleItem.getScale();
        centerX = scaleItem.getCenterX();
        centerY = scaleItem.getCenterY();

        // If the scale of the board has changed we need to create a new image cache.
        if (board.getScaledImageCache() == null || Double.compare(scale, board.getScaledImageCache().getScale()) != 0) {
            board.setScaledImageCache(new ScaledImageCache(scale));
        }

        // Start with a clean slate.
        boardPanel.removeAll();

        drawPieces(board, boardPanel, rotatedView);
        drawPiecesInHand(board, boardPanel, rotatedView);
        drawCoordinates(board, boardPanel, rotatedView);
        drawGrid(board, boardPanel);
        drawHighlights(board, boardPanel, rotatedView);
        drawBans(board, boardPanel);
        drawBackground(board, boardPanel);
        drawTurnNotification(board, boardPanel, rotatedView);

        boardPanel.setVisible(true);
        boardPanel.repaint();
    }

    public static void drawCoordinates(Board board, JPanel boardPanel, boolean rotatedView) {
        String[] rank = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};

        if (rotatedView) {
            ScaledImageCache scaledImageCache = board.getScaledImageCache();
            for (int i = 0; i < 9; i++) {
                boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                scaledImageCache.getScale(),
                                i,
                                0,
                                MathUtils.KOMA_X + MathUtils.COORD_XY * 4 - 2,
                                MathUtils.BOARD_XY * MathUtils.KOMA_Y + MathUtils.COORD_XY / 2 - 3,
                                centerX,
                                centerY,
                                Integer.toString(i + 1)
                        ));
            }
            for (int i = 0; i < 9; i++) {
                boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                scaledImageCache.getScale(),
                                0,
                                i,
                                MathUtils.COORD_XY * 4.35f,
                                MathUtils.COORD_XY / 2 + 7,
                                centerX,
                                centerY,
                                rank[8 - i]
                        ));
            }
        } else {
            ScaledImageCache scaledImageCache = board.getScaledImageCache();
            for (int i = 0; i < 9; i++) {
                boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                scaledImageCache.getScale(),
                                i,
                                0,
                                MathUtils.KOMA_X + MathUtils.COORD_XY * 4 - 2,
                                -(MathUtils.COORD_XY / 2) - 3,
                                centerX,
                                centerY,
                                Integer.toString(9 - i)
                        ));
            }
            for (int i = 0; i < 9; i++) {
                boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                scaledImageCache.getScale(),
                                0,
                                i,
                                MathUtils.KOMA_X * 10 + MathUtils.COORD_XY * 3.1f,
                                MathUtils.COORD_XY / 2 + 7,
                                centerX,
                                centerY,
                                rank[i]
                        ));
            }
        }
    }

    public static void drawTurnNotification(Board board, JPanel boardPanel, boolean rotatedView) {
        ScaledImageCache scaledImageCache = board.getScaledImageCache();

        if (board.getNextMove() == Turn.SENTE) {
            BufferedImage image = ImageUtils.getScaledImage(
                    scaledImageCache,
                    "sente.svg",
                    MathUtils.KOMA_X, MathUtils.KOMA_Y
            );
            if (rotatedView) {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(
                                scaledImageCache.getScale(),
                                image,
                                0,
                                8,
                                MathUtils.COORD_XY,
                                MathUtils.COORD_XY,
                                centerX,
                                centerY
                        )
                );
            } else {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(
                                scaledImageCache.getScale(),
                                image,
                                0,
                                -2,
                                SBAN_XOFFSET,
                                SBAN_YOFFSET - MathUtils.COORD_XY,
                                centerX,
                                centerY
                        )
                );
            }
        } else {
            BufferedImage image = ImageUtils.getScaledImage(
                    scaledImageCache,
                    "gote.svg",
                    MathUtils.KOMA_X, MathUtils.KOMA_Y
            );
            if (rotatedView) {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(
                                scaledImageCache.getScale(),
                                image,
                                0,
                                -2,
                                SBAN_XOFFSET,
                                SBAN_YOFFSET - MathUtils.COORD_XY,
                                centerX,
                                centerY
                        )
                );
            } else {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(
                                scaledImageCache.getScale(),
                                image,
                                0,
                                8,
                                MathUtils.COORD_XY,
                                MathUtils.COORD_XY,
                                centerX,
                                centerY
                        )
                );
            }
        }
    }

    public static void drawPiecesInHand(Board board, JPanel boardPanel, boolean rotatedView) {

        //<editor-fold defaultstate="collapsed" desc="Map initialization">
        xOffsetMap.put(Koma.Type.GFU, 0);
        yOffsetMap.put(Koma.Type.GFU, 0);
        xCoordMap.put(Koma.Type.GFU, 0);
        yCoordMap.put(Koma.Type.GFU, 0);
        xOffsetMap.put(Koma.Type.GKY, 0);
        yOffsetMap.put(Koma.Type.GKY, 0);
        xCoordMap.put(Koma.Type.GKY, 0);
        yCoordMap.put(Koma.Type.GKY, 1);
        xOffsetMap.put(Koma.Type.GKE, 0);
        yOffsetMap.put(Koma.Type.GKE, 0);
        xCoordMap.put(Koma.Type.GKE, 0);
        yCoordMap.put(Koma.Type.GKE, 2);
        xOffsetMap.put(Koma.Type.GGI, 0);
        yOffsetMap.put(Koma.Type.GGI, 0);
        xCoordMap.put(Koma.Type.GGI, 0);
        yCoordMap.put(Koma.Type.GGI, 3);
        xOffsetMap.put(Koma.Type.GKI, 0);
        yOffsetMap.put(Koma.Type.GKI, 0);
        xCoordMap.put(Koma.Type.GKI, 0);
        yCoordMap.put(Koma.Type.GKI, 4);
        xOffsetMap.put(Koma.Type.GKA, 0);
        yOffsetMap.put(Koma.Type.GKA, 0);
        xCoordMap.put(Koma.Type.GKA, 0);
        yCoordMap.put(Koma.Type.GKA, 5);
        xOffsetMap.put(Koma.Type.GHI, 0);
        yOffsetMap.put(Koma.Type.GHI, 0);
        xCoordMap.put(Koma.Type.GHI, 0);
        yCoordMap.put(Koma.Type.GHI, 6);

        xOffsetMap.put(Koma.Type.SFU, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SFU, SBAN_YOFFSET);
        xCoordMap.put(Koma.Type.SFU, 0);
        yCoordMap.put(Koma.Type.SFU, 6);
        xOffsetMap.put(Koma.Type.SKY, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKY, SBAN_YOFFSET);
        xCoordMap.put(Koma.Type.SKY, 0);
        yCoordMap.put(Koma.Type.SKY, 5);
        xOffsetMap.put(Koma.Type.SKE, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKE, SBAN_YOFFSET);
        xCoordMap.put(Koma.Type.SKE, 0);
        yCoordMap.put(Koma.Type.SKE, 4);
        xOffsetMap.put(Koma.Type.SGI, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SGI, SBAN_YOFFSET);
        xCoordMap.put(Koma.Type.SGI, 0);
        yCoordMap.put(Koma.Type.SGI, 3);
        xOffsetMap.put(Koma.Type.SKI, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKI, SBAN_YOFFSET);
        xCoordMap.put(Koma.Type.SKI, 0);
        yCoordMap.put(Koma.Type.SKI, 2);
        xOffsetMap.put(Koma.Type.SKA, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SKA, SBAN_YOFFSET);
        xCoordMap.put(Koma.Type.SKA, 0);
        yCoordMap.put(Koma.Type.SKA, 1);
        xOffsetMap.put(Koma.Type.SHI, SBAN_XOFFSET);
        yOffsetMap.put(Koma.Type.SHI, SBAN_YOFFSET);
        xCoordMap.put(Koma.Type.SHI, 0);
        yCoordMap.put(Koma.Type.SHI, 0);

        xOffsetMapRotated.put(Koma.Type.SFU, 0);
        yOffsetMapRotated.put(Koma.Type.SFU, 0);
        xCoordMapRotated.put(Koma.Type.SFU, 0);
        yCoordMapRotated.put(Koma.Type.SFU, 0);
        xOffsetMapRotated.put(Koma.Type.SKY, 0);
        yOffsetMapRotated.put(Koma.Type.SKY, 0);
        xCoordMapRotated.put(Koma.Type.SKY, 0);
        yCoordMapRotated.put(Koma.Type.SKY, 1);
        xOffsetMapRotated.put(Koma.Type.SKE, 0);
        yOffsetMapRotated.put(Koma.Type.SKE, 0);
        xCoordMapRotated.put(Koma.Type.SKE, 0);
        yCoordMapRotated.put(Koma.Type.SKE, 2);
        xOffsetMapRotated.put(Koma.Type.SGI, 0);
        yOffsetMapRotated.put(Koma.Type.SGI, 0);
        xCoordMapRotated.put(Koma.Type.SGI, 0);
        yCoordMapRotated.put(Koma.Type.SGI, 3);
        xOffsetMapRotated.put(Koma.Type.SKI, 0);
        yOffsetMapRotated.put(Koma.Type.SKI, 0);
        xCoordMapRotated.put(Koma.Type.SKI, 0);
        yCoordMapRotated.put(Koma.Type.SKI, 4);
        xOffsetMapRotated.put(Koma.Type.SKA, 0);
        yOffsetMapRotated.put(Koma.Type.SKA, 0);
        xCoordMapRotated.put(Koma.Type.SKA, 0);
        yCoordMapRotated.put(Koma.Type.SKA, 5);
        xOffsetMapRotated.put(Koma.Type.SHI, 0);
        yOffsetMapRotated.put(Koma.Type.SHI, 0);
        xCoordMapRotated.put(Koma.Type.SHI, 0);
        yCoordMapRotated.put(Koma.Type.SHI, 6);

        xOffsetMapRotated.put(Koma.Type.GFU, SBAN_XOFFSET);
        yOffsetMapRotated.put(Koma.Type.GFU, SBAN_YOFFSET);
        xCoordMapRotated.put(Koma.Type.GFU, 0);
        yCoordMapRotated.put(Koma.Type.GFU, 6);
        xOffsetMapRotated.put(Koma.Type.GKY, SBAN_XOFFSET);
        yOffsetMapRotated.put(Koma.Type.GKY, SBAN_YOFFSET);
        xCoordMapRotated.put(Koma.Type.GKY, 0);
        yCoordMapRotated.put(Koma.Type.GKY, 5);
        xOffsetMapRotated.put(Koma.Type.GKE, SBAN_XOFFSET);
        yOffsetMapRotated.put(Koma.Type.GKE, SBAN_YOFFSET);
        xCoordMapRotated.put(Koma.Type.GKE, 0);
        yCoordMapRotated.put(Koma.Type.GKE, 4);
        xOffsetMapRotated.put(Koma.Type.GGI, SBAN_XOFFSET);
        yOffsetMapRotated.put(Koma.Type.GGI, SBAN_YOFFSET);
        xCoordMapRotated.put(Koma.Type.GGI, 0);
        yCoordMapRotated.put(Koma.Type.GGI, 3);
        xOffsetMapRotated.put(Koma.Type.GKI, SBAN_XOFFSET);
        yOffsetMapRotated.put(Koma.Type.GKI, SBAN_YOFFSET);
        xCoordMapRotated.put(Koma.Type.GKI, 0);
        yCoordMapRotated.put(Koma.Type.GKI, 2);
        xOffsetMapRotated.put(Koma.Type.GKA, SBAN_XOFFSET);
        yOffsetMapRotated.put(Koma.Type.GKA, SBAN_YOFFSET);
        xCoordMapRotated.put(Koma.Type.GKA, 0);
        yCoordMapRotated.put(Koma.Type.GKA, 1);
        xOffsetMapRotated.put(Koma.Type.GHI, SBAN_XOFFSET);
        yOffsetMapRotated.put(Koma.Type.GHI, SBAN_YOFFSET);
        xCoordMapRotated.put(Koma.Type.GHI, 0);
        yCoordMapRotated.put(Koma.Type.GHI, 0);

        //</editor-fold>
        ScaledImageCache scaledImageCache = board.getScaledImageCache();

        for (Koma.Type komaType : Koma.Type.values()) {
            Integer numberHeld = board.getInHandKomaMap().get(komaType);
            if (numberHeld != null && numberHeld > 0) {
                BufferedImage pieceImage;
                if (rotatedView) {
                    pieceImage = ImageUtils.getScaledImage(
                            scaledImageCache,
                            substituteKomaNameRotated(komaType.toString()) + ".svg",
                            MathUtils.KOMA_X, MathUtils.KOMA_Y
                    );
                } else {
                    pieceImage = ImageUtils.getScaledImage(
                            scaledImageCache,
                            substituteKomaName(komaType.toString()) + ".svg",
                            MathUtils.KOMA_X, MathUtils.KOMA_Y
                    );
                }
                if (rotatedView) {
                    boardPanel.add(
                            ImageUtils.getPieceLabelForKoma(
                                    scaledImageCache.getScale(),
                                    pieceImage,
                                    xCoordMapRotated.get(komaType),
                                    yCoordMapRotated.get(komaType),
                                    xOffsetMapRotated.get(komaType),
                                    yOffsetMapRotated.get(komaType),
                                    centerX,
                                    centerY
                            ));
                    boardPanel.add(
                            ImageUtils.getTextLabelForBan(
                                    scaledImageCache.getScale(),
                                    xCoordMapRotated.get(komaType) + 1,
                                    yCoordMapRotated.get(komaType),
                                    xOffsetMapRotated.get(komaType),
                                    yOffsetMapRotated.get(komaType),
                                    centerX,
                                    centerY,
                                    numberHeld.toString()
                            ));
                } else {
                    boardPanel.add(
                            ImageUtils.getPieceLabelForKoma(
                                    scaledImageCache.getScale(),
                                    pieceImage,
                                    xCoordMap.get(komaType),
                                    yCoordMap.get(komaType),
                                    xOffsetMap.get(komaType),
                                    yOffsetMap.get(komaType),
                                    centerX,
                                    centerY
                            ));
                    boardPanel.add(
                            ImageUtils.getTextLabelForBan(
                                    scaledImageCache.getScale(),
                                    xCoordMap.get(komaType) + 1,
                                    yCoordMap.get(komaType),
                                    xOffsetMap.get(komaType),
                                    yOffsetMap.get(komaType),
                                    centerX,
                                    centerY,
                                    numberHeld.toString()
                            ));
                }
            }
        }

    }

    public static void drawHighlights(Board board, JPanel boardPanel, boolean rotatedView) {
        ScaledImageCache scaledImageCache = board.getScaledImageCache();
        Coordinate thisCoord = board.getSource();
        if (thisCoord != null) {
            BufferedImage pieceImage = ImageUtils.getScaledImage(
                    scaledImageCache,
                    "highlight.svg",
                    MathUtils.KOMA_X,
                    MathUtils.KOMA_Y
            );
            int x;
            int y;
            if (rotatedView) {
                x = thisCoord.getX()-1;
                y = 9-thisCoord.getY();
            } else {
                x = 9-thisCoord.getX();
                y = thisCoord.getY()-1;
            }
            boardPanel.add(ImageUtils.getPieceLabelForKoma(
                    scaledImageCache.getScale(),
                    pieceImage,
                    x,
                    y,
                    MathUtils.KOMA_X + 3 * MathUtils.COORD_XY,
                    MathUtils.COORD_XY,
                    centerX,
                    centerY
            ));
        }
        thisCoord = board.getDestination();
        if (thisCoord != null) {
            BufferedImage pieceImage = ImageUtils.getScaledImage(
                    scaledImageCache,
                    "highlight.svg",
                    MathUtils.KOMA_X,
                    MathUtils.KOMA_Y
            );
            int x;
            int y;
            if (rotatedView) {
                x = thisCoord.getX()-1;
                y = 9-thisCoord.getY();
            } else {
                x = 9-thisCoord.getX();
                y = thisCoord.getY()-1;
            }
            boardPanel.add(ImageUtils.getPieceLabelForKoma(
                    scaledImageCache.getScale(),
                    pieceImage,
                    x,
                    y,
                    MathUtils.KOMA_X + 3 * MathUtils.COORD_XY,
                    MathUtils.COORD_XY,
                    centerX,
                    centerY
            ));
        }
    }

    public static void drawPieces(Board board, JPanel boardPanel, boolean rotatedView) {
        ScaledImageCache scaledImageCache = board.getScaledImageCache();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (rotatedView) {
                    if (board.getMasu()[8 - i][8 - j] != null) {
                        Koma koma = board.getMasu()[8 - i][8 - j];
                        BufferedImage pieceImage = ImageUtils.getScaledImage(
                                scaledImageCache,
                                substituteKomaNameRotated(koma.getType().toString()) + ".svg",
                                MathUtils.KOMA_X,
                                MathUtils.KOMA_Y
                        );
                        boardPanel.add(ImageUtils.getPieceLabelForKoma(
                                scaledImageCache.getScale(),
                                pieceImage,
                                i,
                                j,
                                MathUtils.KOMA_X + 3 * MathUtils.COORD_XY,
                                MathUtils.COORD_XY,
                                centerX,
                                centerY
                        ));
                    }
                } else {
                    if (board.getMasu()[i][j] != null) {
                        Koma koma = board.getMasu()[i][j];
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
                                MathUtils.KOMA_X + 3 * MathUtils.COORD_XY,
                                MathUtils.COORD_XY,
                                centerX,
                                centerY
                        ));
                    }
                }

            }
        }
    }

    public static void drawGrid(Board board, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                boardPanel,
                "grid.svg",
                MathUtils.KOMA_X + MathUtils.COORD_XY * 2,
                0,
                MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2,
                MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2,
                centerX,
                centerY
        );
    }

    public static void drawBans(Board board, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                boardPanel,
                "ban.svg",
                MathUtils.KOMA_X * (MathUtils.BOARD_XY + 1) + MathUtils.COORD_XY * 5,
                MathUtils.COORD_XY * 2 + MathUtils.KOMA_Y * 2,
                MathUtils.KOMA_X + MathUtils.COORD_XY,
                MathUtils.KOMA_Y * 7,
                centerX,
                centerY
        );

        ImageUtils.drawImage(
                board,
                boardPanel,
                "ban.svg",
                0,
                0,
                MathUtils.KOMA_X + MathUtils.COORD_XY,
                MathUtils.KOMA_Y * 7,
                centerX,
                centerY
        );
    }

    public static void drawBackground(Board board, JPanel boardPanel) {
        ImageUtils.drawImage(
                board,
                boardPanel,
                "background.svg",
                MathUtils.KOMA_X + MathUtils.COORD_XY * 2,
                0,
                MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2,
                MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2,
                centerX,
                centerY
        );
    }

}
