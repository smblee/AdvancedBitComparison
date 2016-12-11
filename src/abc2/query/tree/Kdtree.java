package abc2.query.tree;

import java.util.Comparator;
import java.util.List;

import abc2.struct.Data;

public class Kdtree<T extends Data> {
	public int dimen;
	private List<T> points;
	private Node<T> root;
	private int size;
	
	public Kdtree(List<T> _points, int _dimen){
		dimen = _dimen;
		points = _points;
		
		root = new Node(points, 0);
		size = points.size();
	}
	
	public int axis(){ return 0; }
	public int size(){ return size; }
	
	class Node<T extends Data>{
		public final int axis;
		public final int depth;
		
		public final T median;
		private Node<? extends T> LC, RC;
		public Node<? extends T> LC(){ return LC; }
		public Node<? extends T> RC(){ return RC; }
		
		// size cascade update at every insert. if needed
		public final boolean isLeaf;
		private int size = 0;
		public int size(){ return size; }
		
		Node(List<? extends T> _points, int _depth){
			depth = _depth;
			axis = depth % dimen;
			
			size = _points.size();
			isLeaf = size == 1;

			//How to select median?

			//O(n) find?
			//O(nlogn) sort then pick pivot
			//assume random selection median is real median.
			// CASES! n small, n large different ways
			int median_index = pickMedian_simply(_points, axis);
			median = _points.get(median_index);
			
			
			/* create subtree */
			if(size > 1){
				//LC contains every point less than median.
				//RC contains every point greater than or equal to median.
				LC = new Node<T>(_points.subList(0, median_index), 			 	depth + 1);
				RC = new Node<T>(_points.subList(median_index, _points.size()), depth + 1);
			}
		}
		
		private int pickMedian_simply(List<? extends T> _points, int axis){
			Comparator<T> comparator = (a, b) -> {
				return a.compareTo(b, axis);
			};
			
			_points.sort(comparator);
			
			return _points.size() / 2;
		}	
	}
}
