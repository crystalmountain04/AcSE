package acse.oneDim.algorithms;

import acse.oneDim.solutions.*;
import acse.oneDim.potentials.*;
import acse.oneDim.interfaces.*;

public class AskarCakmakVisscher1D extends Algorithm {

    /*Konstrukutor*/
    public AskarCakmakVisscher1D(int max, int height, double a, double b, double dx, double dt, String potential, String initial, boolean isSolution) {
        super(max,height,a,b,dx,dt,potential,initial,isSolution);
    }

    /*Implementiert den Algorithmus*/
    public void nextTimeStep() {
        int i;
        double h=myConst.h;
        double m=myConst.m;
        double[] ReOld = new double[max];
        double[] ImOld = new double[max];

        /*Lokale Kopie des alten Zustandes*/
        for(i=0;i<max;i++) {
            ReOld[i]=psi[i].getReal();
            ImOld[i]=psi[i].getImag();
        }

        //Realteil, Betragsquadrat
        norm=0.0;
        double tmpPot;
		x=a;
        for ( i=1; i<max-1; i++ )  {
		  x=a+i*dx;
          tmpPot=((Potential)myPot).getValue(x);
          psi[i].setReal(ReOld[i] - h/(2*m)*dt/(dx*dx)*(ImOld[i+1] + ImOld[i-1]-2.0*ImOld[i])+dt/h*tmpPot*ImOld[i]);
          p2[i] = ReOld[i]*psi[i].getReal()+ImOld[i]*ImOld[i];
          norm+=p2[i];
        }
        norm+=p2[0];
        norm+=p2[max-1];
        
        //Imaginärteil
		x=a;
        for ( i=1; i<max-1; i++ )  {
		  x=a+i*dx;
          tmpPot=((Potential)myPot).getValue(x);
          psi[i].setImag(ImOld[i] + h/(2*m)*dt/(dx*dx)*(psi[i+1].getReal() + psi[i-1].getReal()-2.0*psi[i].getReal())-dt/h*tmpPot*psi[i].getReal());
        }

        //Nächster Schritt innerhalb der analytischen Lösung
        if(hasSolution) ((Solution)mySol).nextTimeStep();
        ((Potential)myPot).nextTimeStep();
        n=n+1;
        norm=norm/this.norm0;
    }
}
