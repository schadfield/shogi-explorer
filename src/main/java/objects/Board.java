package objects;

import java.util.EnumMap;

public class Board {

    public Koma[][] masu = new Koma[9][9];
    public ScaledImageCache scaledImageCache = null;
    public EnumMap<Koma.Type, Integer> inHandKomaMap;
    public enum Turn { SENTE, GOTE };
    public Turn nextMove;
    public boolean isRotated;

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
        this.masu = masu;
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

}
