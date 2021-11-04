package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Coordinate;
import com.chadfield.shogiexplorer.objects.Koma;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class PositionEditor {

    private PositionEditor() {
        throw new IllegalStateException("Utility class");
    }

    public static void processLeft(Board board) {
        if (board.getEdit() != null) {
            int x = board.getEdit().getX();
            int y = board.getEdit().getY();
            if (x < 9) {
                board.setEdit(new Coordinate(++x, y));
            } else {
                if (y > 1) {
                    board.setEdit(new Coordinate(1, --y));
                } else {
                    board.setEdit(null);
                    board.setEditBan(Board.Turn.GOTE);
                }
            }
        } else {
            if (board.getEditBan() == Board.Turn.GOTE) {
                board.setEditBan(Board.Turn.SENTE);
            } else {
                board.setEditBan(null);
                board.setEdit(new Coordinate(1, 9));
            }
        }
    }

    public static void processRight(Board board) {
        if (board.getEdit() != null) {
            int x = board.getEdit().getX();
            int y = board.getEdit().getY();
            if (x > 1) {
                board.setEdit(new Coordinate(--x, y));
            } else {
                if (y < 9) {
                    board.setEdit(new Coordinate(9, ++y));
                } else {
                    board.setEdit(null);
                    board.setEditBan(Board.Turn.SENTE);
                }
            }
        } else {
            if (board.getEditBan() == Board.Turn.SENTE) {
                board.setEditBan(Board.Turn.GOTE);
            } else {
                board.setEditBan(null);
                board.setEdit(new Coordinate(9, 1));
            }
        }
    }

    public static void processUp(Board board) {
        if (board.getEdit() != null) {
            int x = board.getEdit().getX();
            int y = board.getEdit().getY();
            if (y > 1) {
                board.setEdit(new Coordinate(x, --y));
            }
        }
    }

    public static void processDown(Board board) {
        if (board.getEdit() != null) {
            int x = board.getEdit().getX();
            int y = board.getEdit().getY();
            if (y < 9) {
                board.setEdit(new Coordinate(x, ++y));
            }
        }
    }

    public static boolean processKey(char thisChar, Board board, boolean modified, int komadaiCount) {
        if (board.getEdit() != null) {
            Koma koma = getKoma(thisChar, modified);
            if (koma != null) {
                KifParser.putKoma(board, board.getEdit(), koma);
                return true;
            }
        } else {
            Board.Turn thisTurn = board.getEditBan();
            if (thisTurn != null) {
                Koma koma;
                if (thisTurn == Board.Turn.SENTE) {
                    koma = getKoma(Character.toLowerCase(thisChar), false);
                } else {
                    koma = getKoma(Character.toUpperCase(thisChar), false);
                }
                updateInHand(board, koma.getType(), komadaiCount);
                return true;
            }
        }
        return false;
    }
    
    private static void updateInHand(Board board, Koma.Type komaType, int komadaiCount) {
        if (komadaiCount == -1) {
            if (board.getInHandKomaMap().containsKey(komaType)) {
                board.getInHandKomaMap().put(komaType, board.getInHandKomaMap().get(komaType) + 1); 
            } else {
                board.getInHandKomaMap().put(komaType, 1);
            }
        } else {
            if (komadaiCount == 0) {
                board.getInHandKomaMap().remove(komaType);
            } else {
                board.getInHandKomaMap().put(komaType, komadaiCount);
            }
        }
    }

    private static Koma getKoma(char thisChar, boolean modified) {
        switch (thisChar) {
            case 'p':
                if (modified) {
                    return new Koma(Koma.Type.STO);
                } else {
                    return new Koma(Koma.Type.SFU);
                }
            case 'l':
                if (modified) {
                    return new Koma(Koma.Type.SNY);
                } else {
                    return new Koma(Koma.Type.SKY);
                }
            case 'n':
                if (modified) {
                    return new Koma(Koma.Type.SNK);
                } else {
                    return new Koma(Koma.Type.SKE);
                }
            case 's':
                if (modified) {
                    return new Koma(Koma.Type.SNG);
                } else {
                    return new Koma(Koma.Type.SGI);
                }
            case 'g':
                if (modified) {
                    return new Koma(Koma.Type.SKI);
                } else {
                    return new Koma(Koma.Type.SKI);
                }
            case 'b':
                if (modified) {
                    return new Koma(Koma.Type.SUM);
                } else {
                    return new Koma(Koma.Type.SKA);
                }
            case 'r':
                if (modified) {
                    return new Koma(Koma.Type.SRY);
                } else {
                    return new Koma(Koma.Type.SHI);
                }
            case 'k':
                if (modified) {
                    return new Koma(Koma.Type.SOU);
                } else {
                    return new Koma(Koma.Type.SGY);
                }
            case 'P':
                if (modified) {
                    return new Koma(Koma.Type.GTO);
                } else {
                    return new Koma(Koma.Type.GFU);
                }
            case 'L':
                if (modified) {
                    return new Koma(Koma.Type.GNY);
                } else {
                    return new Koma(Koma.Type.GKY);
                }
            case 'N':
                if (modified) {
                    return new Koma(Koma.Type.GNK);
                } else {
                    return new Koma(Koma.Type.GKE);
                }
            case 'S':
                if (modified) {
                    return new Koma(Koma.Type.GNG);
                } else {
                    return new Koma(Koma.Type.GGI);
                }
            case 'G':
                if (modified) {
                    return new Koma(Koma.Type.GKI);
                } else {
                    return new Koma(Koma.Type.GKI);
                }
            case 'B':
                if (modified) {
                    return new Koma(Koma.Type.GUM);
                } else {
                    return new Koma(Koma.Type.GKA);
                }
            case 'R':
                if (modified) {
                    return new Koma(Koma.Type.GRY);
                } else {
                    return new Koma(Koma.Type.GHI);
                }
            case 'K':
                if (modified) {
                    return new Koma(Koma.Type.GGY);
                } else {
                    return new Koma(Koma.Type.GOU);
                }
            default:
                return null;
        }
    }

}
