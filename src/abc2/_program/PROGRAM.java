package abc2._program;

import abc2.bktree.*;
import abc2.imageprocess.corner.Harris_Stephens;
import abc2.imageprocess.corner.filter.CornerFilter;
import abc2.imageprocess.corner.filter.ImageDerivative;
import abc2.imageprocess.corner.filter.Prewitt;
import abc2.imageprocess.corner.filter.Sobel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import abc2.query.tree.KDTree;
import abc2.query.tree.QueryTree;
import abc2.struct.*;
import abc2.util.MathTools;
import abc2.util.Util;
import abc2.util.fn;

import static abc2._program.BKTreeTest.asSortedList;

public class PROGRAM {
	private static int tools_count = 2;
	
	private static FileReader fr; 
	private static BufferedReader br;

	private static int row_l, col_l;
	
	/* for lookup */
	private static DLMap<String, Integer> file_map;
	//list of
	// map
	private static ArrayList<DLMap<Integer, SimpleData>> listof_data_map_folder1, listof_data_map_folder2;
	/* for kdtree forest */
	// list of 
	// list of various tool generated Different data sets(lists).
	private static ArrayList<ArrayList<ArrayList<Data>>> listof_data_lists_folder1, listof_data_lists_folder2;
	private static ArrayList<ArrayList<KDTree>> Forest;

	private static MutableBkTree bktree_col;
	private static MutableBkTree bktree_row;
	private static Map<Integer, Data_stupidhash> stupidhashmap;



	private static String folder1, folder2, outputfolder;
	private static int query_size;
	private static File f1, f2, outf;
	private static String[] f1_list, f2_list;
	private static double[] a_min, a_max, b_min, b_max, S_min, S_max;
	
	private static CornerFilter corner_filter = Sobel.instance(); 

