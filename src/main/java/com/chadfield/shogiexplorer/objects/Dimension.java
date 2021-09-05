package com.chadfield.shogiexplorer.objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Dimension {
    private Integer width;
    private Integer height;
    
    public Dimension() {
        
    }
    
    public Dimension(Integer x, Integer y) {
        this.width = x;
        this.height = y;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Integer height) {
        this.height = height;
    }
}
