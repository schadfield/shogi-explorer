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
package com.chadfield.shogiexplorer.utils;

public class StringUtils {

    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String substituteKomaName(String name) {
        return name.replaceAll("^S", "0").replaceAll("^G", "1");
    }

    public static String substituteKomaNameRotated(String name) {
        return name.replaceAll("^S", "1").replaceAll("^G", "0");
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOf + 1);
    }

}
