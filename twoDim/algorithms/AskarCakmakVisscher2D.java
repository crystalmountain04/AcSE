package acse.twoDim.algorithms;

/*
Program: AcSE - AcSE calculates the Schrödinger Equation
This Software provides the possibility to easily create your own quantum-mechanical simulations.

Copyright (C) 2009  Steffen Roland

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import acse.twoDim.solutions.*;
import acse.twoDim.potentials.*;
import acse.twoDim.interfaces.*;

/*
Implementiert die Algorithmus-Klasse mithilfe des Askar-Cakmak-Visscher Algorithmus
*/
public class AskarCakmakVisscher2D extends Algorithm {

    /*Konstrukutor*/
    public AskarCakmakVisscher2D(int xMax, int yMax, double a, double b, double c, double d, double dx, double dt, String potential, String initial, boolean isSolution) {
        super(xMax,yMax,a,b,c,d,dx,dt,potential,initial,isSolution);
    }

    /*Implementiert den Algorithmus*/
    public void nextTimeStep() {
        int i,j;
        double h=myConst.h;		/*plancksches Wirkungsquantum aus Konstantenset*/
        double m=myConst.m;		/*Teilchenmasse aus Konstantenset*/
        double[][] ReOld = new double[xMax][yMax];	/*Realteil des alten Zustands*/
        double[][] ImOld = new double[xMax][yMax];	/*Imaginärteil des alten Zustands*/

        /*Lokale Kopie des alten Zustandes*/
        for(i=0;i<xMax;i++) {
			for(j=0;j<yMax;j++) {
				ReOld[i][j]=psi[i][j].getReal();
				ImOld[i][j]=psi[i][j].getImag();
			}
        }

        /*neuen Realteil, Betragsquadrat berechnen mithilfe des AskarCakmakVisscher-Algorithmus*/
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
        
        /*neuen Imaginärteil berechnen*/
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

        /*Nächster Schritt innerhalb der analytischen Lösung, im Potential und Hochzählen der zeitlichen Schrittzahl*/
        if(hasSolution) ((Solution)mySol).nextTimeStep();
        ((Potential)myPot).nextTimeStep();
        n=n+1;
    }
}
