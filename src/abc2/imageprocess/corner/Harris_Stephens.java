package abc2.imageprocess.corner;

import abc2.imageprocess.corner.filter.ImageDerivative;
import abc2.imageprocess.corner.filter.Prewitt;
import abc2.imageprocess.filters.ImageFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import struct.Complex;
import struct.Data;
import struct.MathTools;
import struct.Matrix;
import abc2.util.Util;

public class Harris_Stephens{
	
	/**
	 * 
	 * @param image 
	 * @param derivative_kernel 
	 * @param w window function
	 * @return Data{a, b, S}
	 */
	public static Data processImageEigen(Complex[][] image, 
			Complex[][] derivative_kernel, 
			BiFunction<Integer, Integer, Double> w
			){
		int row_l, col_l;
		Complex[][] Ix, Iy;
		Complex[][] st;
		Complex ix, iy, window;
		Complex[] eigen;
		
		Double[] datum;
		List<Double[]> data = new ArrayList<Double[]>();
		
		col_l = image.length;
		row_l = image[0].length;
		
	
		Ix = ImageDerivative.derivative(image, derivative_kernel);
		Iy = ImageDerivative.derivative(image, Matrix.transpose(derivative_kernel));
		for(int v=0; v < col_l; v++){
			for(int u=0; u < row_l; u++){
				st = structure_tensor(Ix, Iy, u, v, w);
				eigen = Matrix.eigen(st);
								
				datum = new Double[]{ eigen[0].Re, eigen[1].Re };
				data.add(datum);
				//
				//ret[v][u] = R(st, k);
			}
		}

		return MathTools.linear_regression_R2(data);
	}
	
	/**
	 * 
	 * @param image 
	 * @param derivative_kernel 
	 * @param w : window function
	 * @param k : a factor as a signum
	 * @return Data{a, b, S}
	 */
	public static Data processImageR(
			Complex[][] image, 
			Complex[][] derivative_kernel, 
			BiFunction<Integer, Integer, Double> w, 
			Complex k
			){
		int row_l, col_l;
		Complex[][] Ix, Iy;
		Complex[][] st;
		Complex ix, iy, window;
		
		col_l = image.length;
		row_l = image[0].length;
		
		Double[] datum;
		List<Double[]> data = new ArrayList<Double[]>();
		
		//ret = new Complex[col_l][row_l];
		
		Ix = ImageDerivative.derivative(image, derivative_kernel);
		Iy = ImageDerivative.derivative(image, Matrix.transpose(derivative_kernel));
		
		for(int v=0; v < col_l; v++){
			for(int u=0; u < row_l; u++){
				st = structure_tensor(Ix, Iy, u, v, w);
				
				if(R(st, k).Re != 0){
					//Util.pl("(" + u + ", " + v + ")");
					datum = new Double[]{ u * 1.0 , v * 1.0 };
					data.add(datum);
				}
				//
				//ret[v][u] = R(st, k);
			}
		}

		//Util.pl("\ncount : " + data.size());
		//Data ret = MathTools.linear_regression(data);
		Data ret = MathTools.inverse_linear_regression(data);
		
		return ret;
	}
	
	
	public static Data[] forestImageR(
			Complex[][] image, 
			Complex[][] derivative_kernel, 
			BiFunction<Integer, Integer, Double> w, 
			Complex k
			){
		int row_l, col_l;
		Complex[][] Ix, Iy;
		Complex[][] st;
		Complex ix, iy, window;
		
		col_l = image.length;
		row_l = image[0].length;
		
		Double[] datum;
		List<Double[]> data = new ArrayList<Double[]>();
		
		//ret = new Complex[col_l][row_l];
		
		Ix = ImageDerivative.derivative(image, derivative_kernel);
		Iy = ImageDerivative.derivative(image, Matrix.transpose(derivative_kernel));
		
		for(int v=0; v < col_l; v++){
			for(int u=0; u < row_l; u++){
				st = structure_tensor(Ix, Iy, u, v, w);
				
				if(R(st, k).Re != 0){
					//Util.pl("(" + u + ", " + v + ")");
					datum = new Double[]{ u * 1.0 , v * 1.0 };
					data.add(datum);
				}
				//
				//ret[v][u] = R(st, k);
			}
		}

		//Util.pl("\ncount : " + data.size());
		Data[] ret = {MathTools.linear_regression_R2(data),
				MathTools.inverse_linear_regression(data)};
		
		return ret;
	}
	
	
	/**
	 * 
	 * @param image 
	 * @param derivative_kernel 
	 * @param w window function
	 * @return 2d_Complex array with all the R values.
	 */
	public static Complex[][] ImageR(
			Complex[][] image, 
			Complex[][] derivative_kernel, 
			BiFunction<Integer, Integer, Double> w, 
			Complex k
			){
		Complex[][] ret;
		int row_l, col_l;
		Complex[][] Ix, Iy;
		Complex[][] st;
		Complex ix, iy, window;
		
		col_l = image.length;
		row_l = image[0].length;
		
		ret = new Complex[col_l][row_l];
		
		Ix = ImageDerivative.derivative(image, derivative_kernel);
		Iy = ImageDerivative.derivative(image, Matrix.transpose(derivative_kernel));
		Util.pl(k);
		for(int v=0; v < col_l; v++){
			for(int u=0; u < row_l; u++){
				st = structure_tensor(Ix, Iy, u, v, w);
				//System.out.println(st);
				if(R(st, k).Re != 0){
					Util.pl("(" + u + ", " + v + ")");
				}
				ret[v][u] = R(st, k);
			}
		}
				
		return ret;
	}
	
