package com.chadfield.shogiexplorer.objects;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ConfigurationItem {

    private String optionName;

    private JComponent component;

    private EngineOption engineOption;
    
    private JCheckBox checkBox;

    public ConfigurationItem(String optionName) {
        this.optionName = optionName;
    }

    /**
     * @return the optionName
     */
    public String getOptionName() {
        return optionName;
    }

    /**
     * @param optionName the optionName to set
     */
    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    /**
     * @return the component
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * @param component the component to set
     */
    public void setComponent(JComponent component) {
        this.component = component;
    }

    /**
     * @return the checkBox
     */
    public JCheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * @param checkBox the checkBox to set
     */
    public void setCheckBox(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }

    /**
     * @return the engineOption
     */
    public EngineOption getEngineOption() {
        return engineOption;
    }

    /**
     * @param engineOption the engineOption to set
     */
    public void setEngineOption(EngineOption engineOption) {
        this.engineOption = engineOption;
    }

}
