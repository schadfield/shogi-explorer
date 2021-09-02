package com.chadfield.shogiexplorer.objects;

import javax.swing.JComponent;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ConfigurationItem {

    private String optionName;

    private JComponent component;

    private EngineOption.Type type;

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
     * @return the type
     */
    public EngineOption.Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(EngineOption.Type type) {
        this.type = type;
    }

}
