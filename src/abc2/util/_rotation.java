package util;
import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class _rotation{
	static boolean DEBUG = true;

	static final int X = 0;
	static final int Y = 1;
	
	public static void main(String[] args){
		String imgname;
		if(args.length == 0){
			System.out.println("no args");
			return;
		}

		imgname = args[0];
		debug(imgname);

		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(imgname));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}

		
		int Width = img.getWidth();
		int Height = img.getHeight();
		System.out.printf("Width: %d; Height: %d.\n", Width, Height);

		int[][] matrix = new int[Width][Height];
		//
		for(int w=0; w<Width; w++){
			for(int h=0; h<Height; h++){
				int rgb = img.getRGB(w, h);
				matrix[w][h] = ((rgb >>  0) & 0x000000FF) == 0? 1 : 0;
			}
		}

		debug("matrix");
		show(matrix);
		//
		debug("rotated");
		int[][] rotated = rotation(matrix, (30 / 180.0) * Math.PI);
		show(rotated);

		
		BufferedImage img1;
		try{
			img1 = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_ARGB);
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		for(int w=0; w<Width; w++){
			for(int h=0; h<Height; h++){
				int rgb = (rotated[w][h] == 0)? 0 : 0xFFFFFFFF;
				img1.setRGB(w, h, rgb);
			}
		}

		try{
		ImageIO.write(img1, "png", new File("rotated.png"));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}

		
	}

	//	x = cos(t)x + sin(t)y
	//	y = -sin(t)x + cos(t)y
	public static int[][] rotation(int[][] matrix, double theta){
		int Width = matrix.length;
		int Height = matrix[0].length;
		int[][] rotated = new int[Width][Height];
		int[] coord = new int[2];
		for(int w=0; w<Width; w++){
			for(int h=0; h<Height; h++){
				coord[X] = h;
				coord[Y] = w;
				coord = rotate(coord, theta);
				
				rotated[w][h] = (inBounds(coord, Width, Height))?
							matrix[coord[X]][coord[Y]] : 1;
			}
		}
		return rotated;
	}

	public static boolean inBounds(int[] coord, int Width, int Height){
		return (coord[X] < Width && coord[X] >= 0) && (coord[Y] < Height && coord[Y] >= 0);
	}

	public static int[] rotate(int[] coord, double theta){
		int[] ret = new int[2];
		ret[X] = (int) (Math.cos(theta) * coord[X] + Math.sin(theta) * coord[Y]);
		ret[Y] = (int) (-Math.sin(theta) * coord[X] + Math.cos(theta) * coord[Y]);
		return ret;
	}

	public static void show(int[][] matrix){
		for(int[] m1: matrix){
			for(int m2: m1){
				System.out.printf("%1d ", m2);
			}
			System.out.print('\n');
		}
	}

	public static void debug(Object s, Object ... o){
		if(DEBUG){
			System.out.printf(s.toString() + "\n", o);
		}
	}
	
}
