package acse.twoDim.potentials;

import acse.twoDim.util.*;
import acse.twoDim.interfaces.Potential;

public class Doppelspalt implements Potential {
private Constants myConst;
private double dt;
private int n;

public Doppelspalt(Constants myConst, double dt) {
this.myConst = myConst;
this.dt = dt;
this.n=0;
}

public double getValue(double x, double y) {
double out=0.0;
double t = n*dt;
/*Beginn der Implementierung*/
if(x<1&&x>0) out = 400;
if(y<0.6&&y>0.1) out = 0;
if(y<-0.1&&y>-0.6) out = 0;
/*Ende der Implementierung*/
return out;
}

public void nextTimeStep() {
n=n+1;
}
}
