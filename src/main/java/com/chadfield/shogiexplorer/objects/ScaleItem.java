package com.chadfield.shogiexplorer.objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ScaleItem {

    private double scale;
    private long centerX;
    private long centerY;

    /**
     * @return the scale
     */
    public double getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * @return the centerX
     */
    public long getCenterX() {
        return centerX;
    }

    /**
     * @param centerX the centerX to set
     */
    public void setCenterX(long centerX) {
        this.centerX = centerX;
    }

    /**
     * @return the centerY
     */
    public long getCenterY() {
        return centerY;
    }

    /**
     * @param centerY the centerY to set
     */
    public void setCenterY(long centerY) {
        this.centerY = centerY;
    }

}
