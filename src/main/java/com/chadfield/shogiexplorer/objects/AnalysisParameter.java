package com.chadfield.shogiexplorer.objects;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class AnalysisParameter {
    private int analysisTimePerMove;
    private JRadioButtonMenuItem graphView1;
    private JRadioButtonMenuItem graphView2;
    private JRadioButtonMenuItem graphView3;
    private JButton haltAnalysisButton;
    private JMenuItem stopAnalysisMenuItem;
    private File kifFile;

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

    /**
     * @return the haltAnalysisButton
     */
    public JButton getHaltAnalysisButton() {
        return haltAnalysisButton;
    }

    /**
     * @param haltAnalysisButton the haltAnalysisButton to set
     */
    public void setHaltAnalysisButton(JButton haltAnalysisButton) {
        this.haltAnalysisButton = haltAnalysisButton;
    }

    /**
     * @return the kifFile
     */
    public File getKifFile() {
        return kifFile;
    }

    /**
     * @param kifFile the kifFile to set
     */
    public void setKifFile(File kifFile) {
        this.kifFile = kifFile;
    }

    /**
     * @return the stopAnalysisMenuItem
     */
    public JMenuItem getStopAnalysisMenuItem() {
        return stopAnalysisMenuItem;
    }

    /**
     * @param stopAnalysisMenuItem the stopAnalysisMenuItem to set
     */
    public void setStopAnalysisMenuItem(JMenuItem stopAnalysisMenuItem) {
        this.stopAnalysisMenuItem = stopAnalysisMenuItem;
    }
}
