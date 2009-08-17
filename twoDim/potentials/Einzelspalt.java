package acse.twoDim.potentials;

import acse.twoDim.interfaces.Potential;
import acse.twoDim.util.*;

public class Einzelspalt implements Potential {
    private Constants myConst;
	private double dt;
	private int n;

    public Einzelspalt(Constants myConst, double dt) {
        this.myConst = myConst;
		this.dt = dt;
		this.n=0;
    }

    public double getValue(double x, double y) {
		double out=0.0;
		if(x<1&&x>0) out = 200;
		if(y<0.5&&y>-0.5) out = 0;
		return out;
    }

    public void nextTimeStep() {
	    n=n+1;
	}
}