package com.chadfield.shogiexplorer.objects;

import java.util.List;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
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
