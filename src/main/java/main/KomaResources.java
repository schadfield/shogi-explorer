package main;

import java.io.File;

public class KomaResources {

    public static final String RESOURCE_PATH = "src/main/resources/";

    public static String getImagePath(Koma.Type komaType) {
      
        String komaCode = komaType.toString();
        komaCode = komaCode.replaceAll("^S", "0");
        komaCode = komaCode.replaceAll("^G", "1");
        return RESOURCE_PATH + komaCode + ".svg";
    }

    public static File getKomaImageFile(Koma.Type komaType) {
        return new File(KomaResources.getImagePath(komaType));
    }

}
