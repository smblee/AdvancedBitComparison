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
 *
 *  cols = [4, 1, 2, 4]
 *  rows = [4, 2, 3, 2]
 */
public class Histogram {
    int[] cols;
    int[] rows;
    int factor;

    public Histogram(int width, int height) {
        cols = new int[width];
        rows = new int[height];
    }

    public Histogram(int width, int height, int factor) {
        cols = new int[width];
        rows = new int[height];
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

    public String toString() {
        return String.format("cols: %s \nrows: %s", Arrays.toString(cols), Arrays.toString(rows));
    }

}
