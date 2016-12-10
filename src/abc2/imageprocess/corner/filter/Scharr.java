package abc2.imageprocess.corner.filter;

import abc2.struct.Complex;

public class Scharr {
	private static Object o = new Object();
	private static Scharr singleton = null;
	private Scharr (){
		init_scharr_kernel();
	}
	
	public static Scharr instance(){
		if(singleton == null)
			singleton = new Scharr();
		return singleton;
	}
	
	
	/* Initialize Kernel */
	//kernel
	private static Complex[][] 
			sobel_x_left_kernel = null, 
			sobel_x_right_kernel = null,
			sobel_y_up_kernel = null, 
			sobel_y_down_kernel = null;
	private static void init_scharr_kernel(){
		Complex neg_ten = Complex.cartesian(-10, 0);
		Complex ten = Complex.cartesian(10, 0);
		Complex neg_three = Complex.cartesian(-3, 0);
		Complex zero = Complex.cartesian(0, 0);
		Complex three = Complex.cartesian(3, 0);
		if(sobel_x_right_kernel == null){
			sobel_x_right_kernel = new Complex[3][3];
			
			sobel_x_right_kernel[0][0] = sobel_x_right_kernel[2][0] = neg_three;
			sobel_x_right_kernel[1][0] = neg_ten;
			sobel_x_right_kernel[0][1] = sobel_x_right_kernel[1][1] = sobel_x_right_kernel[2][1] = zero;
			sobel_x_right_kernel[0][2] = sobel_x_right_kernel[2][2] = three;
			sobel_x_right_kernel[1][2] = ten;

		}

		if(sobel_x_left_kernel == null){
			sobel_x_left_kernel = new Complex[3][3];
			sobel_x_left_kernel[0][0] = sobel_x_left_kernel[2][0] = three;
			sobel_x_left_kernel[1][0] = ten;
			sobel_x_left_kernel[0][1] = sobel_x_left_kernel[1][1] = sobel_x_left_kernel[2][1] = zero;
			sobel_x_left_kernel[0][2] = sobel_x_left_kernel[2][2] = neg_three;
			sobel_x_left_kernel[1][2] = neg_ten;
		}
		
		if(sobel_y_down_kernel == null){
			sobel_y_down_kernel = new Complex[3][3];
			sobel_y_down_kernel[0][0] = sobel_y_down_kernel[0][2] = neg_three;
			sobel_y_down_kernel[0][1] = neg_ten;
			sobel_y_down_kernel[1][0] = sobel_y_down_kernel[1][1] = sobel_y_down_kernel[1][2] = zero;
			sobel_y_down_kernel[2][0] = sobel_y_down_kernel[2][2] = three;
			sobel_y_down_kernel[2][1] = ten;
		}
		if(sobel_y_up_kernel == null){
			sobel_y_up_kernel = new Complex[3][3];
			sobel_y_up_kernel[0][0] = sobel_y_up_kernel[0][2] = three;
			sobel_y_up_kernel[0][1] = ten;
			sobel_y_up_kernel[1][0] = sobel_y_up_kernel[1][1] = sobel_y_up_kernel[1][2] = zero;
			sobel_y_up_kernel[2][0] = sobel_y_up_kernel[2][2] = neg_three;
			sobel_y_up_kernel[2][1] = neg_ten;
		}
	}
	
	/**
	 * -3  0 3\n
	 * -10 0 10\n
	 * -3  0 3\n
	 * @return
	 */
	public Complex[][] x_right_kernel()	{	return sobel_x_right_kernel;		}
	
	/**
	 * 3  0 -3\n
	 * 10 0 -10\n
	 * 3  0 -3\n
	 * @return
	 */
	public Complex[][] x_left_kernel()	{	return sobel_x_left_kernel; 	}
	
	/**
	 *  3  10  3\n
	 *  0   0  0\n
	 * -3 -10 -3\n
	 * @return
	 */
	public Complex[][] y_up_kernel()	{	return sobel_y_up_kernel;	}
	
	/**
	 * -3 -10 -3\n
	 *  0  0  0\n
	 *  3  10  3\n
	 * @return
	 */
	public Complex[][] y_down_kernel()	{	return sobel_y_down_kernel; 	}
}
