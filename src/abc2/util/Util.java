package abc2.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.math.BigInteger;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import javax.imageio.ImageIO;

import abc2.struct.Complex;

public class Util {
	
	private static String indir = "shapes/";
	private static String outdir = "OutputImages/";
	
	public static final int WHITE = 0xFFFFFFFF;
	public static final int BLACK = 0xFF000000;
	
	public static int[][] extractRGB(BufferedImage bi){
		int Width = bi.getWidth();
		int Height = bi.getHeight();
		int[][] r = new int[Height][Width];
		//int[][] g = new int[Width][Height];
		//int[][] b = new int[Width][Height];

		for(int w=0; w<Width; w++){
			for(int h=0; h<Height; h++){
				int rgb = bi.getRGB(w, h);
				r[h][w] = rgb & 0x00FFFFFF;
				//r[h][w] = r[h][w] == 0 ? 0 : 1;
				//r[h][w] = (rgb >> 16) & 0x000000FF;
				//g[w][h] = (rgb >>  8) & 0x000000FF;
				//b[w][h] = (rgb >>  0) & 0x000000FF;
			}
		}
		return r;
	}

	public static BufferedImage read2(String path) {
		System.out.println("Getting " + path);
		try (FileInputStream fis = new FileInputStream(indir + path);
			 FileChannel channel = fis.getChannel();
			 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			channel.transferTo(0, channel.size(), Channels.newChannel(byteArrayOutputStream));
			return ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] result = new int[height][width];
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff); // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += -16777216; // 255 alpha
				argb += ((int) pixels[pixel] & 0xff); // blue
				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}

		return result;
	}

	public static BufferedImage read(String imgname){
		BufferedImage img = null;
		try{
			Util.pl(indir + imgname);
			File imgfile = new File(indir + imgname);
			img = ImageIO.read(imgfile);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		int Width = img.getWidth();
		int Height = img.getHeight();
		System.out.printf("Extracted Width: %d; Height: %d.\n", Width, Height);

		return img;
	}
	
	public static void write(Complex[][] I, String imgname){
		int width, height, DOUBLE, pixel;
		width = I[0].length; height = I.length;
		
		int count = 0;
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    for (int i = 0; i < width; i++) {
	        for (int j = 0; j < height; j++) {
	        	DOUBLE = (int) I[j][i].Re;
	        	pixel = DOUBLE > 0 ? WHITE : BLACK;
	        	if(DOUBLE != 0)
	        		count++;
	        /*
	        	pixel = BLACK + DOUBLE;
	        	pixel += DOUBLE << 8;
	        	pixel += DOUBLE << 16;
	        */
	            bi.setRGB(i, j, pixel);
	            //System.out.println("The pixel in Matrix: " + pixel);
	            //System.out.println("The pixel in BufferedImage: " + bi.getRGB(i, j));
	        }
	    }
	  
	    System.out.println(count);
	    write(bi, imgname);
	}
	
	public static void write(BufferedImage bi, String imgname){
		File outputfile = null;
		try {
			outputfile = new File(outdir + imgname);
			if(!outputfile.exists())
				outputfile.createNewFile();
			ImageIO.write(bi, "jpg", outputfile);
			
		} catch (IOException e) {
			Util.pl(imgname + " write error");
			e.printStackTrace();
		}
	}
	
	//------------------------------------------------------------------------------------

	public static boolean isalphabet(char c){
		return upper(c) || lower(c);
	}

	public static boolean upper(char c){
		return (c <= 'Z' && c >= 'A');
	}

	public static boolean lower(char c){
		return (c <= 'z' && c >= 'a');
	}

	//------------------------------------------------------------------------------------

	public static BigInteger b(String val){
		return new BigInteger(val);
	}
	
	public static int Int(String val){
		return Integer.parseInt(val);
	}
	
	public static double Dou(String val){
		return Double.parseDouble(val);
	}
	
	//------------------------------------------------------------------------------------

	public static void p(Object o){
		System.out.print(o);
	}

	public static void pf(String format, Object... os){
		System.out.printf(format, os);
	}

	public static void pl(Object o){
		System.out.println(o);
	}
	
	//------------------------------------------------------------------------------------

	private static boolean debug = true;
	public static void db(Object s){
		if(debug)
			System.out.println(s);
	}
	
	//------------------------------------------------------------------------------------
	
	public static String arr_s(Object[] arr, String s){
		StringBuilder sb = new StringBuilder();
		for(Object o: arr){
			sb.append(o.toString());
			sb.append(s);
			}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	public static String arr_s(Object[][] arr2, String s){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<arr2.length; i++){
			Object[] arr = arr2[i];
			for(int j=0; j<arr.length; j++){
				Object o = arr[j];
				sb.append(o);
				sb.append(s);
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
