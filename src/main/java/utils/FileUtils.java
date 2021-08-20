package utils;

import java.io.File;
import objects.Koma;
import static utils.StringUtils.substituteKomaName;

public class FileUtils {

    public static final String RESOURCE_PATH = "src/main/resources/";

    public static String getImagePath(Koma.Type komaType) {      
        return RESOURCE_PATH + substituteKomaName(komaType.toString()) + ".svg";
    }

    public static File getKomaImageFile(Koma.Type komaType) {
        return new File(FileUtils.getImagePath(komaType));
    }

}
