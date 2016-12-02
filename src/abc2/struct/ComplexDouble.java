package abc2.struct;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by brylee on 12/2/16.
 */
public class ComplexDouble {
    public final double Re, Im;
    public final double R, theta;

    private ComplexDouble(double _Re, double _Im, double _R, double _theta){
        Re = _Re;
        Im = _Im;
        R = _R;
        theta = _theta;
    }

    public ComplexDouble add(Complex c) {return ComplexDouble.cartesian(Re + c.Re, Im + c.Im);}
    public ComplexDouble sub(Complex c) {return ComplexDouble.cartesian(Re - c.Re, Im - c.Im);}
    public ComplexDouble mult(Complex c){return ComplexDouble.cartesian(Re * c.Re - Im * c.Im, Re * c.Im + Im * c.Re);}

    public double magnitude() { return Math.sqrt(Re * Re + Im * Im); }

    public ComplexDouble conjg(){
        return ComplexDouble.cartesian(Re, -1 * Im);
    }

    public ComplexDouble sqrt(){
        return polar(Math.sqrt(R), theta / 2);
    }

    public static ComplexDouble polar(double R, double theta){
        double Re, Im;
        Re = R * cos(theta);
        Im = R * sin(theta);
        return new ComplexDouble(Re, Im, R, theta);
    }
    public static ComplexDouble cartesian(double Re, double Im){
        double R, theta;
        R = Math.sqrt(Im * Im + Re * Re);
        theta = atan(Im / Re);
        return new ComplexDouble(Re, Im, R, theta);
    }

    public String toString(){
        if(Im < (1.0 / (1 << 30)))
            return Re + "";
        return Re + "+" + Im + "i";
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ComplexDouble){
            ComplexDouble c = ((ComplexDouble) o);
            return c.Re == this.Re && c.Im == this.Im;
        }
        return false;
    }

    /*
     * Opt
     */
    private static Map<Integer, ComplexDouble> pool = new HashMap<>();
    public static ComplexDouble cartesian(int i){
        if(!pool.containsKey(i))
            pool.put(i, ComplexDouble.cartesian(i, 0));
        return pool.get(i);
    }

    public static ComplexDouble cartesian(double d){
        return ComplexDouble.cartesian(d, 0);
    }
}
