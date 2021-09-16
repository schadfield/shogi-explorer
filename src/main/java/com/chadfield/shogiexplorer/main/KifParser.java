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
import java.util.List;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class KifParser {

    public static final String PROMOTED = "成";
    public static final String RESIGNS = "投了";
    public static final String DROP = "打";
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
        String[] splitLine = line.trim().split("\\s+|\\u3000");

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
            addEngineMoveToMoveList(moveListModel, gameNum, position.getNotation().getEngineMove());
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
        String[] splitLine = line.trim().split("\\s+|\\u3000");
        int gameNum = Integer.parseInt(splitLine[0]);
        
        Notation  notation = new Notation();
        notation.setEngineMove("Resigns");
        notation.setJapanese("Resigns");
        Position position = new Position(SFENParser.getSFEN(board), board.getSource(), board.getDestination(), notation);
        addEngineMoveToMoveList(moveListModel, gameNum, position.getNotation().getEngineMove());
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
        
    private static void addEngineMoveToMoveList(DefaultListModel<String> moveListModel, int gameNum, String move) {
        moveListModel.addElement(gameNum + " " + move + "\n");
    }
    
    private static Notation getNotation(Coordinate thisSource, Coordinate thisDestination, String move) {
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
        
        return notation;
    }

    private static Notation executeRegularMove(Board board, Coordinate thisDestination, Coordinate thisSource, String move) {
        if (getKoma(board, thisDestination) != null) {
            Koma thisKoma = getKoma(board, thisDestination);
            if (thisKoma != null) {
                ParserUtils.addPieceToInHand(getKoma(board, thisDestination), board);
            }
        }
        Koma thisKoma = getKoma(board, thisSource);
        if (thisKoma != null) {
            putKoma(board, thisDestination, promCheck(thisKoma, move));
        }
        putKoma(board, thisSource, null);
              
        return getNotation(thisSource, thisDestination, move);
    }
    
    private static Notation executeSameMove(Board board, Coordinate thisDestination, Coordinate thisSource, String move) {
        Koma thisKoma = getKoma(board, thisDestination);
        if (thisKoma != null) {
            ParserUtils.addPieceToInHand(thisKoma, board);
        }
        Koma thisOtherKoma = getKoma(board, thisSource);
        if (thisOtherKoma != null) {
            putKoma(board, thisDestination, promCheck(thisOtherKoma, move));
        }
        putKoma(board, thisSource, null);
        
        return getNotation(thisSource, thisDestination, move);
    }
    
    private static Notation executeDropMove(Board board, Coordinate thisDestination, String move) {
        String engineMove = "";
        Koma koma;
        try {
            koma = ParserUtils.getDropKoma(move.substring(2, 3), board.getNextTurn());
            if (koma == null) {
                return null;
            }
            putKoma(board, thisDestination, koma);
            removePieceInHand(koma.getType(), board);
            engineMove = getKomaLetter(koma.getType()) + "*" + getEngineMoveCoordinate(thisDestination);
        } catch (Exception ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        Notation notation = new Notation();
        notation.setEngineMove(engineMove);
        
        return notation;
    }
    
    private static String getKomaLetter(Koma.Type type) {
        switch(type) {
            case SFU:
                return "P";
            case SGI:
                return "S";
            case SHI:
                return "R";
            case SKA:
                return "B";
            case SKE:
                return "N";
            case SKI:
                return "G";
            case SKY:
                return "L";
            case GFU:
                return "P";
            case GGI:
                return "S";
            case GHI:
                return "R";
            case GKA:
                return "B";
            case GKE:
                return "N";
            case GKI:
                return "G";
            case GKY:
                return "L";
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
        if (coords == null) {
            return null;
        }
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
        return move.contains(RESIGNS);
    }

    private static boolean isDrop(String move) {
        return move.contains(DROP);
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

    private static Coordinate getSourceCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf("(") + 2)));
        result.setY(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf(")"))) - result.getX() * 10);
        return result;
    }

}
