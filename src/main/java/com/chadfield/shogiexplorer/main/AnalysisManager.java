package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Analysis;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Position;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
        try (FileWriter fileWriter = new FileWriter(analysisFile, false)) {
            fileWriter.write(dataXml);
        } catch (IOException ex) {
            Logger.getLogger(AnalysisManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void load(File kifFile, Game game, JTable analysisTable) {
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
    }
    
    private static File getAnalysisFile(File kifFile) {
        int dotPos = kifFile.getPath().lastIndexOf(".");
        String newPath = kifFile.getPath().substring(0, dotPos) + ".kan";
        return new File(newPath);        
    }
        
}
