package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.ConfigurationItem;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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

    public static void configureEngine(Engine engine, JDialog engineConfDialog, JDialog jEngineManagerDialog, JPanel jEngineConfPanel) {
        List<ConfigurationItem> configurationItemList = new ArrayList<>();

        jEngineConfPanel.removeAll();
        int count = 0;
        for (EngineOption thisOption : engine.getEngineOptionList()) {
            ConfigurationItem thisConfigurationItem = new ConfigurationItem(thisOption.getName());
            thisConfigurationItem.setType(thisOption.getType());
            System.out.println(thisOption.getName());          
                switch(thisOption.getType()) {
                    case check:
                        JCheckBox newCheckBox = new JCheckBox(thisOption.getName());
                        if (thisOption.getValue().contains("true")) {
                            newCheckBox.setSelected(true);
                        } else {
                            newCheckBox.setSelected(false);
                        }
                        jEngineConfPanel.add(newCheckBox);
                        count++;
                        jEngineConfPanel.add(new JLabel(""));
                        count++;
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
                        configurationItemList.add(thisConfigurationItem);   
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
                        configurationItemList.add(thisConfigurationItem);   
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
                        break;                    default:
                        JLabel itemNameDef = new JLabel(thisOption.getName());
                        jEngineConfPanel.add(itemNameDef);
                        count++;
                        jEngineConfPanel.add(new JLabel("DUMMY"));
                        count++;
                }   
            }
        int remainder = count % 4;
        System.out.println("rem: " + remainder + " count: " + count);
        for (int i = 0; i < remainder; i++) {
            jEngineConfPanel.add(new JLabel(""));
        }
        JButton applyButton = new JButton("Apply");
        jEngineConfPanel.add(applyButton);
        JButton cancelButton = new JButton("Cancel");
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
}
