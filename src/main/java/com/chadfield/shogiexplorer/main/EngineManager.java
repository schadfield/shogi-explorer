package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Engine;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    public static List<Engine> deleteSelectedEngine(DefaultListModel engineListModel, JList<String> jEngineList, List<Engine> engineList) {
        int index = jEngineList.getSelectedIndex();
        String name = (String) engineListModel.get(index);
        engineListModel.remove(index);
        List<Engine> newEngineList = new ArrayList<>();
        for (Engine thisEngine : engineList) {
            if (!thisEngine.getName().contentEquals(name)) {
                newEngineList.add(thisEngine);
            }
        }
        int newIndex = index - 1;
        if (newIndex < 0) {
            index = 0;
        }
        jEngineList.setSelectedIndex(index - 1);
        SaveEngines(newEngineList);
        return newEngineList;
    }
    
    public static Engine addOption(Engine engine, String optionLine) {
        return engine;
    }

    public static void addNewEngine(File engineFile, DefaultListModel engineListModel, JList<String> jEngineList, List<Engine> engineList) {
        for (Engine thisEngine : engineList) {
            if (thisEngine.getPath().contentEquals(engineFile.getPath())) {
                return;
            }
        }

        Process process;

        try {
            process = new ProcessBuilder(engineFile.getPath()).start();
        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
        String engineName = "";

        try {
            stdin.write("usi\n".getBytes());
            stdin.flush();
            String line;
            try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdout))) {
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("id name")) {
                        engineName = line.substring(7);
                    } if (line.startsWith("option")) {
                        System.out.println(line);
                    } else {
                        if (line.contains("usiok")) {
                            stdin.write("quit\n".getBytes());
                            stdin.flush();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        Engine newEngine = new Engine(engineName, engineFile.getPath());
        engineList.add(newEngine);
        engineListModel.add(engineListModel.size(), newEngine.getName());
        jEngineList.setSelectedIndex(engineListModel.size() - 1);
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
