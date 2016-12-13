package abc2.struct;

import abc2.util.Util;

public class Data_a_b implements Data{
	public final SimpleData sd;
	private Double[] comparables;
	
	public Data_a_b(SimpleData _sd){
		sd = _sd;
		comparables = new Double[]{sd.a, sd.b};
	}
	
	@Override
	public Comparable[] getData() {
		return comparables;
	}

	@Override
	public int axis_num() {
		return 2;
	}

	@Override
	public double distance(Data _other) {
		Data_a_b other = (Data_a_b) _other;
		double da = other.sd.a - sd.a;
		double db = other.sd.b - sd.b;
		return da * da + db * db;
	}

	@Override
	public double axis_distance(Data _other, int axis) {
		Data_a_b other = (Data_a_b) _other;
		double d = this.comparables[axis] - other.comparables[axis];
		return d * d;
	}
	
	@Override
	public String toString(){
		return "Data_a_b : " +  Util.arr_s(comparables, " ");
	}

}
