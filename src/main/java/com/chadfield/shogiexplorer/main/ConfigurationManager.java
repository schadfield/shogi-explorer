package com.chadfield.shogiexplorer.main;

import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.EngineOption;
import javax.swing.JDialog;
import javax.swing.JTextArea;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ConfigurationManager {

    public static void configureEngine(Engine engine, JTextArea textArea, JDialog engineConfDialog, JDialog jEngineManagerDialog) {
        textArea.setText(null);
        for (EngineOption thisOption : engine.getEngineOptionList()) {
            System.out.println(thisOption.getName());
            textArea.append(thisOption.getName() + "\n");
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                engineConfDialog.pack();
                engineConfDialog.setLocationRelativeTo(jEngineManagerDialog);
                engineConfDialog.setVisible(true);
            }
        });

    }
}
