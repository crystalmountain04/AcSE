package acse.oneDim.potentials;

import acse.oneDim.util.*;
import acse.oneDim.interfaces.Potential;

public class Sombrero implements Potential {
private Constants myConst;
private double dt;
private int n;

/*Definition möglicher Konstanten*/
public Sombrero(Constants myConst, double dt) {
this.myConst = myConst;
this.dt = dt;
this.n=0;
}

public double getValue(double x) {
double out=0.0;
/*Implementierung des Potentials*/
//out=-30*x*x+x*x*x*x+230;
//out=-15*x*x+x*x*x*x+100;
out=-10*x*x+x*x*x*x+50;
//out=-30*x*x+x*x*x*x+230;
return out;
}

public void nextTimeStep() {
n=n+1;
double t=n*dt;
/*Änderung nach jedem Zeitschritt*/}
}
