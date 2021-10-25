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
import com.chadfield.shogiexplorer.objects.Board.Turn;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Koma;
import com.chadfield.shogiexplorer.objects.Notation;
import com.chadfield.shogiexplorer.objects.Position;
import com.chadfield.shogiexplorer.utils.NotationUtils;
import com.chadfield.shogiexplorer.utils.ParserUtils;
import com.ibm.icu.text.Transliterator;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class KifParser {

    private static final String DATE = "開始日時：";
    private static final String PLACE = "場所：";
    private static final String TIME_LIMIT = "持ち時間：";
    private static final String SENTE = "先手：";
    private static final String GOTE = "後手：";
    private static final String MOVE_HEADER = "手数----指手---------消費時間-";
    private static final String MULTI_WHITESPACE = "\\s+|\\u3000";
    private static final String HANDICAP = "手合割：";

    private KifParser() {
        throw new IllegalStateException("Utility class");
    }

    public static Game parseKif(DefaultListModel<String> moveListModel, File kifFile, String clipboardStr, boolean shiftFile, List<List<Position>> analysisPositionList) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
        moveListModel.clear();
        moveListModel.addElement(bundle.getString("label_start_position"));
        Board board = null;
        Game game = new Game();
        game.setAnalysisPositionList(analysisPositionList);
        LinkedList<Position> positionList = new LinkedList<>();

        boolean foundHeader = false;

        BufferedReader fileReader = null;

        try {
            if (clipboardStr == null) {
                if (shiftFile) {
                    fileReader = Files.newBufferedReader(kifFile.toPath(), Charset.forName("SJIS"));
                } else {
                    fileReader = Files.newBufferedReader(kifFile.toPath(), StandardCharsets.UTF_8);
                }
            } else {
                fileReader = new BufferedReader(new StringReader(clipboardStr));
            }

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
                    if (board == null) {
                        board = getStartBoard(game);
                        positionList.add(new Position(SFENParser.getSFEN(board), null, null, new Notation()));
                    }

                    if (line.isEmpty()) {
                        break;
                    }

                    if (isComment(line)) {
                        positionList.getLast().setComment(positionList.getLast().getComment() + line.substring(1) + "\n");
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

        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }

        game.setPositionList(positionList);
        return game;
    }

    private static Board getStartBoard(Game game) {
        String sfen;
        sfen = switch (game.getHandicap()) {
            case Game.HANDICAP_LANCE ->
                "lnsgkgsn1/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1";
            case Game.HANDICAP_BISHOP ->
                "lnsgkgsnl/1r7/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1";
            case Game.HANDICAP_ROOK ->
                "lnsgkgsnl/7b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1";
            case Game.HANDICAP_ROOK_LANCE ->
                "lnsgkgsn1/7b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1";
            case Game.HANDICAP_2_PIECE ->
                "lnsgkgsnl/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1";
            case Game.HANDICAP_4_PIECE ->
                "1nsgkgsn1/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1";
            case Game.HANDICAP_6_PIECE ->
                "2sgkgs2/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1";
            default ->
                "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1";
        };
        return SFENParser.parse(sfen);
    }

    private static Coordinate parseRegularMove(Board board, String line, DefaultListModel<String> moveListModel, Coordinate lastDestination, LinkedList<Position> positionList) {
        String[] splitLine = line.trim().split(MULTI_WHITESPACE);

        int gameNum;

        try {
            gameNum = Integer.parseInt(splitLine[0]);
        } catch (NumberFormatException ex) {
            positionList.getLast().setComment(positionList.getLast().getComment() + line + "\n");
            return lastDestination;
        }

        String move = extractRegularMove(splitLine, isSame(line));

        Position position = executeMove(board, move, lastDestination);
        if (position != null) {
            lastDestination = position.getDestination();
            addMoveToMoveList(moveListModel, gameNum, position.getNotation().getJapanese(), board.getNextTurn());
            positionList.add(position);
        }

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
            game.setSente(line.substring(SENTE.length()).trim());
        }
        if (line.startsWith(GOTE)) {
            game.setGote(line.substring(GOTE.length()).trim());
        }
        if (line.startsWith(PLACE)) {
            game.setPlace(line.substring(PLACE.length()).trim());
        }
        if (line.startsWith(HANDICAP)) {
            game.setHandicap(line.substring(HANDICAP.length()).trim());
        }
        if (line.startsWith(TIME_LIMIT)) {
            game.setTimeLimit(line.substring(TIME_LIMIT.length()).trim());
        }
        if (line.startsWith(DATE)) {
            game.setDate(line.substring(DATE.length()).trim());
        }
    }

    private static void addMoveToMoveList(DefaultListModel<String> moveListModel, int gameNum, String move, Turn turn) {
        Transliterator trans = Transliterator.getInstance("Halfwidth-Fullwidth");
        if (turn == Turn.SENTE) {
            moveListModel.addElement(gameNum + trans.transliterate(" ☖" + move));
        } else {
            moveListModel.addElement(gameNum + trans.transliterate(" ☗" + move));
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
        Koma.Type sourceKomaType = sourceKoma.getType();
        String disambiguation = NotationUtils.getDisambiguation(board, thisSource, thisDestination, sourceKomaType);
        putKoma(board, thisDestination, promCheck(sourceKoma, move));
        putKoma(board, thisSource, null);

        boolean same;
        if (lastDestination == null) {
            same = false;
        } else {
            same = thisDestination.sameValue(lastDestination);
        }

        return getNotation(thisSource, thisDestination, same, move, NotationUtils.getKomaKanji(sourceKomaType), disambiguation);
    }

    private static Notation executeSameMove(Board board, Coordinate thisDestination, Coordinate thisSource, String move) {
        Koma destinationKoma = getKoma(board, thisDestination);
        if (destinationKoma != null) {
            ParserUtils.addPieceToInHand(destinationKoma, board);
        }
        Koma sourceKoma = getKoma(board, thisSource);
        Koma.Type sourceKomaType = sourceKoma.getType();
        String disambiguation = NotationUtils.getDisambiguation(board, thisSource, thisDestination, sourceKomaType);
        putKoma(board, thisDestination, promCheck(sourceKoma, move));
        putKoma(board, thisSource, null);

        return getNotation(thisSource, thisDestination, true, move, NotationUtils.getKomaKanji(sourceKomaType), disambiguation);
    }

    private static Notation executeDropMove(Board board, Coordinate thisDestination, String move) {
        String engineMove;
        Koma koma;
        try {
            koma = ParserUtils.getDropKoma(move.substring(2, 3), board.getNextTurn());
            Koma.Type komaType = koma.getType();
            String dropNotation = NotationUtils.getDropNotation(board, thisDestination, komaType);
            putKoma(board, thisDestination, koma);
            removePieceInHand(komaType, board);
            engineMove = getKomaLetter(komaType) + "*" + getEngineMoveCoordinate(thisDestination);
            Notation notation = new Notation();
            notation.setEngineMove(engineMove);
            notation.setJapanese(NotationUtils.getJapaneseCoordinate(thisDestination) + NotationUtils.getKomaKanji(komaType) + dropNotation);
            return notation;
        } catch (Exception ex) {
            Logger.getLogger(GameAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static String getKomaLetter(Koma.Type type) {
        return switch (type) {
            case SFU, GFU ->
                "P";
            case SGI, GGI ->
                "S";
            case SHI, GHI ->
                "R";
            case SKA, GKA ->
                "B";
            case SKE, GKE ->
                "N";
            case SKI, GKI ->
                "G";
            case SKY, GKY ->
                "L";
            default ->
                null;
        };
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
        return switch (thisChar) {
            case NotationUtils.ICHI ->
                1;
            case NotationUtils.NI ->
                2;
            case NotationUtils.SAN ->
                3;
            case NotationUtils.SHI ->
                4;
            case NotationUtils.GO ->
                5;
            case NotationUtils.ROKU ->
                6;
            case NotationUtils.NANA ->
                7;
            case NotationUtils.HACHI ->
                8;
            case NotationUtils.KYUU ->
                9;
            default ->
                null;
        };
    }

}
