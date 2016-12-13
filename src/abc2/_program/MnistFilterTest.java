package abc2._program;

import abc2.struct.Histogram;
import abc2.util.MathTools;
import abc2.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MnistFilterTest {
	private static FileReader fr; 
	private static BufferedReader br;
	
	private static int row_l, col_l;
	
	private static String folder1, folder2, outputfolder;
	private static File f1, f2, outf;

	private static String[] f1_list, f2_list;
	
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

		for(String filename: f1_list) {
			Util.pl("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			Util.pl(folder1 + "/" + filename);
			process(folder1 + "/" + filename);
			Util.pl("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		}
		for(String filename: f2_list) {
			Util.pl("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			Util.pl(folder2 + "/" + filename);
			process(folder2 + "/" + filename);
			Util.pl("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
		
	}


	/**
	 * Process the image
	 * Generates the following
	 * 1. One dimentional array of the image. (?)
	 * 2. col/row histogram
	 * 3. Kernel filtered image
	 * 4. Gradient image (?)
	 * 5. Edge detected image
	 * @param path
	 */
	private static void process(String path){
		int x, y;

		int[][] img;
		img = Util.read(path, row_l, col_l);
		
		x = img.length;
		y = img[0].length;

		Util.pl("original");
		Util.pl(Util.arr_s(img, " "));

		/* build 1d array for the image. REMOVE IF NOT NEEDED */
		int[] one_d = new int[x * y];
		int i=0;
		for(int u=0; u<y; u++){
			for(int v=0; v<x; v++){
				one_d[i++] = img[v][u];
			}
		}
//		Util.pl(Util.arr_s(one_d, " "));



		/* initialize variables */
		float[][] kernelx = {{-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}};

		float[][] kernely = {{-1, -2, -1},
                {0,  0,  0},
                {1,  2,  1}};


		double[][] img_kernelFiltered = new double[x][y];
		double[][] gradientimage = new double[x][y];

		double[][] ix = new double[x][y];
		double[][] iy = new double[x][y];

		Histogram hist = new Histogram(x, y);


		/* process image */
		for(int v=0; v<x; v++){
			for(int u=0; u<y; u++) {
				/* histogram */
				if (img[v][u] == 0) {
					hist.decrementCol(u);
					hist.decrementRow(v);
				} else {
					hist.incrementCol(u);
					hist.incrementRow(v);
				}

				/* kernel filtering */
				if (v > 0 && u > 0 && v < x - 2 && u < y - 2) {
					double magX = 0.0, magY = 0.0;

					for (int a = 0; a < 3; a++) {
						for (int b = 0; b < 3; b++) {
							magX += img[v + a - 1][u + b - 1] * kernelx[a][b];
							magY += img[v + a - 1][u + b - 1] * kernely[a][b];
						}
					}

					// faster method
//					double mag = Math.abs(magX) + Math.abs(magY);
					double mag = (Math.sqrt(magX * magX + magY * magY));
					double gradient = Math.atan(magY / magX);

					img_kernelFiltered[v][u] = mag;
					ix[v][u] = magX;
					iy[v][u] = magY;
					gradientimage[v][u] = gradient;
				}
			}
		}


		Util.pl("finished processing");

		Util.pl(hist);


		/* work with the kernel filtered image */

		/*	generate the following
		  1. Linear Regression
		  2. edge detected image after filtering through threshold.
		  3. gradient image after filtering through threshold.
 		 */

		List<Double[]> data = new ArrayList<Double[]>();
		int[][] img_edgeDetected = new int[x][y];
		int[][] img_gradient = new int[x][y];
		double threshold = 4.1;

		for(int v=0; v<x; v++){
			for(int u=0; u<y; u++){
				if(img_kernelFiltered[v][u] >= threshold) {
					Double[] datum = {v * 1.0, u * 1.0};
					data.add(datum);
				}
				img_edgeDetected[v][u] = img_kernelFiltered[v][u] >= threshold ? 1 : 0;
				img_gradient[v][u] = gradientimage[v][u] == 0.0 ? 0 : 1;
			}
		}
		Util.pl("img_edgeDetected");
		Util.pl(Util.arr_s(img_edgeDetected, " "));
		Util.pl(MathTools.linear_regression_R2(data));









		double[][] yo =
				{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};


// THIS IS FOR THE 5 IMAGE ABOVE ////////////////
//		data.clear();
//		for(int v=0; v<x; v++) {
//			for (int u = 0; u < y; u++) {
//				if (yo[v][u] > 0) {
//					Double[] datum = {v * 1.0, u * 1.0};
//
//					data.add(datum);
//				}
//			}
//		}
//		Util.pl(MathTools.linear_regression_R2(data));
///////////////////////////////////////////////////

	}

}