	public static void main(String[] args){
		long start = System.currentTimeMillis();
	
		if(args.length < 4) {
			Util.pf("bad args count: %d; REQUIRED 4. Usage: ./program ./database ./query ./output 10 \n", args.length);
			System.exit(1);
		}

		folder1 = args[0];
		folder2 = args[1];
		outputfolder = args[2];
		query_size = Integer.valueOf(args[3]);
		
		initStructures();
		
		indexImages();
		
		/* process the images and create tables */

		long s2 = System.currentTimeMillis();								//
		Util.pl("preparation: " + (s2 - start) + " ms");					//

		for(String filename : f1_list){
			processImage(folder1 + "/" + filename, file_map.getValue(filename), false);
		}
		for(String filename : f2_list){
			processImage(folder2 + "/" + filename, file_map.getValue(filename), true);
		}
		
		long s3 = System.currentTimeMillis();								//
		Util.pl("processImage: " + (s3 - s2) + " ms");						//
		Util.pl("overall: " + (s3 - start) + " ms");						//
		Util.pl("totalImages: " + (f1_list.length + f2_list.length));						//


		/* query tree */
		
		for(int i=0; i<tools_count; i++){
			ArrayList<KDTree> forest_partition = Forest.get(i);
			ArrayList<ArrayList<Data>> curr_data_list_list =  listof_data_lists_folder2.get(i);
			
			int tree_count = curr_data_list_list.size();
			for(int j=0; j<tree_count; j++){
				ArrayList<Data> input_to_tree = curr_data_list_list.get(j);
				KDTree KDTREE = new KDTree(input_to_tree, input_to_tree.get(0).axis_num());
				forest_partition.add(KDTREE);
			}
		}
		
		//TreeMap from overlap counts to img_index
		for(String f1_imgname: f1_list){
			int f1_img_index = file_map.getValue(f1_imgname);
			

			// index to count
			HashMap<Integer, Integer> table = new HashMap<Integer, Integer>();
			for(int i=0; i<tools_count; i++){
				DLMap<Integer, SimpleData> folder1_data_map = listof_data_map_folder1.get(i);
				
				Data query_data;
				
				//doesn't matter here
				
				
				ArrayList<KDTree> forest_partition = Forest.get(i);
				
				Data[] ret;
				for(int j=0; j<forest_partition.size(); j++){
					KDTree tree = forest_partition.get(j);
					
					switch(j){
						case 0:
							query_data = new Data_a_b(folder1_data_map.getValue(f1_img_index));
							break;
						case 1:
							query_data = new Data_gof(folder1_data_map.getValue(f1_img_index));
							break;
						default:
							query_data = null;
					}
					
					ret = tree.query(query_data, query_size);
					
					for(Data datum : ret){
						SimpleData sd;
						if(datum instanceof Data_a_b)
							sd = ((Data_a_b) datum).sd;
						else
							sd = ((Data_gof) datum).sd;

						DLMap<Integer, SimpleData> tool_i_data_map_folder2 = listof_data_map_folder2.get(i);
						int index = tool_i_data_map_folder2.getKey(sd);
								
						if(table.containsKey(index)){
							table.put(index, table.get(index) + 1);
						}else{
							table.put(index, 1);
						}

					}
				}
			}

			System.out.println("********* STUPID HASH ********");
			Data_stupidhash hash_data =  stupidhashmap.get(f1_img_index);
			BkTreeSearcher<Data_stupidholder> colsearcher = new BkTreeSearcher<>(bktree_col);
			BkTreeSearcher<Data_stupidholder> rowsearcher = new BkTreeSearcher<>(bktree_row);

			System.out.println("Stupid hashing with image file " + f1_imgname);
			//TODO: verify row and col
			System.out.println("Query col with "  + hash_data.col);
			System.out.println("Query row with " + hash_data.row);
			Set<BkTreeSearcher.Match<? extends Data_stupidholder>> colmatches = colsearcher.search(new Data_stupidholder(f1_img_index, hash_data.col), (int) 2,(int) Math.sqrt(col_l));
			Set<BkTreeSearcher.Match<? extends Data_stupidholder>> rowmatches = rowsearcher.search(new Data_stupidholder(f1_img_index, hash_data.row), (int) 2,(int) Math.sqrt(row_l));

			List<BkTreeSearcher.Match<? extends Data_stupidholder>> collst = asSortedList(colmatches);
			List<BkTreeSearcher.Match<? extends Data_stupidholder>> rowlst = asSortedList(rowmatches);

			List<BkTreeSearcher.Match<? extends Data_stupidholder>> intersection = new ArrayList<>(collst); // use the copy constructor
			intersection.retainAll(rowlst);

						System.out.println("RESULT: Intersection of row and col");
			for (BkTreeSearcher.Match<? extends Data_stupidholder> match : intersection)
				System.out.println(String.format(
						"%s (distance %d)",
						file_map.back().get(match.getMatch().img_index),
						match.getDistance()
				));

//			System.out.println("Col list");
//			for (BkTreeSearcher.Match<? extends Data_stupidholder> match : collst)
//				System.out.println(String.format(
//						"%s (distance %d)",
//						file_map.back().get(match.getMatch().img_index),
//						match.getDistance()
//				));
//			System.out.println("Row list");
//			for (BkTreeSearcher.Match<? extends Data_stupidholder> match : rowlst)
//				System.out.println(String.format(
//						"%s (distance %d)",
//						file_map.back().get(match.getMatch().img_index),
//						match.getDistance()
//				));

			ArrayList<Map.Entry<Integer, Integer>> list = 
					new ArrayList<Map.Entry<Integer, Integer>>(table.entrySet());
			
			list.sort((e1, e2) -> e2.getValue() - e1.getValue());
			
			Util.pl(list);
			
			Util.p(f1_imgname + " is similar to: ");
			for(int i=0; i<query_size; i++){
				//Util.p(" " + list.get(i).getKey() + " = ");
				Util.p(file_map.getKey(list.get(i).getKey()) + " ");
			}
			Util.p("\n");			
		}
		
		
		
		/*
		ArrayList<HashMap<String, ArrayList<String>>> ret = new ArrayList<HashMap<String, ArrayList<String>>>();
		
		for(int i=0; i<tools_count; i++){
			ret.add(new HashMap<String, ArrayList<String>>());
		}
		label1:
		for(int i=0; i<tools_count; i++){
			double[] a_range = {a_min[i], a_max[i]};
			double[] b_range = {b_min[i], b_max[i]};
			double[] S_range = {S_min[i], S_max[i]};

			LTree = new QueryTree(L2.get(i), a_range, b_range, S_range, f2_list.length, query_size);
		
			int stopit = 0;
			for(String f1name : f1_list){
				System.out.println(f1name);
				
				int f1index = file_map.getValue(f1name);
				ArrayList<Integer> resultlist = LTree.query(L1.get(i).getValue(f1index));
				ArrayList<String> result_filenames = new ArrayList<String>();
				for(Integer queryindex : resultlist)
					result_filenames.add(file_map.getKey(queryindex));
				ret.get(i).put(f1name, result_filenames);
				stopit++;
				if (stopit == 20)
					continue label1;
			}
		}
		
		TreeMap<Integer, String> s;
		
		
		int stopit = 0;
		label2:
		for(String f1name : f1_list){
			s = new TreeMap<Integer, String>();

			Util.p("-----\n");
			Util.pl(f1name);
			for(String similar : ret.get(0).get(f1name)){
				s.put(1, similar);
			}

			stopit++;
			if (stopit == 20){
				stopit = 0;
			
				for(String _f1name : f1_list){
					Collection<String> collection = s.values();
					for(String similar : ret.get(1).get(_f1name))
						if(collection.contains(similar))
							s.put(2, similar);
						else
							s.put(1, similar);
					
					stopit++;
					if (stopit == 20){
						Util.pl(s);
						continue label2;
					}
				}
			}
		}
		//		LTree.query(d)
		 */
}

