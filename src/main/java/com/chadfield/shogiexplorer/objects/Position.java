package com.chadfield.shogiexplorer.objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Position {
    private String gameSFEN;
    private Notation notation;
    private Coordinate source;
    private Coordinate destination;
    private String comment;
    
    public Position(String gameSFEN, Coordinate source, Coordinate destination, Notation notation) {
        this.gameSFEN = gameSFEN;
        this.notation = notation;
        this.source = source;
        this.destination = destination;
        this.comment = "";
    }

    /**
     * @return the gameSFEN
     */
    public String getGameSFEN() {
        return gameSFEN;
    }

    /**
     * @param gameSFEN the gameSFEN to set
     */
    public void setGameSFEN(String gameSFEN) {
        this.gameSFEN = gameSFEN;
    }

    /**
     * @return the source
     */
    public Coordinate getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Coordinate source) {
        this.source = source;
    }

    /**
     * @return the destination
     */
    public Coordinate getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(Coordinate destination) {
        this.destination = destination;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the notation
     */
    public Notation getNotation() {
        return notation;
    }

    /**
     * @param notation the notation to set
     */
    public void setNotation(Notation notation) {
        this.notation = notation;
    }
}
