package util;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class nonprogram_convert{
	static Scanner sc = new Scanner(System.in);
	static String line;
	static String[] w;

	static Scanner sc1;
	static List<Character> l = Arrays.asList('R', 'G', 'B', 'W');
	static List<Integer> ints;
	public static void main(String[] args) throws IOException{
		File f = new File(args[0]);
		

		for(String fname : f.list()){
			if(fname.startsWith("."))
				continue;
			
			BufferedImage bi = Util.read(fname);
			int[][] I = Util.extractRGB(bi);
			
			File dir = new File(args[1]);
			if(!dir.exists())
				dir.mkdir();
			File out = new File(args[1] + "/" + fname);
			if(!out.exists())
				out.createNewFile();
			
			FileWriter fw = new FileWriter(out);
			for(int i=0; i<I.length; i++){
				for(int j=0; j<I[0].length; j++){
					fw.write(String.valueOf(I[i][j]));
					if(j != I[0].length - 1)
						fw.write(" ");
					else
						fw.write("\n");
				}
				
			}
			
			fw.close();
		}
	}
}
