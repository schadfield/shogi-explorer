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

public class Analysis {

    private List<List<Position>> analysisPositionList;
    private List<Object[]> tableRows;
    private List<Integer> scoreList;

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
     * @return the tableRows
     */
    public List<Object[]> getTableRows() {
        return tableRows;
    }

    /**
     * @param tableRows the tableRows to set
     */
    public void setTableRows(List<Object[]> tableRows) {
        this.tableRows = tableRows;
    }

    /**
     * @return the scoreList
     */
    public List<Integer> getScoreList() {
        return scoreList;
    }

    /**
     * @param scoreList the scoreList to set
     */
    public void setScoreList(List<Integer> scoreList) {
        this.scoreList = scoreList;
    }

}
