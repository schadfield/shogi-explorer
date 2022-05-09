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

import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class AnalysisParameter {

    private int analysisTimePerMove;
    private JRadioButtonMenuItem graphView1;
    private JRadioButtonMenuItem graphView2;
    private JRadioButtonMenuItem graphView3;
    private JButton haltAnalysisButton;
    private JMenuItem analyseGameMenuItem;
    private JButton analyseGameToolbarButton;
    private JMenuItem analysePositionMenuItem;
    private JButton analysePositionToolbarButton;
    private JMenuItem stopAnalysisMenuItem;
    private JMenuItem resumeAnalysisMenuItem;
    private JButton resumeAnalysisToolbarButton;
    private List<List<Position>> positionAnalysisList;
    private File kifFile;
    private double[] x1Start;
    private double[] x1;
    private double[] x1End;
    private double[] y1Start;
    private double[] y1;
    private double[] y1End;
    private double[] x2Start;
    private double[] x2;
    private double[] x2End;
    private double[] y2Start;
    private double[] y2;
    private double[] y2End;


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

    /**
     * @return the x1Start
     */
    public double[] getX1Start() {
        return x1Start;
    }

    /**
     * @param x1Start the x1Start to set
     */
    public void setX1Start(double[] x1Start) {
        this.x1Start = x1Start;
    }

    /**
     * @return the x1
     */
    public double[] getX1() {
        return x1;
    }

    /**
     * @param x1 the x1 to set
     */
    public void setX1(double[] x1) {
        this.x1 = x1;
    }

    /**
     * @return the x1End
     */
    public double[] getX1End() {
        return x1End;
    }

    /**
     * @param x1End the x1End to set
     */
    public void setX1End(double[] x1End) {
        this.x1End = x1End;
    }

    /**
     * @return the y1Start
     */
    public double[] getY1Start() {
        return y1Start;
    }

    /**
     * @param y1Start the y1Start to set
     */
    public void setY1Start(double[] y1Start) {
        this.y1Start = y1Start;
    }

    /**
     * @return the y1
     */
    public double[] getY1() {
        return y1;
    }

    /**
     * @param y1 the y1 to set
     */
    public void setY1(double[] y1) {
        this.y1 = y1;
    }

    /**
     * @return the y1End
     */
    public double[] getY1End() {
        return y1End;
    }

    /**
     * @param y1End the y1End to set
     */
    public void setY1End(double[] y1End) {
        this.y1End = y1End;
    }

    /**
     * @return the x2Start
     */
    public double[] getX2Start() {
        return x2Start;
    }

    /**
     * @param x2Start the x2Start to set
     */
    public void setX2Start(double[] x2Start) {
        this.x2Start = x2Start;
    }

    /**
     * @return the x2
     */
    public double[] getX2() {
        return x2;
    }

    /**
     * @param x2 the x2 to set
     */
    public void setX2(double[] x2) {
        this.x2 = x2;
    }

    /**
     * @return the x2End
     */
    public double[] getX2End() {
        return x2End;
    }

    /**
     * @param x2End the x2End to set
     */
    public void setX2End(double[] x2End) {
        this.x2End = x2End;
    }

    /**
     * @return the y2Start
     */
    public double[] getY2Start() {
        return y2Start;
    }

    /**
     * @param y2Start the y2Start to set
     */
    public void setY2Start(double[] y2Start) {
        this.y2Start = y2Start;
    }

    /**
     * @return the y2
     */
    public double[] getY2() {
        return y2;
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(double[] y2) {
        this.y2 = y2;
    }

    /**
     * @return the y2End
     */
    public double[] getY2End() {
        return y2End;
    }

    /**
     * @param y2End the y2End to set
     */
    public void setY2End(double[] y2End) {
        this.y2End = y2End;
    }

    /**
     * @return the resumeAnalysisMenuItem
     */
    public JMenuItem getResumeAnalysisMenuItem() {
        return resumeAnalysisMenuItem;
    }

    /**
     * @param resumeAnalysisMenuItem the resumeAnalysisMenuItem to set
     */
    public void setResumeAnalysisMenuItem(JMenuItem resumeAnalysisMenuItem) {
        this.resumeAnalysisMenuItem = resumeAnalysisMenuItem;
    }

    /**
     * @return the analyseGameMenuItem
     */
    public JMenuItem getAnalyseGameMenuItem() {
        return analyseGameMenuItem;
    }

    /**
     * @param analyseGameMenuItem the analyseGameMenuItem to set
     */
    public void setAnalyseGameMenuItem(JMenuItem analyseGameMenuItem) {
        this.analyseGameMenuItem = analyseGameMenuItem;
    }

    /**
     * @return the positionAnalysisList
     */
    public List<List<Position>> getPositionAnalysisList() {
        return positionAnalysisList;
    }

    /**
     * @param positionAnalysisList the positionAnalysisList to set
     */
    public void setPositionAnalysisList(List<List<Position>> positionAnalysisList) {
        this.positionAnalysisList = positionAnalysisList;
    }

    /**
     * @return the analysePositionMenuItem
     */
    public JMenuItem getAnalysePositionMenuItem() {
        return analysePositionMenuItem;
    }

    /**
     * @param analysePositionMenuItem the analysePositionMenuItem to set
     */
    public void setAnalysePositionMenuItem(JMenuItem analysePositionMenuItem) {
        this.analysePositionMenuItem = analysePositionMenuItem;
    }

    /**
     * @return the analyseGameToolbarButton
     */
    public JButton getAnalyseGameToolbarButton() {
        return analyseGameToolbarButton;
    }

    /**
     * @param analyseGameToolbarButton the analyseGameToolbarButton to set
     */
    public void setAnalyseGameToolbarButton(JButton analyseGameToolbarButton) {
        this.analyseGameToolbarButton = analyseGameToolbarButton;
    }

    /**
     * @return the resumeAnalysisToolbarButton
     */
    public JButton getResumeAnalysisToolbarButton() {
        return resumeAnalysisToolbarButton;
    }

    /**
     * @param resumeAnalysisToolbarButton the resumeAnalysisToolbarButton to set
     */
    public void setResumeAnalysisToolbarButton(JButton resumeAnalysisToolbarButton) {
        this.resumeAnalysisToolbarButton = resumeAnalysisToolbarButton;
    }

    /**
     * @return the analysePositionToolbarButton
     */
    public JButton getAnalysePositionToolbarButton() {
        return analysePositionToolbarButton;
    }

    /**
     * @param analysePositionToolbarButton the analysePositionToolbarButton to set
     */
    public void setAnalysePositionToolbarButton(JButton analysePositionToolbarButton) {
        this.analysePositionToolbarButton = analysePositionToolbarButton;
    }
}
