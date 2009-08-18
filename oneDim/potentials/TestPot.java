package acse.oneDim.potentials;

import acse.oneDim.util.*;
import acse.oneDim.interfaces.Potential;

public class TestPot implements Potential {
private Constants myConst;
private double dt;
private int n;

public TestPot(Constants myConst, double dt) {
this.myConst = myConst;
this.dt = dt;
this.n=0;
}

public double getValue(double x) {
double t=n*dt;
double out=0.0;
/*Beginn der Implementierung*/
/*Implementierung des Potentials*/
out=5*x*x;
/*Ende der Implementierung*/
return out;
}

public void nextTimeStep() {
n=n+1;
}
}
