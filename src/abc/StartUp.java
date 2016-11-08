package abc;

import abc.*;
import abc.algorithms.*;
import abc.testimages.ImageList;
import abc.tools.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static abc.testimages.ImageList.*;

/**
 * ABC [AdvancedBitComparison]
 * Author:  bryan
 * Date:    10/31/2016
 */
public class StartUp {
    public List<String> folder1 = new ArrayList<>();
    public List<String> folder2 = new ArrayList<>();

    public void demoStart() {
        ImageList.reloadListFiles();
        loadImageLists(); // load image paths to arraylist. NOT loading actual images to memory yet.
        BufferedImage image1 = getImage(folder1.get(0)); // get first image in folder1
        BufferedImage image2 = getImage(folder2.get(0)); // get first image in folder2
        CompareMethod cm = new DHash();
        System.out.println(cm.compare(image1, image2));
    }

    public void start() {
    }

    private void loadImageLists() {
        // load list of images into an ArrayList.
        try {
            // Dir1
            try (BufferedReader br = new BufferedReader(new FileReader(F1_IMAGE_LIST))) {
                String line;
                while ((line = br.readLine()) != null) {
                    //// TODO: make this more efficient
                    folder1.add(F1_IMAGE_DIR + "/" + line);
                }
            }
            // Dir2
            try (BufferedReader br = new BufferedReader(new FileReader(F2_IMAGE_LIST))) {
                String line;
                while ((line = br.readLine()) != null) {
                    //// TODO: make this more efficient
                    folder2.add(F2_IMAGE_DIR + "/" + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the actual image into a byte array.
     * @param path path of the image
     * @return ByteBuffer buf
     */
    private BufferedImage getImage(String path) {
        System.out.println("Getting " + path);
        try (FileInputStream fis = new FileInputStream(path);
             FileChannel channel = fis.getChannel();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            channel.transferTo(0, channel.size(), Channels.newChannel(byteArrayOutputStream));
            return ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        StartUp s = new StartUp();
        s.demoStart();
    }
}
