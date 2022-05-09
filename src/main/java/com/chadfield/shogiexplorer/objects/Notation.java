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

public class Notation {

    private String engineMove;
    private String japanese;

    /**
     * @return the engineMove
     */
    public String getEngineMove() {
        return engineMove;
    }

    /**
     * @param engineMove the engineMove to set
     */
    public void setEngineMove(String engineMove) {
        this.engineMove = engineMove;
    }

    /**
     * @return the japanese
     */
    public String getJapanese() {
        return japanese;
    }

    /**
     * @param japanese the japanese to set
     */
    public void setJapanese(String japanese) {
        this.japanese = japanese;
    }

}
