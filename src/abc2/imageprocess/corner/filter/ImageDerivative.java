package abc2.imageprocess.corner.filter;

import abc2.imageprocess.filters.ImageFilter;

import java.util.function.BiFunction;

import struct.Complex;

public class ImageDerivative {
	public static Complex[][] derivative(Complex[][] image, Complex[][] kernel){
		return ImageFilter.mask(kernel, image);
	}
	
	
	/**
	 * 
	 * @param A amplitude
	 * @param x0 center X
	 * @param y0 center Y
	 * @param sigmaX X spread of the blob
	 * @param sigmaY Y spread of the blob
	 * @param boundX boundX
	 * @param boundY boundY
	 * @return
	 */
	public static final BiFunction<Integer, Integer, Double> Gaussian(
			double A, double x0, double y0, double sigmaX, double sigmaY){
		
		BiFunction<Integer, Integer, Double> ret = 
				new BiFunction<Integer, Integer, Double>(){

			@Override
			public Double apply(Integer x, Integer y) {
				double ret, dx, dy, factorX, factorY;
				
				dx = x - x0; dy = y - y0;
				
				ret = A * Math.exp(-1 * ( 
							(dx * dx) / (2 * sigmaX * sigmaX) 
						+ 	(dy * dy) / (2 * sigmaY * sigmaY)
						));
				return ret;
			}
		};
		
		return ret;
	}
}
