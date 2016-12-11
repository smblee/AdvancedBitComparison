package abc2.test;

import abc2.imageprocess.corner.Harris_Stephens;
import abc2.imageprocess.corner.filter.ImageDerivative;
import abc2.imageprocess.corner.filter.Prewitt;
import abc2.imageprocess.corner.filter.RobertsCross;
import abc2.imageprocess.corner.filter.Scharr;
import abc2.imageprocess.corner.filter.Sobel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import abc2.struct.Complex;
import abc2.util.MathTools;
import abc2.util.Matrix;
import abc2.util.Util;
import abc2.util.fn;

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

		for(String filename: f1_list) {
			Util.pl(folder1 + "/" + filename);
			show(folder1 + "/" + filename);
		}
		for(String filename: f2_list) {
			Util.pl(folder2 + "/" + filename);
			show(folder2 + "/" + filename);
		}

	}

	private static void show(String path){
		int x, y;

		int[][] img;
		img = Util.read(path, row_l, col_l);


		x = img.length;
		y = img[0].length;
		Complex[][] I = new Complex[x][y];
		for(int i=0; i<x; i++)
			for(int j=0; j<y; j++)
				I[i][j] = Complex.cartesian(img[i][j]);

		Util.pl(Util.arr_s(I, " "));

		Complex[][] dx_kernel = Sobel.instance().x_left_kernel();
		Complex[][] dy_kernel = Sobel.instance().y_up_kernel();
		Complex[][] Ix, Iy, st;
		Complex k = Complex.cartesian(0.10);

		Ix = ImageDerivative.derivative(I, dx_kernel);
		Iy = ImageDerivative.derivative(I, dy_kernel);

		double min_x, max_x, min_y, max_y;
		min_x = max_x = min_y = max_y = 0;

		for(int v=0; v<Ix.length; v++)
			for(int u=0; u<Ix[0].length; u++){
				min_x = Ix[v][u].Re < min_x ? Ix[v][u].Re : min_x;
				max_x = Ix[v][u].Re > max_x ? Ix[v][u].Re : max_x;
				min_y = Iy[v][u].Re < min_y ? Iy[v][u].Re : min_y;
				max_y = Iy[v][u].Re < max_y ? Iy[v][u].Re : max_y ;
			}

		//		Util.pl("IX");
		//		Util.pl(Util.arr_s(Ix, " "));
		//		Util.pl("IX");
		//		
		//
		//		Util.pl("IY");
		//		Util.pl(Util.arr_s(Ix, " "));
		//		Util.pl("IY");

		double A, x0, y0, sigmaX, sigmaY;

		A = 1.0;
		x0 = 0;
		y0 = 0;
		sigmaX = x;
		sigmaY = y;


		Complex[][] R_matrix = new Complex[x][y];

		List<Double[]> data_25 = new ArrayList<Double[]>();
		List<Double[]> data_0 = new ArrayList<Double[]>();
		List<Double[]> data_32 = new ArrayList<Double[]>();
		List<Double[]> data_40 = new ArrayList<Double[]>();
		for(int v=0; v < col_l; v++){
			for(int u=0; u < row_l; u++){
				st = Harris_Stephens.structure_tensor(Ix, Iy, u, v,
						fn.Constant(1));
				//fn.Gaussian(A, x0, y0, sigmaX, sigmaY));

				R_matrix[v][u] =  Harris_Stephens.R(st, k);
				//R_matrix[v][u] = Ix[v][u].mult(Ix[v][u]).add(Iy[v][u].mult(Iy[v][u])).sqrt();

				//Util.pl("(" + u + ", " + v + ")");

				Double[] datum = {u * 1.0, v * 1.0};
				if(R_matrix[v][u].Re != 0){
					data_0.add(datum);
				}
				if(R_matrix[v][u].Re <= -32){
					data_32.add(datum);
				}
				if(R_matrix[v][u].Re <= -40){
					data_40.add(datum);
				}

				if(R_matrix[v][u].Re <= -25){
					R_matrix[v][u] = Complex.cartesian(1);
					data_25.add(datum);
				}
				else{
					R_matrix[v][u] = Complex.cartesian(0);
				}
				
				//
				//ret[v][u] = R(st, k);
			}
		}

		Util.pl("");
		Util.pl(Util.arr_s(R_matrix, " "));

		Util.pl(data_0.size());
		Util.pl("0  linear :  " + MathTools.linear_regression_R2(data_0));
		Util.pl("0  inverse : " + MathTools.inverse_linear_regression_R2(data_0));
		Util.pl(data_25.size());
		Util.pl("25 linear :  " + MathTools.linear_regression_R2(data_25));
		Util.pl("25 inverse : " + MathTools.inverse_linear_regression_R2(data_25));
		Util.pl(data_32.size());
		Util.pl("32 linear :  " + MathTools.linear_regression_R2(data_32));
		Util.pl("32 inverse : " + MathTools.inverse_linear_regression_R2(data_32));
		Util.pl(data_40.size());
		Util.pl("40 linear :  " + MathTools.linear_regression_R2(data_40));
		Util.pl("40 inverse : " + MathTools.inverse_linear_regression_R2(data_40));

		//		/* Generation of image2 after linear transformtion */
		//		  for (y = 1; y < y_size1 - 1; y++) {
		//		    for (x = 1; x < x_size1 - 1; x++) {
		//		      pixel_value = 0.0;
		//		      for (j = -1; j <= 1; j++) {
		//			    for (i = -1; i <= 1; i++) {
		//			      pixel_value += weight[j + 1][i + 1] * image1[y + j][x + i];
		//			    }
		//		      }
		//		      pixel_value = MAX_BRIGHTNESS * (pixel_value - min) / (max - min);
		//		      image2[y][x] = (unsigned char)pixel_value;
		//		    }
		//		  }
		//		  


		/* NOT THESE */

		//
		//		float[][] kernelx = {{-1, 0, 1}, 
		//                {-2, 0, 2}, 
		//                {-1, 0, 1}};
		//		
		//		float[][] kernely = {{-1, -2, -1}, 
		//                {0,  0,  0}, 
		//                {1,  2,  1}};
		//	
		//			
		//		double[][] outputimage = new double[x][y];
		//		double[][] gradientimage = new double[x][y];
		//		for(int v=0; v<x; v++){
		//			for(int u=0; u<y; u++){
		//				
		//
		//				double magX = 0.0, magY = 0.0; // this is your magnitude
		//
		//				if(u == 0 || v == 0 || u == y - 1 || v == x - 1){
		//					outputimage[v][u] = 0.0;
		//					gradientimage[v][u] = 0.0;
		//				}
		//				else{
		//					for(int a = 0; a < 3; a++)
		//					{
		//						for(int b = 0; b < 3; b++)
		//						{         
		//							Util.pl((v + a - 1) + " : " + (u + b - 1));
		//							magX += img[v + a - 1][u + b - 1] * kernelx[a][b];
		//							magY += img[v + a - 1][u + b - 1] * kernely[a][b];
		//						}
		//					}
		//
		//					Util.pl(magX + " : " + magY);
		//					double mag = Math.sqrt(magX * magX + magY * magY);
		//					double gradient = Math.atan(magY / magX);
		//
		//					outputimage[v][u] = mag;
		//					gradientimage[v][u] = gradient;
		//				}
		//			}
		//		}
		//
		//		Util.pl("converted");
		//		int[][] XX = new int[x][y];
		//		int[][] XXX = new int[x][y];
		//		for(int v=0; v<x; v++){
		//			for(int u=0; u<y; u++){
		//				XX[v][u] = outputimage[v][u] == 0.0 ? 0 : 1;
		//				XXX[v][u] = gradientimage[v][u] == 0.0 ? 0 : 1;
		//			}
		//		}
		//		Util.pl("mag");
		//		Util.pl(Util.arr_s(XX, " "));
		//
		//		Util.pl("gradient");
		//		Util.pl(Util.arr_s(XXX, " "));
		//		
	}

}
