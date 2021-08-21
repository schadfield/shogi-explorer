package utils;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class StringUtils {
    
    public static String substituteKomaName(String name) {
        return name.replaceAll("^S", "0").replaceAll("^G", "1");
    }    
    
    public static String substituteKomaNameRotated(String name) {
        return name.replaceAll("^S", "1").replaceAll("^G", "0");
    }
    
}
