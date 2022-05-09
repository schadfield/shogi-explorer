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

package com.chadfield.shogiexplorer.objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;

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
