package abc2.struct;

import abc2.util.Util;

public class Data_gof_a_b implements Data {
	public final SimpleData sd;
	private Double[] comparables;

	public Data_gof_a_b(SimpleData _sd){
		sd = _sd;
		comparables = new Double[]{sd.gof, sd.a, sd.b};
	}

	@Override
	public Comparable[] getData() {
		return comparables;
	}

	@Override
	public int axis_num() {
		return 3;
	}

	@Override
	public double distance(Data other) {
		return d_gof(other) + d_a(other) + d_b(other);
	}

	@Override
	public double axis_distance(Data other, int axis) {
		switch(axis % axis_num()){
			case 0:
				return d_gof(other);
			case 1:
				return d_a(other);
			case 2:
				return d_b(other);
			default:
				return d_gof(other);
		}
	}

	@Override
	public String toString(){
		return "Data_gof : " + Util.arr_s( comparables, " ");
	}
	
	private double d_gof(Object other){
		double ret = ((Data_gof_a_b) other).sd.gof - this.sd.gof;
		return ret * ret;
	}
	private double d_a(Object other){
		double ret = ((Data_gof_a_b) other).sd.a - this.sd.a;
		return ret * ret;
	}
	private double d_b(Object other){
		double ret = ((Data_gof_a_b) other).sd.b - this.sd.b;
		return ret * ret;
	}

}