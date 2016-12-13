package abc2.query.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import abc2.struct.Data;
import abc2.struct.MaxHeap;
import abc2.util.Util;

public class KDTree<T extends Data> {
	public int dimen;
	private List<T> points;
	private Node<T> root;
	private int size;

	public KDTree(List<T> _points, int _dimen){
		dimen = _dimen;
		points = _points;

		root = new Node(points, 0);
		root.parent = null;
		
		size = points.size();
	}

	public int axis(){ return 0; }
	public int size(){ return size; }

	class Node<T extends Data>{
		public final int axis;
		public final int depth;

		public final T median;
		private Node<T> LC, RC, parent;
		public Node<T> LC(){ return LC; }
		public Node<T> RC(){ return RC; }
		public Node<T> parent(){ return parent; }
		public boolean isRoot() { return parent == null; }

		// size cascade update at every insert. if needed
		public final boolean isLeaf;
		private int size = 0;
		public int size(){ return size; }

		Node(List<T> _points, int _depth){
			Util.pl("creating node with " + _points);
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


			/* create subtree */
			if(size > 1){
				median = _points.get(median_index);
				//LC contains every point less than median.
				//RC contains every point greater than or equal to median.
				List<T> first_half	 = _points.subList(0, median_index),
						second_half  = _points.subList(median_index+1, _points.size());
				

				LC = new Node<T>(first_half, 	depth + 1);
				RC = new Node<T>(second_half,	depth + 1);

				LC.parent = this;
				RC.parent = this;
			}else if(size == 1){
				median = _points.get(0);
				
				LC = RC = new Node<T>(new ArrayList<T>(), depth + 1);
			}else{
				median = null;
				
				LC = RC = null;
			}
		}
		
	
		private int pickMedian_simply(List<T> _points, int axis){
			Comparator<T> comparator = (a, b) -> {
				return a.compareTo(b, axis);
			};

			_points.sort(comparator);

			return _points.size() / 2;
		}	
	}

	/**
	 * k small constant time
	 * 
	 * @param query_data
	 * @param node
	 * @return
	 */

	private MaxHeap<DataDistPair> best_data = new MaxHeap<DataDistPair>(
			new Comparator<DataDistPair>(){
				@Override
				public int compare(KDTree<T>.DataDistPair o1,
						KDTree<T>.DataDistPair o2) {
					if(o1.dist > o2.dist)
						return 1;
					else if(o1.dist == o2.dist)
						return 0;
					else
						return -1;
				}
			});

	/* first call */	
	public Data[] query(T query_data, int k){
		if(query_data == null){
			Util.pl("null query_data");
			return new Data[0];
		}

		Node current = root;
		while(current.size() > k){
			int axis = current.axis;
			Util.pl(current.median);
			if(query_data.compareTo(current.median, axis) <= 0){
				current = current.LC();

				Util.pl("goes left");
			}else{
				current = current.RC();
				
				Util.pl("goes right");
			}
		}

		Util.pl(k + ":" + current.size());
		/* recursively add our first ~k best_data*/
		insertTree(query_data, current.parent, k);

		while(!current.isLeaf){
			int axis = current.axis;
			if(query_data.compareTo(current.median, axis) <= 0){
				current = current.LC();
			}else{
				current = current.RC();
			}
		}

		//go back search for closest n;
		check_axis_flags = new boolean[dimen];
		Arrays.fill(check_axis_flags, true);
		remaining_flag_count = dimen;

		inverse_query(query_data, current, k, root);

		System.out.println(k);
		Data[] ret = new Data[k];
		List<DataDistPair> ddp_arr = best_data.toList();
		for(int i=0; i<k; i++){
			ret[i] = ddp_arr.get(i).data;
		}
		return ret;
	}

	private void insertTree(T query_data, Node<Data> n, int k){
		if(n.size() == 0) return;

		Util.pl(n.parent.median);
		Util.pl(n.parent.LC == n);
		Util.pl(n.parent.RC == n);
		Util.pl(n.median);
		Data data = n.median;
		double dist = n.median.distance(query_data);
		insert(data, dist, k);

		insertTree(query_data, n.LC(), k);
		insertTree(query_data, n.RC(), k);
	}

	/**
	 * query a certain branch from node.
	 * @param query_data
	 * @param node
	 * @param k
	 */
	private void query1(T query_data, Node<T> node, int k, Node<T> ROOT){
		Node<T> current = node;

		label1:
		while(!current.isLeaf){
			int axis = current.axis;
			if(query_data.compareTo(current.median, axis) <= 0){
				current = current.LC();
			}else{
				current = current.RC();	
			}
			if(current.size() == 0) break label1;
		}

		inverse_query(query_data, current, k, ROOT);
	}

	/**
	 * flags indicating a certain index(of axis) needs to be checked
	 * 
	 * init to all trues before calling query
	 */
	private boolean[] check_axis_flags;
	private int remaining_flag_count;
	private void deflag(int axis){
		check_axis_flags[axis] = false;
		remaining_flag_count--;
	}
	/**
	 * query our way back top
	 * @param query_data
	 * @param node
	 * @param k
	 */
	private void inverse_query(T query_data, Node<T> node, int k, Node<T> ROOT){	

		int parent_axis;
		Node<T> current = node;

		parent_axis = current.parent().axis;
		double parent_axis_distance = query_data.axis_distance(current.parent().median, parent_axis);

		double largest_best_data_dist = best_data.peek().dist;

		while(remaining_flag_count != 0 && current == ROOT){
			if(parent_axis_distance > largest_best_data_dist){
				deflag(parent_axis);
			}else{
				Node<T> RC = current.parent().RC(), LC = current.parent().LC();

				if(current == RC)
					query1(query_data, LC, k, LC);
				else
					query1(query_data, RC, k, LC);
			}

			current = current.parent();
		}


	}


	class DataDistPair{
		public final Data data;
		public final Double dist;
		public DataDistPair(Data _data, Double _dist){
			data = _data; dist = _dist;
		}
	}

	private void insert(Data datum, double dist, int k){
		if(best_data.size() == k){
			best_data.poll();
		}

		DataDistPair ddp = new DataDistPair(datum, dist);		
		best_data.insert(ddp);
	}

}
