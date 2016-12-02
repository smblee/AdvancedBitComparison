package abc2.corner.filter;
import abc2.filters.ImageFilter;
import abc2.struct.Complex;
import abc2.util.Util;

public class ABC implements CornerFilter{
	private static Object o = new Object();
	private static ABC singleton = null;
	private ABC(){
		init_abc_kernel();
	}
	
	public static ABC instance(){
		if(singleton == null)
			singleton = new ABC();
		return singleton;
	}
	
	public Complex[][] dx(Complex[][] I){
		Complex[][] J, K, ret;
		
		J = dx_down(I);
		K = dx_up(I);
		
		ret = new Complex[J.length][J[0].length];
		for(int j=0; j<J.length; j++){
			for(int i=0; i<J[0].length; i++){
				ret[j][i] = (J[j][i].Re > K[j][i].Re) ? 
						J[j][i] : K[j][i];
			}
		}
		
		return ret;
	}
	
	public Complex[][] dy(Complex[][] I){
		Complex[][] J, K, ret;
		
		J = dy_left(I);
		K = dy_right(I);
		
		ret = new Complex[J.length][J[0].length];
		for(int j=0; j<J.length; j++){
			for(int i=0; i<J[0].length; i++){
				ret[j][i] = (J[j][i].Re > K[j][i].Re) ? 
						J[j][i] : K[j][i];
			}
		}
		
		return ret;
	}
	
	public Complex[][] dx_down(Complex[][] I){
		Util.pl(Util.arr_s(abc_x_down_kernel, " "));
		return mask(abc_x_down_kernel, I);
	}
	
	public Complex[][] dx_up(Complex[][] I){
		Util.pl(Util.arr_s(abc_x_up_kernel, " "));
		return mask(abc_x_up_kernel, I);
	}
	

	public Complex[][] dy_left(Complex[][] I){
		Util.pl(Util.arr_s(abc_y_left_kernel, " "));
		return mask(abc_y_left_kernel, I);
	}
	

	public Complex[][] dy_right(Complex[][] I){
		Util.pl(Util.arr_s(abc_y_right_kernel, " "));
		return mask(abc_y_right_kernel, I);
	}
	
	public Complex[][] d(Complex[][] I){
		Complex[][] dx_I, dy_I, ret;
		Complex X, Y;
		int row, col;

		dx_I = dx_down(I);
		dy_I = dy_right(I);
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
	
	//kernel
	private static Complex[][] 
			abc_y_left_kernel = null,
			abc_y_right_kernel = null,
			abc_x_up_kernel = null,
			abc_x_down_kernel = null;
	private static void init_abc_kernel(){
		Complex neg_one = Complex.cartesian(-1, 0);
		Complex zero = Complex.cartesian(0, 0);
		Complex one = Complex.cartesian(1, 0);
		if(abc_y_right_kernel == null){
			abc_y_right_kernel = new Complex[3][3];
			abc_y_right_kernel[0][0] = abc_y_right_kernel[1][0] = abc_y_right_kernel[2][0] = neg_one;
			abc_y_right_kernel[0][1] = abc_y_right_kernel[1][1] = abc_y_right_kernel[2][1] = zero;
			abc_y_right_kernel[0][2] = abc_y_right_kernel[1][2] = abc_y_right_kernel[2][2] = one;

		}

		if(abc_y_left_kernel == null){
			abc_y_left_kernel = new Complex[3][3];
			abc_y_left_kernel[0][0] = abc_y_left_kernel[1][0] = abc_y_left_kernel[2][0] = one;
			abc_y_left_kernel[0][1] = abc_y_left_kernel[1][1] = abc_y_left_kernel[2][1] = zero;
			abc_y_left_kernel[0][2] = abc_y_left_kernel[1][2] = abc_y_left_kernel[2][2] = neg_one;

		}
		
		if(abc_x_down_kernel == null){
			abc_x_down_kernel = new Complex[3][3];
			abc_x_down_kernel[0][0] = abc_x_down_kernel[0][1] = abc_x_down_kernel[0][2] = neg_one;
			abc_x_down_kernel[1][0] = abc_x_down_kernel[1][1] = abc_x_down_kernel[1][2] = zero;
			abc_x_down_kernel[2][0] = abc_x_down_kernel[2][1] = abc_x_down_kernel[2][2] = one;
		}
		if(abc_x_up_kernel == null){
			abc_x_up_kernel = new Complex[3][3];
			abc_x_up_kernel[0][0] = abc_x_up_kernel[0][1] = abc_x_up_kernel[0][2] = one;
			abc_x_up_kernel[1][0] = abc_x_up_kernel[1][1] = abc_x_up_kernel[1][2] = zero;
			abc_x_up_kernel[2][0] = abc_x_up_kernel[2][1] = abc_x_up_kernel[2][2] = neg_one;
		}
	}
}
