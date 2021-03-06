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

import java.util.Map;

public class Board {

    public enum Turn {
        SENTE, GOTE
    }

    private Koma[][] masu = new Koma[9][9];
    private Map<Koma.Type, Integer> inHandKomaMap;
    private Turn nextTurn;
    private int moveCount;
    private Coordinate source = null;
    private Coordinate destination = null;
    private Coordinate edit = null;

    public Board() {

        for (int i = 0;
                i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                masu[i][j] = null;
            }
        }
    }

    /**
     * @return the masu
     */
    public Koma[][] getMasu() {
        return masu;
    }

    /**
     * @param masu the masu to set
     */
    public void setMasu(Koma[][] masu) {
        this.masu = masu;
    }

    /**
     * @return the inHandKomaMap
     */
    public Map<Koma.Type, Integer> getInHandKomaMap() {
        return inHandKomaMap;
    }

    /**
     * @param inHandKomaMap the inHandKomaMap to set
     */
    public void setInHandKomaMap(Map<Koma.Type, Integer> inHandKomaMap) {
        this.inHandKomaMap = inHandKomaMap;
    }

    /**
     * @return the nextTurn
     */
    public Turn getNextTurn() {
        return nextTurn;
    }

    /**
     * @param nextTurn the nextTurn to set
     */
    public void setNextTurn(Turn nextTurn) {
        this.nextTurn = nextTurn;
    }

    /**
     * @return the moveCount
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * @param moveCount the moveCount to set
     */
    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
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
     * @return the edit
     */
    public Coordinate getEdit() {
        return edit;
    }

    /**
     * @param edit the edit to set
     */
    public void setEdit(Coordinate edit) {
        this.edit = edit;
    }

}
