package com.chadfield.shogiexplorer.objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Position {
    public String game;
    public Coordinate source;
    public Coordinate destination;
    public String comment;
    
    public Position(String game, Coordinate source, Coordinate destination) {
        this.game = game;
        this.source = source;
        this.destination = destination;
        this.comment = "";
    }

    /**
     * @return the game
     */
    public String getGame() {
        return game;
    }

    /**
     * @param game the game to set
     */
    public void setGame(String game) {
        this.game = game;
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
}
