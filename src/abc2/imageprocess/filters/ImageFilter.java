package abc2.imageprocess.filters;

import abc2.struct.Complex;
import abc2.util.Matrix;
import abc2.util.Util;

public interface ImageFilter {	

	public Complex[][] x_right_kernel();
	public Complex[][] x_left_kernel();
	public Complex[][] y_up_kernel();
	public Complex[][] y_down_kernel();
	
	/* Deprecated methods */
	/*
	public Complex[][] dy_up(Complex[][] I);
	public Complex[][] dy_down(Complex[][] I);
	public Complex[][] dx_left(Complex[][] I);
	public Complex[][] dx_right(Complex[][] I);
	
	public Complex[][] dx(Complex[][] I);
	public Complex[][] dy(Complex[][] I);
	
	public Complex[][] d(Complex[][] I);
	*/
	
	/**
	 * mask an image I with masking kernel
	 * @param kernel : masking kernel
	 * @param I : Image
	 * @return masked Image
	 */
	public static Complex[][] mask(Complex[][] kernel, Complex[][] I){
		int col_l = I.length;
		int row_l = I[0].length;

		Complex[][] ret = new Complex[col_l][row_l];

		/*
		Util.pl(row_l);
		Util.pl(col_l);
		Util.pl(Util.arr_s(kernel, " "));
		*/
		
		for(int j=0; j<col_l; j++){
			for(int i=0; i<row_l; i++){
				if(i == 0 || j == 0 || i == row_l - 1 || j == col_l - 1){
					ret[j][i] = Complex.cartesian(0);
					//Util.pf("skipped %d %d\n", j, i);
				}
				else{
					ret[j][i] = patch_mask(kernel, I, i, j);
					//Util.pf("%d %d : %s\n", j, i, ret[j][i]);
					if(Double.isNaN(ret[j][i].Re))
						Util.pf("ret[%d][%d] is NaN\n", j, i);
				}
				//Util.pf("ret[%d][%d] = %s\n", j, i, ret[j][i]);
				
			}
		}

		//Util.pl(Util.arr_s(ret, " "));
		return ret;
	}
	
	

			      /**
			       * for each image row in input image:
		   				for each pixel in image row:

			      			set accumulator to zero

			      			for each kernel row in kernel:
			         			for each element in kernel row:

				            	if element position  corresponding* to pixel position then
				               		multiply element value  corresponding* to pixel value
				               		add result to accumulator
				            	endif

			      		set output image pixel to accumulator
			       * @param kernel
			       * @param I
			       * @return
			       */
//	public static Complex[][] convolution(Complex[][] kernel, Complex[][] I){
//		for(Complex[] image_row: I){
//			for(Complex pixel : image_row){
//				Complex accumulator = Complex.cartesian(0);
//				
//				for(Complex[] kernel_row : kernel){
//					for(Complex Element : kernel_row){
//						if()
//					}
//				}
//			}
//		}
//	}
	
	/**
	 * No bounds check for whether an I patch at i, j is within range
	 * @param kernel : masking kernel
	 * @param I : Image
	 * @param i : masking pixel X-coord
	 * @param j : masking pixel Y-coord
	 * @return masked pixel value
//	 */
//	public default Complex patch_mask(Complex[][] kernel, Complex[][] I, int i, int j){
//		int x, y;
//		Complex[][] rst; Complex sum;
//		
//		
//		x = kernel[0].length; y = kernel.length;
//		
//		rst = Matrix.mult(kernel, 0, 0, y, x, I, j-1, i-1, y, x);
//		
//		//Util.pl(Util.arr_s(rst, " "));
//		///////
//		sum = Complex.cartesian(0);
//		//Util.pl(Util.arr_s(rst, " "));
//		for(int a=0; a<y; a++){
//			for(int b=0; b<x; b++){
//				sum = sum.add(rst[a][b]);
//			}
//		}
//		
//		/*
//		for(Complex[] arr : rst)
//			for(Complex c : arr)
//				sum = sum.add(c);*/
//		//Util.pl(sum == null);
//		if(!sum.equals(Complex.cartesian(0)))
//			Util.pl(sum);
//		return sum;
//	}
	
	public static Complex patch_mask(Complex[][] kernel, Complex[][] I, int i, int j){
		int x, y;
		Complex[][] rst; Complex sum;
		
		
		x = kernel[0].length; y = kernel.length;
	
		sum = Complex.cartesian(0);
		for(int a=0; a<y; a++){
			for(int b=0; b<x; b++){
				sum = sum.add(I[j + a - 1][i + b - 1].mult(kernel[a][b]));
			}
		}
//		
//		if(!sum.equals(Complex.cartesian(0)))
//			Util.pl(sum);
		return sum;
	}
}
