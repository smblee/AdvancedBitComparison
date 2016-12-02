package abc2.corner;

import java.awt.image.BufferedImage;
import java.sql.Time;

import abc2.corner.filter.ABC;
import abc2.struct.Complex;
import abc2.util.Util;

public class CornerTest {

	static BufferedImage buf_img;
	static int[][] img;

	static String objectName = "watch";
	
	static String imgname = objectName + "0%d.png";
	static String imgname_dx = objectName + "0%d_dx.png";
	static String imgname_dy = objectName + "0%d_dy.png";
	static int X = 5;
	

	static long start;
	public static void main(String[] args){
		//buf_img = Util.read("ABC_src.jpg");
		start = System.currentTimeMillis();
		buf_img = Util.read2(String.format(imgname, X));
		long readend = System.currentTimeMillis();
		Util.pf("Image read in: %d ms", readend - start);
		System.out.println();
		img = Util.convertTo2DWithoutUsingGetRGB(buf_img);   //Util.extractRGB(buf_img);
		Util.pf("extractRGB() in: %d ms", System.currentTimeMillis() - readend);

		test1();

	}
	
	private static void test1(){
		int x = img.length;
		int y = img[0].length;
		Complex[][] I = new Complex[x][y];
		for(int i=0; i<x; i++)
			for(int j=0; j<y; j++)
				I[i][j] = Complex.cartesian(img[i][j]);

		long start1 = System.currentTimeMillis();
		Complex[][] J;
		Util.pl("\ndx:");
		J = ABC.instance().dx(I);
		//Util.pl(Util.arr_s(J, "\t"));
		Util.write(J, String.format(imgname_dx, X));
		Util.write(J, "ABC_dx.jpg");

		//Util.pf("filter : %d ms\n", System.currentTimeMillis() - start1);
		
		Util.pl("\ndy:");
		J = ABC.instance().dy(I);
		//Util.pl(Util.arr_s(J, "\t"));
		Util.write(J, String.format(imgname_dy, X));
		Util.write(J, "ABC_dy.jpg");
		
		Util.pl("\nd:");
		//J = ABC.instance().d(I);
		//Util.pl(Util.arr_s(J, "\t"));
		long end = System.currentTimeMillis();
		Util.pf("total : %d ms", end - start);
		System.out.println();
		Util.pf("filter : %d ms", end - start1);
	}
}
