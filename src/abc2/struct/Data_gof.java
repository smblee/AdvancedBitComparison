package abc2.struct;

import abc2.util.Util;

public class Data_gof implements Data {
	public final SimpleData sd;
	private Double[] comparables;
	
	public Data_gof(SimpleData _sd){
		sd = _sd;
		comparables = new Double[]{sd.c};
	}

	@Override
	public Comparable[] getData() {
		return comparables;
	}

	@Override
	public int axis_num() {
		return 1;
	}

	@Override
	public double distance(Data other) {
		double dist = ((Data_gof) other).sd.c - this.sd.c;
		return dist * dist;
	}

	@Override
	public double axis_distance(Data other, int axis) {
		return distance(other);
	}
	
	@Override
	public String toString(){
		return "Data_gof : " + Util.arr_s( comparables, " ");
	}

}
