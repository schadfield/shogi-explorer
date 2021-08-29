package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import objects.Board;
import objects.Coordinate;
import objects.Game;
import objects.Koma;
import objects.Position;

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
    public static final String KOMA_HI = "飛";
    public static final String KOMA_KA = "角";
    public static final String KOMA_KI = "金";
    public static final String KOMA_GI = "銀";
    public static final String KOMA_KE = "桂";
    public static final String KOMA_KY = "香";
    public static final String KOMA_FU = "歩";
    public static final String DATE = "開始日時：";
    public static final String PLACE = "場所：";
    public static final String TIME_LIMIT = "持ち時間：";
    public static final String SENTE = "先手：";
    public static final String GOTE = "後手：";
    public static final String MOVE_HEADER = "手数----指手---------消費時間-";

    public static Game parseKif(DefaultListModel moveListModel, File kifFile) throws FileNotFoundException, IOException {
        moveListModel.clear();
        moveListModel.addElement("Start");
        Board board = SFENParser.parse(new Board(), "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1");
        Game game = new Game();
        LinkedList<Position> positionList = new LinkedList<>();
        positionList.add(new Position(SFENParser.getSFEN(board), null, null));
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(kifFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KifParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        Coordinate lastDestination = null;
        BufferedReader kifBuf = new BufferedReader(fileReader);
        String line;
        int count = 0;
        boolean foundHeader = false;
        String comment = "";
        try {
            while ((line = kifBuf.readLine()) != null) {
                if (isResigns(line)) {
                    count++;
                    if (board.getNextMove() == Board.Turn.SENTE) {
                        board.setNextMove(Board.Turn.GOTE);
                    } else {
                        board.setNextMove(Board.Turn.SENTE);
                    }

                    String splitLine[] = line.trim().split("\\s+|\\u3000");
                    String move;
                    int gameNum = Integer.parseInt(splitLine[0]);
                    move = splitLine[1];
                    if (isSame(line)) {
                        move += "\u3000" + splitLine[2];
                    }

                    if (board.getNextMove() == Board.Turn.GOTE) {
                        moveListModel.addElement(gameNum + " ☗" + move + "\n");
                    } else {
                        moveListModel.addElement(gameNum + " ☖" + move + "\n");
                    }
                    Position position = new Position(SFENParser.getSFEN(board), board.getSource(), board.getDestination());
                    positionList.add(position);
                    continue;
                }
                if (isComment(line)) {
                    if (!foundHeader) {
                        continue;
                    }
                }
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
                if (!foundHeader) {
                    foundHeader = isHeader(line);
                    if (foundHeader) {
                        continue;
                    }
                }
                if (foundHeader) {
                    if (isComment(line)) {
                        comment += line.substring(1) + "\n";
                        continue;
                    }
                    if (comment.length() > 0) {
                        positionList.getLast().setComment(comment);
                        comment = "";
                    }
                    count++;
                    if (board.getNextMove() == Board.Turn.SENTE) {
                        board.setNextMove(Board.Turn.GOTE);
                    } else {
                        board.setNextMove(Board.Turn.SENTE);
                    }

                    String splitLine[] = line.trim().split("\\s+|\\u3000");
                    String move;
                    int gameNum;
                    try {
                        gameNum = Integer.parseInt(splitLine[0]);
                    } catch (NumberFormatException ex) {
                        continue;
                    }
                    move = splitLine[1];
                    if (isSame(line)) {
                        move += "\u3000" + splitLine[2];
                    }

                    if (board.getNextMove() == Board.Turn.GOTE) {
                        moveListModel.addElement(gameNum + " ☗" + move + "\n");
                    } else {
                        moveListModel.addElement(gameNum + " ☖" + move + "\n");
                    }
                    Position position = executeMove(board, move, lastDestination);
                    lastDestination = position.getDestination();
                    positionList.add(position);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(KifParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (comment.length() > 0) {
            positionList.getLast().setComment(comment);
        }
        game.setPositionList(positionList);
        return game;
    }

    public static String getTimestamp(String line) {
        long parenCount = line.chars().filter(ch -> ch == '(').count();
        Integer timeStartIndex = null;
        if (parenCount == 2) {
            timeStartIndex = line.indexOf("(", line.indexOf("(") + 1);
        } else {
            if (parenCount == 1) {
                timeStartIndex = line.indexOf("(");
            }
        }
        return line.substring(timeStartIndex);
    }

    public static Position executeMove(Board board, String move, Coordinate lastDestination) {
        Coordinate thisDestination = new Coordinate();
        Coordinate thisSource;
        if (isSame(move)) {
            thisSource = getFromCoordinate(move);
            copyCoords(lastDestination, thisDestination);
        } else if (isResigns(move)) {
            return null;
        } else if (isDrop(move)) {
            thisDestination = getDestinationCoordinate(move);
            thisSource = null;
        } else {
            thisSource = getFromCoordinate(move);
            thisDestination = getDestinationCoordinate(move);
        }
        if (!isResigns(move)) {
            if (!isDrop(move)) {
                if (!isSame(move)) {
                    //regular
                    if (getKoma(board, thisDestination) != null) {
                        addPieceToInHand(getKoma(board, thisDestination), board);
                    }
                    putKoma(board, thisDestination, promCheck(getKoma(board, thisSource), move));
                    putKoma(board, thisSource, null);
                } else {
                    //same
                    Koma thisKoma = getKoma(board, thisDestination);
                    if (thisKoma != null) {
                        addPieceToInHand(thisKoma, board);
                    }
                    putKoma(board, thisDestination, promCheck(getKoma(board, thisSource), move));
                    putKoma(board, thisSource, null);

                }
            } else {
                //drop
                Koma koma = getDropKoma(move, board.getNextMove());
                putKoma(board, thisDestination, koma);
                removePieceInHand(koma.getType(), board);
            }
        }
        return new Position(SFENParser.getSFEN(board), thisSource, thisDestination);
    }

    public static void copyCoords(Coordinate from, Coordinate to) {
        to.setX(from.getX());
        to.setY(from.getY());
    }

    public static Koma getKoma(Board board, Coordinate coords) {
        return board.masu[9 - coords.getX()][coords.getY() - 1];
    }

    public static void putKoma(Board board, Coordinate coords, Koma koma) {
        board.masu[9 - coords.getX()][coords.getY() - 1] = koma;
    }

    public static void removePieceInHand(Koma.Type komaType, Board board) {
        if (board.getInHandKomaMap().get(komaType) == 1) {
            board.getInHandKomaMap().remove(komaType);
        } else {
            board.getInHandKomaMap().put(komaType, board.getInHandKomaMap().get(komaType) - 1);
        }
    }

    public static void addPieceToInHand(Koma koma, Board board) {
        Koma invertedKoma = invertKoma(koma.getType());
        if (board.getInHandKomaMap().containsKey(invertedKoma.type)) {
            board.getInHandKomaMap().put(invertedKoma.type, 1 + board.getInHandKomaMap().get(invertedKoma.type));
        } else {
            board.getInHandKomaMap().put(invertedKoma.type, 1);
        }
    }

    public static Koma invertKoma(Koma.Type komaType) {
        switch (komaType) {
            case SFU:
            case STO:
                return new Koma(Koma.Type.GFU);
            case SKY:
            case SNY:
                return new Koma(Koma.Type.GKY);
            case SKE:
            case SNK:
                return new Koma(Koma.Type.GKE);
            case SGI:
            case SNG:
                return new Koma(Koma.Type.GGI);
            case SKI:
                return new Koma(Koma.Type.GKI);
            case SKA:
            case SUM:
                return new Koma(Koma.Type.GKA);
            case SHI:
            case SRY:
                return new Koma(Koma.Type.GHI);
            case GFU:
            case GTO:
                return new Koma(Koma.Type.SFU);
            case GKY:
            case GNY:
                return new Koma(Koma.Type.SKY);
            case GKE:
            case GNK:
                return new Koma(Koma.Type.SKE);
            case GGI:
            case GNG:
                return new Koma(Koma.Type.SGI);
            case GKI:
                return new Koma(Koma.Type.SKI);
            case GKA:
            case GUM:
                return new Koma(Koma.Type.SKA);
            case GHI:
            case GRY:
                return new Koma(Koma.Type.SHI);
        }
        return null;
    }

    public static Koma promoteKoma(Koma.Type komaType) {
        switch (komaType) {
            case SFU:
                return new Koma(Koma.Type.STO);
            case SKY:
                return new Koma(Koma.Type.SNY);
            case SKE:
                return new Koma(Koma.Type.SNK);
            case SGI:
                return new Koma(Koma.Type.SNG);
            case SKA:
                return new Koma(Koma.Type.SUM);
            case SHI:
                return new Koma(Koma.Type.SRY);
            case GFU:
                return new Koma(Koma.Type.GTO);
            case GKY:
                return new Koma(Koma.Type.GNY);
            case GKE:
                return new Koma(Koma.Type.GNK);
            case GGI:
                return new Koma(Koma.Type.GNG);
            case GKA:
                return new Koma(Koma.Type.GUM);
            case GHI:
                return new Koma(Koma.Type.GRY);
        }
        return null;
    }

    public static Koma getDropKoma(String move, Board.Turn turn) {
        String pieceName = move.substring(2, 3);
        switch (pieceName) {
            case KOMA_HI:
                if (turn == Board.Turn.GOTE) {
                    return new Koma(Koma.Type.SHI);
                } else {
                    return new Koma(Koma.Type.GHI);
                }
            case KOMA_KA:
                if (turn == Board.Turn.GOTE) {
                    return new Koma(Koma.Type.SKA);
                } else {
                    return new Koma(Koma.Type.GKA);
                }
            case KOMA_KI:
                if (turn == Board.Turn.GOTE) {
                    return new Koma(Koma.Type.SKI);
                } else {
                    return new Koma(Koma.Type.GKI);
                }
            case KOMA_GI:
                if (turn == Board.Turn.GOTE) {
                    return new Koma(Koma.Type.SGI);
                } else {
                    return new Koma(Koma.Type.GGI);
                }
            case KOMA_KE:
                if (turn == Board.Turn.GOTE) {
                    return new Koma(Koma.Type.SKE);
                } else {
                    return new Koma(Koma.Type.GKE);
                }
            case KOMA_KY:
                if (turn == Board.Turn.GOTE) {
                    return new Koma(Koma.Type.SKY);
                } else {
                    return new Koma(Koma.Type.GKY);
                }
            case KOMA_FU:
                if (turn == Board.Turn.GOTE) {
                    return new Koma(Koma.Type.SFU);
                } else {
                    return new Koma(Koma.Type.GFU);
                }
        }
        return null;
    }

    public static Koma promCheck(Koma koma, String move) {
        if (!isPromoted(move)) {
            return koma;
        } else {
            return promoteKoma(koma.getType());
        }
    }

    public static boolean usesTimestamp(String line) {
        return line.contains(" (");
    }

    public static boolean isComment(String line) {
        return line.startsWith("*");
    }

    public static boolean isHeader(String line) {
        return line.startsWith(MOVE_HEADER);
    }

    public static boolean isPromoted(String move) {
        if (isDrop(move)) {
            return false;
        }
        return move.charAt(move.indexOf('(') - 1) == PROMOTED.charAt(0);
    }

    public static boolean isResigns(String move) {
        return move.contains(RESIGNS);
    }

    public static boolean isDrop(String move) {
        return move.contains(DROP);
    }

    public static boolean isSame(String move) {
        return move.contains(SAME);
    }

    public static Coordinate getDestinationCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(0, 1)));
        result.setY(parseJapaneseNumber(move.substring(1, 2)));
        return result;
    }

    public static Integer parseJapaneseNumber(String thisChar) {
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
        }
        return null;
    }

    public static Coordinate getFromCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf("(") + 2)));
        result.setY(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf(")"))) - result.getX() * 10);
        return result;
    }

    public static File openFile(String path) {
        return new File(path);
    }

}
