package abc2.readwrite;
import java.util.*;

public class convert{
	static Scanner sc = new Scanner(System.in);
	static String line;
	static String[] w;

	static Scanner sc1;
	static List<Character> l = Arrays.asList('R', 'G', 'B', 'W');
	static List<Integer> ints;
	public static void main(String[] args){
		while(sc.hasNextLine()){
			line = sc.nextLine();
			ints = new ArrayList<Integer>();
			if(l.contains(line.charAt(0))){
				continue;
			}

			sc1 = new Scanner(line);
			while(sc1.hasNextInt()){
				ints.add(sc1.nextInt());
			}

			//ints.forEach(System.out::println);
			for(Integer i: ints)
				System.out.println(i);
		}
	}
}
