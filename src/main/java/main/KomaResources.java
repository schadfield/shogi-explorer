package main;

import java.io.File;
import static main.StringUtils.substituteKomaName;

public class KomaResources {

    public static final String RESOURCE_PATH = "src/main/resources/";

    public static String getImagePath(Koma.Type komaType) {      
        return RESOURCE_PATH + substituteKomaName(komaType.toString()) + ".svg";
    }

    public static File getKomaImageFile(Koma.Type komaType) {
        return new File(KomaResources.getImagePath(komaType));
    }

}
