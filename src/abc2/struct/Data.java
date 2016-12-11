package abc2.struct;

/**
 * class created for Kdtree
 * @author Art
 *
 */
public interface Data {
	public Comparable[] getData();
	public int axis_num();
	
	public default int compareTo(Data other, int axis){
		Comparable a = this.getData()[axis];
		Comparable b = other.getData()[axis];
		return a.compareTo(b);
	}
}
