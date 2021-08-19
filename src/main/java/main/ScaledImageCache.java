package main;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ScaledImageCache {

    private final float scale;
    private HashMap<String, BufferedImage> imageMap = new HashMap<>();

    ScaledImageCache(float scale) {
        this.scale = scale;
    }
    
    public void putImage(String identifier, BufferedImage image) {
        getImageMap().put(identifier, image);
    }
    
    public BufferedImage getImage(String identifier) {
        if (getImageMap().containsKey(identifier)) {
            return getImageMap().get(identifier);
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
    public HashMap<String, BufferedImage> getImageMap() {
        return imageMap;
    }

    /**
     * @param imageMap the imageMap to set
     */
    public void setImageMap(HashMap<String, BufferedImage> imageMap) {
        this.imageMap = imageMap;
    }
}
