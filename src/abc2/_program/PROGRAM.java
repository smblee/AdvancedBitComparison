package abc2._program;

import abc2.imageprocess.corner.Harris_Stephens;
import abc2.imageprocess.corner.filter.ImageDerivative;
import abc2.imageprocess.corner.filter.Prewitt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import struct.Complex;
import struct.Data;
import struct.Map2;
import struct.SComparator;
import abc2.query.tree.QueryTree;
import abc2.util.Util;

public class PROGRAM {
	private static int C = 2;
	
	private static FileReader fr; 
	private static BufferedReader br;

	private static int row_l, col_l;
	private static ArrayList<Map2<Integer, Data>> L1, L2, M1, M2, S1, S2;
	private static QueryTree LTree, MTree, STree;

	private static Map2<String, Integer> file_map;

	private static String folder1, folder2, outputfolder;
	private static File f1, f2, outf;
	private static String[] f1_list, f2_list;
	private static double[] a_min, a_max, b_min, b_max, S_min, S_max;

	public static void main(String[] args){
		long start = System.currentTimeMillis();
		char delim, c;
		int index;
		int query_size;

		if(args.length < 4)
			Util.pf("bad args count: %d; REQUIRED 4 \n", args.length);

		folder1 = args[0];
		folder2 = args[1];
		outputfolder = args[2];
		query_size = Integer.valueOf(args[3]);

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
		file_map = new Map2<String, Integer>();
		index = 0;
		for(String filename : f1_list)
			file_map.put(filename, index++);
		for(String filename : f2_list)
			file_map.put(filename, index++);

		/* process the images and create tables */
		init();

		long s2 = System.currentTimeMillis();								//
		Util.pl("preparation: " + (s2 - start) + " ms");					//

		for(String filename : f1_list)
			processImage(folder1 + "/" + filename, file_map.getValue(filename), false);
		for(String filename : f2_list)
			processImage(folder2 + "/" + filename, file_map.getValue(filename), true);

		long s3 = System.currentTimeMillis();								//
		Util.pl("processImage: " + (s3 - s2) + " ms");						//
		Util.pl("overall: " + (s3 - start) + " ms");						//
		Util.pl("totalImages: " + (f1_list.length + f2_list.length));						//

//		
//		for(Map.Entry<Integer, Data> entry : L2.entrySet()){
//			Integer i = entry.getKey();
//			Data d = entry.getValue();
//			System.out.println(file_map.getKey(i) + " : " + d);
//		}
//					

		//		Data old = null; Integer _index = -1;
		//		for(Map.Entry<Integer, Data> entry : L.entrySet()){
		//			//for(Data d : L.values()){
		//			Data d = entry.getValue();
		//			Integer i = entry.getKey();
		//			if(old == null){
		//				old = d;
		//				_index = i;
		//			}
		//			else{
		//				if(old.equals(d)){
		//					System.out.println(file_map.getKey(_index) + " = " + file_map.getKey(i));
		//				}else{
		//					System.out.println(file_map.getKey(_index) + " =/= " + file_map.getKey(i));
		//					old = d;
		//					_index = i;
		//				}
		//			}
		//		}

		/* query tree */
		ArrayList<HashMap<String, ArrayList<String>>> ret = new ArrayList<HashMap<String, ArrayList<String>>>();
		
		for(int i=0; i<C; i++){
			ret.add(new HashMap<String, ArrayList<String>>());
		}
		label1:
		for(int i=0; i<C; i++){
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
	}

	public static void processImage(String path, int index, boolean updateRange){
		int[][] image;
		image = Util.read(path, row_l, col_l);

		//add to LMS;
		extractStat(image, index, updateRange);
	}

	private static void extractStat(int[][] img, int index, boolean updateRange){
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

		Data[] d = Harris_Stephens.forestImageR(
				I, 
				Prewitt.instance().x_right_kernel(), 
				ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY), 
				Complex.cartesian(0.0)
				);
		
		int d_len = d.length;
		for(int i=0; i<d.length; i++){
			if(d[i].containsNaN())
				Util.pl(file_map.getKey(index));

			//Util.pl(index + " : " + d);

			if(updateRange){
				a_min[i] = d[i].a < a_min[i] ? d[i].a : a_min[i];
				a_max[i] = d[i].a > a_min[i] ? d[i].a : a_min[i];
				b_min[i] = d[i].b < b_min[i] ? d[i].b : b_min[i];
				b_max[i] = d[i].b > b_min[i] ? d[i].b : b_min[i];
				S_min[i] = d[i].gof < S_min[i] ? d[i].gof : S_min[i];
				S_max[i] = d[i].gof > S_min[i] ? d[i].gof : S_min[i];

				L2.get(i).put(index, d[i]);
			}else{
				L1.get(i).put(index, d[i]);
			}
		}
	}
	
	//
	private static void init(){
		L1 = new ArrayList<Map2<Integer, Data>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		M1 = new ArrayList<Map2<Integer, Data>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		S1 = new ArrayList<Map2<Integer, Data>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		L2 = new ArrayList<Map2<Integer, Data>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		M2 = new ArrayList<Map2<Integer, Data>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		S2 = new ArrayList<Map2<Integer, Data>>();//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
	
		for(int i=0; i<C; i++){
			L1.add(new Map2<Integer, Data>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
			M1.add(new Map2<Integer, Data>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
			S1.add(new Map2<Integer, Data>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
			L2.add(new Map2<Integer, Data>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
			M2.add(new Map2<Integer, Data>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
			S2.add(new Map2<Integer, Data>());//new HashMap<Integer, Data>(), new TreeMap<Data, Integer>(new SComparator()));
		}

		a_min = new double[C];
		a_max = new double[C];
		b_min = new double[C];
		b_max = new double[C];
		S_min = new double[C];
		S_max = new double[C];
	}
}
