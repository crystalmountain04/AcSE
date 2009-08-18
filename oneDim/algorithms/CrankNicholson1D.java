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

import acse.oneDim.util.*;
import acse.oneDim.solutions.*;
import acse.oneDim.potentials.*;
import acse.oneDim.interfaces.*;

/*
Implementiert die Algorithmus-Klasse mithilfe des Crank-Nicolson Algorithmus
*/
public class CrankNicholson1D extends Algorithm {

    /*Konstrukutor*/
    public CrankNicholson1D(int max, double a, double b, double dx, double dt, String potential, String initial, boolean isSolution) {
        super(max,a,b,dx,dt,potential,initial,isSolution);
    }

    public void nextTimeStep() {
        double h=myConst.h;		/*plancksches Wirkungsquantum aus Konstantenset*/
        double m=myConst.m;		/*Teilchenmasse aus Konstantenset*/
        Complex[] c1 = new Complex[max];	/*Hilfsvariable c1 wie in der Bachelorarbeit beschrieben*/
        Complex[] c2 = new Complex[max];	/*Hilfsvariable c1 wie in der Bachelorarbeit beschrieben*/
        Complex[] tmpb = new Complex[max];	/*rechte Seite des Gleichungssystems*/

		x=a;
        double tmpPot;
		/*Erstellen des Hilfsvariablen-Arrays*/
        for(int i=0;i<max;i++) {
		    x=a+i*dx;
            tmpPot=((Potential)myPot).getValue(x);
            c1[i]=new Complex(-2.0-2*m*dx*dx*tmpPot/(h*h),4*m*dx*dx/(dt*h));
            c2[i]=new Complex(2.0+2*m*dx*dx*tmpPot/(h*h),4*m*dx*dx/(dt*h));
        }

        /*rechte Seite des Gleichungssystem (Ergebnisvektor) erstellen*/
        tmpb[0]=(c2[0].mul(psi[0])).sub(psi[1]);
        for(int i=1;i<max-1;i++) {
            tmpb[i]=((c2[i].mul(psi[i])).sub(psi[i+1])).sub(psi[i-1]);
        }
        tmpb[max-1]=(c2[max-1].mul(psi[max-1])).sub(psi[max-2]);

        /*Lösen des tridiagonalen LGS*/
        psi=Matrix.solveTriDi(max, c1, tmpb);

        /*Wkeitsdichte, Integrieren des Zustands*/
        norm=0.0;
        for(int i=0;i<max;i++) {
            p2[i]=psi[i].getAbs()*psi[i].getAbs();
            norm+=p2[i];
        }

        /*Nächster Schritt innerhalb der analytischen Lösung, des Potentials und Hochzählen der zeitlichen Schrittzahl*/
        if(hasSolution) ((Solution)mySol).nextTimeStep();
        ((Potential)myPot).nextTimeStep();
        n=n+1;
		/*Defintion des ursprünglichen Flächeninhalts als 1, aktueller Flächeninhalt darauf bezogen*/
        norm=norm/norm0;
    }
}
