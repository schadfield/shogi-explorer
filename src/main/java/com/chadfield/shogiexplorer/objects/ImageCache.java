package com.chadfield.shogiexplorer.objects;

import java.awt.Image;
import java.util.HashMap;

public class ImageCache {

    private HashMap<String, Image> imageMap = new HashMap<>();
    
    public void putImage(String identifier, Image image) {
        getImageMap().put(identifier, image);
    }
    
    public Image getImage(String identifier) {
        if (getImageMap().containsKey(identifier)) {
            return getImageMap().get(identifier);
        } else {
            return null;
        }
    }

    /**
     * @return the imageMap
     */
    public HashMap<String, Image> getImageMap() {
        return imageMap;
    }

    /**
     * @param imageMap the imageMap to set
     */
    public void setImageMap(HashMap<String, Image> imageMap) {
        this.imageMap = imageMap;
    }
}
