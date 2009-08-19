package acse.twoPart.algorithms;

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

import acse.twoPart.solutions.*;
import acse.twoPart.potentials.*;
import acse.twoPart.interfaces.*;

/*
Implementiert die Algorithmus-Klasse mithilfe des Askar-Cakmak-Visscher Algorithmus
*/
public class AskarCakmakVisscher2D extends Algorithm {

    /*Konstrukutor*/
    public AskarCakmakVisscher2D(int xMax, int yMax, double a, double b, double c, double d, double dx, double dt,
			double m1, double m2, String potential, String initial) {
        super(xMax,yMax,a,b,c,d,dx,dt,m1,m2,potential,initial);
    }

    /*Implementiert den Algorithmus*/
    public void nextTimeStep() {
        int i,j;
        double h=myConst.h;								/*plancksches Wirkungsquantum aus Konstantenset*/
        double[][] ReOld = new double[xMax][yMax];		/*Realteil des alten Zustands*/
        double[][] ImOld = new double[xMax][yMax];		/*Imaginärteil des alten Zustands*/

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
			  /*Integration zur Berechnung der Wkeitsdichte des 1. Teilchens*/
			  p2One[i] = p2One[i] + p2[i][j]*dx;
			}
        }
		
		/*Integration zur Berechnung der Wkeitsdichte des 2. Teilchens*/
		y=c;
		for(j=1;j<yMax-1;j++) {
			y=c+j*dx;
			x=a;
			p2Two[j]=0.0;
			for(i=1;i<xMax-1;i++) {
				x=a+i*dx;
				p2Two[j] = p2Two[j] + p2[i][j]*dx;	
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
            psi[i][j].setImag(
								ImOld[i][j] -  
								dt*(
									1.0/h*tmpPot*psi[i][j].getReal() - 
									h/(2.0*dx*dx) * (1.0/m1*(psi[i+1][j].getReal()+psi[i-1][j].getReal()-2*psi[i][j].getReal()) + 1.0/m2*(psi[i][j+1].getReal()+psi[i][j-1].getReal()-2*psi[i][j].getReal()))
									)
								);
		  }
        }

        /*Nächster Schritt innerhalb des Potentials und Hochzählen der zeitlichen Schrittzahl*/
        ((Potential)myPot).nextTimeStep();
        n=n+1;
    }
}
