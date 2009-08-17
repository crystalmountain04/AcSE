package acse.oneDim.algorithms;

import java.lang.reflect.*;
import acse.oneDim.util.*;
import acse.oneDim.interfaces.*;

public abstract class Algorithm {
	protected String initial;
	protected String potential;
    protected Complex[] psi;
    protected double p2[];
    protected double x,dx,dt,a,b;
    protected double norm;
    protected double norm0;
    protected Constants myConst;
    protected int n;
    protected int max;
    protected int height;
    protected Object mySol;
    protected Object myPot;
    protected boolean hasSolution=false;

    public Algorithm(int max, int height, double a, double b, double dx, double dt, String potential, String initial, boolean isSolution) {
		this.potential = potential;
		this.initial = initial;
        this.height=height;             /*Höhe des Frames in px*/
        this.max = max;                 /*Breite des Frames in px*/

        this.a=a;                    /*"linkester" zu berechnender x-Wert (bei 0 px)*/
        this.b=b;                     /*"rechtester" zu berechnender x-Wert (bei max px)*/
        this.x=a;                       /*x = räumliche Variable*/
        this.dx=dx;              /*Schrittbreite (räumlich)*/
        this.dt=dt;    /*Schrittbreite (zeitlich)*/

        this.p2=new double[max];        /*Wahrscheinlichkeitsdichte*/
        this.norm=1.0;

        this.n=0;                       /*Anzahl Zeitschritte*/

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

        psi = new Complex[max];

        //True, falls mySol analytische Lösung der SGL ist
        hasSolution=isSolution;

        int i;
        //Festlegen des Anfangszustands und des Potentials
        for (i=0; i<max; i++)  {
          this.psi[i]=((Solution)mySol).getStatePsi(x);
          this.p2[i]=Math.pow(psi[i].getAbs(),2);
          this.norm0+=this.p2[i];
          x = x + dx;
        }
    }

    public int getMax() {
        return this.max;
    }

    public int getSteps() {
        return this.n;
    }

    public double getState(double x) {
		int i = (int)((x-a)/dx);
        return p2[i];
    }

    public double getPotential(double x) {
        return (((Potential)myPot).getValue(x));
    }

    public double getAnalyticState(double x) {
		//Künstlich den selben Rundungsfehler wie bei numerischer Lösung einbauen
		int i = (int)((x-a)/dx);
		x = a+i*dx;
        return (((Solution)mySol).getState(x));
    }

    public boolean getHasSolution() {
        return this.hasSolution;
    }

    public double getNorm() {
        return this.norm;
    }
	
	public String getPotential() {
		return this.potential;
	}
	
	public String getInitial() {
		return this.initial;
	}

    abstract public void nextTimeStep();
}
