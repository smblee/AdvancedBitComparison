package abc2.struct;

import abc2.util.Util;

public class SimpleData {
	public final double a, b, gof;
	
	public SimpleData(double _a, double _b, double _gof){
		a = _a; b = _b; gof = _gof;
	}
	
	public String toString(){
		return "y = " + a + "x + " + b + "; Goodness of Fit: " + gof;
	}
	
	public boolean equals(Object o){
		if(o instanceof SimpleData){
			SimpleData d = (SimpleData) o;
			return d.a == this.a && d.b == this.b && d.gof == this.gof;
		}	
		return false;
	}
	
	public boolean containsNaN(){		
		return Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(gof);
	}
}
