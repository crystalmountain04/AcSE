package acse.oneDim.gui;

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

import java.io.*;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.Screenshot;
import java.awt.event.*;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.awt.Frame;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.*;

import acse.oneDim.util.*;
import acse.oneDim.algorithms.*;

import acse.oneDim.interfaces.*;
import acse.oneDim.potentials.*;
import acse.oneDim.solutions.*;

/*
Stellt die eigentliche Visualisierung dar; Erzeugt dazu ein
OpenGL-Fenster
*/
public class Visu implements GLEventListener, KeyListener {

    private static int xMax;	/*Anzahl Gitterpunkte*/
    private static int height;	/*Höhe des Simulationsfensters*/
	private static int width;	/*Breite des Simulationsfensters*/
	private static double dx;	/*Abstand Gitterpunkte*/
	private static double a;	/*linker Rand der Simulationsbox*/
	private static double b;	/*rechter Rand der Simulationsbox*/
	
	/*Anzeige-Option, können live umgeschaltet werden*/
    private boolean showPot=true;
    private boolean showAna=true;
    private boolean showCn=true;
    private boolean showAcv=true;
    private boolean showDiff=false;
    private boolean showEqAcv=false;
    private boolean showEqCn=false;
	private boolean showCoord=true;
	private boolean makeScreenshot=false; /*einzelner Screenshot*/
	private boolean makePause=false;
	private boolean logarithmic=false;
	
	/*vorher eingestellte Simulations-Optionen*/
    private boolean isSolution=false;	/*Ist analytische Lösung angegeben?*/
	private boolean makePlot=false;		/*Soll Normierung geplottet werden*/
    private boolean Cn=true;			/*Crank-Nicolson berechnen?*/
    private boolean Acv=true;			/*Askar-Cakmak-Visscher berechnen?*/
	private int makeScreens; 			/*Abstand zwischen automatischen Screenshots, 0 für nicht*/
	
	/*Die übergebenen Algorithmus-Objekte*/
	private AskarCakmakVisscher1D mySimAcv;
	private CrankNicholson1D mySimCn;

	/*Hilfsvariablen*/
    private File outFile;			/*Datei für die Speicherung der Normierungsdaten*/
    private String printFile;		/*Pfad zur Gnuplot-Skript-Datei*/
    private PrintWriter pWriter;
	private String sep = System.getProperty("file.separator");

	public Visu(AskarCakmakVisscher1D myAcv, CrankNicholson1D myCn,
				int xMax, int height, double a, double b, double dx,
				boolean Cn, boolean Acv, boolean makePlot, int makeScreens) {
		this.Cn=Cn;
        this.Acv=Acv;
		this.xMax=xMax;
		this.height=height;
		this.width=750;
		this.a=a;
		this.b=b;
		this.dx=(b-a)/(double)xMax;
		this.makePlot=makePlot;
		this.makeScreens=makeScreens;
		this.mySimAcv=myAcv;
		this.mySimCn=myCn;
		
		/*Initialisierung des Dateipfads für das gnuplot-skript*/
		String path = System.getProperty("user.dir");
        printFile=path+sep+"oneDim"+sep+"plotBoth.g";

		/*Ausgabe der verfügbaren Befehlstasten auf der Konsole*/
		printHelp();
		
		/*Initialisierung der Datei zum Schreiben der Normierungsdaten*/
		try {
			outFile = new java.io.File( "data.dat" );
			pWriter = new PrintWriter(new java.io.FileOutputStream(outFile), true);
		} catch(FileNotFoundException e) {
			outFile = null;
			pWriter = null;
		}
        
		start();
	}
	
