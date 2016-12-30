
package abc2.bktree;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.String.format;
import abc2.bktree.BKTree.BKNode;

public final class BKTreeSearch<E> {

    private final BKTree<E> tree;

    public BKTreeSearch(BKTree<E> tree) {
        if (tree == null) throw new NullPointerException();
        this.tree = tree;
    }
    public Set<Match<? extends E>> search(E query, int maxDistance, int k) {
        //TODO: detect if k is too big
        if (query == null) throw new NullPointerException();
        if (maxDistance < 0) throw new IllegalArgumentException("maxDistance must be non-negative");

        Metric<? super E> metric = tree.getMetric();

        Set<Match<? extends E>> matches = new HashSet<>();

        Queue<BKNode<E>> queue = new ArrayDeque<>();
        queue.add(tree.getRoot());

        while (!queue.isEmpty() || matches.size() < k) {
            if (queue.isEmpty()) {
                queue.add(tree.getRoot());
                maxDistance++;
            }
            BKNode<E> node = queue.remove();
            E element = node.getElement();

            int distance = metric.distance(element, query);
            if (distance < 0)
                throw new IllegalArgumentException("Negative distance found");

            if (distance <= maxDistance)
                matches.add(new Match<>(element, distance));


            int minSearchDistance = max(distance - maxDistance, 0);
            int maxSearchDistance = distance + maxDistance;

            for (int searchDistance = minSearchDistance; searchDistance <= maxSearchDistance; ++searchDistance) {
                BKNode<E> childNode = node.getChildNode(searchDistance);
                if (childNode != null)
                    queue.add(childNode);
            }
        }

        return matches;
    }


    public BKTree<E> getTree() {
        return tree;
    }


    public static final class Match<E> implements Comparable {

        private final E match;
        private final int distance;

        public Match(E match, int distance) {
            if (match == null) throw new NullPointerException();
            if (distance < 0) throw new IllegalArgumentException("distance must be non-negative");

            this.match = match;
            this.distance = distance;
        }

        public E getMatch() {
            return match;
        }

        public int getDistance() {
            return distance;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Match that = (Match) o;

            if (!match.equals(that.match)) return false;

            return true;
        }

        public int hashCode() {
            int result = match.hashCode();
            result = 31 * result + distance;
            return result;
        }

        public int compareTo(Object o) {
            Match m = (Match) o;
            return this.distance > m.distance ? 1 : this.distance == m.distance ? 0 : -1;
        }

        public String toString() {
            return this.match.toString() + "[" + this.distance + "]";
        }
    }
}
