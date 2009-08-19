package acse.twoPart.potentials;

import acse.twoPart.util.*;
import acse.twoPart.interfaces.Potential;

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
double out=0.0;
double t = n*dt;
/*Beginn der Implementierung*/
out=30*(x-y)*(x-y);
/*Ende der Implementierung*/
return out;
}

public void nextTimeStep() {
n=n+1;
}
}
