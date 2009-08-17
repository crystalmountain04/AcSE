package acse.oneDim.solutions;

import acse.oneDim.interfaces.Solution;
import acse.oneDim.util.*;

public class EigenGauss implements Solution {
    private Constants myConst;
	private double x0;
	private double k;
	private double m;
	private double sig;
	private double x0Analytic;
	private double dt;
	private int n;

    public EigenGauss(Constants myConst, double dt) {
        //this.x0=-5.0/Math.sqrt(2.0);
		//this.x0=-Math.sqrt(7.5);
		this.x0=-Math.sqrt(5.0);
		//this.x0=-4;
        this.k=myConst.k;
        this.m=myConst.m;
        this.sig=myConst.sig;
        this.x0Analytic=x0;
        this.n=0;
        this.dt=dt;
    }

    public double getState(double x) {
        double out = Math.pow(1/(2*Math.PI*sig*sig), 0.5)*Math.exp(-(x-x0Analytic)*(x-x0Analytic)/(2*sig*sig));
        return out;
    }

    public void nextTimeStep() {
        n=n+1;
        double t=n*dt;
        x0Analytic = x0*Math.cos(Math.sqrt(k/m)*t);
    }

    public Complex getStatePsi(double x) {
        Complex out = new Complex(
                Math.sqrt(getState(x)),
                0
                );
        return out;
    }
}