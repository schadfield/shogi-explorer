/*
    Copyright © 2021, 2022 Stephen R Chadfield.

    This file is part of Shogi Explorer.

    Shogi Explorer is free software: you can redistribute it and/or modify it under the terms of the 
    GNU General Public License as published by the Free Software Foundation, either version 3 
    of the License, or (at your option) any later version.

    Shogi Explorer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
    See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with Shogi Explorer. 
    If not, see <https://www.gnu.org/licenses/>.
 */

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
