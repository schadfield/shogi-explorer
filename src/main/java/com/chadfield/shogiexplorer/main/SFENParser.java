package com.chadfield.shogiexplorer.main;

import java.util.EnumMap;
import java.util.HashMap;
import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Board.Turn;
import com.chadfield.shogiexplorer.objects.Koma;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class SFENParser {

    public enum ParseState {
        BOARD, MOVE, INHAND, COUNT
    }

    public static Board parse(String sfen) {
        
        Board board = new Board();
        board.setInHandKomaMap(new EnumMap<>(Koma.Type.class));
        
        String[] splitSfen = sfen.split(" ");
     
        parsePieces(board, splitSfen[0]);
        board.setNextTurn(getTurn(splitSfen[1]));
        board = parseInHand(board, splitSfen[2]);
        board.setMoveCount(Integer.parseInt(splitSfen[3]));
        return board;
    }
    
    public static String getSFEN(Board board) {
        StringBuilder bldResult = new StringBuilder();
        int spaceCount = 0;
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                if (board.getMasu()[i][j] != null) {
                    if (spaceCount > 0) {
                        bldResult.append(spaceCount);
                        spaceCount = 0;
                    }
                    bldResult.append(getSFENCode(board.getMasu()[i][j]));
                } else {
                    spaceCount++;
                }
            }
            if (spaceCount > 0) {
                bldResult.append(spaceCount);
                spaceCount = 0;
            }
            if (j < 8) {
                bldResult.append('/');
            }
        }

        bldResult.append(getNextTurnString(board.getNextTurn()));       
        bldResult.append(getInHandString(board));
        bldResult.append(" ");       
        bldResult.append(board.getMoveCount());       
        return bldResult.toString();
    }
    
    private static Board parseInHand(Board board, String pieceStr) {
        int inHandCount = 1;
        for (int k = 0; k < pieceStr.length(); k++) {
            char thisChar = pieceStr.charAt(k);
            if (thisChar == '-') {
                return board;
            } else {
                if (isNumber(thisChar)) {
                    inHandCount = thisChar - 48;
                } else {
                    switch (thisChar) {
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
                        default:
                            break;
                    }
                }
            }
        }
        return board;
    }
    
    private static Board parsePieces(Board board, String pieceStr) {
        int i = 0;
        int j = 0;
        boolean isPromoted = false;
        for (int k = 0; k < pieceStr.length(); k++) {
            char thisChar = pieceStr.charAt(k);
            if (isNumber(thisChar)) {
                i += putBlanks(thisChar, board, i, j, k);
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
        }
        return board;
    }
        
    private static Board.Turn getTurn(String thisStr) {
        if (thisStr.contains("b")) {
            return Turn.SENTE;
        } else {
            return Turn.GOTE;
        }
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
    
    private static int putBlanks(char thisChar, Board board, int i, int j, int k) {
        int numBlanks = thisChar - 48;
        for (int x = k; x < numBlanks; x++) {
            board.getMasu()[i + x][j] = null;
        }
        return numBlanks;
    }

    private static String getNextTurnString(Turn nextMove) {
        if (nextMove == Board.Turn.SENTE) {
            return " b";
        } else {
            return " w";
        }
    }

    private static String getInHandString(Board board) {
        if (board.getInHandKomaMap().isEmpty()) {
            return " -";
        } else {
            return getNonEmptyInHandString(board);
        }

    }

    private static String getNonEmptyInHandString(Board board) {
        LinkedHashMap<Koma.Type, String> pieceMap = new LinkedHashMap<>();

        //<editor-fold defaultstate="collapsed" desc="Map initialization">
        pieceMap.put(Koma.Type.SHI, "R");
        pieceMap.put(Koma.Type.SKA, "B");
        pieceMap.put(Koma.Type.SKI, "G");
        pieceMap.put(Koma.Type.SGI, "S");
        pieceMap.put(Koma.Type.SKE, "N");
        pieceMap.put(Koma.Type.SKY, "L");
        pieceMap.put(Koma.Type.SFU, "P");
        
        pieceMap.put(Koma.Type.GHI, "r");
        pieceMap.put(Koma.Type.GKA, "b");
        pieceMap.put(Koma.Type.GKI, "g");
        pieceMap.put(Koma.Type.GGI, "s");
        pieceMap.put(Koma.Type.GKE, "n");
        pieceMap.put(Koma.Type.GKY, "l");
        pieceMap.put(Koma.Type.GFU, "p");
        //</editor-fold>
                
        StringBuilder bldResult = new StringBuilder(" ");
        
        Set<Koma.Type> keys = pieceMap.keySet();
        for(Koma.Type thisType: keys) {
            if (board.getInHandKomaMap().containsKey(thisType)) {
                int number = board.getInHandKomaMap().get(thisType);
                if (number > 1) {
                    bldResult.append(number);
                }
                bldResult.append(pieceMap.get(thisType));
            }            
        }
        return bldResult.toString();
    }
    
    private static String getSFENCode(Koma koma) {
        EnumMap<Koma.Type, String> pieceMap = new EnumMap<>(Koma.Type.class);

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
