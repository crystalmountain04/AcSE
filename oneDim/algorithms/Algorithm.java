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

import java.lang.reflect.*;
import acse.oneDim.util.*;
import acse.oneDim.interfaces.*;

/*
Diese abstrakte Klasse stellt bis auf die Implementierung des nächsten Zeitschritts
alle Funktionen zur Verfügung, welcher ein Algorithmus bereitstellen muss
*/
public abstract class Algorithm {
	protected String initial;		/*Name des Anfangszustands*/
	protected String potential;		/*Name des Potentials*/
    protected Complex[] psi;		/*Diskretisierte Wellenfunktion zum aktuellen Zeitpunkt*/
    protected double p2[];			/*Betragsquadrat der Wellenfunktion = Wahrscheinlichkeitsdichte*/
    protected double x,dx,dt,a,b;	/*Ortsvariable, Schrittweiten, Ränder der Simulationsbox*/
    protected double norm;			/*aktueller Flächeninhalt der Wahrscheinlichkeitsdichte*/
    protected double norm0;			/*Flächeninhalt zu Beginn der Simulation*/
    protected Constants myConst;	/*Konstantenset*/
    protected int n;				/*zeitliche Schrittzahl (t=n*dt)*/
    protected int max;				/*Anzahl Gitterpunkte*/
    protected Object mySol;
    protected Object myPot;
    protected boolean hasSolution=false;

    public Algorithm(int max, double a, double b, double dx, double dt, String potential, String initial, boolean isSolution) {
		this.potential = potential;
		this.initial = initial;
        this.max = max;
        this.a=a;
        this.b=b;
        this.x=a;
        this.dx=dx;
        this.dt=dt;
        this.p2=new double[max];
        this.norm=1.0;
        this.n=0;

        //Erstellen eines physikalischen Konstanten-Sets
        this.myConst = new Constants();

        /*Dynamisches Erstellen des Solution-Objektes*/
        try {
            Class myClass = Class.forName("acse.oneDim.solutions."+initial);
            Class[] formparas = new Class[2];
            formparas[0]=Constants.class;
            formparas[1]=double.class;
            Constructor cons = myClass.getConstructor(formparas);
            Object[] actargs = new Object[] {myConst,dt};
            mySol=cons.newInstance(actargs);
        } catch(Exception e) {
            System.out.println("Ung\u00fcltige Initialisierung!");
			System.exit(1);
        }

        /*Dynamisches Erstellen des Potential-Objektes*/
        try {
            Class myClass = Class.forName("acse.oneDim.potentials."+potential);
            Class[] formparas = new Class[2];
            formparas[0]=Constants.class;
            formparas[1]=double.class;
            Constructor cons = myClass.getConstructor(formparas);
            Object[] actargs = new Object[] {myConst,dt};
            myPot=cons.newInstance(actargs);
        } catch(Exception e) {
            System.out.println("Ung\u00fcltiges Potential!");
			System.exit(1);
        }

        //True, falls mySol analytische Lösung der SGL ist
        hasSolution=isSolution;

        psi = new Complex[max];
        int i;
        //Festlegen des Anfangszustands, Berechnung des Flächeninhalts
        for (i=0; i<max; i++)  {
          this.psi[i]=((Solution)mySol).getStatePsi(x);
          this.p2[i]=Math.pow(psi[i].getAbs(),2);
          this.norm0+=this.p2[i];
          x = x + dx;
        }
    }

	/*Liefert die Anzahl der Gitterpunkte*/
    public int getMax() {
        return this.max;
    }

	/*Liefert die Anzahl der getätigten Zeitschritte*/
    public int getSteps() {
        return this.n;
    }

	/*Liefert die Wahrscheinlichkeitsdichte am Ort x*/
    public double getState(double x) {
		int i = (int)((x-a)/dx);
        return p2[i];
    }

	/*Liefert das Potential am Ort x*/
    public double getPotential(double x) {
        return (((Potential)myPot).getValue(x));
    }

	/*Liefert die analytische Lösung*/
    public double getAnalyticState(double x) {
		/*
		Künstlich den selben Rundungsfehler wie bei numerischer Lösung einbauen,
		entsteht bei Umrechnung von Ortsvariable x in diskreten Ortspunkt i 
		*/
		int i = (int)((x-a)/dx);
		x = a+i*dx;
        return (((Solution)mySol).getState(x));
    }

	/*Gibt es analytische Lösung?*/
    public boolean getHasSolution() {
        return this.hasSolution;
    }

	/*Flächeninhalt der Wkeitsdichte im aktuellen Zustand*/
    public double getNorm() {
        return this.norm;
    }
	
	/*Liefert Namen des Potentials*/
	public String getPotential() {
		return this.potential;
	}
	
	/*Liefert Namen des Anfangszustands*/
	public String getInitial() {
		return this.initial;
	}

	/*
	Implementierung des eigentlichen Algorithmus zur Berechnung
	des zeitlich weiterentwickelten Zustands
	*/
    abstract public void nextTimeStep();
}
