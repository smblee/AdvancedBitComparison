package abc2.test;

import abc2.bktree.BkTreeSearcher;
import abc2.bktree.HammingDistanceMetric;
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

        Metric<String> hammingDistance = (x, y) -> {

            if (x.length() != y.length())
                throw new IllegalArgumentException();

            int distance = 0;

            for (int i = 0; i < x.length(); i++)
                if (x.charAt(i) != y.charAt(i))
                    distance++;

            return distance;
        };

        MutableBkTree<String> bkTree = new MutableBkTree<>(new HammingDistanceMetric());

        bkTree.addAll("11119", "11100", "10000", "10010", "00000", "01110", "01100", "00001", "00010", "10111");
//        bkTree.addAll("berets", "carrot", "egrets", "marmot", "packet", "pilots", "purist");

        BkTreeSearcher<String> searcher = new BkTreeSearcher<>(bkTree);

        Set<BkTreeSearcher.Match<? extends String>> matches = searcher.search("01110", 2, 6);

//        List<BkTreeSearcher.Match<? extends String>> lst = asSortedList(matches);
//
//
//        for (BkTreeSearcher.Match<? extends String> match : lst)
//            System.out.println(String.format(
//                    "%s (distance %d)",
//                    match.getMatch(),
//                    match.getDistance()
//            ));

// Output:
//   marmot (distance 2)
//   carrot (distance 1)
    }



    public static
    <T extends Comparable<? super T>> List<T> asSortedCombinedList(Collection<T> c1, Collection<T> c2) {
        List<T> list1 = new ArrayList<T>(c1);
        List<T> list2 = new ArrayList<T>(c2);
        list1.retainAll(list2);
        Collections.sort(list1);
        return list1;
    }
}
