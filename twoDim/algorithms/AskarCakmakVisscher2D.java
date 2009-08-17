package acse.twoDim.algorithms;

import acse.twoDim.solutions.*;
import acse.twoDim.potentials.*;
import acse.twoDim.interfaces.*;

public class AskarCakmakVisscher2D extends Algorithm {

    /*Konstrukutor*/
    public AskarCakmakVisscher2D(int xMax, int yMax, int height, double a, double b, double c, double d, double dx, double dt, String potential, String initial, boolean isSolution) {
        super(xMax,yMax,height,a,b,c,d,dx,dt,potential,initial,isSolution);
    }

    /*Implementiert den Algorithmus*/
    public void nextTimeStep() {
        int i,j;
        double h=myConst.h;
        double m=myConst.m;
        double[][] ReOld = new double[xMax][yMax];
        double[][] ImOld = new double[xMax][yMax];

        /*Lokale Kopie des alten Zustandes*/
        for(i=0;i<xMax;i++) {
			for(j=0;j<yMax;j++) {
				ReOld[i][j]=psi[i][j].getReal();
				ImOld[i][j]=psi[i][j].getImag();
			}
        }

        //Realteil, Betragsquadrat
        double tmpPot;
		x=a;
        for ( i=1; i<xMax-1; i++ )  {
			x=a+i*dx;
			y=c;
			for(j=1; j<yMax-1; j++) {
			  y=c+j*dx;
              tmpPot=((Potential)myPot).getValue(x,y);
              psi[i][j].setReal(ReOld[i][j] - h/(2*m)*dt/(dx*dx)*(ImOld[i+1][j]+ImOld[i-1][j]+ImOld[i][j+1]+ImOld[i][j-1]-4.0*ImOld[i][j])+dt/h*tmpPot*ImOld[i][j]);
              p2[i][j] = ReOld[i][j]*psi[i][j].getReal()+ImOld[i][j]*ImOld[i][j];
			}
        }
        
        //Imaginärteil
		x=a;
        for ( i=1; i<xMax-1; i++ )  {
		  x=a+i*dx;
		  y=c;
		  for(j=1;j<yMax-1;j++) {
		    y=c+j*dx;
            tmpPot=((Potential)myPot).getValue(x,y);
            psi[i][j].setImag(ImOld[i][j] + h/(2*m)*dt/(dx*dx)*(psi[i+1][j].getReal()+psi[i-1][j].getReal()+psi[i][j+1].getReal()+psi[i][j-1].getReal()-4.0*psi[i][j].getReal())-dt/h*tmpPot*psi[i][j].getReal());
		  }
        }

        //Nächster Schritt innerhalb der analytischen Lösung
        if(hasSolution) ((Solution)mySol).nextTimeStep();
        ((Potential)myPot).nextTimeStep();
        n=n+1;
    }
}
