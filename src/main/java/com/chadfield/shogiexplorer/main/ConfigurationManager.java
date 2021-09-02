package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.ConfigurationItem;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

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
                        thisConfigurationItem.setComponent(newTextField);
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
                        JTextField newTextFieldStr = new JTextField(thisOption.getValue());
                        jEngineConfPanel.add(newTextFieldStr);
                        count++;
                        thisConfigurationItem.setComponent(newTextFieldStr);
                        break;
                    case combo:
                        JLabel itemNameC = new JLabel(thisOption.getName());
                        jEngineConfPanel.add(itemNameC);
                        count++;
                        JSpinner combo = new JSpinner();
                        jEngineConfPanel.add(combo);
                        count++;
                        break;
                    case spin:
                        JLabel itemNameS = new JLabel(thisOption.getName());
                        jEngineConfPanel.add(itemNameS);
                        count++;
                        JSpinner spin = new JSpinner();
                        jEngineConfPanel.add(spin);
                        count++;
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
                    System.out.println(thisItem.getCheckBox().isSelected() + " " + thisItem.getEngineOption().getValue());
                    if (thisItem.getCheckBox().isSelected()) {
                        thisItem.getEngineOption().setValue("true");
                    } else {
                        thisItem.getEngineOption().setValue("false");
                    }
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
}
