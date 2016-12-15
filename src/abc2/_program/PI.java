package abc2._program;

import java.util.*;
import java.util.function.BiFunction;

import abc2.bktree.BkTreeSearchApp;
import abc2.imageprocess.corner.filter.CornerFilter;
import abc2.imageprocess.filters.ImageFilter;
import abc2.query.tree.KDTree;
import abc2.struct.DLMap;
import abc2.struct.Data;
import abc2.struct.Data_a_b;
import abc2.struct.Data_bell_curve;
import abc2.struct.Data_gof;
import abc2.struct.Data_stupidhash;
import abc2.struct.Data_stupidholder;
import abc2.struct.Histogram;
import abc2.struct.SimpleData;
import abc2.util.MathTools;
import abc2.util.Util;
import abc2.util.fn;

public class PI extends PROGRAM{
	
	public static void processImage(String path, int img_index, boolean folder2){
		/*	Read	*/
		int[][] image;
		image = Util.read(path, row_l, col_l);
		
		processImage(
				image, 
				img_index, 
				folder2,
				CORNER_FILTER.x_kernel(),
				CORNER_FILTER.y_kernel(),
				CORNER_FILTER_K
				);
	}// processImage
	
	private static void processImage(
			int[][] img, 
			int img_index, 
			boolean folder2, 
			int[][] dx_kernel,
			int[][] dy_kernel,
			double k
			) {

		int 	col_l = img.length, 
				row_l = img[0].length;
		
		/* histogram prep */
		Histogram hist = new Histogram(col_l, row_l);
		hist.setThreshold((int) (img.length * 0.24));
		
		/* corner filter prep */
		double	dx_v_u, dy_v_u;
		
		double A, x0, y0, sigmaX, sigmaY;
		
		A = 1.0;
		x0 = y0 = 0.0;
		sigmaX = col_l;
		sigmaY = row_l;
		BiFunction<Integer, Integer, Double> corner_filter_window_function = 
				fn.Gaussian(A, x0, y0, sigmaX, sigmaY);

		Double[] datum;
		List<Double[]> data = new ArrayList<Double[]>();

		/* process image */
		for(int v=0; v<col_l; v++) {
			for (int u = 0; u < row_l; u++) {
				
				/* histogram */
				if (img[v][u] > 0) {
					hist.incrementCol(u);
					hist.incrementRow(v);
				}
				
				/* corner filters */
				if(v == 0 || u == 0 || v == col_l - 1 || u == row_l - 1){
					dx_v_u = 0.0;
					dy_v_u = 0.0;
				}
				else{
					dx_v_u = ImageFilter.patch_mask(dx_kernel, img, v, u); 
					dy_v_u = ImageFilter.patch_mask(dy_kernel, img, v, u); 
				}	

				double[][] st = CornerFilter.decision_matrix(
						dx_v_u, dy_v_u, v, u, corner_filter_window_function
						);

				if(CornerFilter.R(st, k) != 0){
					datum = new Double[]{ u * 1.0 , v * 1.0 };
					data.add(datum);
				}
				
			}
		}
		
		
		/* KDTree process data */
		SimpleData[] d = {
				MathTools.linear_regression_R2(data),
				MathTools.inverse_linear_regression_R2(data),
				MathTools.mean_var_skewness(hist.rows(), col_l),
				MathTools.mean_var_skewness(hist.cols(), row_l)				
		};
		
		/* BKTree save data */
		if(folder2){
			bktree_col_folder2.add(new Data_stupidholder(img_index, hist.colStupidHash()));
			bktree_row_folder2.add(new Data_stupidholder(img_index, hist.rowStupidHash()));
		}else{
			bktree_hashmap_folder1.put(img_index, new Data_stupidhash(hist.colStupidHash(), hist.rowStupidHash()));
		}
		
		/* KDTree save data */
		DLMap<Integer, SimpleData> curr_data_map;
		ArrayList<ArrayList<Data>> curr_data_list_list;
		
		for(int i=0; i<2; i++){
			if(folder2){
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
		for(int i=2; i<4; i++){
			if(folder2){
				curr_data_map = listof_data_map_folder2.get(i);
				curr_data_list_list = listof_data_lists_folder2.get(i);
			}else{
				curr_data_map = listof_data_map_folder1.get(i);
				curr_data_list_list = listof_data_lists_folder1.get(i);
			}
			
			curr_data_map.put(img_index, d[i]); 
			ArrayList<Data> data_bell_curve_list = curr_data_list_list.get(0);
			
			data_bell_curve_list.add(new Data_bell_curve(d[i]));
		}
	}//  processImage
	
	/* BKTree Query */
	public static void query_BKTree(int f1_img_index){
		/*	BKTree query */
		//System.out.println("********* STUPID HASH ********");
		Data_stupidhash hash_data =  bktree_hashmap_folder1.get(f1_img_index);
		
		BkTreeSearchApp<Data_stupidholder> colsearcher = new BkTreeSearchApp<>(bktree_col_folder2);
		BkTreeSearchApp<Data_stupidholder> rowsearcher = new BkTreeSearchApp<>(bktree_row_folder2);

		//System.out.println("Stupid hashing with image file " + file_map.getKey(f1_img_index));
		//TODO: verify row and col
		//System.out.println("Query col with "  + hash_data.col);
		//System.out.println("Query row with " + hash_data.row);
		Set<BkTreeSearchApp.Match<? extends Data_stupidholder>> colmatches = colsearcher.search(new Data_stupidholder(f1_img_index, hash_data.col), col_l, (int) col_l);
		Set<BkTreeSearchApp.Match<? extends Data_stupidholder>> rowmatches = rowsearcher.search(new Data_stupidholder(f1_img_index, hash_data.row), row_l, (int) row_l);

		// image index to the match.
		Map<Integer, BkTreeSearchApp.Match<? extends Data_stupidholder>> collst = new HashMap<>();
		for (BkTreeSearchApp.Match<? extends Data_stupidholder> m : colmatches) {
			collst.put(m.getMatch().img_index, m);
		}

		List<BkTreeSearchApp.Match<Integer>> intersection = new ArrayList<>(); //= new ArrayList<>(collst); // use the copy constructor
		for (BkTreeSearchApp.Match<? extends Data_stupidholder> row_img : rowmatches) {
			if (collst.containsKey(row_img.getMatch().img_index)) {
				intersection.add(new BkTreeSearchApp.Match<Integer>(row_img.getMatch().img_index, row_img.getDistance() + collst.get(row_img.getMatch().img_index).getDistance()));
			}
		}
		// sort the intersection of col row by combined distance.
		intersection.sort((o1, o2) -> Integer.compare(o1.getDistance(), o2.getDistance()));

//		Util.p("\n../queries/" + file_map.back().get(f1_img_index) + " ");
//		int top = 0;
		for (BkTreeSearchApp.Match<Integer> match : intersection){
			// print the top 10 results in the sorted list.
//			if (top++ < 10) {
//				Util.p(match.getDistance() + " ");

//				Util.p(file_map.back().get(match.getMatch()) + " ");
//			}
			RECORD_count_BK(match.getMatch(), match.getDistance());
		}
		
	}// query_BKTree

	/* KDTree Query */
	public static void query_KDTree(int f1_img_index){		
		for(int i=0; i<tools_count; i++){
			DLMap<Integer, SimpleData> folder1_data_map = listof_data_map_folder1.get(i);
			ArrayList<KDTree> forest_partition = Forest.get(i);
			
			Data query_data;		
			Data[] data_arr;

			for(int j=0; j<forest_partition.size(); j++){
				KDTree tree = forest_partition.get(j);
				
				switch(j){
					case 0:
						if(i == 0 || i == 1)
							query_data = new Data_a_b(folder1_data_map.getValue(f1_img_index));
						else
							query_data = new Data_bell_curve(folder1_data_map.getValue(f1_img_index));
						break;
					case 1:
						query_data = new Data_gof(folder1_data_map.getValue(f1_img_index));
						break;
					default:
						query_data = null;
				}
				
				data_arr = tree.query(query_data, query_size);
				
				for(int k=0; k<data_arr.length; k++){
					Data datum = data_arr[k];
					
					SimpleData sd;
					if(datum instanceof Data_a_b)
						sd = ((Data_a_b) datum).sd;
					else if(datum instanceof Data_gof)
						sd = ((Data_gof) datum).sd;
					else
						sd = ((Data_bell_curve) datum).sd;

					DLMap<Integer, SimpleData> tool_i_data_map_folder2 = listof_data_map_folder2.get(i);
					int index = tool_i_data_map_folder2.getKey(sd);
							
					RECORD_count_KD(index);
				}
				
			}
		}
	}// query_KDTree
	
}
