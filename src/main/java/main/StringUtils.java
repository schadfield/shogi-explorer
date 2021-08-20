package main;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class StringUtils {
    
    public static String substituteKomaName(String name) {
        return name.replaceAll("^S", "0").replaceAll("^G", "1");
    }
    
}
