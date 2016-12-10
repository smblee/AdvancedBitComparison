package abc2.query.struct;

import java.util.Comparator;

public class SComparator implements Comparator<Data>{

	@Override
	public int compare(Data o1, Data o2) {
		double dS = o1.gof - o2.gof;
		if(dS > 0)
			return 1;
		else
			return dS == 0 ? 0 : -1;
	}

}
