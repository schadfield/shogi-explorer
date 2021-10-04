package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Analysis;
import com.chadfield.shogiexplorer.objects.AnalysisParameter;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Position;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultIntervalXYDataset;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class AnalysisManager {

    private AnalysisManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void save(Analysis analysis, File kifFile) {
        File analysisFile = getAnalysisFile(kifFile);
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("analysis", Analysis.class);
        xstream.alias("position", Position.class);
        String dataXml = xstream.toXML(analysis);
        try ( FileWriter fileWriter = new FileWriter(analysisFile, false)) {
            fileWriter.write(dataXml);
        } catch (IOException ex) {
            Logger.getLogger(AnalysisManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void load(File kifFile, Game game, JTable analysisTable, AnalysisParameter analysisParameter, XYPlot plot, JList<String> moveList) {
        File analysisFile = getAnalysisFile(kifFile);
        if (!analysisFile.exists()) {
            return;
        }
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("analysis", Analysis.class);
        xstream.alias("position", Position.class);
        Analysis analysis;
        try ( FileInputStream inputFileStream = new FileInputStream(analysisFile)) {
            analysis = (Analysis) xstream.fromXML(inputFileStream);
        } catch (IOException ex) {
            Logger.getLogger(AnalysisManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        for (int i = 0; i < analysis.getAnalysisPositionList().size(); i++) {
            DefaultTableModel analysisTableModel = (DefaultTableModel) analysisTable.getModel();
            analysisTableModel.addRow(analysis.getTableRows().get(i));
        }
        game.setAnalysisPositionList(analysis.getAnalysisPositionList());
        updateScoreChart(analysis, analysisParameter, plot, moveList);
    }

    private static void updateScoreChart(Analysis analysis, AnalysisParameter analysisParameter, XYPlot plot, JList<String> moveList) {
        int moveNum = 0;
        double[] x1Start = new double[]{};
        double[] x1 = new double[]{};
        double[] x1End = new double[]{};
        double[] y1Start = new double[]{};
        double[] y1 = new double[]{};
        double[] y1End = new double[]{};
        double[][] data1 = new double[][]{x1, x1Start, x1End, y1, y1Start, y1End};
        double[] x2Start = new double[]{};
        double[] x2 = new double[]{};
        double[] x2End = new double[]{};
        double[] y2Start = new double[]{};
        double[] y2 = new double[]{};
        double[] y2End = new double[]{};
        double[][] data2 = new double[][]{x2, x2Start, x2End, y2, y2Start, y2End};
        DefaultIntervalXYDataset plotDataset = (DefaultIntervalXYDataset) plot.getDataset();

        for (int score : analysis.getScoreList()) {
            int testRange = Math.abs(score);
            int range = 1000;
            int newRange;
            moveNum++;
            JRadioButtonMenuItem thisItem;
            if (testRange > 2000) {
                newRange = 3000;
                thisItem = analysisParameter.getGraphView3();
            } else if (testRange > 1000) {
                newRange = 2000;
                thisItem = analysisParameter.getGraphView2();
            } else {
                newRange = 1000;
                thisItem = analysisParameter.getGraphView1();
            }
            if (newRange > range) {
                range = newRange;
                thisItem.setSelected(true);
                plot.getRangeAxis().setRange(-range, range);
            }

            if (moveNum < 50) {
                plot.getDomainAxis().setRange(0, 50);
            } else if (moveNum == 50) {
                plot.getDomainAxis().setAutoRange(true);
            }
            if (moveNum % 2 == 0) {
                x1Start = arrayAppend(x1Start, moveNum - 1.0);
                x1 = arrayAppend(x1, moveNum - 0.5);
                x1End = arrayAppend(x1End, moveNum);
                y1Start = arrayAppend(y1Start, 0);
                y1 = arrayAppend(y1, score);
                y1End = arrayAppend(y1End, 0);
                data1 = new double[][]{x1, x1Start, x1End, y1, y1Start, y1End};
                plotDataset.addSeries("S", data1);
            } else {
                x2Start = arrayAppend(x2Start, moveNum - 1.0);
                x2 = arrayAppend(x2, moveNum - 0.5);
                x2End = arrayAppend(x2End, moveNum);
                y2Start = arrayAppend(y2Start, 0);
                y2 = arrayAppend(y2, score);
                y2End = arrayAppend(y2End, 0);
                data2 = new double[][]{x2, x2Start, x2End, y2, y2Start, y2End};
                plotDataset.addSeries("G", data2);
            }
            moveList.setSelectedIndex(moveNum);
        }
    }

    private static double[] arrayAppend(double[] array, double value) {
        double[] result = Arrays.copyOf(array, array.length + 1);
        result[result.length - 1] = value;
        return result;
    }

    private static File getAnalysisFile(File kifFile) {
        int dotPos = kifFile.getPath().lastIndexOf(".");
        String newPath = kifFile.getPath().substring(0, dotPos) + ".kan";
        return new File(newPath);
    }

}
