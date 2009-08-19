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

import java.lang.reflect.*;
import acse.twoPart.util.*;
import acse.twoPart.interfaces.*;

/*
Diese abstrakte Klasse stellt bis auf die Implementierung des nächsten Zeitschritts
alle Funktionen zur Verfügung, welcher ein Algorithmus bereitstellen muss
*/
public abstract class Algorithm {
    protected Complex[][] psi;				/*diskretisierte Wellenfunktion zum aktuellen Zeitpunkt*/
    protected double p2[][];				/*Wahrscheinlichkeitsdichte gesamt*/
	protected double p2One[];				/*Wahrscheinlichkeitsdichte Teilchen 1*/
	protected double p2Two[];				/*Wahrscheinlichkeitsdichte Teilchen 2*/
    protected double x,dx,y,dt,a,b,c,d;		/*räumliche Variablen, Abstand Gitterpunkte, Ränder der Simulationsbox*/
	protected double m1,m2;					/*Teilchenmassen*/
    protected Constants myConst;			/*Konstantenset*/
    protected int n;						/*Anzahl Zeitschritte*/
    protected int xMax;						/*Anzahl Gitterpunkte Teilchen 1*/
	protected int yMax;						/*Anzahl Gitterpunkte Teilchen 2*/
    protected Object mySol;					/*Objekt für den Anfangszustand*/
    protected Object myPot;					/*Objekt für das Potential*/
	protected String potential;				/*Name des Anfangszustands*/
	protected String initial;				/*Name des Potentials*/

    public Algorithm(int xMax, int yMax, double a, double b, double c, double d, double dx, double dt,
			double m1, double m2, String potential, String initial) {
		this.potential = potential;
		this.initial = initial;
        this.xMax = xMax;
		this.yMax = yMax;
        this.a=a;
        this.b=b;
		this.c=c;
		this.d=d;
        this.x=a;
		this.y=c;
        this.dx=dx;
        this.dt=dt;
		this.m1=m1;
		this.m2=m2;
        this.p2=new double[xMax][yMax];
		this.p2One=new double[xMax];
		this.p2Two=new double[yMax];
        this.n=0;

        //Erstellen eines physikalischen Konstanten-Sets
        this.myConst = new Constants();

        /*Dynamisches Erstellen des Solution-Objektes*/
        try {
            Class myClass = Class.forName("acse.twoPart.solutions."+initial);
            Class[] formparas = new Class[2];
            formparas[0]=Constants.class;
            formparas[1]=double.class;
            Constructor cons = myClass.getConstructor(formparas);
            Object[] actargs = new Object[] {myConst,dt};
            mySol=cons.newInstance(actargs);
        } catch(Exception e) {
            System.out.println("Ung\u00fcltige Initialisierung!");
            //System.exit(1);
        }

        /*Dynamisches Erstellen des Potential-Objektes*/
        try {
            Class myClass = Class.forName("acse.twoPart.potentials."+potential);
            Class[] formparas = new Class[2];
            formparas[0]=Constants.class;
            formparas[1]=double.class;
            Constructor cons = myClass.getConstructor(formparas);
            Object[] actargs = new Object[] {myConst,dt};
            myPot=cons.newInstance(actargs);
        } catch(Exception e) {
            System.out.println("Ung\u00fcltiges Potential!");
            //System.exit(1);
        }

        psi = new Complex[xMax][yMax];

        int i,j;
        //Festlegen des Anfangszustands
        for (i=0; i<xMax; i++)  {
		  y=c;
		  for(j=0; j<yMax; j++) {
            this.psi[i][j]=((Solution)mySol).getStatePsi(x,y);
			this.p2[i][j]=((Solution)mySol).getState(x,y);
			y = y + dx;
		  }
		  x = x + dx;
        }
    }

	/*Liefert die Anzahl der Gitterpunkte Teilchen 1*/
    public int getXMax() {
        return this.xMax;
    }
	
	/*Liefert die Anzahl der Gitterpunkte Teilchen 2*/
	public int getYMax() {
		return this.yMax;
	}

	/*Liefert die Anzahl der getätigten Zeitschritte*/
    public int getSteps() {
        return this.n;
    }

	/*Liefert die Wahrscheinlichkeitsdichte am für Teilchen 1 bei x, Teilchen 2 bei y*/
    public double getState(double x, double y) {
		int i = (int)((x-a)/dx);
		int j = (int)((y-c)/dx);
        return p2[i][j];
    }
	
	/*Liefert die Wahrscheinlichkeitsdichte des 1. Teilchens am Ort x*/
	public double getStateOne(double x) {
		int i = (int)((x-a)/dx);
		return p2One[i];
	}
	
	/*Liefert die Wahrscheinlichkeitsdichte des 2. Teilchens am Ort x*/
	public double getStateTwo(double x) {
		int i = (int)((x-a)/dx);
		return p2Two[i];
	}

	/*Liefert das Potential am Ort (x,y)*/
    public double getPotential(double x, double y) {
        return (((Potential)myPot).getValue(x,y));
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
