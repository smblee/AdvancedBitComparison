package abc.testimages;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ABC [AdvancedBitComparison]
 * Author:  bryan
 * Date:    10/31/2016
 */
public class ImageList {
    private ImageList() {}
    public static final String PREFIX_IMAGE_DIR = System.getProperty("user.dir") + "/src/abc/testimages";
    public static final String F1_IMAGE_DIR = PREFIX_IMAGE_DIR + "/Dir1";
    public static final String F2_IMAGE_DIR = PREFIX_IMAGE_DIR + "/Dir2";
    public static final String[] IMAGE_DIRS = {F1_IMAGE_DIR, F2_IMAGE_DIR};
    public static final String F1_IMAGE_LIST = F1_IMAGE_DIR + "/list.txt";
    public static final String F2_IMAGE_LIST = F2_IMAGE_DIR + "/list.txt";

    public static void reloadListFiles() {
        for (String dir : IMAGE_DIRS) {
            File folder = new File(dir);
            String[] listOfFiles = folder.list();
            if (listOfFiles.length == 0) {
                System.out.println("Folder is empty.");
                return;
            }
            List<String> lines = Arrays.stream(listOfFiles).filter(p -> !p.contains("list.txt")).collect(Collectors.toList());
            Path file = Paths.get(dir + "/list.txt");
            try {
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            System.out.println("RELOADED: " + dir + "\\list.txt");
        }
    }

    public static void main(String[] args) {
        ImageList.reloadListFiles();
    }

}
