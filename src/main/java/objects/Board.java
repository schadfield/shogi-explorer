package objects;

import java.util.EnumMap;

public class Board {

    private Koma[][] masu = new Koma[9][9];
    private ScaledImageCache scaledImageCache = null;
    private EnumMap<Koma.Type, Integer> inHandKomaMap;
    public enum Turn { SENTE, GOTE };
    private Turn nextMove;
    private boolean isRotated;
    private int moveCount;

    public Board() {
        
        isRotated = false;

        for (int i = 0;
                i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                masu[i][j] = null;
            }
        }
    }

    /**
     * @return the masu
     */
    public Koma[][] getMasu() {
        return masu;
    }

    /**
     * @param masu the masu to set
     */
    public void setMasu(Koma[][] masu) {
        this.setMasu(masu);
    }

    /**
     * @return the scaledImageCache
     */
    public ScaledImageCache getScaledImageCache() {
        return scaledImageCache;
    }

    /**
     * @param scaledImageCache the scaledImageCache to set
     */
    public void setScaledImageCache(ScaledImageCache scaledImageCache) {
        this.scaledImageCache = scaledImageCache;
    }

    /**
     * @return the inHandKomaMap
     */
    public EnumMap<Koma.Type, Integer> getInHandKomaMap() {
        return inHandKomaMap;
    }

    /**
     * @param inHandKomaMap the inHandKomaMap to set
     */
    public void setInHandKomaMap(EnumMap<Koma.Type, Integer> inHandKomaMap) {
        this.inHandKomaMap = inHandKomaMap;
    }

    /**
     * @return the nextMove
     */
    public Turn getNextMove() {
        return nextMove;
    }

    /**
     * @param nextMove the nextMove to set
     */
    public void setNextMove(Turn nextMove) {
        this.nextMove = nextMove;
    }

    /**
     * @return the isRotated
     */
    public boolean isIsRotated() {
        return isRotated;
    }

    /**
     * @param isRotated the isRotated to set
     */
    public void setIsRotated(boolean isRotated) {
        this.isRotated = isRotated;
    }

    /**
     * @return the moveCount
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * @param moveCount the moveCount to set
     */
    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

}
