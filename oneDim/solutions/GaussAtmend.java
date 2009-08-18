package acse.oneDim.solutions;

import acse.oneDim.util.*;
import acse.oneDim.interfaces.Solution;

public class GaussAtmend implements Solution {
private Constants myConst;
private double dt;
private int n;

public GaussAtmend(Constants myConst, double dt) {
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
double w=Math.sqrt(myConst.k/myConst.m);
double siga=myConst.sig*Math.sqrt(Math.cosh(myConst.delta)/Math.sinh(myConst.delta));
double sigb=myConst.sig*Math.sqrt(Math.tanh(myConst.delta));
double sigt =(siga-0.5*(siga+sigb))*Math.cos(2*w*t)+0.5*(siga+sigb);
double tmp = Math.pow(1/(2*Math.PI*sigt*sigt), 0.5)*Math.exp(-x*x/(2*sigt*sigt));
real = Math.sqrt(tmp);
imag=0.0;
/*Ende der Implementierung*/

Complex out = new Complex(real,imag);
return out;
}
}