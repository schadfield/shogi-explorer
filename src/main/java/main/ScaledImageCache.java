package main;

import java.awt.image.BufferedImage;
import java.util.EnumMap;

public class ScaledImageCache {

    private final float scale;
    private EnumMap<Koma.Type, BufferedImage> imageMap = new EnumMap<>(Koma.Type.class);
    private BufferedImage ban;
    private BufferedImage masu;
    private BufferedImage masuBG;

    ScaledImageCache(float scale) {
        this.scale = scale;
    }
    
    public void putKomaImage(Koma.Type komaType, BufferedImage image) {
        getImageMap().put(komaType, image);
    }
    
    public BufferedImage getKomaImage(Koma.Type komaType) {
        if (getImageMap().containsKey(komaType)) {
            return getImageMap().get(komaType);
        } else {
            return null;
        }
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @return the imageMap
     */
    public EnumMap<Koma.Type, BufferedImage> getImageMap() {
        return imageMap;
    }

    /**
     * @param imageMap the imageMap to set
     */
    public void setImageMap(EnumMap<Koma.Type, BufferedImage> imageMap) {
        this.imageMap = imageMap;
    }

    /**
     * @return the ban
     */
    public BufferedImage getBan() {
        return ban;
    }

    /**
     * @param ban the ban to set
     */
    public void setBan(BufferedImage ban) {
        this.ban = ban;
    }

    /**
     * @return the masu
     */
    public BufferedImage getMasu() {
        return masu;
    }

    /**
     * @param masu the masu to set
     */
    public void setMasu(BufferedImage masu) {
        this.masu = masu;
    }

    /**
     * @return the masuBG
     */
    public BufferedImage getMasuBG() {
        return masuBG;
    }

    /**
     * @param masuBG the masuBG to set
     */
    public void setMasuBG(BufferedImage masuBG) {
        this.masuBG = masuBG;
    }
}
