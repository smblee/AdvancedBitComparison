package abc2.imageprocess.corner;

import abc2.imageprocess.corner.filter.ImageDerivative;
import abc2.imageprocess.corner.filter.Prewitt;

import java.awt.image.BufferedImage;
import java.sql.Time;
import java.util.function.BiFunction;

import struct.Complex;
import struct.Data;
import abc2._program.PROGRAM;
import abc2.util.Util;

public class CornerTest {

	static BufferedImage buf_img;
	static int[][] img;

	static String img_s = "bell";
	
	static String imgname = img_s + "%d.png";
	static String imgname_dx = img_s + "%d_dx.png";
	static String imgname_dy = img_s + "%d_dy.png";
	static int X = 20;
	

	static long start ;
	public static void main(String[] args){
		//buf_img = Util.read("Prewitt_src.jpg");
		/*
		start = System.currentTimeMillis();
		buf_img = Util.read(String.format(imgname, X));
		img = Util.extractRGB(buf_img);

		//Gaussian_test();
		//Harris_Stephens_test();
		Harris_Stephens_test2();
		*/
		int[][] image = Util.read("Test1/database/101046775", 28, 28);
		
		double A, x0, y0, sigmaX, sigmaY;
		int x, y;
		
		
		x = image.length;
		y = image[0].length;
		Complex[][] I = new Complex[x][y];
		for(int i=0; i<x; i++)
			for(int j=0; j<y; j++)
				I[i][j] = Complex.cartesian(image[i][j]);
		
		Util.pl(Util.arr_s(I, " "));

		Util.pf("I.length = %d; I[0].length = %d.\n", I.length, I[0].length);
				 
		 A = 1.0;
		 x0 = 0;
		 y0 = 0;
		 sigmaX = x;
		 sigmaY = y;

		 Data d = Harris_Stephens.processImageR(
				I, 
				Prewitt.instance().x_right_kernel(), 
				ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY), 
				Complex.cartesian(0.0)
				);
		 
		 Complex[][] R = Harris_Stephens.ImageR(
				 I, 
				 Prewitt.instance().x_right_kernel(), 
				 ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY),
				 Complex.cartesian(0.0)
				 );
		 Util.pl(Util.arr_s(R, " "));
		 
		 Util.write(R, "Test1/output/101046775_1.png");
	}
	
	private static void Harris_Stephens_test2(){
		long start1 = System.currentTimeMillis();
		/* Gaussian param */
		double A, x0, y0, sigmaX, sigmaY;
		
		
		int x = img.length;
		int y = img[0].length;
		Complex[][] I = new Complex[x][y];
		for(int i=0; i<x; i++)
			for(int j=0; j<y; j++)
				I[i][j] = Complex.cartesian(img[i][j]);
		
		A = 1.0;
		x0 = 0;
		y0 = 0;
		sigmaX = x;
		sigmaY = y;
		Complex[][] R = Harris_Stephens.ImageR(
				I, 
				Prewitt.instance().x_right_kernel(), 
				ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY), 
				Complex.cartesian(0.0)
				);
		long end = System.currentTimeMillis();
		Util.pf("total : %d ms", end - start);
		Util.pf("filter : %d ms", end - start1);
		
		//Util.pl(Util.arr_s(R, " "));
		
		Util.write(R, "test1.png");
	}

	private static void Gaussian_test(){
		
		double A, x0, y0, sigmaX, sigmaY;
		A = 1.0;
		x0 = 0;
		y0 = 0;
		sigmaX = 100;
		sigmaY = 100;
		
		BiFunction<Integer, Integer, Double> b = ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY);
		
		for(int i=0; i<100; i++)
			for(int j=0; j<100; j++)
				System.out.println("(" + i + ", " + j + ") : " + b.apply(i, j));
	}
	
	private static void Harris_Stephens_test(){
		long start1 = System.currentTimeMillis();
		/* Gaussian param */
		double A, x0, y0, sigmaX, sigmaY;
		
		
		int x = img.length;
		int y = img[0].length;
		Complex[][] I = new Complex[x][y];
		for(int i=0; i<x; i++)
			for(int j=0; j<y; j++)
				I[i][j] = Complex.cartesian(img[i][j]);
		
		A = 1.0;
		x0 = 0;
		y0 = 0;
		sigmaX = x;
		sigmaY = y;
		Complex[][] R = Harris_Stephens.ImageR(
				I, 
				Prewitt.instance().x_right_kernel(), 
				ImageDerivative.Gaussian(A, x0, y0, sigmaX, sigmaY), 
				Complex.cartesian(0.0)
				);
		long end = System.currentTimeMillis();
		Util.pf("total : %d ms", end - start);
		Util.pf("filter : %d ms", end - start1);
		
		//Util.pl(Util.arr_s(R, " "));
		
		Util.write(R, "test1.png");
	}
	
	private static void test1(){
		int x = img.length;
		int y = img[0].length;
		Complex[][] I = new Complex[x][y];
		for(int i=0; i<x; i++)
			for(int j=0; j<y; j++)
				I[i][j] = Complex.cartesian(img[i][j]);

		long start1 = System.currentTimeMillis();
		Complex[][] J;
		Util.pl("\ndx:");
		J = Prewitt.instance().dx(I);
		//Util.pl(Util.arr_s(J, "\t"));
		//Util.write(J, String.format(imgname_dx, X));
		//Util.write(J, "Prewitt_dx.jpg");

		//Util.pf("filter : %d ms\n", System.currentTimeMillis() - start1);
		
		Util.pl("\ndy:");
		J = Prewitt.instance().dy(I);
		//Util.pl(Util.arr_s(J, "\t"));
		//Util.write(J, String.format(imgname_dy, X));
		//Util.write(J, "Prewitt_dy.jpg");
		
		Util.pl("\nd:");
		//J = Prewitt.instance().d(I);
		//Util.pl(Util.arr_s(J, "\t"));
		long end = System.currentTimeMillis();
		Util.pf("total : %d ms", end - start);
		Util.pf("filter : %d ms", end - start1);
	}
}
