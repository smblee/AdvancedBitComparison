package abc2.imageprocess.corner.filter;

import abc2.struct.Complex;

public class Sobel {
	private static Object o = new Object();
	private static Sobel singleton = null;
	private Sobel(){
		init_sobel_kernel();
	}
	
	public static Sobel instance(){
		if(singleton == null)
			singleton = new Sobel();
		return singleton;
	}
	
	
	/* Initialize Kernel */
	//kernel
	private static Complex[][] 
			sobel_x_left_kernel = null, 
			sobel_x_right_kernel = null,
			sobel_y_up_kernel = null, 
			sobel_y_down_kernel = null;
	private static void init_sobel_kernel(){
		Complex neg_two = Complex.cartesian(-2, 0);
		Complex two = Complex.cartesian(2, 0);
		Complex neg_one = Complex.cartesian(-1, 0);
		Complex zero = Complex.cartesian(0, 0);
		Complex one = Complex.cartesian(1, 0);
		if(sobel_x_right_kernel == null){
			sobel_x_right_kernel = new Complex[3][3];
			
			sobel_x_right_kernel[0][0] = sobel_x_right_kernel[2][0] = neg_one;
			sobel_x_right_kernel[1][0] = neg_two;
			sobel_x_right_kernel[0][1] = sobel_x_right_kernel[1][1] = sobel_x_right_kernel[2][1] = zero;
			sobel_x_right_kernel[0][2] = sobel_x_right_kernel[2][2] = one;
			sobel_x_right_kernel[1][2] = two;

		}

		if(sobel_x_left_kernel == null){
			sobel_x_left_kernel = new Complex[3][3];
			sobel_x_left_kernel[0][0] = sobel_x_left_kernel[2][0] = one;
			sobel_x_left_kernel[1][0] = two;
			sobel_x_left_kernel[0][1] = sobel_x_left_kernel[1][1] = sobel_x_left_kernel[2][1] = zero;
			sobel_x_left_kernel[0][2] = sobel_x_left_kernel[2][2] = neg_one;
			sobel_x_left_kernel[1][2] = neg_two;
		}
		
		if(sobel_y_down_kernel == null){
			sobel_y_down_kernel = new Complex[3][3];
			sobel_y_down_kernel[0][0] = sobel_y_down_kernel[0][2] = neg_one;
			sobel_y_down_kernel[0][1] = neg_two;
			sobel_y_down_kernel[1][0] = sobel_y_down_kernel[1][1] = sobel_y_down_kernel[1][2] = zero;
			sobel_y_down_kernel[2][0] = sobel_y_down_kernel[2][2] = one;
			sobel_y_down_kernel[2][1] = two;
		}
		if(sobel_y_up_kernel == null){
			sobel_y_up_kernel = new Complex[3][3];
			sobel_y_up_kernel[0][0] = sobel_y_up_kernel[0][2] = one;
			sobel_y_up_kernel[0][1] = two;
			sobel_y_up_kernel[1][0] = sobel_y_up_kernel[1][1] = sobel_y_up_kernel[1][2] = zero;
			sobel_y_up_kernel[2][0] = sobel_y_up_kernel[2][2] = neg_one;
			sobel_y_up_kernel[2][1] = neg_two;
		}
	}
	
	/**
	 * -1 0 1\n
	 * -2 0 2\n
	 * -1 0 1\n
	 * @return
	 */
	public Complex[][] x_right_kernel()	{	return sobel_x_right_kernel;		}
	
	/**
	 * 1 0 -1\n
	 * 2 0 -2\n
	 * 1 0 -1\n
	 * @return
	 */
	public Complex[][] x_left_kernel()	{	return sobel_x_left_kernel; 	}
	
	/**
	 *  1  2  1\n
	 *  0  0  0\n
	 * -1 -2 -1\n
	 * @return
	 */
	public Complex[][] y_up_kernel()	{	return sobel_y_up_kernel;	}
	
	/**
	 * -1 -2 -1\n
	 *  0  0  0\n
	 *  1  2  1\n
	 * @return
	 */
	public Complex[][] y_down_kernel()	{	return sobel_y_down_kernel; 	}
}
