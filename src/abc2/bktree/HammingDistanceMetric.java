package abc2.bktree;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

/**
 * Created by brylee on 12/13/16.
 */
public class HammingDistanceMetric implements Metric {
    @Override
    public int distance(Object a, Object b) {
        String x = (String) a;
        String y = (String) b;
        if (x.length() != y.length())
            throw new IllegalArgumentException();

        int distance = 0;

        for (int i = 0; i < x.length(); i++)
            if (x.charAt(i) != y.charAt(i))
                distance++;

        return distance;
    }
}
