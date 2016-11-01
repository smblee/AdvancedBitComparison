package abc.tools;

/**
 * ABC [AdvancedBitComparison]
 * Author:  bryan
 * Date:    10/31/2016
 */
public class RotateImage {
    public void rotate(int[][] matrix) {
        int length = matrix.length-1;
        int j=0;
        while(j < matrix.length/2){
            for(int i=j; i < length-j; i++){
                int temp = matrix[j][i];
                matrix[j][i] = matrix[length-i][j];
                matrix[length-i][j] = matrix[length-j][length-i];
                matrix[length-j][length-i] = matrix[i][length-j];
                matrix[i][length-j] = temp;
            }
            j++;
        }
    }

    private void print(int arr[][]){
        for(int i=0; i < arr.length; i++){
            for(int j=0; j < arr.length; j++){
                System.out.print(arr[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
}
