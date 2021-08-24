package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import objects.Board;
import objects.Coordinate;
import objects.Koma;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class KifParser {

    public static void parseKif(Board board, JPanel boardPanel, JTextArea moveText) throws FileNotFoundException, IOException {

        File kifFile = openFile("test.kif");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(kifFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KifParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        Coordinate lastDestination = null;
        BufferedReader kifBuf = new BufferedReader(fileReader);
        String line;
        int lineNum = 0;
        try {
            while ((line = kifBuf.readLine()) != null) {
                lineNum++;
                if (lineNum > 8) {
                    String splitLine[] = line.split("\\s+");
                    int gameNum = Integer.parseInt(splitLine[0]);
                    String move;
                    String time;
                    if (splitLine.length == 4) {
                        move = splitLine[1] + " " + splitLine[2];
                        time = splitLine[3];
                    } else {
                        move = splitLine[1];
                        time = splitLine[2];
                    }
                    moveText.append(gameNum + " " + move + "\n");
                    moveText.setCaretPosition(moveText.getDocument().getLength());
                    System.out.println(move);
                    lastDestination = executeMove(board, boardPanel, move, lastDestination);
                    if (board.getNextMove() == Board.Turn.SENTE) {
                        board.setNextMove(Board.Turn.GOTE);
                    } else {
                        board.setNextMove(Board.Turn.SENTE);

                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(KifParser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Coordinate executeMove(Board board, JPanel boardPanel, String move, Coordinate lastDestination) {
        Coordinate thisDestination = new Coordinate();
        Coordinate thisSource = null;
        if (isSame(move)) {
            System.out.println("same");
            thisSource = getFromCoordinate(move);
            System.out.println("(" + lastDestination.getX() + "," + lastDestination.getY() + ")");
        } else if (isResigns(move)) {
            System.out.println("resign");
        } else if (isDrop(move)) {
            System.out.println("drop");
            thisDestination = getDestinationCoordinate(move);
        } else {
            System.out.println("regular");
            thisSource = getFromCoordinate(move);
            thisDestination = getDestinationCoordinate(move);
        }
        if (!isResigns(move)) {
            if (!isDrop(move)) {
                if (!isSame(move)) {
                    //regular
                    if (board.masu[9 - thisDestination.getX()][thisDestination.getY() - 1] != null) {
                        addPieceToInHand(board.masu[9 - thisDestination.getX()][thisDestination.getY() - 1], board);
                    }
                    board.masu[9 - thisDestination.getX()][thisDestination.getY() - 1]
                            = promCheck(board.masu[9 - thisSource.getX()][thisSource.getY() - 1], move);
                    board.masu[9 - thisSource.getX()][thisSource.getY() - 1] = null;
                } else {
                    //same
                    thisDestination.setX(lastDestination.getX());
                    thisDestination.setY(lastDestination.getY());
                    if (board.masu[9 - thisDestination.getX()][thisDestination.getY() - 1] != null) {
                        addPieceToInHand(board.masu[9 - thisDestination.getX()][thisDestination.getY() - 1], board);
                    }
                    board.masu[9 - thisDestination.getX()][thisDestination.getY() - 1]
                            = promCheck(board.masu[9 - thisSource.getX()][thisSource.getY() - 1], move);
                    board.masu[9 - thisSource.getX()][thisSource.getY() - 1] = null;

                }
            } else {
                //drop
                Koma koma = getDropKoma(move, board.getNextMove());
                board.masu[9 - thisDestination.getX()][thisDestination.getY() - 1] = koma;
                removePieceInHand(koma, board);
            }
        }
        RenderBoard.loadBoard(board, boardPanel);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(KifParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return thisDestination;
    }

    public static void removePieceInHand(Koma koma, Board board) {
        if (board.getInHandKomaMap().get(koma.getType()) == 1) {
            board.getInHandKomaMap().remove(koma.getType());
        } else {
            board.getInHandKomaMap().put(koma.getType(), board.getInHandKomaMap().get(koma.getType()) - 1);
        }
    }

    public static void addPieceToInHand(Koma koma, Board board) {
        Koma invertedKoma = invertKoma(koma);
        if (board.getInHandKomaMap().containsKey(invertedKoma.type)) {
            board.getInHandKomaMap().put(invertedKoma.type, 1 + board.getInHandKomaMap().get(invertedKoma.type));
        } else {
            board.getInHandKomaMap().put(invertedKoma.type, 1);
        }
    }

    public static Koma invertKoma(Koma koma) {
        switch (koma.type) {
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

    public static Koma promoteKoma(Koma koma) {
        switch (koma.type) {
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
            case GNG:
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
            case "飛":
                if (turn == Board.Turn.SENTE) {
                    return new Koma(Koma.Type.SHI);
                } else {
                    return new Koma(Koma.Type.GHI);
                }
            case "角":
                if (turn == Board.Turn.SENTE) {
                    return new Koma(Koma.Type.SKA);
                } else {
                    return new Koma(Koma.Type.GKA);
                }
            case "金":
                if (turn == Board.Turn.SENTE) {
                    return new Koma(Koma.Type.SKI);
                } else {
                    return new Koma(Koma.Type.GKI);
                }
            case "銀":
                if (turn == Board.Turn.SENTE) {
                    return new Koma(Koma.Type.SGI);
                } else {
                    return new Koma(Koma.Type.GGI);
                }
            case "桂":
                if (turn == Board.Turn.SENTE) {
                    return new Koma(Koma.Type.SKE);
                } else {
                    return new Koma(Koma.Type.GKE);
                }
            case "香":
                if (turn == Board.Turn.SENTE) {
                    return new Koma(Koma.Type.SKY);
                } else {
                    return new Koma(Koma.Type.GKY);
                }
            case "歩":
                if (turn == Board.Turn.SENTE) {
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
            return promoteKoma(koma);
        }
    }

    public static boolean isPromoted(String move) {
        return move.contains("成");
    }

    public static boolean isResigns(String move) {
        return move.contains("投了");
    }

    public static boolean isDrop(String move) {
        return move.contains("打");
    }

    public static boolean isSame(String move) {
        return move.contains("同");
    }

    public static Coordinate getDestinationCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(0, 1)));
        result.setY(parseJapaneseNumber(move.substring(1, 2)));
        System.out.println("(" + result.getX() + "," + result.getY() + ")");
        return result;
    }

    public static Integer parseJapaneseNumber(String thisChar) {
        switch (thisChar) {
            case "一":
                return 1;
            case "二":
                return 2;
            case "三":
                return 3;
            case "四":
                return 4;
            case "五":
                return 5;
            case "六":
                return 6;
            case "七":
                return 7;
            case "八":
                return 8;
            case "九":
                return 9;
        }
        return null;
    }

    public static Coordinate getFromCoordinate(String move) {
        Coordinate result = new Coordinate();
        result.setX(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf("(") + 2)));
        result.setY(Integer.parseInt(move.substring(move.indexOf("(") + 1, move.indexOf(")"))) - result.getX() * 10);
        System.out.println("(" + result.getX() + "," + result.getY() + ")");
        return result;
    }

    public static File openFile(String path) {
        return new File(path);
    }

}
