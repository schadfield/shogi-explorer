package objects;

import java.util.EnumMap;

public class Board {

    public Koma[][] masu = new Koma[9][9];
    public ScaledImageCache scaledImageCache = null;
    public EnumMap<Koma.Type, Integer> inHandKomaMap = new EnumMap<>(Koma.Type.class);

    public Board() {
        
        inHandKomaMap.put(Koma.Type.GFU, 2);

        for (int i = 0;
                i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                masu[i][j] = null;
            }
        }

        masu[0][8] = new Koma(Koma.Type.SKY);
        masu[1][8] = new Koma(Koma.Type.SKE);
        masu[2][8] = new Koma(Koma.Type.SGI);
        masu[3][8] = new Koma(Koma.Type.SKI);
        masu[4][8] = new Koma(Koma.Type.SGY);
        masu[5][8] = new Koma(Koma.Type.SKI);
        masu[6][8] = new Koma(Koma.Type.SGI);
        masu[7][8] = new Koma(Koma.Type.SKE);
        masu[8][8] = new Koma(Koma.Type.SKY);
        masu[1][7] = new Koma(Koma.Type.SKA);
        masu[7][7] = new Koma(Koma.Type.SHI);
        masu[0][6] = new Koma(Koma.Type.SFU);
        masu[1][6] = new Koma(Koma.Type.SFU);
        masu[2][6] = new Koma(Koma.Type.SFU);
        masu[3][6] = new Koma(Koma.Type.SFU);
        masu[4][6] = new Koma(Koma.Type.SFU);
        masu[5][6] = new Koma(Koma.Type.SFU);
        masu[6][6] = new Koma(Koma.Type.SFU);
        masu[7][6] = new Koma(Koma.Type.SFU);
        masu[8][6] = new Koma(Koma.Type.SFU);

        masu[0][0] = new Koma(Koma.Type.GKY);
        masu[1][0] = new Koma(Koma.Type.GKE);
        masu[2][0] = new Koma(Koma.Type.GGI);
        masu[3][0] = new Koma(Koma.Type.GKI);
        masu[4][0] = new Koma(Koma.Type.GOU);
        masu[5][0] = new Koma(Koma.Type.GKI);
        masu[6][0] = new Koma(Koma.Type.GGI);
        masu[7][0] = new Koma(Koma.Type.GKE);
        masu[8][0] = new Koma(Koma.Type.GKY);
        masu[1][1] = new Koma(Koma.Type.GHI);
        masu[7][1] = new Koma(Koma.Type.GKA);
        masu[0][2] = new Koma(Koma.Type.GFU);
        masu[1][2] = new Koma(Koma.Type.GFU);
        masu[2][2] = new Koma(Koma.Type.GFU);
        masu[3][2] = new Koma(Koma.Type.GFU);
        masu[4][2] = new Koma(Koma.Type.GFU);
        masu[5][2] = new Koma(Koma.Type.GFU);
        masu[6][2] = new Koma(Koma.Type.GFU);
        masu[7][2] = new Koma(Koma.Type.GFU);
        masu[8][2] = new Koma(Koma.Type.GFU);

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
