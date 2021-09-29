package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import com.chadfield.shogiexplorer.utils.OSValidator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class EngineManager {
    
    private EngineManager() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Engine> deleteSelectedEngine(DefaultListModel<String> engineListModel, JList<String> jEngineList, List<Engine> engineList) {
        int index = jEngineList.getSelectedIndex();
        String name = engineListModel.get(index);
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
        saveEngines(newEngineList);
        return newEngineList;
    }
    
    private static EngineOption parseOption(String optionLine) {
        EngineOption engineOption = new EngineOption();
        String[] optionArray = optionLine.trim().split(" ");
        int index = 0;
        while (index < optionArray.length) {
            switch(optionArray[index]) {
                case "option":
                    index++;
                    break;
                case "name":
                   engineOption.setName(optionArray[index+1]);
                   index += 2;
                   break;
                case "type":
                    switch(optionArray[index+1]) {
                        case "check":
                            engineOption.setType(EngineOption.Type.CHECK);
                            break;
                        case "spin":
                            engineOption.setType(EngineOption.Type.SPIN);
                            break;
                        case "combo":
                            engineOption.setType(EngineOption.Type.COMBO);
                            break;
                        case "button":
                            engineOption.setType(EngineOption.Type.BUTTON);
                            break;
                        case "string":
                            engineOption.setType(EngineOption.Type.STRING);
                            break;
                        case "filename":
                            engineOption.setType(EngineOption.Type.FILENAME);
                            break;
                        default:
                            engineOption.setType(null);
                    }
                   index += 2;
                   break;
                case "default":
                   engineOption.setDef(optionArray[index+1]);
                   engineOption.setValue(optionArray[index+1]);
                   index += 2;
                   break;
                case "min":
                   engineOption.setMin(optionArray[index+1]);
                   index += 2;
                   break;
                case "max":
                   engineOption.setMax(optionArray[index+1]);
                   index += 2;
                   break;
                case "var":
                    List<String> varList = engineOption.getVarList();
                    if (varList == null) {
                        varList = new ArrayList<>();
                    }
                   varList.add(optionArray[index+1]);
                   engineOption.setVarList(varList);
                   index += 2;
                   break;
                default:
                    index +=2;
            }
        }
        return engineOption;
    }

    public static void addNewEngine(File engineFile, DefaultListModel<String> engineListModel, JList<String> jEngineList, List<Engine> engineList) {
        for (Engine thisEngine : engineList) {
            if (thisEngine.getPath().contentEquals(engineFile.getPath())) {
                return;
            }
        }

        Process process;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(engineFile.getPath());
            processBuilder.directory((new File(engineFile.getPath())).getParentFile());
            process = processBuilder.start();

        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
        Engine newEngine = new Engine("", engineFile.getPath());
        List<EngineOption> engineOptionList = new ArrayList<>();
        
        // These options are necessary.
        // We may change default later.
        engineOptionList.add(parseOption("option name USI_Ponder type check default false"));
        engineOptionList.add(parseOption("option name USI_Hash type spin default 16 min 8 max 1024"));

        try {
            stdin.write("usi\n".getBytes());
            stdin.flush();
            String line;
            EngineOption thisOption;
            try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdout))) {
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("id name")) {
                        newEngine.setName(line.substring(7).trim());
                    } else if (line.startsWith("option")) {
                        thisOption = parseOption(line);
                        checkAndAddEngineOption(engineOptionList, thisOption);
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

        newEngine.setEngineOptionList(engineOptionList);
        engineList.add(newEngine);
        engineListModel.add(engineListModel.size(), newEngine.getName());
        jEngineList.setSelectedIndex(engineListModel.size() - 1);
        saveEngines(engineList);
    }
    
    private static void checkAndAddEngineOption(List<EngineOption> engineOptionList, EngineOption engineOption) {
        for (EngineOption thisOption : engineOptionList) {
            if (thisOption.getName().contentEquals(engineOption.getName())) {
                thisOption.setDef(engineOption.getDef());
                thisOption.setMin(engineOption.getMin());
                thisOption.setMax(engineOption.getMax());
                return;
            } 
        }
        engineOptionList.add(engineOption);
    }

    public static List<Engine> loadEngines(DefaultListModel<String> engineListModel) {
        AppDirs appDirs = AppDirsFactory.getInstance();
        String directoryName = appDirs.getUserDataDir("Shogi Explorer", null, "Stephen R Chadfield");
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("engine", Engine.class);
        xstream.alias("engineOption", EngineOption.class);
        try {
            List<Engine> result;
            try ( FileInputStream inputFileStream = new FileInputStream(directoryName + File.separator + "engines.xml")) {
                result = (List<Engine>) xstream.fromXML(inputFileStream);
            } catch (IOException ex) {
                return new ArrayList<>();
            }
            int index = 0;
            for (Engine engine : result) {
                engineListModel.add(index, engine.getName());
                index++;
            }
            return result;
        } catch (StreamException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return new ArrayList<>();
    }

    public static void saveEngines(List<Engine> engineList) {
        AppDirs appDirs = AppDirsFactory.getInstance();
        String directoryName = appDirs.getUserDataDir("Shogi Explorer", null, "Stephen R Chadfield");
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File engineFile = new File(directoryName + File.separator + "engines.xml");

        XStream xstream = new XStream();
        xstream.alias("engine", Engine.class);
        xstream.alias("engineOption", EngineOption.class);
        String dataXml = xstream.toXML(engineList);
        try (FileWriter fileWriter = new FileWriter(engineFile, false)) {
            fileWriter.write(dataXml);
        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
