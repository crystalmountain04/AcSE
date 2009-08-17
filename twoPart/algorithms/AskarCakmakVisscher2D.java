package acse.twoPart.algorithms;

import acse.twoPart.solutions.*;
import acse.twoPart.potentials.*;
import acse.twoPart.interfaces.*;

public class AskarCakmakVisscher2D extends Algorithm {

    /*Konstrukutor*/
    public AskarCakmakVisscher2D(int xMax, int yMax, int height, double a, double b, double c, double d, double dx, double dt,
			double m1, double m2, String potential, String initial, boolean isSolution) {
        super(xMax,yMax,height,a,b,c,d,dx,dt,m1,m2,potential,initial,isSolution);
    }

    /*Implementiert den Algorithmus*/
    public void nextTimeStep() {
        int i,j,ii;
        double h=myConst.h;
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
			p2One[i]=0.0;
			for(j=1; j<yMax-1; j++) {
			  y=c+j*dx;
              tmpPot=((Potential)myPot).getValue(x,y);
              psi[i][j].setReal(
								ReOld[i][j] + 
								dt*(
									1.0/h*tmpPot*ImOld[i][j] - 
									h/(2.0*dx*dx) * (1.0/m1*(ImOld[i+1][j]+ImOld[i-1][j]-2*ImOld[i][j]) + 1.0/m2*(ImOld[i][j+1]+ImOld[i][j-1]-2*ImOld[i][j]))
									)
								);
              p2[i][j] = ReOld[i][j]*psi[i][j].getReal()+ImOld[i][j]*ImOld[i][j];
			  p2One[i] = p2One[i] + p2[i][j]*dx;
			}
        }
		
		y=c;
		for(j=1;j<yMax-1;j++) {
			y=c+j*dx;
			x=a;
			p2Two[j]=0.0;
			for(i=1;i<xMax-1;i++) {
				x=a+i*dx;
				p2Two[j] = p2Two[j] + p2[i][j]*dx;	
				//if(p2[i][j]*dx>0.01) System.out.println(p2[i][j]*dx);
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
            psi[i][j].setImag(
								ImOld[i][j] -  
								dt*(
									1.0/h*tmpPot*psi[i][j].getReal() - 
									h/(2.0*dx*dx) * (1.0/m1*(psi[i+1][j].getReal()+psi[i-1][j].getReal()-2*psi[i][j].getReal()) + 1.0/m2*(psi[i][j+1].getReal()+psi[i][j-1].getReal()-2*psi[i][j].getReal()))
									)
								);
		  }
        }

        //Nächster Schritt innerhalb der analytischen Lösung
        if(hasSolution) ((Solution)mySol).nextTimeStep();
        ((Potential)myPot).nextTimeStep();
        n=n+1;
    }
}
