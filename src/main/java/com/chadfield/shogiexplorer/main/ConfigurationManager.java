package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.ConfigurationItem;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ConfigurationManager {
    
    public final static int XDIM = 400;
    public final static  int YDIM = 20;

    public static void configureEngine(Engine engine, JDialog engineConfDialog, JDialog jEngineManagerDialog, JPanel jEngineConfPanel) {
        List<ConfigurationItem> configurationItemList = new ArrayList<>();

        jEngineConfPanel.removeAll();
        for (EngineOption thisOption : engine.getEngineOptionList()) {
            ConfigurationItem thisConfigurationItem = new ConfigurationItem(thisOption.getName());
            thisConfigurationItem.setType(thisOption.getType());
            System.out.println(thisOption.getName());
                switch(thisOption.getType()) {
                    case check:
                        JCheckBox newCheckBox = new JCheckBox(thisOption.getName());
                        newCheckBox.setSize(XDIM, YDIM);
                        jEngineConfPanel.add(newCheckBox);
                        thisConfigurationItem.setComponent(newCheckBox);
                        configurationItemList.add(thisConfigurationItem);
                        break;
                    default:
                        JCheckBox newCheckBoxX = new JCheckBox("Z:" + thisOption.getName());
                        newCheckBoxX.setSize(XDIM, YDIM);
                        jEngineConfPanel.add(newCheckBoxX);
                        thisConfigurationItem.setComponent(newCheckBoxX);
                        configurationItemList.add(thisConfigurationItem);                }   
            }
        if (engine.getEngineOptionList().size()  % 2 != 0) {
            JLabel nullLabel = new JLabel("");
            nullLabel.setSize(XDIM, YDIM);
            jEngineConfPanel.add(nullLabel);
        }
        JButton applyButton = new JButton("Apply");
        applyButton.setSize(XDIM, YDIM);
        jEngineConfPanel.add(applyButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setSize(XDIM, YDIM);
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
