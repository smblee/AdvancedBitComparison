package abc2._program;

import abc2.bktree.*;
import abc2.imageprocess.corner.Harris_Stephens;
import abc2.imageprocess.corner.filter.CornerFilter;
import abc2.imageprocess.corner.filter.ImageDerivative;
import abc2.imageprocess.corner.filter.Prewitt;
import abc2.imageprocess.corner.filter.Sobel;
import abc2.imageprocess.filters.ImageFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import abc2.query.tree.KDTree;
import abc2.query.tree.QueryTree;
import abc2.struct.*;
import abc2.util.MathTools;
import abc2.util.Util;
import abc2.util.fn;
//import static abc2.test.BKTreeTest.asSortedList;

public class PROGRAM {
	private static boolean SHOW_RUNTIMES = false;
	
	private static FileReader fr; 
	private static BufferedReader br;
	private static FileWriter fw;

	private static String folder1, folder2, outputfolder;
	protected static int query_size;
	private static File f1, f2, outf;
	private static String[] f1_list, f2_list;

	protected static int row_l, col_l;
	
	/* for lookup */
	protected static DLMap<String, Integer> file_map;
	
	/* BKTree vars */
	protected static MutableBkTree bktree_col_folder2;
	protected static MutableBkTree bktree_row_folder2;
	protected static Map<Integer, Data_stupidhash> bktree_hashmap_folder1;
	
	/* KDTree vars */
		protected static int tools_count = 2;
		//list of
		// map
		protected static ArrayList<DLMap<Integer, SimpleData>> listof_data_map_folder1;

		protected static ArrayList<DLMap<Integer, SimpleData>> listof_data_map_folder2;
		// for kdtree forest 
		// list of 
		// list of various tool generated Different data sets(lists).
		protected static ArrayList<ArrayList<ArrayList<Data>>> listof_data_lists_folder1, listof_data_lists_folder2;
		protected static ArrayList<ArrayList<KDTree>> Forest;
		
		protected static CornerFilter CORNER_FILTER = Sobel.instance(); 
		
		protected static double CORNER_FILTER_K = 0.07;


	/* OVERALL COUNTS */
	private static HashMap<Integer, Double> RESULT_COUNT_TABLE= new HashMap<Integer, Double>();
	protected static void RECORD_count_KD(Integer index){
        if(RESULT_COUNT_TABLE.containsKey(index)){
            RESULT_COUNT_TABLE.put(index, RESULT_COUNT_TABLE.get(index) + row_l+col_l);
        }else{
            RESULT_COUNT_TABLE.put(index, (double) row_l+col_l);
        }
//		Util.pl(index + " count: " + RESULT_COUNT_TABLE.get(index));
    }

    protected static void RECORD_count_BK(Integer index, double weight){
        if(RESULT_COUNT_TABLE.containsKey(index)){
            RESULT_COUNT_TABLE.put(index, RESULT_COUNT_TABLE.get(index) + weight);
        }else{
            RESULT_COUNT_TABLE.put(index, weight);
        }
//		Util.pl(index + " count: " + RESULT_COUNT_TABLE.get(index));
    }
	
	/* Main PROGRAM */
	public static void main(String[] args) throws IOException{
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

		long s2 = System.currentTimeMillis();													//
		if(SHOW_RUNTIMES)																		//
			Util.pl("preparation: " + (s2 - start) + " ms");									//

		for(String filename : f1_list){
			PI.processImage(folder1 + "/" + filename, file_map.getValue(filename), false);
		}
		for(String filename : f2_list){
			PI.processImage(folder2 + "/" + filename, file_map.getValue(filename), true);
		}



		/* KDTree query */
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
		
		long s3 = System.currentTimeMillis();													//
		if(SHOW_RUNTIMES){																		//
			Util.pl("processImage: " + (s3 - s2) + " ms");										//
			Util.pl("overall: " + (s3 - start) + " ms");										//
			Util.pl("totalImages: " + (f1_list.length + f2_list.length));						//
		}
		

		if(!outf.exists())
			outf.createNewFile();
		fw = new FileWriter(outf);
		
		//TreeMap from overlap counts to img_index
		for(String f1_imgname: f1_list){
			int f1_img_index = file_map.getValue(f1_imgname);
			
			RESULT_COUNT_TABLE.clear();
			// index to count
			long ss1 = System.nanoTime();
//			PI.query_KDTree(f1_img_index);
			long ss2 = System.nanoTime();
			PI.query_BKTree(f1_img_index);
			long ss3 = System.nanoTime();
			
			if(SHOW_RUNTIMES)	Util.pl("KDTree : " + (ss2 - ss1) + " ns");
			if(SHOW_RUNTIMES)	Util.pl("BKTree : " + (ss3 - ss2) + " ns");

			ArrayList<Map.Entry<Integer, Double>> list =
					new ArrayList<Map.Entry<Integer, Double>>(RESULT_COUNT_TABLE.entrySet());
			
			list.sort((e1, e2) -> Double.compare(e1.getValue(), e2.getValue()));

//            System.out.println();
//
//            Util.pl(list);
//            Util.p(f1_imgname + " ");
			fw.write(f1_imgname + " ");
			//query_size
			for(int i=0; i<query_size; i++) {
//				Util.p(list.get(i).getKey() + " ");
				fw.write(file_map.getKey(list.get(i).getKey()) + " ");
			}
			fw.write("\n");	
		}
		
		long end = System.currentTimeMillis();														//
		if(SHOW_RUNTIMES)																			//
			Util.pl("Total runtime: " + (end - start) + " ms.");									//

		fw.close();
	}

