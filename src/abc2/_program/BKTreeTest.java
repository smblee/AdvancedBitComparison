package abc2._program;

import abc2.bktree.BkTreeSearcher;
import abc2.bktree.Metric;
import abc2.bktree.MutableBkTree;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

/**
 * Created by brylee on 12/12/16.
 */
public class BKTreeTest {
    public static void main(String[] args) {
        // The Hamming distance is a simple metric that counts the number
// of positions on which the strings (of equal length) differ.
        Metric<String> hammingDistance = new Metric<String>() {
            public int distance(String x, String y) {

                if (x.length() != y.length())
                    throw new IllegalArgumentException();

                int distance = 0;

                for (int i = 0; i < x.length(); i++)
                    if (x.charAt(i) != y.charAt(i))
                        distance++;

                return distance;
            }
        };

        MutableBkTree<String> bkTree = new MutableBkTree<>(hammingDistance);

        bkTree.addAll("11111", "11100", "10000", "10010", "00000", "01110", "01100", "00001", "00010", "10111");
//        bkTree.addAll("berets", "carrot", "egrets", "marmot", "packet", "pilots", "purist");

        BkTreeSearcher<String> searcher = new BkTreeSearcher<>(bkTree);

        Set<BkTreeSearcher.Match<? extends String>> matches = searcher.search("01110", 3);

        for (BkTreeSearcher.Match<? extends String> match : matches)
            System.out.println(String.format(
                    "%s (distance %d)",
                    match.getMatch(),
                    match.getDistance()
            ));

// Output:
//   marmot (distance 2)
//   carrot (distance 1)
    }
}
