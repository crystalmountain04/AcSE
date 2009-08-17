package acse.oneDim.solutions;

import acse.oneDim.interfaces.Solution;
import acse.oneDim.util.*;

public class ImpulsGauss implements Solution {
    private Constants myConst;
	private double x0;
	private double k;
	private double m;
	private double sig;
	private double x0Analytic;
	private double dt;
	private int n;

    public ImpulsGauss(Constants myConst, double dt) {
        this.x0=myConst.x0;
        this.k=myConst.k;
        this.m=myConst.m;
        this.sig=myConst.sig;
        this.x0Analytic=x0;
        this.n=0;
        this.dt=dt;
    }

    public double getState(double x) {
        /*noch ohne Impuls!!!*/
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
                Math.sqrt(getState(x))*Math.cos(k*(x-x0)),
                Math.sqrt(getState(x))*Math.sin(k*(x-x0))
                );
        return out;
    }
}