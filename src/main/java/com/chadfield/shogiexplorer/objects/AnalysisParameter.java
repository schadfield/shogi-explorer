package com.chadfield.shogiexplorer.objects;

import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class AnalysisParameter {
    private int analysisTimePerMove;
    private int analysisMistakeThreshold;
    private int analysisBlunderThreshold;
    private int analysisIgnoreThreshold;
    private JRadioButtonMenuItem graphView1;
    private JRadioButtonMenuItem graphView2;
    private JRadioButtonMenuItem graphView3;

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

    /**
     * @return the graphView1
     */
    public JRadioButtonMenuItem getGraphView1() {
        return graphView1;
    }

    /**
     * @param graphView1 the graphView1 to set
     */
    public void setGraphView1(JRadioButtonMenuItem graphView1) {
        this.graphView1 = graphView1;
    }

    /**
     * @return the graphView2
     */
    public JRadioButtonMenuItem getGraphView2() {
        return graphView2;
    }

    /**
     * @param graphView2 the graphView2 to set
     */
    public void setGraphView2(JRadioButtonMenuItem graphView2) {
        this.graphView2 = graphView2;
    }

    /**
     * @return the graphView3
     */
    public JRadioButtonMenuItem getGraphView3() {
        return graphView3;
    }

    /**
     * @param graphView3 the graphView3 to set
     */
    public void setGraphView3(JRadioButtonMenuItem graphView3) {
        this.graphView3 = graphView3;
    }
}
