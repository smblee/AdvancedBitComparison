package abc2.imageprocess.corner.filter;

import abc2.imageprocess.filters.ImageFilter;
import abc2.struct.Complex;

import java.util.function.BiFunction;

public class ImageDerivative {
	public static Complex[][] derivative(Complex[][] image, Complex[][] kernel){
		return ImageFilter.mask(kernel, image);
	}
}
