package acse.oneDim.algorithms;

import acse.oneDim.util.*;
import acse.oneDim.solutions.*;
import acse.oneDim.potentials.*;
import acse.oneDim.interfaces.*;

public class CrankNicholson1D extends Algorithm {

    /*Konstrukutor*/
    public CrankNicholson1D(int max, int height, double a, double b, double dx, double dt, String potential, String initial, boolean isSolution) {
        super(max,height,a,b,dx,dt,potential,initial,isSolution);
    }

    public void nextTimeStep() {
        double h=myConst.h;
        double m=myConst.m;
        Complex[] c1 = new Complex[max];
        Complex[] c2 = new Complex[max];
        Complex[] tmpb = new Complex[max];

		x=a;
        double tmpPot;
        for(int i=0;i<max;i++) {
		    x=a+i*dx;
            tmpPot=((Potential)myPot).getValue(x);
            c1[i]=new Complex(-2.0-2*m*dx*dx*tmpPot/(h*h),4*m*dx*dx/(dt*h));
            c2[i]=new Complex(2.0+2*m*dx*dx*tmpPot/(h*h),4*m*dx*dx/(dt*h));
        }

        /*Ergebnis-Vektor*/
        tmpb[0]=(c2[0].mul(psi[0])).sub(psi[1]);
        for(int i=1;i<max-1;i++) {
            tmpb[i]=((c2[i].mul(psi[i])).sub(psi[i+1])).sub(psi[i-1]);
        }
        tmpb[max-1]=(c2[max-1].mul(psi[max-1])).sub(psi[max-2]);

        /*Lösen des tridiagonalen LGS*/
        psi=Matrix.solveTriDi(max, c1, tmpb);

        /*Wkeitsdichte*/
        norm=0.0;
        for(int i=0;i<max;i++) {
            p2[i]=psi[i].getAbs()*psi[i].getAbs();
            norm+=p2[i];
        }

        //Nächster Schritt innerhalb der analytischen Lösung
        if(hasSolution) ((Solution)mySol).nextTimeStep();
        ((Potential)myPot).nextTimeStep();
        n=n+1;
        norm=norm/norm0;
    }
}
