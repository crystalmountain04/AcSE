package acse.twoPart.solutions;

import acse.twoPart.util.*;
import acse.twoPart.interfaces.Solution;

public class GaussImpuls implements Solution {
private Constants myConst;
private double dt;
private int n;

public GaussImpuls(Constants myConst, double dt) {
this.myConst = myConst;
this.n=0;
this.dt=dt;
}

public double getState(double x, double y) {
double out=0.0;
out=Math.pow(getStatePsi(x,y).getReal(),2)+Math.pow(getStatePsi(x,y).getImag(),2);

return out;
}

public void nextTimeStep() {
n+=1;
}

public Complex getStatePsi(double x, double y) {
double real=0.0;
double imag=0.0;
double t=n*dt;
/*Beginn der Implementierung*/
double k1=10.0;
double k2=10.0;
double tmp=0.8*Math.exp(-(x+2)*(x+2)-(y-2)*(y-2));
real=tmp*Math.cos(k1*x-k2*y);
imag=tmp*Math.sin(k1*x-k2*y);
/*Ende der Implementierung*/
Complex out = new Complex(real,imag);
return out;
}
}