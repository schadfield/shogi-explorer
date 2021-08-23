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
        board.inHandKomaMap = new EnumMap<>(Koma.Type.class);

        for (int k = 0; k < sfenLength; k++) {
            char thisChar = sfen.charAt(k);
            if (thisChar != ' ') {
                switch (state) {
                    case BOARD:
                        if (isNumber(thisChar)) {
                            int numBlanks = thisChar - 48;
                            for (int x = k; x < numBlanks; x++) {
                                board.masu[i + x][j] = null;
                            }
                            i += numBlanks;
                        } else {
                            if (thisChar != '/') {
                                if (thisChar == '+') {
                                    isPromoted = true;
                                } else {
                                    board.masu[i][j] = getKoma(thisChar, isPromoted);
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
                            board.nextMove = Turn.SENTE;
                        } else {
                            board.nextMove = Turn.GOTE;
                        }
                        state = ParseState.INHAND;
                        break;
                    case INHAND:
                        if (thisChar == '-') {
                            state = ParseState.COUNT;
                        } else {
                            if (isNumber(thisChar)) {
                                inHandCount = thisChar - 48;
                            } else {
                                switch (thisChar) {
                                    case 'R':
                                        board.inHandKomaMap.put(Koma.Type.SHI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'B':
                                        board.inHandKomaMap.put(Koma.Type.SKA, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'G':
                                        board.inHandKomaMap.put(Koma.Type.SGI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'S':
                                        board.inHandKomaMap.put(Koma.Type.SKI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'N':
                                        board.inHandKomaMap.put(Koma.Type.SKE, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'L':
                                        board.inHandKomaMap.put(Koma.Type.SKY, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'P':
                                        board.inHandKomaMap.put(Koma.Type.SFU, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'r':
                                        board.inHandKomaMap.put(Koma.Type.GHI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'b':
                                        board.inHandKomaMap.put(Koma.Type.GKA, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'g':
                                        board.inHandKomaMap.put(Koma.Type.GGI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 's':
                                        board.inHandKomaMap.put(Koma.Type.GKI, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'n':
                                        board.inHandKomaMap.put(Koma.Type.GKE, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'l':
                                        board.inHandKomaMap.put(Koma.Type.GKY, inHandCount);
                                        inHandCount = 1;
                                        break;
                                    case 'p':
                                        board.inHandKomaMap.put(Koma.Type.GFU, inHandCount);
                                        inHandCount = 1;
                                        break;
                                }
                            }
                        }
                        break;
                }
            }
        }

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

    private static boolean isNumber(char thisChar) {
        return thisChar > '0' && thisChar <= '9';
    }

}
