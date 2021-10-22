package com.chadfield.shogiexplorer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class URLUtils {

    private URLUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String readGameURL(String URLStr, boolean shiftURL) {
        String parseLine;
        StringBuilder gameStrBld = new StringBuilder();
        /* variable definition *//* create objects */
        URL URL;
        try {
            URL = new URL(URLStr);
            InputStreamReader inputStreamReader;
            if (shiftURL) { 
                inputStreamReader = new InputStreamReader(URL.openStream(), Charset.forName("SJIS"));
            } else {
                inputStreamReader = new InputStreamReader(URL.openStream(), Charset.forName("UTF8"));
            }
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((parseLine = bufferedReader.readLine()) != null) {
                gameStrBld.append(parseLine);
                gameStrBld.append("\n");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(URLUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(URLUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return gameStrBld.toString();
    }

}
