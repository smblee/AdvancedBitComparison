package abc2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import abc2.struct.SimpleData;

public class MathTools {
	
//	public static void main(String[] args){
//		Double[] d1 = {1.0, 4.0};
//		Double[] d2 = {3.0, 9.0};
//		Double[] d3 = {1.1, 4.25};
//		Double[] d4 = {2.0, 6.5};
//		
//		Util.pl(regression(Arrays.asList(d1, d2, d3, d4)));
//	}
//	
	/**
	 * takes the squares to avoid sqrt operation
	 * 
	 * normalized mu and median by dividing n;
	 * @param rows
	 * @return
	 */
	public static SimpleData mean_var_skewness(int[] rows, int col_l){
		int row_len = rows.length;
		double y_sum, mu, median, var, d , skewness_sqr;
		
		y_sum = 0.0;
		for(int i=0; i<row_len; i++){
			y_sum += rows[i];
		}
				
		mu = (1.0 * y_sum) / row_len;
		median = rows[row_len / 2];
		
		var = 0.0;
		for(int i=0; i<row_len; i++){
			d = rows[i] - mu;
			var += d * d;
		}
		
		var = var / row_len;
		
		d = (mu - median);
		skewness_sqr  = d * d / var;
		return new SimpleData(mu / col_l, median / col_l, skewness_sqr);
	}
	
	public static SimpleData linear_regression_Var(List<Double[]> data){
		double a_numerator, a_denominator, a, b, S;
		double n, x, y, x_sqr_sum, xy_sum, x_sum, y_sum, d;
		
		n = data.size();
		if(n == 0.0)
			n = 0.1;
		
		x_sqr_sum = xy_sum = x_sum = y_sum = 0.0;
		//Util.pf("xy_sum %.4f, x_sum %.4f, y_sum %.4f, x_sqr_sum %.4f\n", xy_sum, x_sum, y_sum, x_sqr_sum);
		
		
		for(Double[] point : data){
			x = point[0]; y = point[1];
			//Util.pf("x %.4f, y %.4f\n", x, y);
			
			
			x_sqr_sum += x * x;
			xy_sum += x * y;
			x_sum += x;
			y_sum += y;
		}
		
		//Util.pf("xy_sum %.4f, x_sum %.4f, y_sum %.4f, x_sqr_sum %.4f\n", xy_sum, x_sum, y_sum, x_sqr_sum);
		
		a_numerator = (n * xy_sum - x_sum * y_sum);
		a_denominator = (n * x_sqr_sum - x_sum * x_sum);
		a = a_numerator / a_denominator;
		b = (y_sum - a * x_sum) / n;

		if(a_denominator == 0){
			a = Double.MAX_VALUE;
			b = 0;
			return new SimpleData(a, b, 0);
		}
		S = 0;
		for(Double[] point : data){
			x = point[0]; y = point[1];
			d = (y - a * x - b);
			S += d * d;
		}
		
		S = S / n;
		
		
		SimpleData ret = new SimpleData(a, b, S);
		if(ret.containsNaN()){
			Util.pl(a_numerator + "/" +  a_denominator + ", " + b);
		}
		return ret;		
	}
	
	public static SimpleData linear_regression_R2(List<Double[]> data){
		double a_numerator, a_denominator, a, b, R2;
		double n, x, y, x_sqr_sum, xy_sum, x_sum, y_sum, d, y_bar, SSres, SStot;
		
		n = data.size();
		if(n == 0.0)
			n = 0.1;
		
		x_sqr_sum = xy_sum = x_sum = y_sum = 0.0;
		//Util.pf("xy_sum %.4f, x_sum %.4f, y_sum %.4f, x_sqr_sum %.4f\n", xy_sum, x_sum, y_sum, x_sqr_sum);
		
		for(Double[] point : data){
			x = point[0]; y = point[1];
			//Util.pf("x %.4f, y %.4f\n", x, y);
			
			
			x_sqr_sum += x * x;
			xy_sum += x * y;
			x_sum += x;
			y_sum += y;
		}
		
		//Util.pf("xy_sum %.4f, x_sum %.4f, y_sum %.4f, x_sqr_sum %.4f\n", xy_sum, x_sum, y_sum, x_sqr_sum);
		
		a_numerator = (n * xy_sum - x_sum * y_sum);
		a_denominator = (n * x_sqr_sum - x_sum * x_sum);
		a = a_numerator / a_denominator;
		b = (y_sum - a * x_sum) / n;

		// NEED TO BE CHANGED TO MORE EFFICIENT CODE LATER!
		if(a_denominator == 0){
			ArrayList<Double[]> new_data = new ArrayList<Double[]>();
			for(Double[] datum : data){
				Double[] new_datum = {datum[1], datum[0]};
				new_data.add(new_datum);
			}
			return linear_regression_R2(new_data);			
		}
		

		y_bar = y_sum / n;
		
		SSres = 0;
		SStot = 0;
		
		double expectation;
		for(Double[] point : data){
			x = point[0]; y = point[1];
			expectation = a * x + b;
			SSres += (y - expectation) * (y - expectation);
			SStot += (y - y_bar) * (y - y_bar);
		}

		R2 = 1 - SSres  / SStot;
		SimpleData ret = new SimpleData(a, b, R2);
		return ret;		
	}
	
	public static SimpleData inverse_linear_regression_R2(List<Double[]> data){
		double a_numerator, a_denominator, a, b, R2;
		double n, x, y, d, y_sum, one_over_x_sum, one_over_x_sqr_sum, y_over_x_sum, y_bar, SSres, SStot;
		
		n = data.size();
		if(n == 0.0)
			n = 0.1;
		
		y_sum = one_over_x_sum = one_over_x_sqr_sum = y_over_x_sum= 0.0;
		//Util.pf("xy_sum %.4f, x_sum %.4f, y_sum %.4f, x_sqr_sum %.4f\n", xy_sum, x_sum, y_sum, x_sqr_sum);
		
		
		for(Double[] point : data){
			x = point[0]; y = point[1];
			//Util.pf("x %.4f, y %.4f\n", x, y);
			
			y_sum += y;
			one_over_x_sum += 1/x;
			one_over_x_sqr_sum += 1 / (x * x);
			y_over_x_sum += y/x;
		}
		
		//Util.pf("xy_sum %.4f, x_sum %.4f, y_sum %.4f, x_sqr_sum %.4f\n", xy_sum, x_sum, y_sum, x_sqr_sum);
		
		a_numerator = (n * y_over_x_sum - y_sum * one_over_x_sum);
		a_denominator = (n * one_over_x_sqr_sum - one_over_x_sum * one_over_x_sum);
		a = a_numerator / a_denominator;
		b = (y_sum - a * one_over_x_sum) / n;

		if(a_denominator == 0){
			a = Double.MAX_VALUE;
			b = Double.MAX_VALUE;
			return new SimpleData(a, b, 0);
		}

		y_bar = y_sum / n;
		
		SSres = 0;
		SStot = 0;
		
		double expectation;
		for(Double[] point : data){
			x = point[0]; y = point[1];
			expectation = a / x + b;
			SSres += (y - expectation) * (y - expectation);
			SStot += (y - y_bar) * (y - y_bar);
		}

		R2 = 1 - SSres  / SStot;
		
		
		SimpleData ret = new SimpleData(a, b, R2);
		if(ret.containsNaN()){
			Util.pl(a_numerator + "/" +  a_denominator + ", " + b);
		}
		return ret;		
	}
}
