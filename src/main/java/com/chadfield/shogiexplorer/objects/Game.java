package com.chadfield.shogiexplorer.objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Game {

    private List<Position> positionList;
    private List<List<Position>> analysisPositionList;
    private String date;
    private String place;
    private String timeLimit;
    private String sente;
    private String gote;
    private String handicap;
    public static final String HANDICAP_NONE = "平手";
    public static final String HANDICAP_LANCE = "香落ち";
    public static final String HANDICAP_BISHOP = "角落ち";
    public static final String HANDICAP_ROOK = "飛車落ち";
    public static final String HANDICAP_ROOK_LANCE = "飛香落ち";
    public static final String HANDICAP_2_PIECE = "二枚落ち";
    public static final String HANDICAP_4_PIECE = "四枚落ち";
    public static final String HANDICAP_6_PIECE = "六枚落ち";

    public Game() {
        date = "";
        place = "";
        timeLimit = "";
        sente = "";
        gote = "";
        handicap = "";
        analysisPositionList = new ArrayList<>();
    }

    /**
     * @return the positionList
     */
    public List<Position> getPositionList() {
        return positionList;
    }

    /**
     * @param positionList the positionList to set
     */
    public void setPositionList(List<Position> positionList) {
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

    /**
     * @return the analysisPositionList
     */
    public List<List<Position>> getAnalysisPositionList() {
        return analysisPositionList;
    }

    /**
     * @param analysisPositionList the analysisPositionList to set
     */
    public void setAnalysisPositionList(List<List<Position>> analysisPositionList) {
        this.analysisPositionList = analysisPositionList;
    }

    /**
     * @return the handicap
     */
    public String getHandicap() {
        return handicap;
    }

    /**
     * @param handicap the handicap to set
     */
    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }
    
    public boolean isHandicap() {
        return !this.handicap.contentEquals(HANDICAP_NONE);
    }

}
