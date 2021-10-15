package com.chadfield.shogiexplorer.main;

import java.awt.Image;
import com.chadfield.shogiexplorer.objects.ImageCache;
import com.chadfield.shogiexplorer.objects.Koma;
import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.utils.MathUtils;
import com.chadfield.shogiexplorer.utils.ImageUtils;
import javax.swing.JPanel;
import com.chadfield.shogiexplorer.objects.Board.Turn;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Dimension;
import static com.chadfield.shogiexplorer.utils.StringUtils.substituteKomaName;
import static com.chadfield.shogiexplorer.utils.StringUtils.substituteKomaNameRotated;
import java.util.EnumMap;

public class RenderBoard {

    static final int SBAN_XOFFSET = (MathUtils.BOARD_XY + 1) * MathUtils.KOMA_X + MathUtils.COORD_XY * 5;
    static final int SBAN_YOFFSET = MathUtils.KOMA_Y * 2 + MathUtils.COORD_XY * 2;
    static final int CENTRE_X = 5;
    static final int CENTRE_Y = 5;
    static final String IMAGE_STR_SENTE = "sente";
    static final String IMAGE_STR_GOTE = "gote";
    static final String IMAGE_STR_HIGHLIGHT = "highlight";
    static final String PIECE_SET_CLASSIC = "classic";
    static final String PIECE_SET_MODERN = "modern";

    private RenderBoard() {
        throw new IllegalStateException("Utility class");
    }

    public static void loadBoard(Board board, ImageCache imageCache, javax.swing.JPanel boardPanel, boolean rotatedView, boolean classic) {
        if (boardPanel.getWidth() == 0) {
            return;
        }

        // Start with a clean slate.
        boardPanel.removeAll();

        drawPieces(board, imageCache, boardPanel, rotatedView, classic);
        drawPiecesInHand(board, imageCache, boardPanel, rotatedView, classic);
        drawCoordinates(boardPanel, rotatedView);
        drawGrid(imageCache, boardPanel);
        drawHighlights(board, imageCache, boardPanel, rotatedView);
        drawBans(imageCache, boardPanel);
        drawBackground(imageCache, boardPanel);
        drawTurnNotification(board, imageCache, boardPanel, rotatedView);
        boardPanel.setVisible(true);
        boardPanel.repaint();
    }

    private static void drawCoordinates(JPanel boardPanel, boolean rotatedView) {
        String[] rank = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};

