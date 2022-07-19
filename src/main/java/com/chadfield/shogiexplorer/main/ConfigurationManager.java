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

import com.chadfield.shogiexplorer.objects.ConfigurationItem;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import java.awt.Dimension;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class ConfigurationManager {

    private ConfigurationManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void configureEngine(List<Engine> engineList, Engine engine, JDialog engineConfDialog, JDialog jEngineManagerDialog, JPanel jEngineConfPanel) {
        List<ConfigurationItem> configurationItemList = new ArrayList<>();

        jEngineConfPanel.removeAll();
        int count = 0;
        for (EngineOption thisOption : engine.getEngineOptionList()) {
            ConfigurationItem thisConfigurationItem = new ConfigurationItem(thisOption.getName());
            thisConfigurationItem.setEngineOption(thisOption);
            switch (thisOption.getType()) {
                case CHECK -> {
                    thisConfigurationItem.setCheckBox(new JCheckBox(thisOption.getName()));
                    thisConfigurationItem.getCheckBox().setSelected(thisOption.getValue().contains("true"));
                    jEngineConfPanel.add(thisConfigurationItem.getCheckBox());
                    count++;
                    jEngineConfPanel.add(new JLabel(""));
                    count++;
                    configurationItemList.add(thisConfigurationItem);
                }
                case FILENAME -> {
                    int remainder = count % 4;
                    for (int i = remainder; i == 4; i++) {
                        jEngineConfPanel.add(new JLabel(""));
                        count++;
                    }
                    JLabel itemName = new JLabel(thisOption.getName());
                    jEngineConfPanel.add(itemName);
                    count++;
                    JTextField newTextField = new JTextField();
                    Dimension sizeTF = newTextField.getPreferredSize();
                    newTextField.setText(thisOption.getValue());
                    newTextField.setPreferredSize(sizeTF);
                    jEngineConfPanel.add(newTextField);
                    thisConfigurationItem.setTextField(newTextField);
                    count++;
                    JButton chooseFileButton = new JButton("Choose File");
                    chooseFileButton.addActionListener((java.awt.event.ActionEvent evt)
                            -> chooseFile(engineConfDialog, new File(engine.getPath()), thisConfigurationItem.getTextField())
                    );
                    jEngineConfPanel.add(chooseFileButton);
                    count++;
                    jEngineConfPanel.add(new JLabel(""));
                    count++;
                    configurationItemList.add(thisConfigurationItem);
                }
                case STRING -> {
                    JLabel itemNameStr = new JLabel(thisOption.getName());
                    jEngineConfPanel.add(itemNameStr);
                    count++;
                    thisConfigurationItem.setTextField(new JTextField(thisOption.getValue()));
                    jEngineConfPanel.add(thisConfigurationItem.getTextField());
                    count++;
                    configurationItemList.add(thisConfigurationItem);
                }
                case COMBO -> {
                    JLabel itemNameC = new JLabel(thisOption.getName());
                    jEngineConfPanel.add(itemNameC);
                    count++;
                    JComboBox<String> combo = new JComboBox<>();
                    for (String thisVar : thisOption.getVarList()) {
                        combo.addItem(thisVar);
                    }
                    combo.setSelectedItem(thisOption.getValue());
                    thisConfigurationItem.setComboBox(combo);
                    jEngineConfPanel.add(thisConfigurationItem.getComboBox());
                    count++;
                    configurationItemList.add(thisConfigurationItem);
                }
                case SPIN -> {
                    JLabel itemNameS = new JLabel(thisOption.getName());
                    jEngineConfPanel.add(itemNameS);
                    count++;
                    JSpinner thisSpinner = new JSpinner();
                    Dimension size = thisSpinner.getPreferredSize();
                    thisSpinner.setModel(new SpinnerNumberModel(
                            Long.valueOf(thisOption.getValue()),
                            Long.valueOf(thisOption.getMin()),
                            Long.valueOf(thisOption.getMax()),
                            Long.valueOf("1")
                    ));
                    thisSpinner.setPreferredSize(size);
                    thisConfigurationItem.setSpinField(thisSpinner);
                    jEngineConfPanel.add(thisConfigurationItem.getSpinField());
                    count++;
                    configurationItemList.add(thisConfigurationItem);
                }
            }
        }
        int remainder = count % 4;
        for (int i = 0; i < remainder; i++) {
            jEngineConfPanel.add(new JLabel(""));
        }
        jEngineConfPanel.add(new JLabel(""));
        jEngineConfPanel.add(new JLabel(""));
        ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
        JButton applyButton = new JButton(bundle.getString("ShogiExplorer.prefsSaveButton.text"));
        applyButton.addActionListener((java.awt.event.ActionEvent evt)
                -> applyChanges(configurationItemList, engineList, engineConfDialog));
        jEngineConfPanel.add(applyButton);
        JButton cancelButton = new JButton(bundle.getString("ShogiExplorer.prefsCancelButton.text"));
        cancelButton.addActionListener((java.awt.event.ActionEvent evt)
                -> cancelChanges(engineConfDialog));
        jEngineConfPanel.add(cancelButton);
        engineConfDialog.pack();
        java.awt.EventQueue.invokeLater(() -> {
            engineConfDialog.setLocationRelativeTo(jEngineManagerDialog);
            engineConfDialog.setVisible(true);
        });

    }

    private static void applyChanges(List<ConfigurationItem> configurationItemList, List<Engine> engineList, JDialog engineConfDialog) {
        for (ConfigurationItem thisItem : configurationItemList) {
            switch (thisItem.getEngineOption().getType()) {
                case CHECK -> {
                    if (thisItem.getCheckBox().isSelected()) {
                        thisItem.getEngineOption().setValue("true");
                    } else {
                        thisItem.getEngineOption().setValue("false");
                    }
                }
                case STRING ->
                    thisItem.getEngineOption().setValue(thisItem.getTextField().getText());
                case SPIN -> {
                    try {
                        thisItem.getSpinField().commitEdit();
                    } catch (ParseException ex) {
                        Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    thisItem.getEngineOption().setValue(((Long) thisItem.getSpinField().getModel().getValue()).toString());
                }
                case COMBO ->
                    thisItem.getEngineOption().setValue(thisItem.getComboBox().getSelectedItem().toString());
                case FILENAME ->
                    thisItem.getEngineOption().setValue(thisItem.getTextField().getText());
            }
            EngineManager.saveEngines(engineList);
            java.awt.EventQueue.invokeLater(()
                    -> engineConfDialog.setVisible(false));
        }
    }

    private static void cancelChanges(JDialog engineConfDialog) {
        java.awt.EventQueue.invokeLater(()
                -> engineConfDialog.setVisible(false));
    }

    private static void chooseFile(JDialog dialog, File startDir, JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(startDir);
        java.awt.EventQueue.invokeLater(() -> {
            chooser.showOpenDialog(dialog);
            File thisFile = chooser.getSelectedFile();
            textField.setText(thisFile.getAbsoluteFile().toString());
        });
    }
}
