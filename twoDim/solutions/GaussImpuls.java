package acse.twoDim.solutions;

import acse.twoDim.util.*;
import acse.twoDim.interfaces.Solution;

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
double x0=4;
double y0=0;
real=0.8*Math.exp(-(x-x0)*(x-x0)-(y-y0)*(y-y0))*Math.cos(-10*x);
imag=0.8*Math.exp(-(x-x0)*(x-x0)-(y-y0)*(y-y0))*Math.sin(-10*x);
/*Ende der Implementierung*/
Complex out = new Complex(real,imag);
return out;
}
}