	private static void indexImages(){
		int index;

		f1 = new File(folder1);
		f2 = new File(folder2);
		outf = new File(outputfolder);

		f1_list = f1.list((dir, name) -> !name.startsWith("."));
		f2_list = f2.list((dir, name) -> !name.startsWith("."));

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
		//Util.pl(col_l + " x " + row_l);

		/* index the files */
		file_map = new DLMap<String, Integer>();
		index = 0;
		for(String filename : f1_list)
			file_map.put(filename, index++);
		for(String filename : f2_list)
			file_map.put(filename, index++);
	}
	
	//
	private static void initStructures(){
		//BKTree init
		Metric<Data_stupidholder> hammingDistance = (x, y) -> {
			if (x.hash.length != y.hash.length)
				throw new IllegalArgumentException();

			int distance = 0;
			for (int i = 0; i < x.hash.length; i++) {
				distance += x.hash[i] != y.hash[i] ? 1 : 0;
//				distance+=Math.abs(x.hash[i] - y.hash[i]);
            }

//			for (int i = 0; i < x.hash.length; i+=2) {
//				//check odd case
//				if (i == x.hash.length - 1)
//					distance+=Math.abs(x.hash[i] - y.hash[i]);
//				else
//	                distance+=Math.abs(x.hash[i] + x.hash[i+1] - y.hash[i] - y.hash[i+1]);
//            }
			return distance;
		};
		bktree_col_folder2 = new MutableBkTree<>(hammingDistance);
		bktree_row_folder2 = new MutableBkTree<>(hammingDistance);
		bktree_hashmap_folder1 = new HashMap<>();
		
		// KDTree init
		listof_data_map_folder1 = new ArrayList<DLMap<Integer, SimpleData>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		listof_data_map_folder2 = new ArrayList<DLMap<Integer, SimpleData>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		listof_data_lists_folder1 = new ArrayList<ArrayList<ArrayList<Data>>>();
		listof_data_lists_folder2 = new ArrayList<ArrayList<ArrayList<Data>>>();
		
		Forest = new ArrayList<ArrayList<KDTree>>();

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
	}
	
//	
//	
//	/**
//	 * process image tool by tool
//	 * @param path
//	 * @param img_index
//	 * @param folder2
//	 */
//	public static void processImage(String path, int img_index, boolean folder2){
//		/*	Read	*/
//		int[][] image;
//		image = Util.read(path, row_l, col_l);
//		
//		processHistogram(image, img_index, folder2);
//		
//		processImageKDTree(image, img_index, folder2);
//	}
//	
//	public static void processImageKDTree(int[][] img, int img_index, boolean updateRange){
//		//add to LMS;
//		extractStat(img, img_index, updateRange);
//	}
//
//	private static void processHistogram(int[][] img, int img_index, boolean folder2) {
//		int 	width = img.length, 
//				height = img[0].length;
//		
//		Histogram hist = new Histogram(width, height);
//		hist.setThreshold((int) (img.length * 0.25));
//
//		/* process image */
//		for(int v=0; v<width; v++) {
//			for (int u = 0; u < height; u++) {
//				/* histogram */
//				if (img[v][u] > 0) {
//					hist.incrementCol(u);
//					hist.incrementRow(v);
//				}
//			}
//		}
//		if(folder2){
//			bktree_col_folder2.add(new Data_stupidholder(img_index, hist.colStupidHash()));
//			bktree_row_folder2.add(new Data_stupidholder(img_index, hist.rowStupidHash()));
//		}else{
//			bktree_hashmap_folder1.put(img_index, new Data_stupidhash(hist.colStupidHash(), hist.rowStupidHash()));
//		}
//	}
//
//	private static void extractStat(int[][] img, int img_index, boolean folder2){
//		double A, x0, y0, sigmaX, sigmaY;
//		int x, y;
//
//		x = img.length;
//		y = img[0].length;
//		Complex[][] I = new Complex[x][y];
//		for(int i=0; i<x; i++)
//			for(int j=0; j<y; j++)
//				I[i][j] = Complex.cartesian(img[i][j]);
//
//		A = 1.0;
//		x0 = 0;
//		y0 = 0;
//		sigmaX = x;
//		sigmaY = y;
//
//		SimpleData[] d = Harris_Stephens.forestImageR(
//				I, 
//				corner_filter.x_right_kernel(), 
//				fn.Gaussian(A, x0, y0, sigmaX, sigmaY), 
//				Complex.cartesian(0.0)
//				);
//		
//		DLMap<Integer, SimpleData> curr_data_map;
//		ArrayList<ArrayList<Data>> curr_data_list_list;
//		int d_len = d.length;
//		for(int i=0; i<d.length; i++){
//						
//			if(folder2){
//				curr_data_map = listof_data_map_folder2.get(i);
//				curr_data_list_list = listof_data_lists_folder2.get(i);
//			}else{
//				curr_data_map = listof_data_map_folder1.get(i);
//				curr_data_list_list = listof_data_lists_folder1.get(i);
//			}
//
//			curr_data_map.put(img_index, d[i]); 
//			ArrayList<Data> 
//					data_a_b_list = curr_data_list_list.get(0),
//					data_gof_list = curr_data_list_list.get(1);
//			
//			data_a_b_list.add(new Data_a_b(d[i]));
//			data_gof_list.add(new Data_gof(d[i]));
//		}
//	}
	
	
}
