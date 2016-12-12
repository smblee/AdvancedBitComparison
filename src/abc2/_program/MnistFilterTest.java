package abc2._program;

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
//		Complex[][] I = new Complex[x][y];
//		for(int i=0; i<x; i++)
//			for(int j=0; j<y; j++)
//				I[i][j] = Complex.cartesian(img[i][j]);

		Util.pl("original");
		Util.pl(Util.arr_s(img, " "));
		
		int[] one_d = new int[x * y];
		int i=0;
		for(int u=0; u<y; u++){
			for(int v=0; v<x; v++){
				one_d[i++] = img[v][u];
			}
		}
		Util.pl(Util.arr_s(one_d, " "));

		float[][] kernelx = {{-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}};

		float[][] kernely = {{-1, -2, -1},
                {0,  0,  0},
                {1,  2,  1}};


		double[][] outputimage = new double[x][y];
		double[][] ix = new double[x][y];
		double[][] iy = new double[x][y];

		double[][] gradientimage = new double[x][y];
		for(int v=1; v<x-2; v++){
			for(int u=1; u<y-2; u++){


				double magX = 0.0, magY = 0.0; // this is your magnitude

					for(int a = 0; a < 3; a++)
					{
						for(int b = 0; b < 3; b++)
						{
//							Util.pl((v + a - 1) + " : " + (u + b - 1));
							magX += img[v + a - 1][u + b - 1] * kernelx[a][b];
							magY += img[v + a - 1][u + b - 1] * kernely[a][b];
						}
					}

//					Util.pl(magX + " : " + magY);
					double mag = Math.abs(magX) + Math.abs(magY);
//					double mag = (Math.sqrt(magX * magX + magY * magY));
					double gradient = Math.atan(magY / magX);

					outputimage[v][u] = mag;
					ix[v][u] = magX;
					iy[v][u] = magY;
					gradientimage[v][u] = gradient;
			}
		}

		Util.pl("converted");

		List<Double[]> data = new ArrayList<Double[]>();

		int[][] XX = new int[x][y];
		int[][] XXX = new int[x][y];
		for(int v=0; v<x; v++){
			for(int u=0; u<y; u++){
				if(outputimage[v][u] >= 4.1) {
					Double[] datum = {v * 1.0, u * 1.0};

					data.add(datum);
				}
				XX[v][u] = outputimage[v][u] >= 4.1 ? 1 : 0;
				XXX[v][u] = gradientimage[v][u] == 0.0 ? 0 : 1;
			}
		}
		Util.pl("mag");
		Util.pl(Util.arr_s(outputimage, " "));
		Util.pl("XX");
		Util.pl(Util.arr_s(XX, " "));
		Util.pl(MathTools.linear_regression_R2(data));
		//MathTools.orthogonal(outputimage, 0, 0);

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

//		Util.pl("magX");
//		Util.pl(Util.arr_s(ix, " "));
//		Util.pl("magY");
//		Util.pl(Util.arr_s(iy, " "));


//		Util.pl("gradient");
//		Util.pl(Util.arr_s(XXX, " "));

		
		
//
//		Util.pl(Util.arr_s(I, " "));
//		
////		Complex[][] derivative_kernel = Sobel.instance().x_right_kernel();
//		Complex[][] dx_kernel = Sobel.instance().x_left_kernel();
//		Complex[][] dy_kernel = Sobel.instance().y_up_kernel();
//		Complex[][] Ix, Iy, st;
//		Complex k = Complex.cartesian(0);
//		
//
//
//		Ix = ImageDerivative.derivative(I, dx_kernel);
//		Iy = ImageDerivative.derivative(I, dy_kernel);
//
//		
////		Ix = ImageDerivative.derivative(I, derivative_kernel);
////		Iy = ImageDerivative.derivative(I, Matrix.transpose(derivative_kernel));
//
//		Util.pl("IX");
//		Util.pl(Util.arr_s(Ix, " "));
//		Util.pl("IX");
//		
//
//		Util.pl("IY");
//		Util.pl(Util.arr_s(Ix, " "));
//		Util.pl("IY");
//
//		double A, x0, y0, sigmaX, sigmaY;
//		
//		A = 1.0;
//		x0 = 0;
//		y0 = 0;
//		sigmaX = x;
//		sigmaY = y;
//		
//		
//		Complex[][] R_matrix = new Complex[x][y];
//		
//		List<Double[]> data = new ArrayList<Double[]>();
//		for(int v=0; v < col_l; v++){
//			for(int u=0; u < row_l; u++){
//				
////				st = Harris_Stephens.structure_tensor(Ix, Iy, u, v,
////						ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY));
////				
////				R_matrix[v][u] =  Harris_Stephens.R(st, k);
//				R_matrix[v][u] = Ix[v][u].mult(Ix[v][u]).add(Iy[v][u].mult(Iy[v][u])).sqrt();
//					//Util.pl("(" + u + ", " + v + ")");
//				if(R_matrix[v][u].Re != 0){
//					R_matrix[v][u] = Complex.cartesian(1);
//					Util.pl("(" + u  + ", " +  v  + ")");
//					Double[] datum = {u*1.0, v*1.0};
//					data.add(datum);
//				}
//				//
//				//ret[v][u] = R(st, k);
//			}
//		}
//		
//		
//		Util.pl(Util.arr_s(R_matrix, " "));
//		
//		Util.pl(MathTools.linear_regression_R2(data));
	}

}
