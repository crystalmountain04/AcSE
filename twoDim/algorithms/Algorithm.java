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

import java.lang.reflect.*;
import acse.twoDim.util.*;
import acse.twoDim.interfaces.*;

/*
Diese abstrakte Klasse stellt bis auf die Implementierung des nächsten Zeitschritts
alle Funktionen zur Verfügung, welcher ein Algorithmus bereitstellen muss
*/
public abstract class Algorithm {
    protected Complex[][] psi;				/*diskretisierte Wellenfunktion zum aktuellen Zeitpunkt*/
    protected double p2[][];				/*Wahrscheinlichkeitsdichte*/
    protected double x,dx,y,dt,a,b,c,d;		/*räumliche Variablen, Abstand Gitterpunkte, Ränder der Simulationsbox*/
    protected Constants myConst;			/*KonstantenSet*/
    protected int n;						/*Anzahl Zeitschritte*/
    protected int xMax;						/*Anzahl Gitterpunkte x-Richtung*/
	protected int yMax;						/*Anzahl Gitterpunkte y-Richtung*/
    protected Object mySol;					/*Objekt für den Anfangszustand*/
    protected Object myPot;					/*Objekt für das Potential*/
    protected boolean hasSolution=false;	/*Analytische Lösung?*/
	protected String potential;				/*Name des Anfangszustands*/
	protected String initial;				/*Name des Potentials*/

    public Algorithm(int xMax, int yMax, double a, double b, double c, double d, double dx, double dt, String potential, String initial, boolean isSolution) {
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
        this.p2=new double[xMax][yMax];        
        this.n=0;                       

        //Erstellen eines physikalischen Konstanten-Sets
        this.myConst = new Constants();

        /*Dynamisches Erstellen des Solution-Objektes*/
        try {
            Class myClass = Class.forName("acse.twoDim.solutions."+initial);
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
            Class myClass = Class.forName("acse.twoDim.potentials."+potential);
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

        psi = new Complex[xMax][yMax];

        //True, falls mySol analytische Lösung der SGL ist
        hasSolution=isSolution;

        int i,j;
        //Festlegen des Anfangszustands
        for (i=0; i<xMax; i++)  {
		  y=c;
		  for(j=0; j<yMax; j++) {
            this.psi[i][j]=((Solution)mySol).getStatePsi(x,y);
            this.p2[i][j]=Math.pow(psi[i][j].getAbs(),2);
			y = y + dx;
		  }
		  x = x + dx;
        }
    }

	/*Liefert die Anzahl der Gitterpunkte in x-Richtung*/
    public int getXMax() {
        return this.xMax;
    }
	
	/*Liefert die Anzahl der Gitterpunkte in y-Richtung*/
	public int getYMax() {
		return this.yMax;
	}

	/*Liefert die Anzahl der getätigten Zeitschritte*/
    public int getSteps() {
        return this.n;
    }

	/*Liefert die Wahrscheinlichkeitsdichte am Ort (x,y)*/
    public double getState(double x, double y) {
		int i = (int)((x-a)/dx);
		int j = (int)((y-c)/dx);
        return p2[i][j];
    }

	/*Liefert das Potential am Ort (x,y)*/
    public double getPotential(double x, double y) {
        return (((Potential)myPot).getValue(x,y));
    }

	/*Liefert die analytische Lösung*/
    public double getAnalyticState(double x, double y) {
        return (((Solution)mySol).getState(x,y));
    }

	/*Gibt es analytische Lösung?*/
    public boolean getHasSolution() {
        return this.hasSolution;
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
