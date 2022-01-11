package com.chadfield.shogiexplorer.objects;

import java.awt.image.BaseMultiResolutionImage;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {

    private Map<String, BaseMultiResolutionImage> imageMap = new HashMap<>();

    public void putImage(String identifier, BaseMultiResolutionImage image) {
        getImageMap().put(identifier, image);
    }

    public BaseMultiResolutionImage getImage(String identifier) {
        if (getImageMap().containsKey(identifier)) {
            return getImageMap().get(identifier);
        } else {
            return null;
        }
    }

    /**
     * @return the imageMap
     */
    public Map<String, BaseMultiResolutionImage> getImageMap() {
        return imageMap;
    }

    /**
     * @param imageMap the imageMap to set
     */
    public void setImageMap(Map<String, BaseMultiResolutionImage> imageMap) {
        this.imageMap = imageMap;
    }
}
