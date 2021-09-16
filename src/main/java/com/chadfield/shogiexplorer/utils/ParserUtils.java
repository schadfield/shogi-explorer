package com.chadfield.shogiexplorer.utils;

import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Koma;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ParserUtils {
    
    public static final String KOMA_HI = "飛";
    public static final String KOMA_KA = "角";
    public static final String KOMA_KI = "金";
    public static final String KOMA_GI = "銀";
    public static final String KOMA_KE = "桂";
    public static final String KOMA_KY = "香";
    public static final String KOMA_FU = "歩";
    
        private ParserUtils() {
            throw new IllegalStateException("Utility class");
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
            default:
                return null;
        }
    }

    public static void addPieceToInHand(Koma koma, Board board) {
        Koma invertedKoma = invertKoma(koma.getType());
        if (invertedKoma != null) {
            if (board.getInHandKomaMap().containsKey(invertedKoma.getType())) {
                board.getInHandKomaMap().put(invertedKoma.getType(), 1 + board.getInHandKomaMap().get(invertedKoma.getType()));
            } else {
                board.getInHandKomaMap().put(invertedKoma.getType(), 1);
            }
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
            default:
                return null;
        }
    }
    
    public static Koma getDropKoma(String locationString, Board.Turn turn) {
        Koma.Type komaType;
        switch (locationString) {
            case KOMA_HI:
            case "R":
                komaType = Koma.Type.SHI;
                break;
            case KOMA_KA:
            case "B":
                komaType = Koma.Type.SKA;
                break;
            case KOMA_KI:
            case "G":
                komaType = Koma.Type.SKI;
                break;
            case KOMA_GI:
            case "S":
                komaType = Koma.Type.SGI;
                break;
            case KOMA_KE:
            case "N":
                komaType = Koma.Type.SKE;
                break;
            case KOMA_KY:
            case "L":
                komaType = Koma.Type.SKY;
                break;
            case KOMA_FU:
            case "P":
                komaType = Koma.Type.SFU;
                break;
            default:
                return null;
        }
        if (turn == Board.Turn.SENTE) {
            return new Koma(komaType);
        } else {
            return invertKoma(komaType);
        }
    }
    
    public static Board.Turn switchTurn(Board.Turn turn) {
        if (turn == Board.Turn.GOTE) {
            return Board.Turn.SENTE;
        } else {
            return Board.Turn.GOTE;
        }
    }

}
