package abc2.imageprocess.corner.filter;

import abc2.imageprocess.struct.Complex;

public class RobertsCross {
	private static Object o = new Object();
	private static RobertsCross singleton = null;
	private RobertsCross(){
		init_kernel();
	}
	
	public static RobertsCross instance(){
		if(singleton == null)
			singleton = new RobertsCross();
		return singleton;
	}
	
	
	/* Initialize Kernel */
	//kernel
	private static Complex[][] 
			x_kernel = null,
			y_kernel = null;
	private static void init_kernel(){
		Complex neg_one = Complex.cartesian(-1, 0);
		Complex zero = Complex.cartesian(0, 0);
		Complex one = Complex.cartesian(1, 0);
		if(x_kernel == null){
			x_kernel = new Complex[2][2];
			x_kernel[0][0] = one;
			x_kernel[0][1] = zero;
			x_kernel[1][0] = zero;
			x_kernel[0][0] = neg_one;
		}

		if(y_kernel == null){
			y_kernel = new Complex[2][2];
			y_kernel[0][0] = zero;
			y_kernel[0][1] = one;
			y_kernel[1][0] = neg_one;
			y_kernel[0][0] = zero;
		}
	}
	
	/**
	 * 1  0 \n
	 * 0 -1 \n
	 * @return
	 */
	public Complex[][] x_kernel()	{	return x_kernel;		}
	

	/**
	 * 0  1 \n
	 * -1 0 \n
	 * @return
	 */
	public Complex[][] y_kernel()	{	return y_kernel;		}
}
