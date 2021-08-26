package objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Position {
    public String game;
    public Coordinate source;
    public Coordinate destination;
    
    public Position(String game, Coordinate source, Coordinate destination) {
        this.game = game;
        this.source = source;
        this.destination = destination;
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
}
