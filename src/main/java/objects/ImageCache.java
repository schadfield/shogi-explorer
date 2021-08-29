package objects;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ImageCache {

    private HashMap<String, BufferedImage> imageMap = new HashMap<>();

    public ImageCache() {
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
