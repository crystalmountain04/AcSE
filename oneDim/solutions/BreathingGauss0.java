package acse.oneDim.solutions;

import acse.oneDim.util.*;
import acse.oneDim.interfaces.*;

public class BreathingGauss0 implements Solution {
    private double x0;
    private double k;
    private double m;
    private double sig;
    private int n;
    private double dt;
    private double delta;

    public BreathingGauss0(Constants myConst, double dt) {
        this.x0=0;
        this.k=myConst.k;
        this.m=myConst.m;
        this.sig=myConst.sig;
        this.delta=myConst.delta;
        this.n=0;
        this.dt=dt;
    }
    public double getState(double x) {
        double t=n*dt;
        double w=Math.sqrt(k/m);
        double siga=sig*Math.sqrt(Math.cosh(delta)/Math.sinh(delta));
        double sigb=sig*Math.sqrt(Math.tanh(delta));
        double sigt =(siga-0.5*(siga+sigb))*Math.cos(2*w*t)+0.5*(siga+sigb);
        double x0Analytic = 0;
        double out = Math.pow(1/(2*Math.PI*sigt*sigt), 0.5)*Math.exp(-(x-x0Analytic)*(x-x0Analytic)/(2*sigt*sigt));
        return out;
    }
    public void nextTimeStep() {
        n=n+1;
    }

    public Complex getStatePsi(double x) {
        Complex out = new Complex(
                Math.sqrt(getState(x)),
                0
                );
        return out;
    }
}

