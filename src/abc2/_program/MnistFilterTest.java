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

import struct.Complex;
import struct.MathTools;
import struct.Matrix;
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
			show(folder1 + "/" + filename);
		}
		for(String filename: f2_list) {
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
		
		Complex[][] derivative_kernel = Scharr.instance().x_right_kernel();
//		Complex[][] dx_kernel = RobertsCross.instance().x_kernel();
//		Complex[][] dy_kernel = RobertsCross.instance().x_kernel();
		Complex[][] Ix, Iy, st;
		Complex k = Complex.cartesian(0);
		


//		Ix = ImageDerivative.derivative(I, dx_kernel);
//		Iy = ImageDerivative.derivative(I, dy_kernel);

		
		Ix = ImageDerivative.derivative(I, derivative_kernel);
		Iy = ImageDerivative.derivative(I, Matrix.transpose(derivative_kernel));

		double A, x0, y0, sigmaX, sigmaY;
		
		A = 1.0;
		x0 = 0;
		y0 = 0;
		sigmaX = x;
		sigmaY = y;
		
		
		Complex[][] R_matrix = new Complex[x][y];
		
		List<Double[]> data = new ArrayList<Double[]>();
		for(int v=0; v < col_l; v++){
			for(int u=0; u < row_l; u++){
				st = Harris_Stephens.structure_tensor(Ix, Iy, u, v,
						ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY));
				
				R_matrix[v][u] =  Harris_Stephens.R(st, k);
					//Util.pl("(" + u + ", " + v + ")");
				if(R_matrix[v][u].Re != 0){
					R_matrix[v][u] = Complex.cartesian(1);
					Util.pl("(" + u  + ", " +  v  + ")");
					Double[] datum = {u*1.0, v*1.0};
					data.add(datum);
				}
				//
				//ret[v][u] = R(st, k);
			}
		}
		
		
		Util.pl(Util.arr_s(R_matrix, " "));
		
		Util.pl(MathTools.linear_regression_R2(data));
	}
}
