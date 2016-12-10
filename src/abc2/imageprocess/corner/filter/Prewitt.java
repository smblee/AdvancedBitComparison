package abc2.imageprocess.corner.filter;

import abc2.imageprocess.filters.ImageFilter;

import java.util.function.BiFunction;

import abc2.struct.Complex;
import abc2.util.Util;

public class Prewitt implements CornerFilter{
	private static Object o = new Object();
	private static Prewitt singleton = null;
	private Prewitt(){
		init_prewitt_kernel();
	}
	
	public static Prewitt instance(){
		if(singleton == null)
			singleton = new Prewitt();
		return singleton;
	}
	
	
	/* Old derivative methods */
	/**
	 * Old
	 */
	public Complex[][] dx(Complex[][] I){
		Complex[][] J, K, ret;
		
		J = dy_down(I);
		K = dy_up(I);
		
		ret = new Complex[J.length][J[0].length];
		for(int j=0; j<J.length; j++){
			for(int i=0; i<J[0].length; i++){
				ret[j][i] = (J[j][i].Re > K[j][i].Re) ? 
						J[j][i] : K[j][i];
			}
		}
		
		return ret;
	}
	
	/**
	 * Old
	 */
	public Complex[][] dy(Complex[][] I){
		Complex[][] J, K, ret;
		
		J = dx_left(I);
		K = dx_right(I);
		
		ret = new Complex[J.length][J[0].length];
		for(int j=0; j<J.length; j++){
			for(int i=0; i<J[0].length; i++){
				ret[j][i] = (J[j][i].Re > K[j][i].Re) ? 
						J[j][i] : K[j][i];
			}
		}
		
		return ret;
	}
	
	/**
	 * Old
	 */
	public Complex[][] dy_down(Complex[][] I){
		Util.pl(Util.arr_s(prewitt_y_down_kernel, " "));
		return ImageFilter.mask(prewitt_y_down_kernel, I);
	}
	
	/**
	 * Old
	 */
	public Complex[][] dy_up(Complex[][] I){
		Util.pl(Util.arr_s(prewitt_y_up_kernel, " "));
		return ImageFilter.mask(prewitt_y_up_kernel, I);
	}
	
	/**
	 * Old
	 */
	public Complex[][] dx_left(Complex[][] I){
		Util.pl(Util.arr_s(prewitt_x_left_kernel, " "));
		return ImageFilter.mask(prewitt_x_left_kernel, I);
	}
	
	/**
	 * Old
	 */
	public Complex[][] dx_right(Complex[][] I){
		Util.pl(Util.arr_s(prewitt_x_right_kernel, " "));
		return ImageFilter.mask(prewitt_x_right_kernel, I);
	}
	
	/**
	 * Old
	 */
	public Complex[][] d(Complex[][] I){
		Complex[][] dx_I, dy_I, ret;
		Complex X, Y;
		int row, col;

		dx_I = dy_down(I);
		dy_I = dx_right(I);
		row = dx_I.length;
		col = dx_I[0].length;
		ret = new Complex[row][col];

		for(int i=0; i<dx_I.length; i++){
			for(int j=0; j<dx_I[0].length; j++){
				X = dx_I[i][j];
				Y = dy_I[i][j];
				ret[i][j] = X.mult(X).add(Y.mult(Y));
				ret[i][j] = ret[i][j].sqrt();
			}
		}
		return ret;
	}
	
	/* Initialize Kernel */
	//kernel
	private static Complex[][] 
			prewitt_x_left_kernel = null, 
			prewitt_x_right_kernel = null,
			prewitt_y_up_kernel = null, 
			prewitt_y_down_kernel = null;
	private static void init_prewitt_kernel(){
		Complex neg_one = Complex.cartesian(-1, 0);
		Complex zero = Complex.cartesian(0, 0);
		Complex one = Complex.cartesian(1, 0);
		if(prewitt_x_right_kernel == null){
			prewitt_x_right_kernel = new Complex[3][3];
			prewitt_x_right_kernel[0][0] = prewitt_x_right_kernel[1][0] = prewitt_x_right_kernel[2][0] = neg_one;
			prewitt_x_right_kernel[0][1] = prewitt_x_right_kernel[1][1] = prewitt_x_right_kernel[2][1] = zero;
			prewitt_x_right_kernel[0][2] = prewitt_x_right_kernel[1][2] = prewitt_x_right_kernel[2][2] = one;

		}

		if(prewitt_x_left_kernel == null){
			prewitt_x_left_kernel = new Complex[3][3];
			prewitt_x_left_kernel[0][0] = prewitt_x_left_kernel[1][0] = prewitt_x_left_kernel[2][0] = one;
			prewitt_x_left_kernel[0][1] = prewitt_x_left_kernel[1][1] = prewitt_x_left_kernel[2][1] = zero;
			prewitt_x_left_kernel[0][2] = prewitt_x_left_kernel[1][2] = prewitt_x_left_kernel[2][2] = neg_one;

		}
		
		if(prewitt_y_down_kernel == null){
			prewitt_y_down_kernel = new Complex[3][3];
			prewitt_y_down_kernel[0][0] = prewitt_y_down_kernel[0][1] = prewitt_y_down_kernel[0][2] = neg_one;
			prewitt_y_down_kernel[1][0] = prewitt_y_down_kernel[1][1] = prewitt_y_down_kernel[1][2] = zero;
			prewitt_y_down_kernel[2][0] = prewitt_y_down_kernel[2][1] = prewitt_y_down_kernel[2][2] = one;
		}
		if(prewitt_y_up_kernel == null){
			prewitt_y_up_kernel = new Complex[3][3];
			prewitt_y_up_kernel[0][0] = prewitt_y_up_kernel[0][1] = prewitt_y_up_kernel[0][2] = one;
			prewitt_y_up_kernel[1][0] = prewitt_y_up_kernel[1][1] = prewitt_y_up_kernel[1][2] = zero;
			prewitt_y_up_kernel[2][0] = prewitt_y_up_kernel[2][1] = prewitt_y_up_kernel[2][2] = neg_one;
		}
	}
	
	/**
	 * -1 0 1\n
	 * -1 0 1\n
	 * -1 0 1\n
	 * @return
	 */
	public Complex[][] x_right_kernel()	{	return prewitt_x_right_kernel;		}
	
	/**
	 * 1 0 -1\n
	 * 1 0 -1\n
	 * 1 0 -1\n
	 * @return
	 */
	public Complex[][] x_left_kernel()	{	return prewitt_x_left_kernel; 	}
	
	/**
	 *  1  1  1\n
	 *  0  0  0\n
	 * -1 -1 -1\n
	 * @return
	 */
	public Complex[][] y_up_kernel()	{	return prewitt_y_up_kernel;	}
	
	/**
	 * -1 -1 -1\n
	 *  0  0  0\n
	 *  1  1  1\n
	 * @return
	 */
	public Complex[][] y_down_kernel()	{	return prewitt_y_down_kernel; 	}
}
