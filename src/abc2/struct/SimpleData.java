package abc2.struct;

import abc2.util.Util;

public class SimpleData {
	public final double a, b, c;
	
	public SimpleData(double _a, double _b, double _c){
		a = _a; b = _b; c = _c;
	}
	
	public String toString(){
		return "[ " + a + " : " + b + " : " + c + " ]";
	}
	
	public boolean equals(Object o){
		if(o instanceof SimpleData){
			SimpleData d = (SimpleData) o;
			return d.a == this.a && d.b == this.b && d.c == this.c;
		}	
		return false;
	}
	
	public boolean containsNaN(){		
		return Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c);
	}
}
