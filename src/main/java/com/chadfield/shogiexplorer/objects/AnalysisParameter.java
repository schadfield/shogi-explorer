package com.chadfield.shogiexplorer.objects;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class AnalysisParameter {
    private int analysisTimePerMove;
    private int analysisMistakeThreshold;
    private int analysisBlunderThreshold;
    private int analysisIgnoreThreshold;

    /**
     * @return the analysisTimePerMove
     */
    public int getAnalysisTimePerMove() {
        return analysisTimePerMove;
    }

    /**
     * @param analysisTimePerMove the analysisTimePerMove to set
     */
    public void setAnalysisTimePerMove(int analysisTimePerMove) {
        this.analysisTimePerMove = analysisTimePerMove;
    }

    /**
     * @return the analysisMistakeThreshold
     */
    public int getAnalysisMistakeThreshold() {
        return analysisMistakeThreshold;
    }

    /**
     * @param analysisMistakeThreshold the analysisMistakeThreshold to set
     */
    public void setAnalysisMistakeThreshold(int analysisMistakeThreshold) {
        this.analysisMistakeThreshold = analysisMistakeThreshold;
    }

    /**
     * @return the analysisBlunderThreshold
     */
    public int getAnalysisBlunderThreshold() {
        return analysisBlunderThreshold;
    }

    /**
     * @param analysisBlunderThreshold the analysisBlunderThreshold to set
     */
    public void setAnalysisBlunderThreshold(int analysisBlunderThreshold) {
        this.analysisBlunderThreshold = analysisBlunderThreshold;
    }

    /**
     * @return the analysisIgnoreThreshold
     */
    public int getAnalysisIgnoreThreshold() {
        return analysisIgnoreThreshold;
    }

    /**
     * @param analysisIgnoreThreshold the analysisIgnoreThreshold to set
     */
    public void setAnalysisIgnoreThreshold(int analysisIgnoreThreshold) {
        this.analysisIgnoreThreshold = analysisIgnoreThreshold;
    }
}
