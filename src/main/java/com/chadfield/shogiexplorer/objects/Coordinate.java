package com.chadfield.shogiexplorer.objects;

import java.util.Objects;

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
    
    public boolean equals(Coordinate otherCoordinate) {
        return (Objects.equals(this.x, otherCoordinate.getX())) && (Objects.equals(this.y, otherCoordinate.getY()));
    }
}
