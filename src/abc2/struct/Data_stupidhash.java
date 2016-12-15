package abc2.struct;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

/**
 * Created by brylee on 12/13/16.
 */
public class Data_stupidhash {
    public int[] col;
    public int[] row;
    public int img_index;

    public Data_stupidhash(int[] col, int[] row) {
        this.col = col;
        this.row = row;
    }
    public Data_stupidhash(int[] col, int[] row, int img_index) {
        this.col = col;
        this.row = row;
        this.img_index = img_index;
    }
}
