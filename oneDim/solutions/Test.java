package acse.oneDim.solutions;

import acse.oneDim.util.*;
import acse.oneDim.interfaces.Solution;

public class Test implements Solution {
private Constants myConst;
private double dt;
private int n;

public Test(Constants myConst, double dt) {
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
real=0.8*Math.exp(-x*x);
imag=0;
/*Ende der Implementierung*/

Complex out = new Complex(real,imag);
return out;
}
}