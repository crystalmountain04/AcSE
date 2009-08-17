package acse.twoPart.solutions;

import acse.twoPart.interfaces.Solution;
import acse.twoPart.util.*;

public class GaussUngleichImpuls implements Solution {
    private Constants myConst;
	private double k1;
	private double k2;
	private double m1;
	private double m2;
	private double sig1;
	private double sig2;
        private double x0;
	private double y0;
	private double dt;
	private int n;

    public GaussUngleichImpuls(Constants myConst, double dt) {
        this.k1=10.0;
        this.k2=2.0;
        this.m1=myConst.m1;
        this.m2=myConst.m2;
        this.sig1=myConst.sig1;
        this.sig2=sig1;
		this.x0 = -2.0;
		this.y0 = 2.0;
        this.n=0;
        this.dt=dt;
    }

    public double getState(double x, double y) {
	double out=Math.pow(getStatePsi(x,y).getReal(),2)+Math.pow(getStatePsi(x,y).getImag(),2);
        return out;
    }

    public void nextTimeStep() {
        n=n+1;
    }

    public Complex getStatePsi(double x, double y) {
	double tmp = 1/(4*Math.PI*sig1*sig1*sig2*sig2) *
			Math.exp(-1/(4*sig1*sig1)*(x-x0)*(x-x0)-1/(4*sig2*sig2)*(y-y0)*(y-y0));
        Complex out = new Complex(
                tmp*Math.cos(k1*x-k2*y),
                tmp*Math.sin(k1*x-k2*y)
                );
        return out;
    }
}