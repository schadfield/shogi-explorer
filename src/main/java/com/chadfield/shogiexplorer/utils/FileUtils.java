package com.chadfield.shogiexplorer.utils;

import java.io.File;
import com.chadfield.shogiexplorer.objects.Koma;
import static com.chadfield.shogiexplorer.utils.StringUtils.substituteKomaName;

public class FileUtils {
    
    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String RESOURCE_PATH = "src/main/resources/";

    public static String getImagePath(Koma.Type komaType) {      
        return RESOURCE_PATH + substituteKomaName(komaType.toString()) + ".svg";
    }

    public static File getKomaImageFile(Koma.Type komaType) {
        return new File(FileUtils.getImagePath(komaType));
    }

}
