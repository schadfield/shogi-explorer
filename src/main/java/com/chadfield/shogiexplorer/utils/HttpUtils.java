package com.chadfield.shogiexplorer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class HttpUtils {

    public static String getLatestVersion(String httpsURL, boolean seenPaid) {
        try {
            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            try ( BufferedReader in = new BufferedReader(isr)) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (!seenPaid && inputLine.contains("x3VersStart")) {
                        return inputLine.substring(inputLine.indexOf("x3VersStart") + 11, inputLine.indexOf("x3VersEnd"));
                    }
                    if (inputLine.contains("x2VersStart")) {
                        return inputLine.substring(inputLine.indexOf("x2VersStart") + 11, inputLine.indexOf("x2VersEnd"));
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