	/*Erzeugt OpenGL-Simulationsfenster*/
	public void start() {
		final Frame frame = new Frame("Visualisierung Schr\u00f6dingergleichung in 1D");
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
        frame.add(canvas);
        frame.setSize(width, height);
        final Animator animator = new Animator(canvas);		
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        pWriter.close();
                        if(makePlot) plotNorm();
						frame.dispose();
                    }
                }).start();
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
	}

	/*Initialisierung des OpenGL-Bereichs*/
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(1);
        gl.glClearColor(0.95f, 0.95f, 0.95f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
    }

	/*Verarbeitet Größenänderungen des Simulationsfensters*/
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) {
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
	
	/*Zeichnet ein rudimentäres Koordinatensystem*/
	public void drawCoordSys(GL gl) {
		//y-Achse bei x=0 zeichnen
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		gl.glVertex2i(xMax/2,0);
		gl.glVertex2i(xMax/2,height);
		
		//x-Achse bei y=0 zeichnen
		gl.glVertex2i(0,10);
		gl.glVertex2i(width,10);
		
		/*Markierungen an der y-Achse zeichnen*/
		if(logarithmic) {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(201)/Math.log(10)*height));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(201)/Math.log(10)*height));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(401)/Math.log(10)*height));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(401)/Math.log(10)*height));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(601)/Math.log(10)*height));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(601)/Math.log(10)*height));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(801)/Math.log(10)*height));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(801)/Math.log(10)*height));
		}
		else {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.2*height));
			gl.glVertex2i(width/2+10,10+(int)(0.2*height));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.4*height));
			gl.glVertex2i(width/2+10,10+(int)(0.4*height));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.6*height));
			gl.glVertex2i(width/2+10,10+(int)(0.6*height));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.8*height));
			gl.glVertex2i(width/2+10,10+(int)(0.8*height));
		}
	}

	/*Wird nach jedem Zeitschritt aufgerufen und zeichnet alle sichtbaren Elemente*/
    public void display(GLAutoDrawable drawable) {
        /*
		Erstellen der Zeichenumgebung und des Koordinatensystems
        (0,0) unten links, (xMax, height) oben rechts)
		*/
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-0.5, xMax-0.5, -0.5, height-0.5, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
		
		double x=a; 	/*x beginnt am linken Rand der Simulationsbox*/
		int xPx=0;		/*x in Pixeln*/
		int xxPx=0;		/*x+dx in Pixeln*/
	
	    gl.glLineWidth(2.0f); /*Liniendicke*/
        gl.glBegin(GL.GL_LINES);
		
		/*Koordinatensystem zeichnen*/
		if(showCoord) drawCoordSys(gl);
		
        int i; 					/*räumlicher Index x=a+i*dx*/
        double tmpAcv1=0.0;		/*Wert des Wellenfunktionsbetragsquadrats mit AskarCakmakVisscher*/
        double tmpAcv2=0.0;		/*Wert einen räumlichen Schritt weiter*/
        double tmpCn1=0.0;		/*Wert des Wellenfunktionsbetragsquadrats mit CrankNicolson*/
        double tmpCn2=0.0;		/*Wert einen räumlichen Schritt weiter*/
        double tmpDiff1=0.0;	/*Differenz der beiden Algorithmen*/
        double tmpDiff2=0.0;	/*Differenz am jeweils nächsten Ortsschritt*/
		double tmpAna1=0.0;		/*analytischer Wert des Wellenfunktionsbetragsquadrats*/
		double tmpAna2=0.0;		/*analytischer Wert räumlicher Schritt weiter*/

		/*durchläuft die Simulationsbox*/
        for (i=0;i<xMax-1;i++) {
			/*Berechnen der Pixelkoordinaten*/
			xPx=(int)(x/dx+xMax/2);
			xxPx=(int)((x+dx)/dx+xMax/2);
					
            //Potential zeichhnen (grün)
            if(showPot){
                gl.glColor3f(0.3f, 0.5f, 0.3f);
                gl.glVertex2i(xPx,(int)(mySimAcv.getPotential(x))+10);
                gl.glVertex2i(xxPx,(int)(mySimAcv.getPotential(x+dx))+10);
            }
			
            //analytische Lösung zeichnen (blau)
            if(mySimAcv.getHasSolution()) {
				if(Acv) {
					tmpAna1=mySimAcv.getAnalyticState(x);
					tmpAna2=mySimAcv.getAnalyticState(x+dx);
				}
				else {
					tmpAna1=mySimCn.getAnalyticState(x);
					tmpAna2=mySimCn.getAnalyticState(x+dx);
				}
				/*unter Umständen: logarithmische Skalierung*/
				if(logarithmic) {
					tmpAna1=0.3*Math.log(tmpAna1*1000+1)/Math.log(10);
					tmpAna2=0.3*Math.log(tmpAna2*1000+1)/Math.log(10);
				}
				if(showAna) {
					gl.glColor3f(0.0f, 0.0f, 1.0f);
					gl.glVertex2i(xPx,(int)(tmpAna1*height)+10);
					gl.glVertex2i(xxPx,(int)(tmpAna2*height)+10);
				}
            }

            //AskarCakmakVisscher-Lösung zeichnen
            if(Acv) {
				/*unter Umständen: logarithmische Skalierung*/
				if(logarithmic) {
					tmpAcv1=0.3*Math.log(mySimAcv.getState(x)*1000+1)/Math.log(10);
					tmpAcv2=0.3*Math.log(mySimAcv.getState(x+dx)*1000+1)/Math.log(10);
				}
				else {
					tmpAcv1=mySimAcv.getState(x);
					tmpAcv2=mySimAcv.getState(x+dx);
				}
				if(showAcv) {
					gl.glColor3f(1.0f, 0.0f, 0.0f);
					gl.glVertex2i(xPx,(int)(tmpAcv1*height)+10);
					gl.glVertex2i(xxPx,(int)(tmpAcv2*height)+10);
				}
            }

            //CrankNicolson-Lösung zeichnen
            if(Cn) {
				/*unter Umständen: logarithmische Skalierung*/
				if(logarithmic) {
					tmpCn1=0.3*Math.log(mySimCn.getState(x)*1000+1)/Math.log(10);
					tmpCn2=0.3*Math.log(mySimCn.getState(x+dx)*1000+1)/Math.log(10);
				}
				else {
					tmpCn1=mySimCn.getState(x);
					tmpCn2=mySimCn.getState(x+dx);
				}
				if(showCn) {
					gl.glColor3f(1.0f, 0.0f, 1.0f);
					gl.glVertex2i(xPx,(int)(tmpCn1*height)+10);
					gl.glVertex2i(xxPx,(int)(tmpCn2*height)+10);
				}
            }

            //Differenz der beiden Algorithmen zeichnen
            if(showDiff) {
                gl.glColor3f(0.0f, 0.0f, 0.0f);
                gl.glVertex2i(xPx,(int)(Math.sqrt((tmpCn1-tmpAcv1)*(tmpCn1-tmpAcv1))*height+10));
                gl.glVertex2i(xxPx,(int)(Math.sqrt((tmpCn2-tmpAcv2)*(tmpCn2-tmpAcv2))*height+10));
            }

            //Differenz zur analytischen Lösung (Acv)
            if(showEqAcv) {
                tmpDiff1 = Math.sqrt(Math.pow(tmpAcv1-tmpAna1,2))*height;
                tmpDiff2 = Math.sqrt(Math.pow(tmpAcv2-tmpAna2,2))*height;
                gl.glColor3f(1.0f, 0.0f, 0.0f);
                gl.glVertex2i(xPx,(int)(tmpDiff1)+10);
                gl.glVertex2i(xxPx,(int)(tmpDiff2)+10);
            }

            //Differenz zur analytischen Lösung (Cn)
            if(showEqCn) {
                tmpDiff1 = Math.sqrt(Math.pow(tmpCn1-tmpAna1,2))*height;
                tmpDiff2 = Math.sqrt(Math.pow(tmpCn2-tmpAna2,2))*height;
                gl.glColor3f(1.0f, 0.0f, 1.0f);
                gl.glVertex2i(xPx,(int)(tmpDiff1)+10);
                gl.glVertex2i(xxPx,(int)(tmpDiff2)+10);
            }
			//räumlicher Schritt
			x+=dx;
        }
		
        gl.glEnd();
		
		//Screenshot machen
		if(makeScreenshot || (makeScreens!=0 && (Acv && mySimAcv.getSteps()%makeScreens==0)) || (makeScreens!=0 && (Cn && mySimCn.getSteps()%makeScreens==0))) {
			try {
				String sep = System.getProperty("file.separator");
				String path = System.getProperty("user.dir");
				if(Acv) path=path+sep+"acse"+sep+"screens"+sep+mySimAcv.getPotential()+"_"+mySimAcv.getInitial()+"_"+mySimAcv.getSteps()+".png";
				else path=path+sep+"acse"+sep+"screens"+sep+mySimCn.getPotential()+"_"+mySimCn.getInitial()+"_"+mySimCn.getSteps()+".png";
				Screenshot.writeToFile(new File(path),xMax-20,height-50);
				System.out.println("Screenshot in "+path+" gespeichert!");
			} catch(Exception e) {
				System.out.println("Fehler beim Screenshot!");
				System.out.println(e);
			}
			makeScreenshot=false;
		}

        /*Schreiben der Normierung in die Plot-Datei*/
		if(makePlot) {
			if(Acv) {
				if(mySimAcv.getSteps()%100==0) {
					pWriter.print(mySimAcv.getSteps()+"\t");
					pWriter.print((1-mySimCn.getNorm())*100000000.0+"\t");
					pWriter.print((1-mySimAcv.getNorm())*100000000.0+"\n");
				}
			}
			else {
				if(mySimCn.getSteps()%100==0) {
					pWriter.print(mySimCn.getSteps()+"\t");
					pWriter.print((1-mySimCn.getNorm())*100000000.0+"\t");
					pWriter.print((1-mySimAcv.getNorm())*100000000.0+"\n");
				}
			}
		}
		
		//nächster Zeitschritt, falls Simulation nicht pausiert ist
		if(!makePause) {
			if(Acv) mySimAcv.nextTimeStep();
			if(Cn) mySimCn.nextTimeStep();
		}
		
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
	
    @Override
    public void keyTyped(KeyEvent e) {
    }
	
    @Override
	/*Verarbeitet die Tastatureingaben und passt die Anzeigeparameter entsprechend an*/
    public void keyPressed(KeyEvent e) {
        char c=e.getKeyChar();
        if(c=='p') showPot=!showPot;
        if(c=='a' && mySimAcv.getHasSolution()) showAna=!showAna;
        if(c=='c' && Cn) showCn=!showCn;
        if(c=='v' && Acv) showAcv=!showAcv;
        if(c=='d' && Acv && Cn) showDiff=!showDiff;
        if(c=='w' && mySimAcv.getHasSolution() && Acv) showEqAcv=!showEqAcv;
        if(c=='e' && mySimAcv.getHasSolution() && Cn) showEqCn=!showEqCn;
		if(c=='s') makeScreenshot=true;
		if(c==' ') makePause=!makePause;
		if(c=='l') logarithmic=!logarithmic;
		if(c=='k') showCoord=!showCoord;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
	
	/*
	Plottet die Normierung der Wellenfunktion in eine Datei und auf dem Bildschirm;
	Hierfür wird die OpenSource-Software Gnuplot benötigt
	*/
    public void plotNorm() {
		String path = System.getProperty("user.dir");
        System.out.println("Plotte Normierung des Wellenpakets in "+path+sep+"Normierung.png");
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("wgnuplot "+printFile+" -");
        } catch(IOException e) {
            System.out.println("Gnuplot-Fehler!!!");
        }
    }
	
	/*Schreibt die schaltbaren Anzeigeparameter in die Konsole*/
	public void printHelp() {
		System.out.println("\n--- verwendbare Befehle im Simulationsfenster ---");
		System.out.println("p.....Potential an/aus");
		System.out.println("s.....Screenshot erstellen");
		System.out.println("l.....logarithmische Skala");
		System.out.println("k.....Koordinatenachsen an/aus");
		System.out.println("leer..Simulation pausieren");
		if(Cn) System.out.println("c.....Crank-Nicholson anzeigen/nicht anzeigen");
		if(Acv) System.out.println("v.....Askar-Cakmak-Visscher anzeigen/nicht anzeigen");
		if(mySimCn.getHasSolution() || mySimAcv.getHasSolution()) System.out.println("a.....analytische Loesung an/aus");
		if(Cn && Acv) System.out.println("d.....Differenz beider Algorithmen an/aus");
		if(mySimAcv.getHasSolution()&&Acv) System.out.println("w.....Differenz A.C.V.-Analytisch an/aus");
		if(mySimCn.getHasSolution()&&Cn) System.out.println("e.....Differenz C.N.-Analytisch an/aus");
		System.out.println("");
	}
}