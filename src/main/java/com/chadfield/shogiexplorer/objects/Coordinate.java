package com.chadfield.shogiexplorer.objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Coordinate {
    private Integer x;
    private Integer y;
    
    public Coordinate() {
        
    }
    
    public Coordinate(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x
     */
    public Integer getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public Integer getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(Integer y) {
        this.y = y;
    }
}
