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
                    if (inputLine.contains(">Version ")) {
                        return inputLine.substring(inputLine.indexOf(">Version ") + 9, inputLine.indexOf("</h2>"));
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
