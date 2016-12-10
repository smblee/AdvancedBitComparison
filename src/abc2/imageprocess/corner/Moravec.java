package imageprocess.corner;

import imageprocess.struct.Complex;
import imageprocess.struct._Patch;

import java.util.function.BiFunction;

/**
 * Moravec corner detection algorithm
 * <p>
 * The algorithm tests each pixel in the image to see if a corner is present, by considering how similar a patch centered on the pixel is to nearby, largely overlapping patches. 
 * <p>
 * The similarity is measured by taking the <it>sum of squared differences (SSD)</it> between the corresponding pixels of two patches. 
 * <p>
 * A lower number indicates more similarity.
 * @author Art
 *
 */
public class Moravec {
	
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return -1 if p1, p2 does not match size; distance between two patches
	 */
	public static double distance(_Patch p1, _Patch p2, BiFunction<Integer, Integer, Double> b){
		if(!_Patch.matchSize(p1, p2)){
			return -1.0;
		}
		
		int width = p1.width(); int height = p1.height();
		Complex weight, diff;
		
		Complex sum = Complex.cartesian(0);
		for(int u=0; u<width; u++){
			for(int v=0; v<height; v++){
				weight = Complex.cartesian(b.apply(u, v));
				diff = (p1.getValue(u, v)).sub(p2.getValue(u, v));
				sum = sum.add(weight.mult(diff.mult(diff)));
			}
		}
		
		return sum.magnitude();
	}
}
