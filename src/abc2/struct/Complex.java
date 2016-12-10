package abc2.struct;

import static java.lang.Math.*;

import java.util.HashMap;

import abc2.util.Util;

public class Complex {
	public final double Re, Im;
	public final double R, theta;
		
	private Complex(double _Re, double _Im, double _R, double _theta){
		Re = _Re;
		Im = _Im;
		R = _R;
		theta = _theta;
	}
	
	public Complex add(Complex c) {return Complex.cartesian(Re + c.Re, Im + c.Im);}
	public Complex sub(Complex c) {return Complex.cartesian(Re - c.Re, Im - c.Im);}
	public Complex mult(Complex c){return Complex.cartesian(Re * c.Re - Im * c.Im, Re * c.Im + Im * c.Re);}
	
	public double magnitude() { return Math.sqrt(Re * Re + Im * Im); }

	public Complex conjg(){
		return Complex.cartesian(Re, -1 * Im);
	}

	public Complex sqrt(){
		return polar(Math.sqrt(R), theta / 2);
	}
	
	public static Complex polar(double R, double theta){
		double Re, Im;
		Re = R * cos(theta);
		Im = R * sin(theta);
		if(Double.isNaN(theta) || Double.isNaN(R) || Double.isNaN(Re) || Double.isNaN(Im))
			Util.pl(R + ", " + theta + " gives NaN");
		return new Complex(Re, Im, R, theta);
	}
	public static Complex cartesian(double Re, double Im){
		double R, theta;
		R = Math.sqrt(Im * Im + Re * Re);
		if(Re == 0)
			theta = Math.PI / 2;
		else
			theta = atan(Im / Re);
		if(Double.isNaN(theta) || Double.isNaN(R) || Double.isNaN(Re) || Double.isNaN(Im))
			Util.pl(Re + ", " + Im + " gives NaN");
		return new Complex(Re, Im, R, theta);
	}
	
	public String toString(){
		return "" + ((int) Re);
	}
//	public String toString(){
//		if(Im < (1.0 / (1 << 30)))
//			return Re + "";
//		return Re + "+" + Im + "i";
//	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Complex){
			Complex c = ((Complex) o);
			return c.Re == this.Re && c.Im == this.Im;
		}
		return false;
	}
	
	/*
	 * Opt
	 */
	private static HashMap<Integer, Complex> pool = new HashMap<Integer, Complex>();
	public static Complex cartesian(int i){
		if(!pool.containsKey(i))
			pool.put(i, Complex.cartesian(i, 0));
		return pool.get(i);
	}
	
	public static Complex cartesian(double d){
		return Complex.cartesian(d, 0);
	}
}
