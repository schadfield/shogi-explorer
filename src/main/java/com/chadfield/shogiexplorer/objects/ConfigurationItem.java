package com.chadfield.shogiexplorer.objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class ConfigurationItem {

    private String optionName;

    private EngineOption engineOption;
    
    private JCheckBox checkBox;
    
    private JTextField textField;
    
    private JSpinner spinField;
    
    private JComboBox<String> comboBox;

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

    /**
     * @return the textField
     */
    public JTextField getTextField() {
        return textField;
    }

    /**
     * @param textField the textField to set
     */
    public void setTextField(JTextField textField) {
        this.textField = textField;
    }

    /**
     * @return the spinField
     */
    public JSpinner getSpinField() {
        return spinField;
    }

    /**
     * @param spinField the spinField to set
     */
    public void setSpinField(JSpinner spinField) {
        this.spinField = spinField;
    }

    /**
     * @return the comboBox
     */
    public JComboBox<String> getComboBox() {
        return comboBox;
    }

    /**
     * @param comboBox the comboBox to set
     */
    public void setComboBox(JComboBox<String> comboBox) {
        this.comboBox = comboBox;
    }

}
