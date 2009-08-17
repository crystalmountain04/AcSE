package acse.twoPart.potentials;

import acse.twoPart.interfaces.Potential;
import acse.twoPart.util.*;

public class Abstossend implements Potential {
    private Constants myConst;
	private double dt;
	private int n;

    public Abstossend(Constants myConst, double dt) {
        this.myConst = myConst;
		this.dt = dt;
		this.n=0;
    }

    public double getValue(double x, double y) {
	    double out;
		if(x!=y) out=1/((x-y)*(x-y));
		else out=500;
		return out;
    }

    public void nextTimeStep() {
	    n=n+1;
	}
}