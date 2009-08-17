package acse.oneDim.potentials;

import acse.oneDim.interfaces.Potential;
import acse.oneDim.util.*;

public class BarriereTwo implements Potential {
    private Constants myConst;
	private double dt;
	private int n;

    public BarriereTwo(Constants myConst, double dt) {
        this.myConst = myConst;
		this.dt = dt;
		this.n=0;
    }

    public double getValue(double x) {
		double out=0.0;
        if(x>6) out=500.0;
        return out;
    }

    public void nextTimeStep() {
	    n=n+1;
        double t=n*dt;
	}
}