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
import com.chadfield.shogiexplorer.utils.NotationUtils;
import com.chadfield.shogiexplorer.utils.ParserUtils;
import com.ibm.icu.text.Transliterator;
import java.util.List;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class KifParser {

    public static final String DATE = "開始日時：";
    public static final String PLACE = "場所：";
    public static final String TIME_LIMIT = "持ち時間：";
    public static final String SENTE = "先手：";
    public static final String GOTE = "後手：";
    public static final String MOVE_HEADER = "手数----指手---------消費時間-";
    public static final String MULTI_WHITESPACE = "\\s+|\\u3000";
    
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
                    if (line.isEmpty()) {
                        break;
                    }
                                        
                    if (isComment(line)) {
                        positionList.getLast().setComment(positionList.getLast().getComment()+line.substring(1) + "\n") ;
                        continue;
                    }
                    
                    if (!isRegularMove(line)) {
                        break;
                    }
                    
                    count++;
                                       
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
            moveListModel.addElement(gameNum + trans.transliterate(" ☖" + move + "\n"));
        } else {
            moveListModel.addElement(gameNum + trans.transliterate(" ☗" + move + "\n"));
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
            japanese += NotationUtils.SAME + piece;
        } else {
            japanese += NotationUtils.getJapaneseCoordinate(thisDestination) + piece + disambiguation;
        }
        
        if (isPromoted(move)) {
            japanese += NotationUtils.PROMOTED;
        }
        
        notation.setJapanese(japanese);
        
        return notation;
    }
    

    private static Notation executeRegularMove(Board board, Coordinate thisDestination, Coordinate thisSource, Coordinate lastDestination, String move) {
        Koma destinationKoma = getKoma(board, thisDestination);
        if (destinationKoma != null) {
            ParserUtils.addPieceToInHand(getKoma(board, thisDestination), board);
        }        
        Koma sourceKoma = getKoma(board, thisSource);
        String disambiguation = NotationUtils.getDisambiguation(board, thisSource, thisDestination, sourceKoma.getType());
        putKoma(board, thisDestination, promCheck(sourceKoma, move));
        putKoma(board, thisSource, null);
        
        
        boolean same;
        if (lastDestination == null) {
            same = false;
        } else {
            same = thisDestination.sameValue(lastDestination);
        }
              
        return getNotation(thisSource, thisDestination, same, move, NotationUtils.getKomaKanji(sourceKoma.getType()), disambiguation);
    }
        
    private static Notation executeSameMove(Board board, Coordinate thisDestination, Coordinate thisSource, String move) {
        Koma destinationKoma = getKoma(board, thisDestination);
        if (destinationKoma != null) {
            ParserUtils.addPieceToInHand(destinationKoma, board);
        }
        Koma sourceKoma = getKoma(board, thisSource);
        String disambiguation = NotationUtils.getDisambiguation(board, thisSource, thisDestination, sourceKoma.getType());
        putKoma(board, thisDestination, promCheck(sourceKoma, move));
        putKoma(board, thisSource, null);
        
        return getNotation(thisSource, thisDestination, true, move, NotationUtils.getKomaKanji(sourceKoma.getType()), disambiguation);
    }
        
    private static Notation executeDropMove(Board board, Coordinate thisDestination, String move) {
        String engineMove;
        Koma koma;
        try {
            koma = ParserUtils.getDropKoma(move.substring(2, 3), board.getNextTurn());
            String dropNotation = NotationUtils.getDropNotation(board, thisDestination, koma.getType());
            putKoma(board, thisDestination, koma);
            removePieceInHand(koma.getType(), board);
            engineMove = getKomaLetter(koma.getType()) + "*" + getEngineMoveCoordinate(thisDestination);
            Notation notation = new Notation();
            notation.setEngineMove(engineMove);
            notation.setJapanese(NotationUtils.getJapaneseCoordinate(thisDestination) + NotationUtils.getKomaKanji(koma.getType()) + dropNotation);
            return notation;
        } catch (Exception ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;      
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
     
    private static Position executeMove(Board board, String move, Coordinate lastDestination) {
        Coordinate thisDestination = new Coordinate();
        Coordinate thisSource;
        
        if (!isRegularMove(move)) {
            return null;
        } else if (isSame(move)) {
            thisSource = NotationUtils.getSourceCoordinate(move);
            copyCoords(lastDestination, thisDestination);        
        } else if (isDrop(move)) {
            thisDestination = getDestinationCoordinate(move);
            thisSource = null;
        } else {
            thisSource = NotationUtils.getSourceCoordinate(move);
            thisDestination = getDestinationCoordinate(move);
        }
        Notation notation;
        if (!isDrop(move)) {
            if (!isSame(move)) {
                notation = executeRegularMove(board, thisDestination, thisSource, lastDestination, move);
            } else {
                notation = executeSameMove(board, thisDestination, thisSource, move);
            }
        } else {
            notation = executeDropMove(board, thisDestination, move);
        }
        board.setNextTurn(ParserUtils.switchTurn(board.getNextTurn()));
        return new Position(SFENParser.getSFEN(board), thisSource, thisDestination, notation);
    }
    
    private static boolean isRegularMove(String move) {
        if (move.contains(NotationUtils.DROPPED)) {
            return true;
        }
        return move.contains("(") && move.contains(")");
    }
    
    private static String getEngineMoveCoordinate(Coordinate coordinate) {
        return Integer.toString(coordinate.getX()) + (char) ('a' + coordinate.getY() - 1);
    }

    private static void copyCoords(Coordinate from, Coordinate to) {
        to.setX(from.getX());
        to.setY(from.getY());
    }

    public static Koma getKoma(Board board, Coordinate coords) {
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
        return move.charAt(move.indexOf('(') - 1) == NotationUtils.PROMOTED.charAt(0);
    }
     
    private static boolean isDrop(String move) {
        return move.contains(NotationUtils.DROPPED);
    }

    private static boolean isSame(String move) {
        return move.contains(NotationUtils.SAME);
    }

    private static Coordinate getDestinationCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(0, 1)));
        result.setY(parseJapaneseNumber(move.substring(1, 2)));
        return result;
    }

    private static Integer parseJapaneseNumber(String thisChar) {
        switch (thisChar) {
            case NotationUtils.ICHI:
                return 1;
            case NotationUtils.NI:
                return 2;
            case NotationUtils.SAN:
                return 3;
            case NotationUtils.SHI:
                return 4;
            case NotationUtils.GO:
                return 5;
            case NotationUtils.ROKU:
                return 6;
            case NotationUtils.NANA:
                return 7;
            case NotationUtils.HACHI:
                return 8;
            case NotationUtils.KYUU:
                return 9;
            default:
                return null;
        }
    }



}
