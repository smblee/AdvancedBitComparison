package abc2.struct;

public class Data_bell_curve implements Data{
	public final SimpleData sd;
	double mu, median, skewness;
	Double[] comparables;

	public Data_bell_curve(SimpleData _sd){
		sd = _sd;
		mu = sd.a;
		median = sd.b;
		skewness = sd.c;
		comparables = new Double[]{mu, median, skewness};
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
		Data_bell_curve o = (Data_bell_curve) other;
		double da, db, dc;
		da = mu - o.mu;
		db = median - o.median;
		dc = skewness - o.skewness;
		return (da * da) + (db * db) + (dc * dc);
	}
	
	@Override
	public double axis_distance(Data other, int axis) {
		Data_bell_curve o = (Data_bell_curve) other;
		double dist = comparables[axis] - o.comparables[axis];
		return dist * dist;
	}
}
