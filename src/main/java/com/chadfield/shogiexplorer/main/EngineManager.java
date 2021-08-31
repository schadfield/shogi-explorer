package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Engine;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class EngineManager {
    
    public static void deleteSelectedEngine(DefaultListModel engineListModel, JList<String> jEngineList, List<Engine> engineList) {
        int index = jEngineList.getSelectedIndex();
        String name = (String) engineListModel.get(index);
        engineListModel.remove(index);
        List<Engine> newEngineList = new ArrayList<>();
        for (Engine thisEngine: engineList) {
            if (!thisEngine.getName().contentEquals(name)) {
                newEngineList.add(thisEngine);
            }
        }
        engineList = newEngineList;
        int newIndex = index-1;
        if (newIndex < 0) {
            index = 0;
        }
        jEngineList.setSelectedIndex(index-1);
        SaveEngines(engineList);
    }

    public static void addNewEngine(File engineFile, DefaultListModel engineListModel, JList<String> jEngineList, List<Engine> engineList) {
        for (Engine thisEngine: engineList) {
            if (thisEngine.getPath().contentEquals(engineFile.getPath())) {
                return;
            }
        }
        Engine newEngine = new Engine(engineFile.getName(), engineFile.getPath());
        engineList.add(newEngine);
        engineListModel.add(engineListModel.size(), newEngine.getName());
        jEngineList.setSelectedIndex(engineListModel.size()-1);
        SaveEngines(engineList);
    }

    public static List<Engine> loadEngines(DefaultListModel engineListModel) {
        String directoryName = System.getProperty("user.home") + File.separator + "Library" + File.separator + "ShogiExplorer";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("engine", Engine.class);
        try {
            List<Engine> result;
            try ( FileInputStream inputFileStream = new FileInputStream(directoryName + File.separator + "engines.xml")) {
                result = (List<Engine>) xstream.fromXML(inputFileStream);
            }
            int index = 0;
            for (Engine engine : result) {
                engineListModel.add(index, engine.getName());
                index++;
            }
            return result;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (StreamException ex) {

        }
        return null;
    }

    public static void SaveEngines(List<Engine> engineList) {
        String directoryName = System.getProperty("user.home") + File.separator + "Library" + File.separator + "ShogiExplorer";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File engineFile = new File(directoryName + File.separator + "engines.xml");

        XStream xstream = new XStream();
        xstream.alias("engine", Engine.class);
        String dataXml = xstream.toXML(engineList);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(engineFile, false); // true to append
            fileWriter.write(dataXml);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(dataXml);
    }

}
