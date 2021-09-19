package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objectclasses.GameAnalyser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Koma;
import com.chadfield.shogiexplorer.objects.Notation;
import com.chadfield.shogiexplorer.objects.Position;
import com.chadfield.shogiexplorer.utils.ParserUtils;
import com.ibm.icu.text.Transliterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class KifParser {

    public static final String PROMOTED = "成";
    public static final String RESIGNS = "投了";
    public static final String SUSPENDED = "中断";
    public static final String  MADE = "まで";
    public static final String SAME = "同";
    public static final String ICHI = "一";
    public static final String NI = "二";
    public static final String SAN = "三";
    public static final String SHI = "四";
    public static final String GO = "五";
    public static final String ROKU = "六";
    public static final String NANA = "七";
    public static final String HACHI = "八";
    public static final String KYUU = "九";
    public static final String DATE = "開始日時：";
    public static final String PLACE = "場所：";
    public static final String TIME_LIMIT = "持ち時間：";
    public static final String SENTE = "先手：";
    public static final String GOTE = "後手：";
    public static final String MOVE_HEADER = "手数----指手---------消費時間-";
    public static final String MULTI_WHITESPACE = "\\s+|\\u3000";
    public static final String DROPPED = "打";
    public static final String DOWNWARD = "引";
    public static final String HORIZONTALLY = "寄";
    public static final String UPWARD = "上";
    public static final String FROM_RIGHT = "右";
    public static final String FROM_LEFT = "左";
    public static final String VERTICAL = "直";
    public static final String UPWARD_UD = "行";
    
    private KifParser() {
        throw new IllegalStateException("Utility class");
    }

    public static Game parseKif(DefaultListModel<String> moveListModel, File kifFile) throws  IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
        moveListModel.clear();
        moveListModel.addElement(bundle.getString("label_start_position"));
        Board board = SFENParser.parse("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1");
        Game game = new Game();
        LinkedList<Position> positionList = new LinkedList<>();
        positionList.add(new Position(SFENParser.getSFEN(board), null, null, new Notation()));

        boolean foundHeader = false;
        try (BufferedReader fileReader = Files.newBufferedReader(kifFile.toPath())) {
            int count = 1;
            String line;
            Coordinate lastDestination = null;
            while ((line = fileReader.readLine()) != null) {
                if (!foundHeader) {
                    foundHeader = isHeader(line);
                    if (foundHeader || isComment(line)) {
                        continue;
                    }
                    parseGameDetails(line, game);
                } else {
                    if (isComment(line)) {
                        positionList.getLast().setComment(positionList.getLast().getComment()+line.substring(1) + "\n") ;
                        continue;
                    }
                    
                    count++;
                    
                    if (isResigns(line)) {
                        parseResignLine(board, line, moveListModel, positionList);
                        continue;
                    }
                    
                    if (isSuspended(line)) {
                        parseSuspendedLine(board, line, moveListModel, positionList);
                        continue;
                    }
                    
                    board.setMoveCount(count);

                    lastDestination = parseRegularMove(board, line, moveListModel, lastDestination, positionList);
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(KifParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        game.setPositionList(positionList);
        return game;
    }
    
    private static Coordinate parseRegularMove(Board board, String line, DefaultListModel<String> moveListModel, Coordinate lastDestination, LinkedList<Position> positionList) {
        String[] splitLine = line.trim().split(MULTI_WHITESPACE);

        int gameNum;
        
        try {
            gameNum = Integer.parseInt(splitLine[0]);
        } catch (NumberFormatException ex) {
            positionList.getLast().setComment(positionList.getLast().getComment()+line + "\n") ;
            return lastDestination;
        }
        
        String move = extractRegularMove(splitLine, isSame(line));

        Position position = executeMove(board, move, lastDestination);
        if (position != null) {
            lastDestination = position.getDestination();
            addMoveToMoveList(moveListModel, gameNum, position.getNotation().getJapanese());
        }
        
        
        positionList.add(position);
        return lastDestination;
    }
    
    private static String extractRegularMove(String[] moveArray, boolean isSame) {
        String move = moveArray[1];
        if (isSame) {
            move += "\u3000" + moveArray[2];
        }
        return move;
    }
        
    private static void parseResignLine(Board board, String line, DefaultListModel<String> moveListModel, List<Position> positionList) {
        String[] splitLine = line.trim().split(MULTI_WHITESPACE);
        int gameNum = Integer.parseInt(splitLine[0]);
        
        Notation  notation = new Notation();
        notation.setEngineMove("Resigns");
        notation.setJapanese(RESIGNS);
        Position position = new Position(SFENParser.getSFEN(board), board.getSource(), board.getDestination(), notation);
        addMoveToMoveList(moveListModel, gameNum, position.getNotation().getJapanese());
        positionList.add(position);
    }
    
    private static void parseSuspendedLine(Board board, String line, DefaultListModel<String> moveListModel, List<Position> positionList) {
        String[] splitLine = line.trim().split(MULTI_WHITESPACE);
        int gameNum = Integer.parseInt(splitLine[0]);
        
        Notation  notation = new Notation();
        notation.setEngineMove("Suspended");
        notation.setJapanese(SUSPENDED);
        Position position = new Position(SFENParser.getSFEN(board), board.getSource(), board.getDestination(), notation);
        addMoveToMoveList(moveListModel, gameNum, position.getNotation().getJapanese());
        positionList.add(position);
    }
    
    private static void parseGameDetails(String line, Game game) {
        if (line.startsWith(SENTE)) {
            game.setSente(line.substring(SENTE.length()));
        }
        if (line.startsWith(GOTE)) {
            game.setGote(line.substring(GOTE.length()));
        }
        if (line.startsWith(PLACE)) {
            game.setPlace(line.substring(PLACE.length()));
        }
        if (line.startsWith(TIME_LIMIT)) {
            game.setTimeLimit(line.substring(TIME_LIMIT.length()).split("#")[0]);
        }
        if (line.startsWith(DATE)) {
            game.setDate(line.substring(DATE.length()));
        }
    }
        
    private static void addMoveToMoveList(DefaultListModel<String> moveListModel, int gameNum, String move) {
        Transliterator trans = Transliterator.getInstance("Halfwidth-Fullwidth");
        if (gameNum % 2 == 0) {
            moveListModel.addElement(gameNum + trans.transliterate("\u3000☖" + move + "\n"));
        } else {
            moveListModel.addElement(gameNum + trans.transliterate("\u3000☗" + move + "\n"));
        }
    }
    
    private static Notation getNotation(Coordinate thisSource, Coordinate thisDestination, boolean same, String move, String piece, String disambiguation) {
        String engineMove = "";
        try {
            engineMove = getEngineMoveCoordinate(thisSource) + getEngineMoveCoordinate(thisDestination);
            if (isPromoted(move)) {
                engineMove += "+";
            } 
        } catch (Exception ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Notation notation = new Notation();
        notation.setEngineMove(engineMove);
        
        String japanese = "";
        
        if (same) {
            japanese += SAME + piece;
        } else {
            japanese += getJapaneseCoordinate(thisDestination) + piece + disambiguation;
        }
        
        if (isPromoted(move)) {
            japanese += PROMOTED;
        }
        
        notation.setJapanese(japanese);
        
        return notation;
    }
    
    private static String getJapaneseCoordinate(Coordinate thisCoordinate) {
        return thisCoordinate.getX() + convertJapaneseNumber(thisCoordinate.getY());
    }

    private static Notation executeRegularMove(Board board, Coordinate thisDestination, Coordinate thisSource, String move) {
        Koma destinationKoma = getKoma(board, thisDestination);
        if (destinationKoma != null) {
            ParserUtils.addPieceToInHand(getKoma(board, thisDestination), board);
        }        
        Koma sourceKoma = getKoma(board, thisSource);
        String disambiguation = getDisambiguation(board, thisSource, thisDestination, sourceKoma.getType());
        putKoma(board, thisDestination, promCheck(sourceKoma, move));
        putKoma(board, thisSource, null);
              
        return getNotation(thisSource, thisDestination, false, move, getKomaKanji(sourceKoma.getType()), disambiguation);
    }
        
    private static Notation executeSameMove(Board board, Coordinate thisDestination, Coordinate thisSource, String move) {
        Koma destinationKoma = getKoma(board, thisDestination);
        if (destinationKoma != null) {
            ParserUtils.addPieceToInHand(destinationKoma, board);
        }
        Koma sourceKoma = getKoma(board, thisSource);
        String disambiguation = getDisambiguation(board, thisSource, thisDestination, sourceKoma.getType());
        putKoma(board, thisDestination, promCheck(sourceKoma, move));
        putKoma(board, thisSource, null);
        
        return getNotation(thisSource, thisDestination, true, move, getKomaKanji(sourceKoma.getType()), disambiguation);
    }
    
    private static String getDisambiguation(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        switch (komaType) {
            case SKE:
            case GKE:
                return disXKE(board, sourceCoordinate, destinationCoordinate, komaType);
            case SGI:
            case GGI:
                return disXGI(board, sourceCoordinate, destinationCoordinate, komaType);
            case SKI:
            case STO:
            case SNK:
            case SNY:
            case SNG:
            case GKI:
            case GTO:
            case GNK:
            case GNY:
            case GNG:
                return disXKXI(board, sourceCoordinate, destinationCoordinate, komaType);
            case SKA:
            case GKA:
                return disXKA(board, sourceCoordinate, destinationCoordinate, komaType);
            case SHI:
            case GHI:
                return disXHI(board, sourceCoordinate, destinationCoordinate, komaType);
            case SUM:
            case SRY:
            case GUM:
            case GRY:
                return disXUMRY(board, sourceCoordinate, destinationCoordinate, komaType);
            default:
                return "";
        }
    }
    
    private static boolean onBoard(Coordinate coordinate) {
        return coordinate.getX() > 0  && coordinate.getX() < 10 &&  coordinate.getY() > 0 && coordinate.getY() < 10;
    }
    
    private static List<Coordinate> getPossibleSources(Board board, Coordinate destination, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        Coordinate resultCoordinate;
        switch (komaType) {
            case SKE:
                return getSourcesForKoma(
                        board, 
                        destination, 
                        new int[] {1, -1},
                        new int[] {2, 2}, 
                        komaType);
            case GKE:
                return getSourcesForKoma(
                        board, 
                        destination, 
                        new int[] {1, -1},
                        new int[] {-2, -2}, 
                        komaType);
            case SGI:
                return getSourcesForKoma(
                        board, 
                        destination, 
                        new int[] {1, 0, -1, 1, -1},
                        new int[] {1, 1, 1, -1, -1}, 
                        komaType);
            case GGI:
                return getSourcesForKoma(
                        board, 
                        destination, 
                        new int[] {1, 0, -1, 1, -1},
                        new int[] {-1, -1,-1, 1, 1}, 
                        komaType);
            case SKI:
            case STO:
            case SNK:
            case SNY:
            case SNG:
                return getSourcesForKoma(
                        board, 
                        destination, 
                        new int[] {1, 0, -1, 1, -1, 0},
                        new int[] {1, 1, 1, 0, 0, -1}, 
                        komaType);
            case GKI:
            case GTO:
            case GNK:
            case GNY:
            case GNG:
                return getSourcesForKoma(
                        board, 
                        destination, 
                        new int[] {1, 0, -1, 1, -1, 0},
                        new int[] {-1, -1, -1, 0, 0, 1}, 
                        komaType);
            case SKY:
                resultCoordinate = getSourceOnVector(board, destination, 0, 1, komaType);
                if (resultCoordinate != null) {
                    result.add(resultCoordinate);
                }
                return result;
            case GKY:
                resultCoordinate = getSourceOnVector(board, destination, 0, -1, komaType);
                if (resultCoordinate != null) {
                    result.add(resultCoordinate);
                }
                return result;
            case SKA:
            case GKA:
                return getSourcesOnDiagonalVectors(board, destination, komaType);
            case SHI:
            case GHI:
                return getSourcesOnHorizontalVectors(board, destination, komaType);
            case SUM:
            case GUM:
                result = getSourcesForKoma(
                        board, 
                        destination,
                        new int[] {-1, 1, 0, 0},
                        new int[] {0, 0, -1, 1}, 
                        komaType);
                for (Coordinate thisCoordinate: getSourcesOnDiagonalVectors(board, destination, komaType)) {
                    result.add(thisCoordinate);
                }
                return result;
            case SRY:
            case GRY:
                result = getSourcesForKoma(
                        board, 
                        destination, 
                        new int[] {-1, 1, 1, -1},
                        new int[] {-1, -1, 1, 1}, 
                        komaType);
                for (Coordinate thisCoordinate: getSourcesOnHorizontalVectors(board, destination, komaType)) {
                    result.add(thisCoordinate);
                }
                return result;
            default:
        }
        return result;
    }
    
    private static List<Coordinate> getSourcesOnDiagonalVectors(Board board, Coordinate destination, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        Coordinate resultCoordinate;
        resultCoordinate = getSourceOnVector(board, destination, -1, -1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 1, -1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, -1, 1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 1, 1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        return result;
    }
    
    private static List<Coordinate> getSourcesOnHorizontalVectors(Board board, Coordinate destination, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        Coordinate resultCoordinate;
        resultCoordinate = getSourceOnVector(board, destination, -1, 0, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 1, 0, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 0, -1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        resultCoordinate = getSourceOnVector(board, destination, 0, 1, komaType);
        if (resultCoordinate != null) {
            result.add(resultCoordinate);
        }
        return result;
    }
    
    private static Coordinate getSourceOnVector(Board board, Coordinate destination, int deltaX, int deltaY, Koma.Type komaType) {
        Coordinate testCoordinate = new Coordinate(destination.getX() + deltaX, destination.getY() + deltaY);
        while (true) {
            if (!onBoard(testCoordinate)) {
                return null;
            }
            Koma koma = board.getMasu()[9 - testCoordinate.getX()][testCoordinate.getY() - 1];
            if (koma != null) {
                if (koma.getType() == komaType) {
                    return testCoordinate;
                } else {
                    return null;
                }
            }
            testCoordinate.setX(testCoordinate.getX() + deltaX);
            testCoordinate.setY(testCoordinate.getY() + deltaY);
        }     
    }
    
    private static List<Coordinate> getSourcesForKoma(Board board, Coordinate destination, int[] offsetX, int[] offsetY, Koma.Type komaType) {
        List<Coordinate> result = new ArrayList<>();
        for (int k = 0; k < offsetX.length; k++) {
            Coordinate testCoordinate = new Coordinate(
                    destination.getX() + offsetX[k],
                    destination.getY() + offsetY[k]
            );
            if (onBoard(testCoordinate)) {
                Koma koma = getKoma(board, testCoordinate);
                if (koma != null && koma.getType().equals(komaType)) {
                    result.add(testCoordinate);
                }
            }
        }
        return result;
    }
        
    private static String disXKE(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SKE;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, Koma.Type.GKE);
        if (sourceList.size() > 1) {
            for (Coordinate thisCoordinate : sourceList) {
                if (!thisCoordinate.sameValue(sourceCoordinate)) {
                    if (onLeft(thisCoordinate, sourceCoordinate, isSente)) {
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                }
            }
        }
        return "";
    }
    
    private static String disXGI(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SGI;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            if (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is one of two locations above the destination.
                if (numWithSameY(sourceCoordinate, sourceList) == 1) {
                    // Simple down case.
                    return DOWNWARD;
                } else  if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
                    // Above-left.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_LEFT;
                    } else {
                        return FROM_LEFT+DOWNWARD;
                    }
                } else {
                    // Above-right.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_RIGHT;
                    } else {
                        return FROM_RIGHT+DOWNWARD;
                    }
                }
            } else {
                // The source is one of three locations below the destination.
                return bottomThree(sourceCoordinate, destinationCoordinate, sourceList, isSente);
            }
        }
        return "";
    }
    
    private static String bottomThree(Coordinate sourceCoordinate, Coordinate destinationCoordinate, List<Coordinate> sourceList, boolean isSente) {
        if (numWithSameY(sourceCoordinate, sourceList) == 1) {
            // Simple up case.
            return UPWARD;
        } else if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
            // Below-left.
            if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                return FROM_LEFT;
            } else {
                return FROM_LEFT+UPWARD;
            }
        } else if (onRight(sourceCoordinate, destinationCoordinate, isSente)) {
            // Below-right.
            if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                return FROM_RIGHT;
            } else {
                return FROM_RIGHT+UPWARD;
            }
        } else {
            // Directly below destination.
            return VERTICAL;
        }
    }
    
    static boolean onLeft(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getX() > secondCoordinate.getX();
        } else {
            return firstCoordinate.getX() < secondCoordinate.getX();
        }
    }
    
    static boolean onRight(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getX() < secondCoordinate.getX();
        } else {
            return firstCoordinate.getX() > secondCoordinate.getX();
        }
    }
    
    static boolean isBelow(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getY() > secondCoordinate.getY();
        } else {
            return firstCoordinate.getY() < secondCoordinate.getY();
        }
    }
        
    static boolean isAbove(Coordinate firstCoordinate, Coordinate secondCoordinate, boolean isSente) {
        if (isSente) {
            return firstCoordinate.getY() < secondCoordinate.getY();
        } else {
            return firstCoordinate.getY() > secondCoordinate.getY();
        }
    }
        
    private static String disXKXI(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = (komaType == Koma.Type.SKI || komaType == Koma.Type.STO || komaType == Koma.Type.SNK || komaType == Koma.Type.SNY || komaType == Koma.Type.SNG); 
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            if (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is in the single location above the destination.
                return DOWNWARD;
            } else if (Objects.equals(sourceCoordinate.getY(), destinationCoordinate.getY())) {
                // The source is next to the destination.
                if (numWithSameY(sourceCoordinate, sourceList) == 1) {
                    // Simple horizontal case.
                    return HORIZONTALLY;
                } else  if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
                    // Above-left.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_LEFT;
                    } else {
                        return FROM_LEFT+HORIZONTALLY;
                    }
                } else {
                    // Above-right.
                    if (numWithSameX(sourceCoordinate, sourceList) == 1) {
                        return FROM_RIGHT;
                    } else {
                        return FROM_RIGHT+HORIZONTALLY;
                    }
                }
            } else {
                // The source is one of three locations below the destination.
                return bottomThree(sourceCoordinate, destinationCoordinate, sourceList, isSente);
            }
        }
        return "";
    }
        
    private static String disXKA(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SKA;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            Coordinate otherCoordinate = getOtherCoordinate(sourceCoordinate, sourceList);
            if (isBelow(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is below the destination.
                if (isBelow(otherCoordinate, destinationCoordinate, isSente)) {
                    // The other piece is also below the destination.
                    if (onLeft(sourceCoordinate,otherCoordinate, isSente)) {
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                }  else {
                    // Simple upward.
                    return UPWARD;
                }
            } else 
                // The source is above the destination.
                if (isAbove(otherCoordinate, destinationCoordinate, isSente)) {
                    // The other piece is also above the destination.
                    if (onLeft(sourceCoordinate,otherCoordinate, isSente)) {
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                }  else {
                    // Simple downward.
                    return DOWNWARD;
                }
        }
        return "";
    }
        
    private static String disXHI(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = komaType == Koma.Type.SHI;
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            if (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is above the destination.
                return DOWNWARD;
            } else if (isBelow(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is below the destination.
                return UPWARD;
            } else {
                if (numWithSameY(sourceCoordinate, sourceList) > 1) {
                    if (onLeft(sourceCoordinate, destinationCoordinate, isSente)) {
                        // The source is left of the destination.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    return HORIZONTALLY;
                }
            }
        }
        return "";
    }
        
    private static String disXUMRY(Board board, Coordinate sourceCoordinate, Coordinate destinationCoordinate, Koma.Type komaType) {
        boolean isSente = (komaType == Koma.Type.SUM || komaType == Koma.Type.SRY);
        List<Coordinate> sourceList = getPossibleSources(board, destinationCoordinate, komaType);
        if (sourceList.size() > 1) {
            // There is ambiguity.
            Coordinate otherCoordinate = getOtherCoordinate(sourceCoordinate, sourceList);
            if (isBelow(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is below the destination.
                if (isBelow(otherCoordinate, destinationCoordinate, isSente)) {
                    // The other piece is also below the destination.
                    if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                        // The source is to left of the other.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    // Simple upwards.
                    return UPWARD;
                }
            } else if  (isAbove(sourceCoordinate, destinationCoordinate, isSente)) {
                // The source is above the destination.
                if (isAbove(otherCoordinate, destinationCoordinate, isSente)) {
                    // The other piece is also above the destination.
                    if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                        // The source is to left of the other.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    // Simple downwards.
                    return DOWNWARD;
                } 
            } else {
                // The source is level with the destination.
                if (numWithSameY(sourceCoordinate, sourceList) > 1) {
                    // Both possible pieces are level with the destination.
                    if (onLeft(sourceCoordinate, otherCoordinate, isSente)) {
                        // The source is to left of the other.
                        return FROM_LEFT;
                    } else {
                        return FROM_RIGHT;
                    }
                } else {
                    // Simple horizontal.
                    return HORIZONTALLY;
                }
            }
        }
        return "";
    }
            
    private static Coordinate getOtherCoordinate(Coordinate sourceCoordinate, List<Coordinate> sourceList) {
        if (!sourceCoordinate.sameValue(sourceList.get(0))) {
            return sourceList.get(0);
        } else {
            return sourceList.get(1);
        }
    }
    
    private static int numWithSameY(Coordinate coordinate, List<Coordinate> coordinateList) {
        int count = 0;
        for (Coordinate thisCoordinate : coordinateList) {
            if (haveSameY(coordinate, thisCoordinate)) {
                count++;
            }
        }
        return count;
    }

    private static int numWithSameX(Coordinate coordinate, List<Coordinate> coordinateList) {
        int count = 0;
        for (Coordinate thisCoordinate : coordinateList) {
            if (haveSameX(coordinate, thisCoordinate)) {
                count++;
            }
        }
        return count;
    }
    
    private static boolean haveSameX(Coordinate first, Coordinate second) {
        return Objects.equals(first.getX(), second.getX());
    }
    
    private static boolean haveSameY(Coordinate first, Coordinate second) {
        return Objects.equals(first.getY(), second.getY());
    }
    
    private static Notation executeDropMove(Board board, Coordinate thisDestination, String move) {
        String engineMove;
        Koma koma;
        try {
            koma = ParserUtils.getDropKoma(move.substring(2, 3), board.getNextTurn());
            String dropNotation = getDropNotation(board, thisDestination, koma.getType());
            putKoma(board, thisDestination, koma);
            removePieceInHand(koma.getType(), board);
            engineMove = getKomaLetter(koma.getType()) + "*" + getEngineMoveCoordinate(thisDestination);
            Notation notation = new Notation();
            notation.setEngineMove(engineMove);
            notation.setJapanese(getJapaneseCoordinate(thisDestination) + getKomaKanji(koma.getType()) + dropNotation);
            return notation;
        } catch (Exception ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;      
    }
    
    private static String getDropNotation(Board board, Coordinate thisDestination, Koma.Type komaType) {
        if (!getPossibleSources(board, thisDestination, komaType).isEmpty()) {
            return DROPPED;
        }
        return "";
    }
    
    private static String getKomaLetter(Koma.Type type) {
        switch(type) {
            case SFU:
            case GFU:
                return "P";
            case SGI:
            case GGI:
                return "S";
            case SHI:
            case GHI:
                return "R";
            case SKA:
            case GKA:
                return "B";
            case SKE:
            case GKE:
                return "N";
            case SKI:
            case GKI:
                return "G";
            case SKY:
            case GKY:
                return "L";
            default:
                return null;
        }
    }
    
    private static String getKomaKanji(Koma.Type type) {
        switch(type) {
            case SFU:
            case GFU:
                return "歩";
            case SGI:
            case GGI:
                return "銀";
            case SHI:
            case GHI:
                return "飛";
            case SKA:
            case GKA:
                return "角";
            case SKE:
            case GKE:
                return "桂";
            case SKI:
            case GKI:
                return "金";
            case SKY:
            case GKY:
                return "香";
            case STO:
            case GTO:
                return "と";
            case SNY:
            case GNY:
                return "成香";
            case SNK:
            case GNK:
                return "成桂";
            case SNG:
            case GNG:
                return "成銀";
            case SUM:
            case GUM:
                return "馬";
            case SRY:
            case GRY:
                return "竜";
            case SGY:
            case SOU:
            case GGY:
            case GOU:
                return "玉";
            default:
                return null;
        }
    }
 
    private static Position executeMove(Board board, String move, Coordinate lastDestination) {
        Coordinate thisDestination = new Coordinate();
        Coordinate thisSource;
        
        if (isSame(move)) {
            thisSource = getSourceCoordinate(move);
            copyCoords(lastDestination, thisDestination);
        } else if (isResigns(move)) {
            return null;
        } else if (isSuspended(move)) {
            return null;
        } else if (isDrop(move)) {
            thisDestination = getDestinationCoordinate(move);
            thisSource = null;
        } else {
            thisSource = getSourceCoordinate(move);
            thisDestination = getDestinationCoordinate(move);
        }
        Notation notation = new Notation();
        if (!isResigns(move)) {
            if (!isDrop(move)) {
                if (!isSame(move)) {
                    notation = executeRegularMove(board, thisDestination, thisSource, move);
                } else {
                    notation = executeSameMove(board, thisDestination, thisSource, move);
                }
            } else {
                notation = executeDropMove(board, thisDestination, move);
            }
        }
        board.setNextTurn(ParserUtils.switchTurn(board.getNextTurn()));
        return new Position(SFENParser.getSFEN(board), thisSource, thisDestination, notation);
    }
    
    private static String getEngineMoveCoordinate(Coordinate coordinate) {
        return Integer.toString(coordinate.getX()) + (char) ('a' + coordinate.getY() - 1);
    }

    private static void copyCoords(Coordinate from, Coordinate to) {
        to.setX(from.getX());
        to.setY(from.getY());
    }

    private static Koma getKoma(Board board, Coordinate coords) {
        return board.getMasu()[9 - coords.getX()][coords.getY() - 1];
    }

    private static void putKoma(Board board, Coordinate coords, Koma koma) {
        if (coords != null) {
            board.getMasu()[9 - coords.getX()][coords.getY() - 1] = koma;
        }
    }

    private static void removePieceInHand(Koma.Type komaType, Board board) {
        if (board.getInHandKomaMap().get(komaType) == 1) {
            board.getInHandKomaMap().remove(komaType);
        } else {
            board.getInHandKomaMap().put(komaType, board.getInHandKomaMap().get(komaType) - 1);
        }
    }
    
    private static Koma promCheck(Koma koma, String move) {
        if (!isPromoted(move)) {
            return koma;
        } else {
            return ParserUtils.promoteKoma(koma.getType());
        }
    }

    private static boolean isComment(String line) {
        return line.startsWith("*");
    }

    private static boolean isHeader(String line) {
        return line.startsWith(MOVE_HEADER);
    }

    private static boolean isPromoted(String move) {
        if (isDrop(move)) {
            return false;
        }
        return move.charAt(move.indexOf('(') - 1) == PROMOTED.charAt(0);
    }

    private static boolean isResigns(String move) {
        return (move.contains(RESIGNS) && !move.contains(MADE));
    }

    private static boolean isSuspended(String move) {
        return (move.contains(SUSPENDED) && !move.contains(MADE));
    }

    private static boolean isDrop(String move) {
        return move.contains(DROPPED);
    }

    private static boolean isSame(String move) {
        return move.contains(SAME);
    }

    private static Coordinate getDestinationCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(0, 1)));
        result.setY(parseJapaneseNumber(move.substring(1, 2)));
        return result;
    }

    private static Integer parseJapaneseNumber(String thisChar) {
        switch (thisChar) {
            case ICHI:
                return 1;
            case NI:
                return 2;
            case SAN:
                return 3;
            case SHI:
                return 4;
            case GO:
                return 5;
            case ROKU:
                return 6;
            case NANA:
                return 7;
            case HACHI:
                return 8;
            case KYUU:
                return 9;
            default:
                return null;
        }
    }

    private static String convertJapaneseNumber(int number) {
        switch (number) {
            case 1:
                return ICHI;
            case 2:
                return NI;
            case 3:
                return SAN;
            case 4:
                return SHI;
            case 5:
                return GO;
            case 6:
                return ROKU;
            case 7:
                return NANA;
            case 8:
                return HACHI;
            case 9:
                return KYUU;
            default:
                return null;
        }
    }

    private static Coordinate getSourceCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf("(") + 2)));
        result.setY(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf(")"))) - result.getX() * 10);
        return result;
    }

}
