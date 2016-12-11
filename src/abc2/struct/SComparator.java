package abc2.struct;

import java.util.Comparator;

public class SComparator implements Comparator<SimpleData>{

	@Override
	public int compare(SimpleData o1, SimpleData o2) {
		double dS = o1.gof - o2.gof;
		if(dS > 0)
			return 1;
		else
			return dS == 0 ? 0 : -1;
	}

}
