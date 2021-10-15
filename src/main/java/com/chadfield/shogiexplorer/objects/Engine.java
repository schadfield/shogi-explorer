package com.chadfield.shogiexplorer.objects;

import java.util.List;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class Engine {

    private String name;
    private String path;
    private List<EngineOption> engineOptionList;

    public Engine(String name, String path) {
        this.name = name;
        this.path = path;
    }

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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the engineOptionList
     */
    public List<EngineOption> getEngineOptionList() {
        return engineOptionList;
    }

    /**
     * @param engineOptionList the engineOptionList to set
     */
    public void setEngineOptionList(List<EngineOption> engineOptionList) {
        this.engineOptionList = engineOptionList;
    }

}
