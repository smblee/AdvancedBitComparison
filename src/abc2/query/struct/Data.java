package query.struct;

import util.Util;

public class Data {
	public final double a, b, S;
	
	public Data(double _a, double _b, double _S){
		a = _a; b = _b; S = _S;
	}
	
	public String toString(){
		return "" + S; //"y = " + a + "x + " + b + "; Var : " + S;
	}
	
	public boolean equals(Object o){
		if(o instanceof Data){
			Data d = (Data) o;
			return d.a == this.a && d.b == this.b && d.S == this.S;
		}	
		return false;
	}
	
	public boolean containsNaN(){		
		return Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(S);
	}
}
