package abc2.fourier;
import java.util.*;
import java.math.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/*
interface Fourier_i{
	public static double[][] dft(double[][] x);
	public static double[][][] dft(int[][] img, boolean normalize);
	public static void show(double[][][] two_dim_dft);
	public static void show(double[][] nums);
}
*/

public class Fourier{
	static boolean DEBUG = true;
	
	public static double[][] dft(double[][] x){
		int n = x.length;
		double[][] X = new double[n][2];
		
		for(int k=0; k<n; k++){
			double real_sum = 0.0;
			double imag_sum = 0.0;
			for(int t=0; t<n; t++){
				double real, imag;
				real = x[t][Re] * Math.cos(Math.PI * 2 * t * k / n)
					+ x[t][Im] * Math.sin(Math.PI * 2 * t * k / n);
				imag = -1 * x[t][Re] * Math.sin(Math.PI * 2 * t * k / n)
					+ x[t][Im] * Math.cos(Math.PI * 2 * t * k / n);

				real_sum += real;
				imag_sum += imag;
			}
			X[k][Re] = real_sum;
			X[k][Im] = imag_sum;
		}

		return X;
	}

	/**
	 * Assuming input is NxN img with 1 as white and 0 as black.
	 * @return double array of complex number represented as double[2]; double[Height][Width][2]
	 */
	public static double[][][] dft(int[][] img, boolean normalize){
		int Height = img.length;
		int Width = img[0].length;
		double[][][] ret = new double[Height][Width][2];

		for(int u=0; u<Height; u++){
			for(int v=0; v<Width; v++){
				ret[u][v] = slot(img, u, v, normalize);
			}
		}

		return ret;
	}

	/**
	 * calculate the phase of an image
	 * @return an double[][] with all the phases
	 */
	public static double[][] phase(double[][][] transformed){
		int Height = transformed.length;
		int Width = transformed[0].length;
		double[][] ret = new double[Height][Width];

		for(int i=0; i< Height; i++){
			for(int j=0; j< Height; j++){
				double[] num = transformed[i][j];
				ret[i][j] = Math.atan(num[Im] / num[Re]);
			}
		}

		return ret;
	}

	/**
	 * calculate the magnitude of an image
	 * @return an double[][] with all the magnitudes
	 */
	public static double[][] magnitude(double[][][] transformed){
		int Height = transformed.length;
		int Width = transformed[0].length;
		double[][] ret = new double[Height][Width];

		for(int i=0; i< Height; i++){
			for(int j=0; j< Height; j++){
				double[] num = transformed[i][j];
				ret[i][j] = Math.sqrt(num[Im] * num[Im] + num[Re] * num[Re]);
			}
		}

		return ret;
	}


	static final int Mag = 0;
	static final int Phs = 1;
	/**
	 * calculate the magnitude and phase of an image
	 * @return double[][][]
	 */
	public static double[][][] info(double[][][] transformed){
		Object[] ret = new Object[2];
		ret[Mag] = magnitude(transformed);
		ret[Phs] = phase(transformed);

		return (double[][][]) ret;
	}

	/**
	 * Assume image NxN;
	 * @return double[2] represented complex number
	 */
	private static double[] slot(int[][] img, int u, int v, boolean normalize){
		int N = img.length;
		double real_sum = 0.0;
		double imag_sum = 0.0;
		for(int x=0; x<img.length; x++){
			for(int y=0; y<img[0].length; y++){
				double b = 2 * Math.PI * (u * x + v * y) / N;
				double real = 1.0 * img[x][y] * Math.cos(b);
				double imag =-1.0 * img[x][y] * Math.sin(b);
				real_sum += real;
				imag_sum += imag;
			}
		}

		if(normalize){
			real_sum = real_sum / N;//(N * N);
			imag_sum = imag_sum / N; //(N * N);
		}

		double[] ret = {real_sum, imag_sum};
		return ret;
	}

	static final int Im = 1;
	static final int Re = 0;
	static Scanner sc;
	public static void main(String[] args){
		/*
		if(args.length < 1){
			pf("no image name found");
			return;
		}

		BufferedImage bi = RW.read(args[0]);
		int[][] img = RW.extractRGB(bi);
		*/

		/*
		ArrayList<int[]> list = new ArrayList<int[]>();
		sc = new Scanner(System.in);
		while(sc.hasNextLine()){
			String[] w = sc.nextLine().split(" ");
			int[] arr = new int[w.length];
			for(int i=0; i<w.length; i++){
				arr[i] = Integer.parseInt(w[i]);
			}
			list.add(arr);
		}

		int[][] img = list.toArray(new int[0][0]);
		for(int[] a: img){
			for(int b: a){
				System.out.print(b + " ");
			}
			System.out.print('\n');
		}

		show(dft(img, true));
		pl("----");
		show(dft(img, false));
		*/

		int[][] X = new int[1][4];
		X[0][0] = 1;
		X[0][1] = 2;
		X[0][2] = 3;
		X[0][3] = 4;
		show(dft(X, false));
		show(dft(X, false));
		
	}

	//
	public static void one_dim_dft_test(){
		sc = new Scanner(System.in);
		ArrayList<double[]> inputs = new ArrayList<double[]>();

		while(sc.hasNextDouble()){
			double[] c_num = new double[2];
			c_num[Re] = sc.nextDouble();
			c_num[Im] = sc.nextDouble();
			inputs.add(c_num);
		}

		double[][] x = (double[][]) inputs.toArray();
		double[][] X = dft(x);

		show(x);
		pl("----");
		show(X);
	}

	/**
	 * show output from 2 dimensional dft
	 */
	public static void show(double[][][] two_dim_dft){
		if(two_dim_dft[0][0].length != 2){
			pf("wrong size");
			return;
		}

		db(two_dim_dft.length);
		db(two_dim_dft[0].length);
		db(two_dim_dft[0][0].length);
		for(double[][] nums: two_dim_dft){
			show(nums);
			p("\n");
		}
	}
	/**
	 * show must take  N x 2 sized double[][];
	 */
	public static void show(double[][] nums){
		if(nums[0].length != 2){
			pf("wrong size");
			return;
		}

		for(double[] num: nums){
			System.out.printf("%f+%fi ", num[Re], num[Im]);
		}
	}

	//----------------------------------------------------------------------------
	public static boolean isalphabet(char c){
		return upper(c) || lower(c);
	}

	public static boolean upper(char c){
		return (c <= 'Z' && c >= 'A');
	}

	public static boolean lower(char c){
		return (c <= 'z' && c >= 'a');
	}

	public static BigInteger b(String val){
		return new BigInteger(val);
	}

	public static void p(Object o){
		System.out.print(o);
	}

	public static void pf(String format, Object... os){
		System.out.printf(format, os);
	}

	public static void pl(Object o){
		System.out.println(o);
	}

	public static void db(Object s, Object ... o){
		if(DEBUG)
			System.out.printf(s + "\n", o);
	}

	public static String arr_s(Object[] arr, String s){
		StringBuilder sb = new StringBuilder();
		for(Object o: arr){
			sb.append(o.toString());
			sb.append(s);
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
