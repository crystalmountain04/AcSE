package acse.twoDim.solutions;

import acse.twoDim.interfaces.Solution;
import acse.twoDim.util.*;

public class ImpulsGauss implements Solution {
    private Constants myConst;
	private double k;
	private double x0;
	private double y0;
	private double m;
	private double sig;
	private double dt;
	private int n;

    public ImpulsGauss(Constants myConst, double dt) {
        this.k=myConst.k;
		this.x0=myConst.x0;
		this.y0=myConst.y0;
        this.m=myConst.m;
        this.sig=myConst.sig;
        this.n=0;
        this.dt=dt;
    }

    public double getState(double x, double y) {
		double out=getStatePsi(x,y).getReal()*getStatePsi(x,y).getReal()+getStatePsi(x,y).getImag()+getStatePsi(x,y).getImag();
        return out;
    }

    public void nextTimeStep() {
        n=n+1;
    }

    public Complex getStatePsi(double x, double y) {
        Complex out = new Complex(
                Math.pow(1/(2*Math.PI*sig*sig), 0.5)*Math.exp(-1/(2*sig*sig)*((x-x0)*(x-x0)+(y-y0)*(y-y0)))*Math.cos(-k*x),
                Math.pow(1/(2*Math.PI*sig*sig), 0.5)*Math.exp(-1/(2*sig*sig)*((x-x0)*(x-x0)+(y-y0)*(y-y0)))*Math.sin(-k*x)
                );
        return out;
    }
}