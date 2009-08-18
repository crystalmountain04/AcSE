package acse.oneDim.solutions;

import acse.oneDim.util.*;
import acse.oneDim.interfaces.Solution;

public class GaussImpuls implements Solution {
private Constants myConst;
private double dt;
private int n;

public GaussImpuls(Constants myConst, double dt) {
this.myConst = myConst;
this.n=0;
this.dt=dt;
}

public double getState(double x) {
double out=0.0;
out=Math.pow(getStatePsi(x).getReal(),2)+Math.pow(getStatePsi(x).getImag(),2);

return out;
}

public void nextTimeStep() {
n+=1;
}

public Complex getStatePsi(double x) {
double t = n*dt;
double real=0.0;
double imag=0.0;
/*Beginn der Implementierung*/
double x0=myConst.x0;
double sig=myConst.sig;
real=Math.pow(1/(2*Math.PI*sig*sig), 0.25)*Math.exp(-(x-x0)*(x-x0)/(4*sig*sig))*Math.cos(10*(x-x0));
imag=Math.pow(1/(2*Math.PI*sig*sig), 0.25)*Math.exp(-(x-x0)*(x-x0)/(4*sig*sig))*Math.sin(10*(x-x0));
/*Ende der Implementierung*/

Complex out = new Complex(real,imag);
return out;
}
}