	private static void indexImages(){
		int index;

		f1 = new File(folder1);
		f2 = new File(folder2);
		outf = new File(outputfolder);

		f1_list = f1.list((dir, name) -> !name.startsWith("."));
		f2_list = f2.list((dir, name) -> !name.startsWith("."));

		//Util.pl(folder1 + "/" + f1_list[0]);
		//if(false){
		/* figure out filesize */
		row_l = col_l = 0;
		fr = null; br = null;
		try{
			row_l = 1;
			fr = new FileReader(folder1 + "/" + f1_list[0]);
			fr.read();
			while(fr.read() != '\n'){
				fr.read();
				++row_l;
			}
			col_l = 1;
			br = new BufferedReader(fr);
			while(br.readLine() != null){
				++col_l;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// col by row
		Util.pl(col_l + " x " + row_l);

		/* index the files */
		file_map = new DLMap<String, Integer>();
		index = 0;
		for(String filename : f1_list)
			file_map.put(filename, index++);
		for(String filename : f2_list)
			file_map.put(filename, index++);
	}
	
	/**
	 * process image tool by tool
	 * @param path
	 * @param img_index
	 * @param updateRange
	 */
	public static void processImage(String path, int img_index, boolean updateRange){
		int[][] image;
		image = Util.read(path, row_l, col_l);
		processImageKDTree(image, img_index, updateRange);
		processHistogram(image, img_index, image.length, image[0].length);
	}
	
	public static void processImageKDTree(int[][] img, int img_index, boolean updateRange){


		//add to LMS;
		extractStat(img, img_index, updateRange);
	}

	private static void processHistogram(int[][] img, int img_index, int width, int height) {
		Histogram hist = new Histogram(width, height);
		hist.setThreshold((int) (img.length * 0.25));

		/* process image */
		for(int v=0; v<width; v++) {
			for (int u = 0; u < height; u++) {
				/* histogram */
//				if (img[v][u] == 0) {
//					hist.decrementCol(u);
//					hist.decrementRow(v);
				if (img[v][u] > 0) {
					hist.incrementCol(u);
					hist.incrementRow(v);
				}
			}
		}
		bktree_col.add(new Data_stupidholder(img_index, hist.colStupidHash()));
		bktree_row.add(new Data_stupidholder(img_index, hist.rowStupidHash()));
		stupidhashmap.put(img_index, new Data_stupidhash(hist.colStupidHash(), hist.rowStupidHash()));
	}

	private static void extractStat(int[][] img, int img_index, boolean updateRange){
		double A, x0, y0, sigmaX, sigmaY;
		int x, y;

		x = img.length;
		y = img[0].length;
		Complex[][] I = new Complex[x][y];
		for(int i=0; i<x; i++)
			for(int j=0; j<y; j++)
				I[i][j] = Complex.cartesian(img[i][j]);

		//Util.pf("I.length = %d; I[0].length = %d.\n", I.length, I[0].length);

		/*
		 Data d = Harris_Stephens.processImageEigen(
				I, 
				Prewitt.instance().x_right_kernel(), 
				ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY, x, y), 
				Complex.cartesian(0)
				);
		 */
		//Util.pl(file_map.getKey(index));

		A = 1.0;
		x0 = 0;
		y0 = 0;
		sigmaX = x;
		sigmaY = y;

		SimpleData[] d = Harris_Stephens.forestImageR(
				I, 
				corner_filter.x_right_kernel(), 
				fn.Gaussian(A, x0, y0, sigmaX, sigmaY), 
				Complex.cartesian(0.0)
				);
		
		DLMap<Integer, SimpleData> curr_data_map;
		ArrayList<ArrayList<Data>> curr_data_list_list;
		int d_len = d.length;
		for(int i=0; i<d.length; i++){

			if(updateRange){
				a_min[i] = d[i].a < a_min[i] ? d[i].a : a_min[i];
				a_max[i] = d[i].a > a_min[i] ? d[i].a : a_min[i];
				b_min[i] = d[i].b < b_min[i] ? d[i].b : b_min[i];
				b_max[i] = d[i].b > b_min[i] ? d[i].b : b_min[i];
				S_min[i] = d[i].gof < S_min[i] ? d[i].gof : S_min[i];
				S_max[i] = d[i].gof > S_min[i] ? d[i].gof : S_min[i];
			}
						
			if(updateRange){
				curr_data_map = listof_data_map_folder2.get(i);
				curr_data_list_list = listof_data_lists_folder2.get(i);
			}else{
				curr_data_map = listof_data_map_folder1.get(i);
				curr_data_list_list = listof_data_lists_folder1.get(i);
			}

			curr_data_map.put(img_index, d[i]); 
			ArrayList<Data> 
					data_a_b_list = curr_data_list_list.get(0),
					data_gof_list = curr_data_list_list.get(1);
			
			data_a_b_list.add(new Data_a_b(d[i]));
			data_gof_list.add(new Data_gof(d[i]));
		}
	}
	
	//
	private static void initStructures(){
		listof_data_map_folder1 = new ArrayList<DLMap<Integer, SimpleData>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		listof_data_map_folder2 = new ArrayList<DLMap<Integer, SimpleData>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		listof_data_lists_folder1 = new ArrayList<ArrayList<ArrayList<Data>>>();
		listof_data_lists_folder2 = new ArrayList<ArrayList<ArrayList<Data>>>();
		
		Forest = new ArrayList<ArrayList<KDTree>>();


		Metric<Data_stupidholder> hammingDistance = (x, y) -> {
			if (x.hash.length() != y.hash.length())
				throw new IllegalArgumentException();

			int distance = 0;

			for (int i = 0; i < x.hash.length(); i++)
				if (x.hash.charAt(i) != y.hash.charAt(i))
					distance++;

			return distance;
		};
		bktree_col = new MutableBkTree<>(hammingDistance);
		bktree_row = new MutableBkTree<>(hammingDistance);
		stupidhashmap = new HashMap<>();

		for(int i=0; i<tools_count; i++){
			listof_data_map_folder1.add(new DLMap<Integer, SimpleData>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
			listof_data_map_folder2.add(new DLMap<Integer, SimpleData>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));

			ArrayList<ArrayList<Data>> data_list_list1 = new ArrayList<ArrayList<Data>>();
			//for data_a_b;
			 data_list_list1.add(new ArrayList<Data>());
			//for data_gof;
			 data_list_list1.add(new ArrayList<Data>());
			ArrayList<ArrayList<Data>> data_list_list2 = new ArrayList<ArrayList<Data>>();
			//for data_a_b;
			 data_list_list2.add(new ArrayList<Data>());
			//for data_gof;
			 data_list_list2.add(new ArrayList<Data>());
						
			listof_data_lists_folder1.add(data_list_list1);
			listof_data_lists_folder2.add(data_list_list2);
			
			Forest.add(new ArrayList<KDTree>());
		
		}

		a_min = new double[tools_count];
		a_max = new double[tools_count];
		b_min = new double[tools_count];
		b_max = new double[tools_count];
		S_min = new double[tools_count];
		S_max = new double[tools_count];

		
		
	}
}
