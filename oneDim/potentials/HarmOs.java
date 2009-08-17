package acse.oneDim.potentials;

import acse.oneDim.util.*;
import acse.oneDim.interfaces.Potential;

public class HarmOs implements Potential {
private Constants myConst;
private double dt;
private int n;

public HarmOs(Constants myConst, double dt) {
this.myConst = myConst;
this.dt = dt;
this.n=0;
}

public double getValue(double x) {
double t=n*dt;
double out=0.0;
/*Implementierung des Potentials*/
out=myConst.k/2.0*x*x;return out;
}

public void nextTimeStep() {
n=n+1;
}
}
