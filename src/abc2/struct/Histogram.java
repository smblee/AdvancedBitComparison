package abc2.struct;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;


/** Example:
 *  1 1 1 1
 *  1 0 0 0
 *  1 0 1 0
 *  1 1 1 1
 *  1 0 0 0
 *  cols = [4, 1, 2, 4, 1]
 *  rows = [5, 2, 3, 2]
 */
public class Histogram {
    int[] cols;
    int[] rows;
    byte[] colHash;
    byte[] rowHash;
    int factor;
    public int threshold;

    public Histogram(int width, int height) {
        cols = new int[width];
        rows = new int[height];
        colHash = new byte[width];
        rowHash = new byte[height];
    }

    public Histogram(int width, int height, int factor) {
        cols = new int[width];
        rows = new int[height];
        colHash = new byte[width];
        rowHash = new byte[height];
        this.factor = factor;
    }

    public Histogram(double[][] arr) {
        populate(arr.length, arr[0].length);
    }

    public Histogram(double[][] arr, int factor) {
        //TODO: implement
    }

    private void populate(int width, int height) {
        //TODO: Implement
    }

    public void incrementCol(int col) {
        cols[col]++;
    }

    public void incrementRow(int row) {
        rows[row]++;
    }


    public void decrementCol(int col) {
        cols[col]--;
    }

    public void decrementRow(int row) {
        rows[row]--;
    }

    public void setThreshold(int t) {
        this.threshold = t;
    }
//TODO: this is stupid. change it later.
    public String colStupidHash() {
        StringBuilder sb = new StringBuilder();
        for (int i =0; i < cols.length; i++) {
            int curr = cols[i];

            if (curr >= threshold) {
                sb.append(1);
            } else
                sb.append(0);
        }
        return sb.toString();

    }

    public String rowStupidHash() {
        StringBuilder sb = new StringBuilder();
        for (int i =0; i < rows.length; i++) {
            int curr = rows[i];
            if (curr >= threshold) {
                sb.append(1);
            } else
                sb.append(0);
        }
        return sb.toString();
    }

    public String toString() {
        return String.format("cols: %s \nrows: %s", Arrays.toString(cols), Arrays.toString(rows));
    }



}
