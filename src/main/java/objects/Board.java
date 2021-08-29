package objects;

import java.util.EnumMap;

public class Board {

    public Koma[][] masu = new Koma[9][9];
    private ImageCache scaledImageCache = null;
    private EnumMap<Koma.Type, Integer> inHandKomaMap;
    public enum Turn { SENTE, GOTE };
    private Turn nextMove;
    private int moveCount;
    private Coordinate source = null;
    private Coordinate destination = null;

    public Board() {
        
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
        this.masu = masu;
    }

    /**
     * @return the scaledImageCache
     */
    public ImageCache getImageCache() {
        return scaledImageCache;
    }

    /**
     * @param scaledImageCache the scaledImageCache to set
     */
    public void setImageCache(ImageCache scaledImageCache) {
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

    /**
     * @return the source
     */
    public Coordinate getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Coordinate source) {
        this.source = source;
    }

    /**
     * @return the destination
     */
    public Coordinate getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(Coordinate destination) {
        this.destination = destination;
    }

}
