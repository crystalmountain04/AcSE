package acse.twoPart.potentials;

import acse.twoPart.interfaces.Potential;
import acse.twoPart.util.*;

public class Anziehend implements Potential {
    private Constants myConst;
	private double dt;
	private int n;

    public Anziehend(Constants myConst, double dt) {
        this.myConst = myConst;
		this.dt = dt;
		this.n=0;
    }

    public double getValue(double x, double y) {
	    double out;
		out=30*(x-y)*(x-y);
		//if((0.01-Math.abs(x-y))<=0) out=0.0;
		//else out=-300;
		return out;
    }

    public void nextTimeStep() {
	    n=n+1;
	}
}