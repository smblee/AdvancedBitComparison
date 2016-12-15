package abc2.struct;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

/**
 * Created by brylee on 12/13/16.
 */
public class Data_stupidholder {
    public int[] hash;
    public int img_index;

    public Data_stupidholder(int index, int[] hash) {
        this.hash = hash;
        this.img_index = index;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data_stupidholder d = (Data_stupidholder) o;
        return this.img_index == d.img_index;
    }

    public int hashCode() {
        return Integer.hashCode(img_index);
    }

    public String toString() {
        return String.format("[" + img_index + "]: " + hash);
    }
}
