package objects;

import java.util.LinkedList;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Game {
    private LinkedList<Position> positionList;
    private String date;
    private String place;
    private String timeLimit;
    private String sente;
    private String gote;
    
    public Game () {
        date = "";
        place = "";
        timeLimit = "";
        sente = "";
        gote = "";
    }

    /**
     * @return the positionList
     */
    public LinkedList<Position> getPositionList() {
        return positionList;
    }

    /**
     * @param positionList the positionList to set
     */
    public void setPositionList(LinkedList<Position> positionList) {
        this.positionList = positionList;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * @param place the place to set
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * @return the timeLimit
     */
    public String getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit the timeLimit to set
     */
    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * @return the sente
     */
    public String getSente() {
        return sente;
    }

    /**
     * @param sente the sente to set
     */
    public void setSente(String sente) {
        this.sente = sente;
    }

    /**
     * @return the gote
     */
    public String getGote() {
        return gote;
    }

    /**
     * @param gote the gote to set
     */
    public void setGote(String gote) {
        this.gote = gote;
    }
    
}
