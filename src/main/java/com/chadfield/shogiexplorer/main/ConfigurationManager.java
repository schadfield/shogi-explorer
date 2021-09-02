package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.ConfigurationItem;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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

        jEngineConfPanel.setLayout(new java.awt.GridLayout(Math.round(engine.getEngineOptionList().size()/2.0f), 2));
        //System.out.println(Math.round(engine.getEngineOptionList().size()/2.0f)+1);
        jEngineConfPanel.removeAll();
        for (EngineOption thisOption : engine.getEngineOptionList()) {
            ConfigurationItem thisConfigurationItem = new ConfigurationItem(thisOption.getName());
            thisConfigurationItem.setType(thisOption.getType());
            System.out.println(thisOption.getName());
                switch(thisOption.getType()) {
                    case check:
                        System.out.println("checkbox");
                        JCheckBox newCheckBox = new JCheckBox(thisOption.getName());
                        newCheckBox.setSize(XDIM, YDIM);
                        jEngineConfPanel.add(newCheckBox);
                        thisConfigurationItem.setComponent(newCheckBox);
                        configurationItemList.add(thisConfigurationItem);
                        break;
                    default:
                        System.out.println("NOTcheckbox");
                        JCheckBox newCheckBoxX = new JCheckBox("Z:" + thisOption.getName());
                        newCheckBoxX.setSize(XDIM, YDIM);
                        jEngineConfPanel.add(newCheckBoxX);
                        thisConfigurationItem.setComponent(newCheckBoxX);
                        configurationItemList.add(thisConfigurationItem);                }   
            }
        
        //jEngineConfPanel.setSize(XDIM*2,y+YDIM);
        //jEngineConfPanel.setPreferredSize(jEngineConfPanel.getPreferredSize());
        //jEngineConfPanel.setBackground(Color.RED);
        //engineConfDialog.setSize(jEngineConfPanel.getSize());
        //engineConfDialog.setPreferredSize(engineConfDialog.getPreferredSize());
        //engineConfDialog.pack();
        //jEngineConfPanel.getTopLevelAncestor().validate();
        engineConfDialog.pack();
        //System.out.println(jEngineConfPanel.getSize());
        //engineConfDialog.setSize(jEngineConfPanel.getSize());


        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                engineConfDialog.setLocationRelativeTo(jEngineManagerDialog);
                engineConfDialog.setVisible(true);
            }
        });

    }
}