        if (rotatedView) {
            for (int i = 0; i < 9; i++) {
                boardPanel.add(ImageUtils.getTextLabelForBan(
                        new Coordinate(i, 0),
                        new Dimension(
                                MathUtils.KOMA_X + MathUtils.COORD_XY * 4 - 2,
                                MathUtils.BOARD_XY * MathUtils.KOMA_Y + MathUtils.COORD_XY / 2 - 3),
                        new Coordinate(CENTRE_X, CENTRE_Y),
                        Integer.toString(i + 1)
                ));
            }
            for (int i = 0; i < 9; i++) {
                boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                new Coordinate(0, i),
                                new Dimension(
                                        MathUtils.COORD_XY * 4 + 7,
                                        MathUtils.COORD_XY / 2 + 7),
                                new Coordinate(CENTRE_X, CENTRE_Y),
                                rank[8 - i]
                        ));
            }
        } else {
            for (int i = 0; i < 9; i++) {
                boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                new Coordinate(i, 0),
                                new Dimension(
                                        MathUtils.KOMA_X + MathUtils.COORD_XY * 4 - 2,
                                        -(MathUtils.COORD_XY / 2) - 3),
                                new Coordinate(CENTRE_X, CENTRE_Y),
                                Integer.toString(9 - i)
                        ));
            }
            for (int i = 0; i < 9; i++) {
                boardPanel.add(
                        ImageUtils.getTextLabelForBan(
                                new Coordinate(0, i),
                                new Dimension(
                                        MathUtils.KOMA_X * 10 + MathUtils.COORD_XY * 3 + 3,
                                        MathUtils.COORD_XY / 2 + 7),
                                new Coordinate(CENTRE_X, CENTRE_Y),
                                rank[i]
                        ));
            }
        }
    }

    private static void drawTurnNotification(Board board, ImageCache imageCache, JPanel boardPanel, boolean rotatedView) {
        if (board.getNextTurn() == Turn.SENTE) {
            Image image = imageCache.getImage(IMAGE_STR_SENTE);
            if (image == null) {
                image = ImageUtils.loadSVGImageFromResources(IMAGE_STR_SENTE, new Dimension(MathUtils.KOMA_X, MathUtils.KOMA_Y));
                imageCache.putImage(IMAGE_STR_SENTE, image);
            }
            if (rotatedView) {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(image,
                                new Coordinate(0, 8),
                                new Dimension(MathUtils.COORD_XY, MathUtils.COORD_XY),
                                new Coordinate(CENTRE_X, CENTRE_Y)
                        )
                );
            } else {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(
                                image,
                                new Coordinate(0, -2),
                                new Dimension(SBAN_XOFFSET, SBAN_YOFFSET - MathUtils.COORD_XY),
                                new Coordinate(CENTRE_X, CENTRE_Y)
                        )
                );
            }
        } else {
            Image image = imageCache.getImage(IMAGE_STR_GOTE);
            if (image == null) {
                image = ImageUtils.loadSVGImageFromResources(IMAGE_STR_GOTE, new Dimension(MathUtils.KOMA_X, MathUtils.KOMA_Y));
                imageCache.putImage(IMAGE_STR_GOTE, image);
            }
            if (rotatedView) {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(image,
                                new Coordinate(0, -2),
                                new Dimension(SBAN_XOFFSET, SBAN_YOFFSET - MathUtils.COORD_XY),
                                new Coordinate(CENTRE_X, CENTRE_Y)
                        )
                );
            } else {
                boardPanel.add(
                        ImageUtils.getPieceLabelForKoma(
                                image,
                                new Coordinate(0, 8),
                                new Dimension(MathUtils.COORD_XY, MathUtils.COORD_XY),
                                new Coordinate(CENTRE_X, CENTRE_Y)
                        )
                );
            }
        }
    }

    private static void drawPiecesInHand(Board board, ImageCache imageCache, JPanel boardPanel, boolean rotatedView, boolean classic) {

        EnumMap<Koma.Type, Integer> xOffsetMap = new EnumMap<>(Koma.Type.class);
        EnumMap<Koma.Type, Integer> yOffsetMap = new EnumMap<>(Koma.Type.class);
        EnumMap<Koma.Type, Integer> xOffsetMapRotated = new EnumMap<>(Koma.Type.class);
        EnumMap<Koma.Type, Integer> yOffsetMapRotated = new EnumMap<>(Koma.Type.class);
        EnumMap<Koma.Type, Integer> xCoordMap = new EnumMap<>(Koma.Type.class);
        EnumMap<Koma.Type, Integer> yCoordMap = new EnumMap<>(Koma.Type.class);
        EnumMap<Koma.Type, Integer> xCoordMapRotated = new EnumMap<>(Koma.Type.class);
        EnumMap<Koma.Type, Integer> yCoordMapRotated = new EnumMap<>(Koma.Type.class);

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
        for (Koma.Type komaType : Koma.Type.values()) {
            Integer numberHeld = board.getInHandKomaMap().get(komaType);
            if (numberHeld != null && numberHeld > 0) {
                Image pieceImage;
                if (rotatedView) {
                    String name;
                    if (classic) {
                        name = PIECE_SET_CLASSIC + "/" + substituteKomaNameRotated(komaType.toString());
                    } else {
                        name = PIECE_SET_MODERN + "/" + substituteKomaNameRotated(komaType.toString());
                    }
                    pieceImage = imageCache.getImage(name);
                    if (pieceImage == null) {
                        pieceImage = ImageUtils.loadSVGImageFromResources(
                                name,
                                new Dimension(MathUtils.KOMA_X, MathUtils.KOMA_Y));
                        imageCache.putImage(name, pieceImage);
                    }
                } else {
                    String name;
                    if (classic) {
                        name = PIECE_SET_CLASSIC + "/" + substituteKomaName(komaType.toString());
                    } else {
                        name = PIECE_SET_MODERN + substituteKomaName(komaType.toString());
                    }
                    pieceImage = imageCache.getImage(name);
                    if (pieceImage == null) {
                        pieceImage = ImageUtils.loadSVGImageFromResources(
                                name,
                                new Dimension(MathUtils.KOMA_X, MathUtils.KOMA_Y));
                        imageCache.putImage(name, pieceImage);
                    }
                }
                if (rotatedView) {
                    boardPanel.add(
                            ImageUtils.getPieceLabelForKoma(
                                    pieceImage,
                                    new Coordinate(xCoordMapRotated.get(komaType), yCoordMapRotated.get(komaType)),
                                    new Dimension(xOffsetMapRotated.get(komaType), yOffsetMapRotated.get(komaType)),
                                    new Coordinate(CENTRE_X, CENTRE_Y)
                            ));
                    boardPanel.add(
                            ImageUtils.getTextLabelForBan(
                                    new Coordinate(xCoordMapRotated.get(komaType) + 1, yCoordMapRotated.get(komaType)),
                                    new Dimension(xOffsetMapRotated.get(komaType), yOffsetMapRotated.get(komaType)),
                                    new Coordinate(CENTRE_X, CENTRE_Y),
                                    numberHeld.toString()
                            ));
                } else {
                    boardPanel.add(
                            ImageUtils.getPieceLabelForKoma(
                                    pieceImage,
                                    new Coordinate(xCoordMap.get(komaType), yCoordMap.get(komaType)),
                                    new Dimension(xOffsetMap.get(komaType), yOffsetMap.get(komaType)),
                                    new Coordinate(CENTRE_X, CENTRE_Y)
                            ));
                    boardPanel.add(
                            ImageUtils.getTextLabelForBan(
                                    new Coordinate(xCoordMap.get(komaType) + 1, yCoordMap.get(komaType)),
                                    new Dimension(xOffsetMap.get(komaType), yOffsetMap.get(komaType)),
                                    new Coordinate(CENTRE_X, CENTRE_Y),
                                    numberHeld.toString()
                            ));
                }
            }
        }

    }

    private static void drawHighlights(Board board, ImageCache imageCache, JPanel boardPanel, boolean rotatedView) {
        Coordinate thisCoord = board.getSource();
        if (thisCoord != null) {
            drawThisHighlight(rotatedView, boardPanel, thisCoord, imageCache);
        }
        thisCoord = board.getDestination();
        if (thisCoord != null) {
            drawThisHighlight(rotatedView, boardPanel, thisCoord, imageCache);
        }
    }

    private static void drawThisHighlight(boolean rotatedView, JPanel boardPanel, Coordinate thisCoord, ImageCache imageCache) {
        Image highLightImage = imageCache.getImage(IMAGE_STR_HIGHLIGHT);
        if (highLightImage == null) {
            highLightImage = ImageUtils.loadSVGImageFromResources(
                    IMAGE_STR_HIGHLIGHT,
                    new Dimension(MathUtils.KOMA_X, MathUtils.KOMA_Y));
            imageCache.putImage(IMAGE_STR_HIGHLIGHT, highLightImage);
        }
        int x;
        int y;
        if (rotatedView) {
            x = thisCoord.getX() - 1;
            y = 9 - thisCoord.getY();
        } else {
            x = 9 - thisCoord.getX();
            y = thisCoord.getY() - 1;
        }
        boardPanel.add(
                ImageUtils.getPieceLabelForKoma(
                        highLightImage,
                        new Coordinate(x, y),
                        new Dimension(MathUtils.KOMA_X + 3 * MathUtils.COORD_XY, MathUtils.COORD_XY),
                        new Coordinate(CENTRE_X, CENTRE_Y)
                ));
    }

    private static void drawPieces(Board board, ImageCache imageCache, JPanel boardPanel, boolean rotatedView, boolean classic) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Image pieceImage = getPieceImage(rotatedView, classic, i, j, board, imageCache);
                if (pieceImage != null) {
                    addPiece(boardPanel, pieceImage, i, j);
                }
            }
        }
    }

    private static Image getPieceImage(boolean rotatedView, boolean classic, int i, int j, Board board, ImageCache imageCache) {
        String name;
        Koma koma;
        if (rotatedView) {
            koma = board.getMasu()[8 - i][8 - j];
            if (koma == null) {
                return null;
            }
            if (classic) {
                name = PIECE_SET_CLASSIC + "/" + substituteKomaNameRotated(koma.getType().toString());
            } else {
                name = PIECE_SET_MODERN + "/" + substituteKomaNameRotated(koma.getType().toString());
            }
        } else {
            koma = board.getMasu()[i][j];
            if (koma == null) {
                return null;
            }
            if (classic) {
                name = PIECE_SET_CLASSIC + "/" + substituteKomaName(koma.getType().toString());
            } else {
                name = PIECE_SET_MODERN + "/" + substituteKomaName(koma.getType().toString());
            }
        }
        Image cacheImage = imageCache.getImage(name);
        if (cacheImage == null) {
            cacheImage = ImageUtils.loadSVGImageFromResources(
                    name,
                    new Dimension(MathUtils.KOMA_X, MathUtils.KOMA_Y));
            imageCache.putImage(name, cacheImage);
        }

        return cacheImage;
    }

    private static void addPiece(JPanel boardPanel, Image pieceImage, int i, int j) {
        boardPanel.add(
                ImageUtils.getPieceLabelForKoma(
                        pieceImage,
                        new Coordinate(i, j),
                        new Dimension(MathUtils.KOMA_X + 3 * MathUtils.COORD_XY, MathUtils.COORD_XY),
                        new Coordinate(CENTRE_X, CENTRE_Y)
                ));
    }

    private static void drawGrid(ImageCache imageCache, JPanel boardPanel) {
        ImageUtils.drawImage(
                imageCache,
                boardPanel,
                "grid",
                new Coordinate(MathUtils.KOMA_X + MathUtils.COORD_XY * 2, 0),
                new Dimension(
                        MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2,
                        MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2
                ),
                new Coordinate(CENTRE_X, CENTRE_Y)
        );
    }

    private static void drawBans(ImageCache imageCache, JPanel boardPanel) {
        ImageUtils.drawImage(
                imageCache,
                boardPanel,
                "komadai",
                new Coordinate(
                        MathUtils.KOMA_X * (MathUtils.BOARD_XY + 1) + MathUtils.COORD_XY * 5,
                        MathUtils.COORD_XY * 2 + MathUtils.KOMA_Y * 2
                ),
                new Dimension(MathUtils.KOMA_X + MathUtils.COORD_XY, MathUtils.KOMA_Y * 7),
                new Coordinate(CENTRE_X, CENTRE_Y)
        );

        ImageUtils.drawImage(
                imageCache,
                boardPanel,
                "komadai",
                new Coordinate(0, 0),
                new Dimension(MathUtils.KOMA_X + MathUtils.COORD_XY, MathUtils.KOMA_Y * 7),
                new Coordinate(CENTRE_X, CENTRE_Y)
        );
    }

    private static void drawBackground(ImageCache imageCache, JPanel boardPanel) {
        ImageUtils.drawImage(
                imageCache,
                boardPanel,
                "background",
                new Coordinate(MathUtils.KOMA_X + MathUtils.COORD_XY * 2, 0),
                new Dimension(
                        MathUtils.KOMA_X * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2,
                        MathUtils.KOMA_Y * MathUtils.BOARD_XY + MathUtils.COORD_XY * 2
                ),
                new Coordinate(CENTRE_X, CENTRE_Y)
        );
    }

}
