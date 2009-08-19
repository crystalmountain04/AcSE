package acse.twoDim.potentials;

import acse.twoDim.util.*;
import acse.twoDim.interfaces.Potential;

public class HarmOsHarmOs implements Potential {
private Constants myConst;
private double dt;
private int n;

public HarmOsHarmOs(Constants myConst, double dt) {
this.myConst = myConst;
this.dt = dt;
this.n=0;
}

public double getValue(double x, double y) {
double out=0.0;
double t = n*dt;
/*Beginn der Implementierung*/
out=5*(x*x+y*y);
/*Ende der Implementierung*/
return out;
}

public void nextTimeStep() {
n=n+1;
}
}
