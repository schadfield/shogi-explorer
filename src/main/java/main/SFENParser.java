package main;

import java.util.EnumMap;
import java.util.HashMap;
import objects.Board;
import objects.Board.Turn;
import objects.Koma;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class SFENParser {

    public enum ParseState {
        BOARD, MOVE, INHAND, COUNT
    }

    public static Board parse(Board board, String sfen) {

        int sfenLength = sfen.length();
        ParseState state = ParseState.BOARD;
        int i = 0;
        int j = 0;
        boolean isPromoted = false;
        int inHandCount = 1;
        int moveCount = 0;
        board.setInHandKomaMap(new EnumMap<>(Koma.Type.class));

        for (int k = 0; k < sfenLength; k++) {
            char thisChar = sfen.charAt(k);
            if (thisChar != ' ' || state == ParseState.INHAND) {
                switch (state) {
                    case BOARD:
                        if (isNumber(thisChar)) {
                            int numBlanks = thisChar - 48;
                            for (int x = k; x < numBlanks; x++) {
                                board.getMasu()[i + x][j] = null;
                            }
                            i += numBlanks;
                        } else {
                            if (thisChar != '/') {
                                if (thisChar == '+') {
                                    isPromoted = true;
                                } else {
                                    board.getMasu()[i][j] = getKoma(thisChar, isPromoted);
                                    isPromoted = false;
                                    i++;
                                }
                            }
                        }
                        if (i == 9) {
                            i = 0;
                            j++;
                        }
                        if (j == 9) {
                            state = ParseState.MOVE;
                        }
                        break;
                    case MOVE:
                        if (thisChar == 'b') {
                            board.setNextMove(Turn.SENTE);
                        } else {
                            board.setNextMove(Turn.GOTE);
                        }
                        state = ParseState.INHAND;
                        k++;
                        break;
                    case INHAND:
                        if (thisChar == '-') {
                            state = ParseState.COUNT;
                        } else {
                            if (isNumber(thisChar)) {
                                inHandCount = thisChar - 48;
                            } else {
                                switch (thisChar) {
                                    case ' ':
                                        state = ParseState.COUNT;
                                        break;
                                    case 'R':
                                        board.getInHandKomaMap().put(Koma.Type.SHI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'B':
                                        board.getInHandKomaMap().put(Koma.Type.SKA, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'G':
                                        board.getInHandKomaMap().put(Koma.Type.SKI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'S':
                                        board.getInHandKomaMap().put(Koma.Type.SGI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'N':
                                        board.getInHandKomaMap().put(Koma.Type.SKE, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'L':
                                        board.getInHandKomaMap().put(Koma.Type.SKY, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'P':
                                        board.getInHandKomaMap().put(Koma.Type.SFU, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'r':
                                        board.getInHandKomaMap().put(Koma.Type.GHI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'b':
                                        board.getInHandKomaMap().put(Koma.Type.GKA, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'g':
                                        board.getInHandKomaMap().put(Koma.Type.GKI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 's':
                                        board.getInHandKomaMap().put(Koma.Type.GGI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'n':
                                        board.getInHandKomaMap().put(Koma.Type.GKE, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'l':
                                        board.getInHandKomaMap().put(Koma.Type.GKY, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'p':
                                        board.getInHandKomaMap().put(Koma.Type.GFU, inHandCount);
                                        inHandCount = 1;
                                        break;
                                }
                            }
                        }
                        break;
                    case COUNT:
                        if (moveCount == 0) {
                            moveCount = thisChar - 48;
                        } else {
                            moveCount = moveCount * 10 + thisChar - 48;
                        }
                        break;
                }
            }
        }

        board.setMoveCount(moveCount);
        return board;
    }

    private static Koma getKoma(char thisChar, boolean isPromoted) {
        HashMap<Character, Koma.Type> pieceMap = new HashMap<>();
        HashMap<Character, Koma.Type> pieceMapPromoted = new HashMap<>();

        //<editor-fold defaultstate="collapsed" desc="Map initialization">
        pieceMap.put('l', Koma.Type.GKY);
        pieceMap.put('n', Koma.Type.GKE);
        pieceMap.put('s', Koma.Type.GGI);
        pieceMap.put('g', Koma.Type.GKI);
        pieceMap.put('k', Koma.Type.GGY);
        pieceMap.put('r', Koma.Type.GHI);
        pieceMap.put('b', Koma.Type.GKA);
        pieceMap.put('p', Koma.Type.GFU);

        pieceMap.put('L', Koma.Type.SKY);
        pieceMap.put('N', Koma.Type.SKE);
        pieceMap.put('S', Koma.Type.SGI);
        pieceMap.put('G', Koma.Type.SKI);
        pieceMap.put('K', Koma.Type.SOU);
        pieceMap.put('R', Koma.Type.SHI);
        pieceMap.put('B', Koma.Type.SKA);
        pieceMap.put('P', Koma.Type.SFU);

        pieceMapPromoted.put('l', Koma.Type.GNY);
        pieceMapPromoted.put('n', Koma.Type.GNK);
        pieceMapPromoted.put('s', Koma.Type.GNG);
        pieceMapPromoted.put('r', Koma.Type.GRY);
        pieceMapPromoted.put('b', Koma.Type.GUM);
        pieceMapPromoted.put('p', Koma.Type.GTO);

        pieceMapPromoted.put('L', Koma.Type.SNY);
        pieceMapPromoted.put('N', Koma.Type.SNK);
        pieceMapPromoted.put('S', Koma.Type.SNG);
        pieceMapPromoted.put('R', Koma.Type.SRY);
        pieceMapPromoted.put('B', Koma.Type.SUM);
        pieceMapPromoted.put('P', Koma.Type.STO);
        //</editor-fold>

        if (isPromoted) {
            return new Koma(pieceMapPromoted.get(thisChar));
        } else {
            return new Koma(pieceMap.get(thisChar));
        }
    }

    public static String getSFEN(Board board) {
        String result = "";
        int spaceCount = 0;
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                if (board.getMasu()[i][j] != null) {
                    if (spaceCount > 0) {
                        result += spaceCount;
                        spaceCount = 0;
                    }
                    result += getSFENCode(board.getMasu()[i][j]);
                } else {
                    spaceCount++;
                }
            }
            if (spaceCount > 0) {
                result += spaceCount;
                spaceCount = 0;
            }
            if (j < 8) {
                result += '/';
            }
        }

        if (board.getNextMove() == Board.Turn.SENTE) {
            result += " b";
        } else {
            result += " w";
        }

        result += getInHandCode(board);

        result += " " + board.getMoveCount();

        return result;
    }

    public static String getInHandCode(Board board) {
        if (board.getInHandKomaMap().isEmpty()) {
            return " -";
        } else {
            String result = " ";
            if (board.getInHandKomaMap().containsKey(Koma.Type.SHI)) {
                int number = board.getInHandKomaMap().get(Koma.Type.SHI);
                if (number > 1) {
                    result += number;
                }
                result += "R";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.SKA)) {
                int number = board.getInHandKomaMap().get(Koma.Type.SKA);
                if (number > 1) {
                    result += number;
                }
                result += "B";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.SKI)) {
                int number = board.getInHandKomaMap().get(Koma.Type.SKI);
                if (number > 1) {
                    result += number;
                }
                result += "G";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.SGI)) {
                int number = board.getInHandKomaMap().get(Koma.Type.SGI);
                if (number > 1) {
                    result += number;
                }
                result += "S";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.SKE)) {
                int number = board.getInHandKomaMap().get(Koma.Type.SKE);
                if (number > 1) {
                    result += number;
                }
                result += "N";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.SKY)) {
                int number = board.getInHandKomaMap().get(Koma.Type.SKY);
                if (number > 1) {
                    result += number;
                }
                result += "L";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.SFU)) {
                int number = board.getInHandKomaMap().get(Koma.Type.SFU);
                if (number > 1) {
                    result += number;
                }
                result += "P";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.GHI)) {
                int number = board.getInHandKomaMap().get(Koma.Type.GHI);
                if (number > 1) {
                    result += number;
                }
                result += "r";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.GKA)) {
                int number = board.getInHandKomaMap().get(Koma.Type.GKA);
                if (number > 1) {
                    result += number;
                }
                result += "b";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.GKI)) {
                int number = board.getInHandKomaMap().get(Koma.Type.GKI);
                if (number > 1) {
                    result += number;
                }
                result += "g";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.GGI)) {
                int number = board.getInHandKomaMap().get(Koma.Type.GGI);
                if (number > 1) {
                    result += number;
                }
                result += "s";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.GKE)) {
                int number = board.getInHandKomaMap().get(Koma.Type.GKE);
                if (number > 1) {
                    result += number;
                }
                result += "n";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.GKY)) {
                int number = board.getInHandKomaMap().get(Koma.Type.GKY);
                if (number > 1) {
                    result += number;
                }
                result += "l";
            }
            if (board.getInHandKomaMap().containsKey(Koma.Type.GFU)) {
                int number = board.getInHandKomaMap().get(Koma.Type.GFU);
                if (number > 1) {
                    result += number;
                }
                result += "p";
            }
            return result;
        }

    }

    public static String getSFENCode(Koma koma) {
        HashMap<Koma.Type, String> pieceMap = new HashMap<>();

        //<editor-fold defaultstate="collapsed" desc="Map initialization">
        pieceMap.put(Koma.Type.GKY, "l");
        pieceMap.put(Koma.Type.GKE, "n");
        pieceMap.put(Koma.Type.GGI, "s");
        pieceMap.put(Koma.Type.GKI, "g");
        pieceMap.put(Koma.Type.GGY, "k");
        pieceMap.put(Koma.Type.GHI, "r");
        pieceMap.put(Koma.Type.GKA, "b");
        pieceMap.put(Koma.Type.GFU, "p");

        pieceMap.put(Koma.Type.SKY, "L");
        pieceMap.put(Koma.Type.SKE, "N");
        pieceMap.put(Koma.Type.SGI, "S");
        pieceMap.put(Koma.Type.SKI, "G");
        pieceMap.put(Koma.Type.SOU, "K");
        pieceMap.put(Koma.Type.SHI, "R");
        pieceMap.put(Koma.Type.SKA, "B");
        pieceMap.put(Koma.Type.SFU, "P");

        pieceMap.put(Koma.Type.GNY, "+l");
        pieceMap.put(Koma.Type.GNK, "+n");
        pieceMap.put(Koma.Type.GNG, "+s");
        pieceMap.put(Koma.Type.GRY, "+r");
        pieceMap.put(Koma.Type.GUM, "+b");
        pieceMap.put(Koma.Type.GTO, "+p");

        pieceMap.put(Koma.Type.SNY, "+L");
        pieceMap.put(Koma.Type.SNK, "+N");
        pieceMap.put(Koma.Type.SNG, "+S");
        pieceMap.put(Koma.Type.SRY, "+R");
        pieceMap.put(Koma.Type.SUM, "+B");
        pieceMap.put(Koma.Type.STO, "+P");

        //</editor-fold>
        return pieceMap.get(koma.getType());
    }

    private static boolean isNumber(char thisChar) {
        return thisChar > '0' && thisChar <= '9';
    }

}
