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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpUtils {
    
    private HttpUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getLatestVersion(String httpsURL) {
        try {
            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            try ( BufferedReader in = new BufferedReader(isr)) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("x3VersStart")) {
                        return inputLine.substring(inputLine.indexOf("x3VersStart") + 11, inputLine.indexOf("x3VersEnd"));
                    }
                }
            }
        } catch (MalformedURLException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
        return null;
    }
}
