package acse.twoDim.algorithms;

import java.lang.reflect.*;
import acse.twoDim.util.*;
import acse.twoDim.interfaces.*;

public abstract class Algorithm {
    protected Complex[][] psi;
    protected double p2[][];
    protected double x,dx,y,dt,a,b,c,d;
    protected Constants myConst;
    protected int n;
    protected int xMax;
	protected int yMax;
    protected int height;
    protected Object mySol;
    protected Object myPot;
    protected boolean hasSolution=false;
	protected String potential;
	protected String initial;

    public Algorithm(int xMax, int yMax, int height, double a, double b, double c, double d, double dx, double dt, String potential, String initial, boolean isSolution) {
		this.potential = potential;
		this.initial = initial;
        this.height=height;             /*Höhe des Frames in px*/
        this.xMax = xMax;                 /*Breite des Frames in px*/
		this.yMax = yMax;

        this.a=a;                    /*"linkester" zu berechnender x-Wert (bei 0 px)*/
        this.b=b;                     /*"rechtester" zu berechnender x-Wert (bei max px)*/
		this.c=c;					/*"linkester" zu berechnender y-Wert (bei 0 px)*/
		this.d=d;					/*"rechtester" zu berechnender y-Wert (bei max px)*/
        this.x=a;                       /*x = räumliche Variable*/
		this.y=c;
        this.dx=dx;              /*Schrittbreite (räumlich)*/
        this.dt=dt;    /*Schrittbreite (zeitlich)*/

        this.p2=new double[xMax][yMax];        /*Wahrscheinlichkeitsdichte*/

        this.n=0;                       /*Anzahl Zeitschritte*/

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
        //Festlegen des Anfangszustands und des Potentials
        for (i=0; i<xMax; i++)  {
		  y=c;
		  for(j=0; j<yMax; j++) {
            this.psi[i][j]=((Solution)mySol).getStatePsi(x,y);
            this.p2[i][j]=Math.pow(psi[i][j].getAbs(),2);
			//if(p2[i][j]>0.01) System.out.println(this.p2[i][j]);
			y = y + dx;
		  }
		  x = x + dx;
        }
    }

    public int getXMax() {
        return this.xMax;
    }
	
	public int getYMax() {
		return this.yMax;
	}

    public int getSteps() {
        return this.n;
    }

    public double getState(double x, double y) {
		int i = (int)((x-a)/dx);
		int j = (int)((y-c)/dx);
        return p2[i][j];
    }

    public double getPotential(double x, double y) {
        return (((Potential)myPot).getValue(x,y));
    }

    public double getAnalyticState(double x, double y) {
        return (((Solution)mySol).getState(x,y));
    }

    public boolean getHasSolution() {
        return this.hasSolution;
    }
	
	public String getPotential() {
		return this.potential;
	}
	
	public String getInitial() {
		return this.initial;
	}

    abstract public void nextTimeStep();
}
