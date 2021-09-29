package com.chadfield.shogiexplorer.utils;

/**
 *
 * @author Stephen Chadfield <stephen@chadfield.com>
 */
public class OSValidator {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    public static boolean IS_WINDOWS = (OS.contains("win"));
    public static boolean IS_MAC = (OS.contains("mac"));
    public static boolean IS_UNIX = (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    public static boolean IS_SOLARIS = (OS.contains("sunos"));

    public static void main(String[] args) {

        System.out.println("os.name: " + OS);

        if (IS_WINDOWS) {
            System.out.println("This is Windows");
        } else if (IS_MAC) {
            System.out.println("This is Mac");
        } else if (IS_UNIX) {
            System.out.println("This is Unix or Linux");
        } else if (IS_SOLARIS) {
            System.out.println("This is Solaris");
        } else {
            System.out.println("Your OS is not support!!");
        }
    }

}