	/**
	 * no bounds check
	 * @param image image represented as complex[][]
	 * @param u pixel row
	 * @param v pixel col
	 * @param w window function
	 * @return the 2 x 2 matrix on which we test the eigenvalues or R value
	 */
	public static Complex[][] structure_tensor(Complex[][] Ix, Complex[][] Iy, int u, int v, BiFunction<Integer, Integer, Double> w){
		Complex[][] ret;
		Complex ix, iy, window;
		
		ret = new Complex[2][2];
		
		window = Complex.cartesian(w.apply(u, v));
		ix = Ix[v][u];
		iy = Iy[v][u];
		ret[0][0] = ix.mult(ix).mult(window);
		ret[0][1] = ix.mult(iy).mult(window);
		ret[1][0] = ix.mult(iy).mult(window);
		ret[1][1] = iy.mult(iy).mult(window);
				
		return ret;
	}
	
	/**
	 * R is positive in the corner region, negative in the edge regions,
	 * 	 and small in Hit flat region.
	 * @param structure_tensor 
	 * @param k
	 * @return R value
	 */
	public static Complex R(Complex[][] structure_tensor, Complex k){
		Complex trace, determinant;
		trace = structure_tensor[0][0].add(structure_tensor[1][1]);
		determinant = structure_tensor[0][0].mult(structure_tensor[1][1]).sub(	structure_tensor[0][1].mult(structure_tensor[1][0])	);
		
		return determinant.sub(k.mult(trace.mult(trace)));
	}
	
	/*
	public static Complex[][] structure_tensor(Complex[][] Ix, Complex[][] Iy,  BiFunction<Integer, Integer, Double> w){
		int u, v;
		Complex[][] ret;
		Complex weight;
		
		ret = new Complex[2][2];
		ret[0][0] = ret[0][1] = ret[1][0] = ret[1][1] = Complex.cartesian(0);
		
		u = Ix.length;
		v = Ix[0].length;
		for(int j=0; j<u; j++){
			for(int i=0; i<v; i++){
				weight = Complex.cartesian(w.apply(u, v));
				ret[0][0] = ret[0][0].add(Ix[u][v].mult(Ix[u][v]).mult(weight));
				ret[0][1] = ret[0][1].add(Ix[u][v].mult(Iy[u][v]).mult(weight));
				ret[1][0] = ret[1][0].add(Ix[u][v].mult(Iy[u][v]).mult(weight));
				ret[1][1] = ret[1][1].add(Iy[u][v].mult(Iy[u][v]).mult(weight));
			}
		}
		
		return ret;
	}
	*/
	/**
	 * return 
	 * @param Ix
	 * @param Iy
	 * @param w
	 * @return Complex[] = {eigen1, eigen2};
	 */
	/*
	public static Complex[] eigen(Complex[][] Ix, Complex[][] Iy,  BiFunction<Integer, Integer, Double> w){
		Complex[][] st;
		Complex[] eigen, ret;
		
		st = structure_tensor(Ix, Iy, w);
		eigen = Matrix.eigen(st);
		ret = new Complex[2];
		ret[0] = eigen[0].add(eigen[1]);
		ret[1] = eigen[0].add(eigen[1]);
		
		return ret;
	}
	*/
}
