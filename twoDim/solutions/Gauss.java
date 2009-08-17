package acse.twoDim.solutions;

import acse.twoDim.interfaces.Solution;
import acse.twoDim.util.*;

public class Gauss implements Solution {
    private Constants myConst;
	private double k;
	private double x0;
	private double y0;
	private double m;
	private double sig;
	private double dt;
	private int n;

    public Gauss(Constants myConst, double dt) {
        this.k=myConst.k;
        this.m=myConst.m;
		this.x0=-Math.sqrt(7.5);
		this.y0=myConst.y0;
        this.sig=myConst.sig;
        this.n=0;
        this.dt=dt;
    }

    public double getState(double x, double y) {
        double out = 1/(2*Math.PI*sig*sig)*Math.exp(-1/(sig*sig)*((x-x0)*(x-x0)+(y-y0)*(y-y0)));
        return out;
    }

    public void nextTimeStep() {
        n=n+1;
    }

    public Complex getStatePsi(double x, double y) {
        Complex out = new Complex(
                Math.sqrt(getState(x,y)),
                0
                );
        return out;
    }
}