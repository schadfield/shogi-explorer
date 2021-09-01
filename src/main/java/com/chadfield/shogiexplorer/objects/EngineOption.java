package com.chadfield.shogiexplorer.objects;

import java.util.List;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class EngineOption {

    private String name;

    public enum Type {
        check, spin, combo, button, string, filename
    };
    
    private Type type;

    private String def;

    private String min;

    private String max;
    
    private List<String> var;
    
    private String value;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the def
     */
    public String getDef() {
        return def;
    }

    /**
     * @param def the def to set
     */
    public void setDef(String def) {
        this.def = def;
    }

    /**
     * @return the min
     */
    public String getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public String getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * @return the var
     */
    public List<String> getVar() {
        return var;
    }

    /**
     * @param var the var to set
     */
    public void setVar(List<String> var) {
        this.var = var;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
