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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class URLUtils {

    private URLUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String readGameURL(String urlStr, boolean shiftURL) {
        String parseLine;
        StringBuilder gameStrBld = new StringBuilder();
        URL url;
        try {
            url = new URL(urlStr);
            InputStreamReader inputStreamReader;
            if (shiftURL) {
                inputStreamReader = new InputStreamReader(url.openStream(), Charset.forName("SJIS"));
            } else {
                inputStreamReader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
            }
            try ( BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                while ((parseLine = bufferedReader.readLine()) != null) {
                    gameStrBld.append(parseLine);
                    gameStrBld.append("\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(URLUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return gameStrBld.toString();
    }

}
