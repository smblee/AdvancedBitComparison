package abc2.struct;

import abc2.util.Util;

public class Matrix{
	public static boolean multiplicable(Complex[][] A, Complex[][] B){
		return A[0].length == B.length;
	}

	/*
	public static Complex[][] mult(Complex[][] A, Complex[][] B){
		if(!multiplicable(A, B))
			return null;

		int row_l = A[0].length;
		int col_l = A.length;
		Complex[][] ret = new Complex[A.length][B[0].length];
		for(int i=0; i<col_l; i++){
			Complex sum = Complex.cartesian(0);
			for(int j=0; j<row_l; j++){
				for(int k=0; k<row_l; k++){
					Complex a = A[i][k];
					Complex b = B[k][j];
					sum = sum.add(a.mult(b));
				}
				ret[i][j] = sum;
			}
		}
		return ret;
	}
	*/
	public static Complex[][] mult(Complex[][] A, Complex[][] B){
		return mult(A, 0, 0, A.length, A[0].length, B, 0, 0, B.length, B[0].length);
	}

	/**
	 * No bounds check
	 */
	public static Complex[][] mult(Complex[][] A, int jA, int iA, int colA, int rowA, Complex[][] B, int jB, int iB, int colB, int rowB){
		if(rowA != colB){
			Util.pf("mismatch : %d x %d with %d x %d\n", colA, rowA, colB, rowB);
			return null;
		}
	
		//Util.pf("%d x %d with %d x %d\n", colA, rowA, colB, rowB);
		
		Complex[][] ret = new Complex[colA][rowB];
		
		for(int i=0; i<rowB; i++){
			Complex sum = Complex.cartesian(0);
			for(int j=0; j<colA; j++){
				for(int k=0; k<rowA; k++){
					Complex a = A[jA + j][iA + k];
					Complex b = B[jB + k][iB + i];
					
					//Util.pf("%s += %s * %s\n", sum.toString(), a.toString(), b.toString());
					sum = sum.add(a.mult(b));
				}
				//Util.pf("ret[%d][%d] = %s\n", j, i, sum.toString());
				ret[j][i] = sum;
				sum = Complex.cartesian(0);
			}
		}
		
		//Util.pl(Util.arr_s(ret, " "));
		return ret;
	}
	
	/**
	 * simple matrix addition same size A and B;
	 * @param A
	 * @param B
	 * @return
	 */
	public static Complex[][] add(Complex[][] A, Complex[][] B){
		if(A.length != B.length || A[0].length != B[0].length){
			Util.pf("mismatch : %d x %d with %d x %d\n", A.length, A[0].length, B.length, B[0].length);
			return null;
		}
		
		Complex[][] ret = new Complex[A.length][A[0].length];
		
		for(int i=0; i<A[0].length; i++){
			for(int j=0; j<A.length; j++){
				ret[j][i] = A[j][i].add(B[j][i]);
			}
		}
		
		//Util.pl(Util.arr_s(ret, " "));
		return ret;
	}
	
	/** 
	 * A is assumed to be always bigger, test it!
	 * @param A
	 * @param jA
	 * @param iA
	 * @param colA
	 * @param rowA
	 * @param B
	 * @param jB
	 * @param iB
	 * @param colB
	 * @param rowB
	 * @return
	 */
	public static Complex[][] add(Complex[][] A, int jA, int iA, int colA, int rowA, Complex[][] B, int jB, int iB, int colB, int rowB){
		if(rowA != rowB || colA != colB){
			Util.pf("mismatch : %d x %d with %d x %d\n", colA, rowA, colB, rowB);
			return null;
		}
	
		//Util.pf("%d x %d with %d x %d\n", colA, rowA, colB, rowB);
		
		Complex[][] ret = new Complex[A.length][A[0].length];
		
		for(int i=0; i<A[0].length; i++){
			for(int j=0; j<A.length; j++){
				ret[j][i] = A[j][i];
			}
		}
		
		for(int i=0; i<rowB; i++){
			for(int j=0; j<colA; j++){
				ret[j][i] = A[jA + j][iA + i].add(B[jB + j][iB + i]);
			}
		}
		
		//Util.pl(Util.arr_s(ret, " "));
		return ret;
	}
}
