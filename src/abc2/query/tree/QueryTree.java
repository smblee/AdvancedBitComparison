package abc2.query.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import struct.Data;
import struct.Entry;
import struct.Map2;
import struct.SComparator;
import abc2.util.Util;

public class QueryTree {
	private Map2<Integer, Data> LMS;
	private int query_size, f2_total_images;
	
	private double  bin_count, bin_width, bin_width_shadow;
	
	private HashMap<Integer, Bin> TREE = new HashMap<Integer, Bin>(); 
	
	private double S_min, S_max;
	private double[] S_range;
	
	//uses two HashMaps
	public QueryTree(Map2<Integer, Data> _LMS, 
			double[] a_range, double[] b_range, double[] S_range,
			int _f2_total_images, int _query_size){
		LMS = _LMS;	
		
		ArrayList<Map.Entry<Data, Integer>> list = new ArrayList<Map.Entry<Data, Integer>>(LMS.
				back().
				entrySet());
	
		list.sort(new Comparator<Map.Entry<Data, Integer>>(){
					@Override
					public int compare(Map.Entry<Data, Integer> o1, Map.Entry<Data, Integer> o2) {
						Data d1 = o1.getKey();
						Data d2 = o2.getKey();
						double d = d1.gof - d2.gof;
						if(d > 0)
							return 1;
						else
							return d == 0? 0 : -1;
					}
		});
		
		
		//Data oldD = list.get(0).getKey();
		Util.pl(list);
		Util.pl(list.get(77));
//		for(Map.Entry<Data, Integer> e : list){
////			if((new SComparator().compare(oldD, e.getKey())) > 0)
//				Util.pl(oldD + " " + e.getKey());
//			oldD = e.getKey();
//		}
		
		
		query_size = _query_size;
		f2_total_images = _f2_total_images;
		
		// 500 / 7
		bin_count = f2_total_images / query_size;

		S_min = S_range[0];
		S_max = S_range[1];
		bin_width = (S_max - S_min) / bin_count;
		bin_width_shadow = bin_width / 4;
		

		//Util.pl(bin_width + " : " + bin_width_shadow);
		
		/* create TREE */
		for(int i=0; i<bin_count; i++){
			TREE.put(i, new Bin());
		}		
		
		/* f2 */
		//double[] factors = {-1 * bin_width_shadow, 0, bin_width_shadow};
		double[] factors = {0};
		
		Map.Entry<Data, Integer> entry;
		for(int data_i=0; data_i<list.size(); data_i++){
			entry = list.get(data_i);
			
			Data datum = entry.getKey();

			for(double factor : factors){
				//Util.pl(datum + " going into " + INDEX(datum, factor));
				Bin n = TREE.get(INDEX(datum, factor));
				if(INDEX(datum, factor) == 42)
					Util.pl(entry + " : " + data_i + " : " + list.get(data_i));
				if(!n.contains(entry))
					n.add(entry);
			}
		}
		
		TREE.forEach((i, n) -> Util.pl(i + " : " + n));
		
		/* f1 */
				
	}
	
	public ArrayList<Integer> query(Data d){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		//Util.pl(INDEX(d) + ":" + d);
		Bin n = TREE.get(INDEX(d));
		
		//Util.pl("--->" + n.entries);
		
		n.entries().forEach(entry -> 
			ret.add((Integer) entry.getValue())
		);	
		
		return ret;
	}
	
	//uses TreeMap;
	//Imp
	
	class Bin{
		//Presumably sorted
		ArrayList<Map.Entry<Data, Integer>> entries = new ArrayList<Map.Entry<Data, Integer>>();
		
		public Bin(){}
		
		public void add(Map.Entry<Data, Integer> e){
			entries.add(e);
		}
		
		public int size(){
			return entries.size();
		}
		
		public boolean contains(Map.Entry<Data, Integer> e){
			return entries.contains(e);
		}
		
		public ArrayList<Map.Entry<Data, Integer>> entries(){
			return entries;
		}
		
		public String toString(){
			return entries.toString();
		}
	}
		
	
	private int INDEX(Data d){
		return INDEX(d, 0);
	}
	
	private int INDEX(Data d, double factor){
		if(d.gof + factor <= S_min)
			return 0;
		if(d.gof + factor >= S_max)
			return (int) bin_count -1;
		return (int) ((d.gof + factor - S_min) / bin_width);
	}


}
