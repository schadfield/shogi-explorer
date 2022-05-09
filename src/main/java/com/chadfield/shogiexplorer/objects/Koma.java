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

public class Koma {

    public enum Type {
        SFU, SGI, SGY, SHI, SKA, SKE, SKI, SKY, SNG, SNK, SNY, SOU, SRY, STO, SUM,
        GFU, GGI, GGY, GHI, GKA, GKE, GKI, GKY, GNG, GNK, GNY, GOU, GRY, GTO, GUM
    }

    private final Type type;

    public Koma(Type komaType) {
        type = komaType;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }
}
