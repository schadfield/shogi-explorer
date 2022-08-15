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

import java.util.List;

public class Game {

    private List<Position> positionList;
    private List<List<Position>> analysisPositionList;
    private String date;
    private String place;
    private String timeLimit;
    private String sente;
    private String gote;
    private String handicap;
    private String tournament;
    public static final String HANDICAP_NONE = "平手";
    public static final String HANDICAP_LANCE = "香落ち";
    public static final String HANDICAP_BISHOP = "角落ち";
    public static final String HANDICAP_ROOK = "飛車落ち";
    public static final String HANDICAP_ROOK_LANCE = "飛香落ち";
    public static final String HANDICAP_2_PIECE = "二枚落ち";
    public static final String HANDICAP_4_PIECE = "四枚落ち";
    public static final String HANDICAP_6_PIECE = "六枚落ち";
    public static final String HANDICAP_8_PIECE = "八枚落ち";

    public Game() {
        date = "";
        place = "";
        timeLimit = "";
        tournament = "";
        sente = "";
        gote = "";
        handicap = "";
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
        return switch (this.handicap) {
            case HANDICAP_2_PIECE, HANDICAP_4_PIECE, HANDICAP_6_PIECE, HANDICAP_8_PIECE, HANDICAP_BISHOP, HANDICAP_LANCE, HANDICAP_ROOK, HANDICAP_ROOK_LANCE ->
                true;
            default ->
                false;
        };
    }

    /**
     * @return the tournament
     */
    public String getTournament() {
        return tournament;
    }

    /**
     * @param tournament the tournament to set
     */
    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

}
