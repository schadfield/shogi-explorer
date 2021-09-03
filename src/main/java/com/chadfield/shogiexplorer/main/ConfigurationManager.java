package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.ConfigurationItem;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import java.awt.Dimension;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ConfigurationManager {
    
    //public final static int XDIM = 0;
    //public final static  int YDIM = 0;

    public static void configureEngine(List<Engine> engineList, Engine engine, JDialog engineConfDialog, JDialog jEngineManagerDialog, JPanel jEngineConfPanel) {
        List<ConfigurationItem> configurationItemList = new ArrayList<>();

        jEngineConfPanel.removeAll();
        int count = 0;
        for (EngineOption thisOption : engine.getEngineOptionList()) {
            ConfigurationItem thisConfigurationItem = new ConfigurationItem(thisOption.getName());
            thisConfigurationItem.setEngineOption(thisOption);
            System.out.println(thisOption.getName());          
                switch(thisOption.getType()) {
                    case check:
                        thisConfigurationItem.setCheckBox(new JCheckBox(thisOption.getName()));
                        if (thisOption.getValue().contains("true")) {
                            thisConfigurationItem.getCheckBox().setSelected(true);
                        } else {
                            thisConfigurationItem.getCheckBox().setSelected(false);
                        }
                        jEngineConfPanel.add(thisConfigurationItem.getCheckBox());
                        count++;
                        jEngineConfPanel.add(new JLabel(""));
                        count++;
                        configurationItemList.add(thisConfigurationItem);
                        break;
                    case button:
                        break;
                    case filename:
                        int remainder = count % 4;
                        for (int i = remainder; i == 4; i++) {
                            jEngineConfPanel.add(new JLabel(""));
                            count++;
                        }
                        JLabel itemName = new JLabel(thisOption.getName());
                        jEngineConfPanel.add(itemName);
                        count++;
                        JTextField newTextField = new JTextField(thisOption.getValue());
                        jEngineConfPanel.add(newTextField);
                        count++;
                        JButton chooseFileButton = new JButton("Choose File");
                        jEngineConfPanel.add(chooseFileButton);
                        count++;
                        jEngineConfPanel.add(new JLabel(""));
                        count++;
                        break;
                    case string:
                        JLabel itemNameStr = new JLabel(thisOption.getName());
                        jEngineConfPanel.add(itemNameStr);
                        count++;
                        thisConfigurationItem.setTextField(new JTextField(thisOption.getValue()));
                        jEngineConfPanel.add(thisConfigurationItem.getTextField());
                        count++;
                        configurationItemList.add(thisConfigurationItem);
                        break;
                    case combo:
                        JLabel itemNameC = new JLabel(thisOption.getName());
                        jEngineConfPanel.add(itemNameC);
                        count++;
                        JComboBox combo = new JComboBox();
                        for (String thisVar : thisOption.getVarList()) {
                            combo.addItem(thisVar);
                        }
                        combo.setSelectedItem(thisOption.getValue());
                        thisConfigurationItem.setComboBox(combo);
                        jEngineConfPanel.add(thisConfigurationItem.getComboBox());
                        count++;
                        configurationItemList.add(thisConfigurationItem);
                        break;
                    case spin:
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
                        break;
                }   
            }
        int remainder = count % 4;
        System.out.println("rem: " + remainder + " count: " + count);
        for (int i = 0; i < remainder; i++) {
            jEngineConfPanel.add(new JLabel(""));
        }
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyChanges(configurationItemList, engineList, engineConfDialog);
            }
        });
        jEngineConfPanel.add(applyButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelChanges(engineConfDialog);
            }
        });
        jEngineConfPanel.add(cancelButton);
        engineConfDialog.pack();
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                engineConfDialog.setLocationRelativeTo(jEngineManagerDialog);
                engineConfDialog.setVisible(true);
            }
        });

    }
    
    private static void applyChanges(List<ConfigurationItem> configurationItemList, List<Engine> engineList, JDialog engineConfDialog) {
        for (ConfigurationItem thisItem : configurationItemList) {
            switch(thisItem.getEngineOption().getType()) {
                case check:
                    if (thisItem.getCheckBox().isSelected()) {
                        thisItem.getEngineOption().setValue("true");
                    } else {
                        thisItem.getEngineOption().setValue("false");
                    }
                    break;
                case string:
                    thisItem.getEngineOption().setValue(thisItem.getTextField().getText());
                    break;
                case spin:
                    {
                        try {
                            thisItem.getSpinField().commitEdit();
                        } catch (ParseException ex) {
                            Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    thisItem.getEngineOption().setValue(((Long) thisItem.getSpinField().getModel().getValue()).toString());
                    break;
                case combo:
                    thisItem.getEngineOption().setValue(thisItem.getComboBox().getSelectedItem().toString());
                    break;
                default:  
            }
            EngineManager.SaveEngines(engineList);
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    engineConfDialog.setVisible(false);
                }
            });
        }
    }
    
    private static void cancelChanges(JDialog engineConfDialog) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                engineConfDialog.setVisible(false);
            }
        });
    }
    
    private static void chooseFile() {
        
    }
}
