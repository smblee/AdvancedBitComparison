
package abc2.bktree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class BKTree<E> {

    private final Metric<? super E> metric;
    BKNode<E> root;
    public int size;

    public BKTree(Metric metric) {
        if (metric == null) throw new NullPointerException();
        this.metric = metric;
    }
    public void add(E element) {
        if (element == null) throw new NullPointerException();

        if (root == null) {
            root = new BKNode<>(element);
        } else {
            BKNode<E> node = root;
            while (!node.getElement().equals(element)) {
                int distance = distance(node.getElement(), element);

                BKNode<E> parent = node;
                node = parent.childrenByDistance.get(distance);
                if (node == null) {
                    node = new BKNode<>(element);
                    parent.childrenByDistance.put(distance, node);
                    break;
                }
            }
        }
        size++;
    }

    private int distance(E x, E y) {
        int distance = metric.distance(x, y);
        if (distance < 0) {
            throw new IllegalArgumentException();
        }
        return distance;
    }

    public void addAll(Iterable<? extends E> elements) {
        if (elements == null) throw new NullPointerException();
        for (E element : elements) {
            add(element);
        }
    }


    public final void addAll(E... elements) {
        if (elements == null) throw new NullPointerException();
        addAll(Arrays.asList(elements));
    }

    public Metric<? super E> getMetric() {
        return metric;
    }

    public BKNode<E> getRoot() {
        return root;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BKTree that = (BKTree) o;

        if (!metric.equals(that.metric)) return false;
        if (root != null ? !root.equals(that.root) : that.root != null) return false;

        return true;
    }

    public int hashCode() {
        int result = metric.hashCode();
        result = 31 * result + (root != null ? root.hashCode() : 0);
        return result;
    }

    static final class BKNode<E> {
        final E element;
        final Map<Integer, BKNode<E>> childrenByDistance = new HashMap<>();

        BKNode(E element) {
            if (element == null) throw new NullPointerException();
            this.element = element;
        }

        public E getElement() {
            return element;
        }

        public BKNode<E> getChildNode(int distance) {
            return childrenByDistance.get(distance);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BKNode that = (BKNode) o;
            if (!element.equals(that.element)) return false;

            return true;
        }

        public int hashCode() {
            int result = element.hashCode();
            return result;
        }
    }
}
