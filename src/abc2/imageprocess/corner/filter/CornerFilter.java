package abc2.imageprocess.corner.filter;

import abc2.imageprocess.filters.ImageFilter;

import java.util.function.BiFunction;

public interface CornerFilter extends ImageFilter {
    public static double[][] decision_matrix(
            double Ix_v_u,
            double Iy_v_u,
            int v,
            int u,
            BiFunction<Integer, Integer, Double> w){
        double[][] ret;
        double window;

        ret = new double[2][2];

        window = w.apply(u, v);
        ret[0][0] = Ix_v_u * Ix_v_u * window;
        ret[0][1] = Ix_v_u * Iy_v_u * window;
        ret[1][0] = Ix_v_u * Iy_v_u * window;
        ret[1][1] = Iy_v_u * Iy_v_u * window;

        return ret;
    }


    /**
     * R is positive in the corner region, negative in the edge regions,
     * 	 and small in Hit flat region.
     * @param decision_matrix
     * @param k
     * @return R value
     */
    public static double R(double[][] decision_matrix, double k){
        double trace, determinant;
        trace = decision_matrix[0][0] + decision_matrix[1][1];
        determinant = 	decision_matrix[0][0] * decision_matrix[1][1] -
                decision_matrix[0][1] * decision_matrix[1][0];

        return determinant - (k * trace * trace);
    }

}
