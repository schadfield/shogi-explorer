package com.chadfield.shogiexplorer.utils;

import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.objects.Koma;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ParserUtils {

    private static final String KOMA_HI = "飛";
    private static final String KOMA_KA = "角";
    private static final String KOMA_KI = "金";
    private static final String KOMA_GI = "銀";
    private static final String KOMA_KE = "桂";
    private static final String KOMA_KY = "香";
    private static final String KOMA_FU = "歩";

    private ParserUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Koma promoteKoma(Koma.Type komaType) {
        return switch (komaType) {
            case SFU ->
                new Koma(Koma.Type.STO);
            case SKY ->
                new Koma(Koma.Type.SNY);
            case SKE ->
                new Koma(Koma.Type.SNK);
            case SGI ->
                new Koma(Koma.Type.SNG);
            case SKA ->
                new Koma(Koma.Type.SUM);
            case SHI ->
                new Koma(Koma.Type.SRY);
            case GFU ->
                new Koma(Koma.Type.GTO);
            case GKY ->
                new Koma(Koma.Type.GNY);
            case GKE ->
                new Koma(Koma.Type.GNK);
            case GGI ->
                new Koma(Koma.Type.GNG);
            case GKA ->
                new Koma(Koma.Type.GUM);
            case GHI ->
                new Koma(Koma.Type.GRY);
            default ->
                null;
        };
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
        return switch (komaType) {
            case SFU, STO ->
                new Koma(Koma.Type.GFU);
            case SKY, SNY ->
                new Koma(Koma.Type.GKY);
            case SKE, SNK ->
                new Koma(Koma.Type.GKE);
            case SGI, SNG ->
                new Koma(Koma.Type.GGI);
            case SKI ->
                new Koma(Koma.Type.GKI);
            case SKA, SUM ->
                new Koma(Koma.Type.GKA);
            case SHI, SRY ->
                new Koma(Koma.Type.GHI);
            case GFU, GTO ->
                new Koma(Koma.Type.SFU);
            case GKY, GNY ->
                new Koma(Koma.Type.SKY);
            case GKE, GNK ->
                new Koma(Koma.Type.SKE);
            case GGI, GNG ->
                new Koma(Koma.Type.SGI);
            case GKI ->
                new Koma(Koma.Type.SKI);
            case GKA, GUM ->
                new Koma(Koma.Type.SKA);
            case GHI, GRY ->
                new Koma(Koma.Type.SHI);
            default ->
                null;
        };
    }

    public static Koma getDropKoma(String locationString, Board.Turn turn) {
        Koma.Type komaType;
        komaType = switch (locationString) {
            case KOMA_HI, "R" ->
                Koma.Type.SHI;
            case KOMA_KA, "B" ->
                Koma.Type.SKA;
            case KOMA_KI, "G" ->
                Koma.Type.SKI;
            case KOMA_GI, "S" ->
                Koma.Type.SGI;
            case KOMA_KE, "N" ->
                Koma.Type.SKE;
            case KOMA_KY, "L" ->
                Koma.Type.SKY;
            case KOMA_FU, "P" ->
                Koma.Type.SFU;
            default ->
                null;
        };
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
