package abc2.struct;

import static java.lang.Math.*;
import java.util.HashMap;

public class Complex {
	public final int Re;
	public final int R, theta;
		
	private Complex(int _Re, int _R, int _theta){
		Re = _Re;
		R = _R;
		theta = _theta;
	}
	
	public Complex add(Complex c) {return Complex.cartesian(Re + c.Re, Im + c.Im);}
	public Complex sub(Complex c) {return Complex.cartesian(Re - c.Re, Im - c.Im);}
	public Complex mult(Complex c){return Complex.cartesian(Re * c.Re - Im * c.Im, Re * c.Im + Im * c.Re);}
	
	public int magnitude() { return Math.sqrt(Re * Re + Im * Im); }

	public Complex conjg(){
		return Complex.cartesian(Re, -1 * Im);
	}

	public Complex sqrt(){
		return polar(Math.sqrt(R), theta / 2);
	}
	
	public static Complex polar(int R, int theta){
		int Re, Im;
		Re = R * cos(theta);
		Im = R * sin(theta);
		return new Complex(Re, Im, R, theta);
	}
	public static Complex cartesian(int Re, int Im){
		int R, theta;
		R = Math.sqrt(Im * Im + Re * Re);
		theta = atan(Im / Re);
		return new Complex(Re, Im, R, theta);
	}
	
	public String toString(){
		if(Im < (1.0 / (1 << 30)))
			return Re + "";
		return Re + "+" + Im + "i";
	}
	
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
	
	public static Complex cartesian(int d){
		return Complex.cartesian(d, 0);
	}
}
