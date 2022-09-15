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
package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import static com.chadfield.shogiexplorer.utils.StringUtils.getFileExtension;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
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

public class EngineManager {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    public static final boolean IS_WINDOWS = (OS.contains("win"));
    public static final boolean IS_MAC = (OS.contains("mac"));
    public static final boolean IS_LINUX = (OS.contains("nux"));

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
            switch (optionArray[index]) {
                case "option" ->
                    index++;
                case "name" -> {
                    engineOption.setName(optionArray[index + 1]);
                    index += 2;
                }
                case "type" -> {
                    switch (optionArray[index + 1]) {
                        case "check" ->
                            engineOption.setType(EngineOption.Type.CHECK);
                        case "spin" ->
                            engineOption.setType(EngineOption.Type.SPIN);
                        case "combo" ->
                            engineOption.setType(EngineOption.Type.COMBO);
                        case "string" ->
                            engineOption.setType(EngineOption.Type.STRING);
                        case "filename" ->
                            engineOption.setType(EngineOption.Type.FILENAME);
                        default ->
                            engineOption.setType(null);
                    }
                    index += 2;
                }
                case "default" -> {
                    if (index + 1 >= optionArray.length) {
                        engineOption.setDef("");
                        engineOption.setValue("");
                    } else {
                        engineOption.setDef(optionArray[index + 1]);
                        engineOption.setValue(optionArray[index + 1]);
                    }
                    index += 2;
                }
                case "min" -> {
                    engineOption.setMin(optionArray[index + 1]);
                    index += 2;
                }
                case "max" -> {
                    engineOption.setMax(optionArray[index + 1]);
                    index += 2;
                }
                case "var" -> {
                    List<String> varList = engineOption.getVarList();
                    if (varList == null) {
                        varList = new ArrayList<>();
                    }
                    varList.add(optionArray[index + 1]);
                    engineOption.setVarList(varList);
                    index += 2;
                }
                default ->
                    index++;
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
            ProcessBuilder processBuilder;
            if (!IS_WINDOWS && getFileExtension(engineFile.getPath()).contentEquals("exe")) {
                processBuilder = new ProcessBuilder("wine", engineFile.getPath());
            } else {
                processBuilder = new ProcessBuilder(engineFile.getPath());
            }
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
        String directoryName = appDirs.getUserDataDir("Shogi Explorer", null, null);
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("engine", Engine.class);
        xstream.alias("engineOption", EngineOption.class);
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
    }

    public static void saveEngines(List<Engine> engineList) {
        AppDirs appDirs = AppDirsFactory.getInstance();
        String directoryName = appDirs.getUserDataDir("Shogi Explorer", null, null);
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File engineFile = new File(directoryName + File.separator + "engines.xml");

        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("engine", Engine.class);
        xstream.alias("engineOption", EngineOption.class);
        String dataXml = xstream.toXML(engineList);
        try ( FileWriter fileWriter = new FileWriter(engineFile, false)) {
            fileWriter.write(dataXml);
        } catch (IOException ex) {
            Logger.getLogger(EngineManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
