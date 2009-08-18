package acse.oneDim.algorithms;

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

import acse.oneDim.solutions.*;
import acse.oneDim.potentials.*;
import acse.oneDim.interfaces.*;

/*
Implementiert die Algorithmus-Klasse mithilfe des Askar-Cakmak-Visscher Algorithmus
*/
public class AskarCakmakVisscher1D extends Algorithm {

    /*Konstrukutor*/
    public AskarCakmakVisscher1D(int max, double a, double b, double dx, double dt, String potential, String initial, boolean isSolution) {
        super(max,a,b,dx,dt,potential,initial,isSolution);
    }

    public void nextTimeStep() {
        int i;
        double h=myConst.h;		/*plancksches Wirkungsquantum aus Konstantenset*/
        double m=myConst.m;		/*Teilchenmasse aus Konstantenset*/
        double[] ReOld = new double[max];	/*Realteil des alten Zustands*/
        double[] ImOld = new double[max];	/*Imaginärteil des alten Zustands*/

        /*Lokale Kopie des alten Zustandes*/
        for(i=0;i<max;i++) {
            ReOld[i]=psi[i].getReal();
            ImOld[i]=psi[i].getImag();
        }

        /*neuen Realteil, Betragsquadrat berechnen mithilfe des AskarCakmakVisscher-Algorithmus*/
        norm=0.0;
        double tmpPot;
		x=a;
        for ( i=1; i<max-1; i++ )  {
		  x=a+i*dx;
          tmpPot=((Potential)myPot).getValue(x);
          psi[i].setReal(ReOld[i] - h/(2*m)*dt/(dx*dx)*(ImOld[i+1] + ImOld[i-1]-2.0*ImOld[i])+dt/h*tmpPot*ImOld[i]);
          p2[i] = ReOld[i]*psi[i].getReal()+ImOld[i]*ImOld[i];
          norm+=p2[i];	/*Integration über den aktuellen Zustand*/
        }
        norm+=p2[0];
        norm+=p2[max-1];
        
        /*neuen Imaginärteil berechnen*/
		x=a;
        for ( i=1; i<max-1; i++ )  {
		  x=a+i*dx;
          tmpPot=((Potential)myPot).getValue(x);
          psi[i].setImag(ImOld[i] + h/(2*m)*dt/(dx*dx)*(psi[i+1].getReal() + psi[i-1].getReal()-2.0*psi[i].getReal())-dt/h*tmpPot*psi[i].getReal());
        }

        /*Nächster Schritt innerhalb der analytischen Lösung, im Potential und Hochzählen der zeitlichen Schrittzahl*/
        if(hasSolution) ((Solution)mySol).nextTimeStep();
        ((Potential)myPot).nextTimeStep();
        n=n+1;
		/*Defintion des ursprünglichen Flächeninhalts als 1, aktueller Flächeninhalt darauf bezogen*/
        norm=norm/this.norm0;
    }
}
