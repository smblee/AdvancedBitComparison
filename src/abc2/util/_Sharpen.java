package abc2.util;

import java.util.*;
import java.math.*;
import java.io.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class _Sharpen{
	static String delimeter = " ";
	static boolean debug = true;

	static BufferedImage buf_img;
	static int[][] img;
	public static void main(String[] args){
		buf_img = Util.read(args[0]);
		img = Util.extractRGB(buf_img);
		_do();
	}

	public static void _do(){
		show(sharpen(img));

	}

	public static int[][] sharpen(int[][] matrix){
		int[][] ret = new int[matrix.length-1][matrix[0].length - 1];
		for(int i=0; i<matrix.length-1; i++){
			for(int j=0; j<matrix[0].length-1; j++){
				ret[i][j] = 
					(matrix[i+1][j] - matrix[i][j] != 0) || (matrix[i][j+1] - matrix[i][j] != 0) ?
					1 : 0;
			}
		}

		return ret;
	}

	public static void show(int[][] matrix){
		for(int[] i: matrix){
			for(int j: i){
				pf("%d ", j);
			}
			pf("\n");
		}
	}

//----------------------------------------------------------------------------
	public static boolean isalphabet(char c){
		return upper(c) || lower(c);
	}

	public static boolean upper(char c){
		return (c <= 'Z' && c >= 'A');
	}

	public static boolean lower(char c){
		return (c <= 'z' && c >= 'a');
	}

	public static boolean sampleLine(){
		return line.charAt(0) == '-';
	}

	public static boolean h(){
		return sc.hasNextLine();
	}

	public static void r(){
		line = sc.nextLine();
		w = line.split(delimeter);
	}

	public static BigInteger b(String val){
		return new BigInteger(val);
	}

	public static void p(Object o){
		System.out.print(o);
	}

	public static void pf(String format, Object... os){
		System.out.printf(format, os);
	}

	public static void pl(Object o){
		System.out.println(o);
	}

	public static void db(Object s){
		if(debug)
			System.out.println(s);
	}

	public static String arr_s(Object[] arr, String s){
		StringBuilder sb = new StringBuilder();
		for(Object o: arr){
			sb.append(o.toString());
			sb.append(s);
			}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	//linear equation;

	static Scanner sc = new Scanner(System.in);
	static String line;
	static String[] w;
}
