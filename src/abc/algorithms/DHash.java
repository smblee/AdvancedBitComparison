package abc.algorithms;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/** ABC [AdvancedBitComparison]
 * Author:  bryan
 * Date:    10/31/2016
 */
public class DHash implements CompareMethod {

    private static final int HASH_SIZE = 8;

    public boolean compare(BufferedImage img1, BufferedImage img2) {
        distance(dHash(img1), dHash(img2));
        return true;
    }

    /**
     * shrink image
     *
     * @param buf
     */
    public String dHash(BufferedImage buf) {
        // resize & build array
        // TODO : Combine resize and array building
        BufferedImage img = resize(buf, HASH_SIZE + 1, HASH_SIZE);
        // DEBUGGING PURPOSE. SAVE THE IMAGE AFTER RESIZE.
        try {
            BufferedImage bi = img;
            File outputfile = new File("saved.png");
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
        }

        byte[][] vals = new byte[HASH_SIZE+1][HASH_SIZE];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                vals[x][y] = getByte(img, x, y);
            }
        }
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                System.out.print(vals[x][y] + "\t");
            }
            System.out.println();
        }
        System.out.println("DONE");
        // compare adjacent pixels
        ArrayList<Byte> diff = new ArrayList<>(HASH_SIZE*HASH_SIZE);
        for (int x = 0; x < HASH_SIZE; x++) {
            for (int y = 0; y < HASH_SIZE; y++) {
               byte isDifferent = vals[x][y] < vals[x+1][y] ? (byte) 1 : 0;
                diff.add(isDifferent);
            }
        }
        // convert binary array to a hexadecimal string.
        int decimalVal = 0;
        StringBuilder hexStr = new StringBuilder();
        for (int i = 0; i < diff.size(); i++) {
            byte val = diff.get(i);
            if (val == 1)
                decimalVal += Math.pow(2, i % 8);
            if ((i % HASH_SIZE) == HASH_SIZE -1) {
                hexStr.append(String.format("%2s", Integer.toHexString(decimalVal)).replace(' ', '0'));
                decimalVal = 0;
            }
        }
            return hexStr.toString();
    }

    public int distance(String h1, String h2) {
        String diff = xorHex(h1, h2);
        int diffCnt = diff.length() - diff.replaceAll("1", "").length();
        System.out.println("Distance: " + diffCnt + " out of 64 bits");
        return diffCnt;
    }
    static String hexToBin(String s) {
        return new BigInteger(s, 16).toString(2);
    }

    public String xorHex(String a, String b) {
        System.out.println(a);
        System.out.println(b);
        char[] chars = new char[a.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = toHex(fromHex(a.charAt(i)) ^ fromHex(b.charAt(i)));
        }
        return new String(chars);
    }

    private static int fromHex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        throw new IllegalArgumentException();
    }

    private char toHex(int nybble) {
        if (nybble < 0 || nybble > 15) {
            throw new IllegalArgumentException();
        }
        return "0123456789ABCDEF".charAt(nybble);
    }

    private BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private static byte getByte(BufferedImage img, int x, int y) {
        int rgb = img.getRGB(x, y) & 0xff;
//        return (byte) rgb;
        return rgb == 0 ? (byte) 1 : 0;
    }
}
