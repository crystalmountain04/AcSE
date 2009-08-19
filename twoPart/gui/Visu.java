package acse.twoPart.gui;

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

import acse.twoPart.util.*;
import acse.twoPart.algorithms.*;

import acse.twoPart.interfaces.*;
import acse.twoPart.potentials.*;
import acse.twoPart.solutions.*;

/*
Stellt die eigentliche Visualisierung dar; Erzeugt dazu ein
OpenGL-Fenster
*/
public class Visu implements GLEventListener, KeyListener {

    private static int xMax;		/*Anzahl Gitterpunkte*/
	private static int height;		/*Höhe des Simulationsfensters*/
	private static int width;		/*Breite des Simulationsfensters*/
	private static double dx;		/*Abstand Gitterpunkte*/
	private static double a;		/*linker Rand der Simulationsbox (Teilchen 1)*/
	private static double b;		/*rechter Rand der Simulationsbox (Teilchen 1)*/
	private static double c;		/*linker Rand der Simulationsbox (Teilchen 2)*/
	private static double d;		/*rechter Rand der Simulationsbox (Teilchen 2)*/
	
	/*Anzeige-Option, können live umgeschaltet werden*/
	private static double oldTime=0.0;
	private boolean makeScreenshot=false;
	private boolean makePause=false;
	private boolean logarithmic=false;
	private boolean showCoord=true;
	
	/*vorher eingestellte Simulations-Optionen*/
	private int makeScreens;
	
	/*Die übergebenen Algorithmus-Objekte*/
	private AskarCakmakVisscher2D mySimAcv;
	
	/*Hilfvariablen*/
	private static String sep = System.getProperty("file.separator");

	public Visu(AskarCakmakVisscher2D myAcv,
				int xMax, int height, double a, double b, double dx,
				int makeScreens) {
		this.mySimAcv = myAcv;
		this.xMax=xMax;
		this.height=height;
		this.width=750;
		this.a=a;
		this.b=b;
		this.c=a;
		this.d=b;
		this.dx=dx;
		this.makeScreens=makeScreens;
		
		/*Ausgabe der verfügbaren Befehlstasten auf der Konsole*/
		printHelp();
		
		/*Starten der Simulation*/
		start();
	}
	
	/*Erzeugt OpenGL-Simulationsfenster*/
	public void start() {
		final Frame frame = new Frame("Visualisierung 2-Teilchen-Schr\u00f6dingergleichung in 1D");
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
						frame.dispose();
                    }
                }).start();
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
	}

    @Override
	/*Initialisierung des OpenGL-Bereichs*/
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(1);
        gl.glClearColor(0.95f, 0.95f, 0.95f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
    }

	@Override
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
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(201)/Math.log(10)*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(201)/Math.log(10)*height));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(401)/Math.log(10)*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(401)/Math.log(10)*height));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(601)/Math.log(10)*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(601)/Math.log(10)*height));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(801)/Math.log(10)*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(801)/Math.log(10)*height));
		}
		else {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.2*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.2*height));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.4*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.4*height));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.6*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.6*height));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.8*height));
			gl.glVertex2i(xMax/2+5,10+(int)(0.8*height));
		}
	}

	/*Wird nach jedem Zeitschritt aufgerufen und zeichnet alle sichtbaren Elemente*/
    public void display(GLAutoDrawable drawable) {
		/*Erstellen der Zeichenumgebung und des Koordinatensystems*/
		/*(0,0) unten links, (xMax, height) oben rechts)*/
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-0.5, xMax-0.5, -0.5, height-0.5, -1, 1);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glLineWidth(2.0f);		/*Liniendicke*/
		gl.glBegin(GL.GL_LINES);
		
		/*Koordinatensystem zeichnen*/
		if(showCoord) drawCoordSys(gl);
	
		double x=a;		/*x beginnt am linken Rand der Simulationsbox*/
		int xPx=0;		/*x in Pixeln*/
		int xxPx=0;		/*x+dx in Pixeln*/
	
		int i;
		double tmpAcv1=0.0;		/*Wert des Wellenfunktionsbetragsquadrats mit AskarCakmakVisscher*/
		double tmpAcv2=0.0;		/*Wert einen räumlichen Schritt weiter*/
	
		/*durchläuft die Simulationsbox*/
		for (i=0;i<xMax-1;i++) {
			/*Berechnen der Pixelkoordinaten*/
			xPx=(int)(x/dx+xMax/2);
			xxPx=(int)((x+dx)/dx+xMax/2);

			tmpAcv1=mySimAcv.getStateOne(x);
			tmpAcv2=mySimAcv.getStateOne(x+dx);
			/*unter Umständen: logarithmische Skalierung*/
			if(logarithmic) {
				tmpAcv1=0.3*Math.log(tmpAcv1*1000+1)/Math.log(10);
				tmpAcv2=0.3*Math.log(tmpAcv2*1000+1)/Math.log(10);
			}
			//Teilchen 1 zeichnen
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glVertex2i(xPx,(int)(tmpAcv1*height)+10);
			gl.glVertex2i(xxPx,(int)(tmpAcv2*height)+10);
			
			tmpAcv1=mySimAcv.getStateTwo(x);
			tmpAcv2=mySimAcv.getStateTwo(x+dx);
			/*unter Umständen: logarithmische Skalierung*/
			if(logarithmic) {
				tmpAcv1=0.3*Math.log(tmpAcv1*1000+1)/Math.log(10);
				tmpAcv2=0.3*Math.log(tmpAcv2*1000+1)/Math.log(10);
			}
			//Teilchen 2 zeichnen
			gl.glColor3f(0.3f, 0.5f, 0.3f);
			gl.glVertex2i(xPx,(int)(tmpAcv1*height)+10);
			gl.glVertex2i(xxPx,(int)(tmpAcv2*height)+10);

			//räumlicher Schritt
			x+=dx;
		}
		
		gl.glEnd();
		
		//Screenshot machen
		if(makeScreenshot || (makeScreens!=0 && mySimAcv.getSteps()%makeScreens==0)) {
			try {
				String sep = System.getProperty("file.separator");
				String path = System.getProperty("user.dir");
				path=path+sep+"acse"+sep+"screens"+sep+mySimAcv.getPotential()+"_"+mySimAcv.getInitial()+"_"+mySimAcv.getSteps()+".png";
				Screenshot.writeToFile(new File(path),width-20,height-50);
				System.out.println("Screenshot in "+path+" gespeichert!");
			} catch(Exception e) {
				System.out.println("Fehler beim Screenshot!");
				System.out.println(e);
			}
			makeScreenshot=false;
		}
		
		gl.glFlush();
		
		//nächster Zeitschritt, falls Simulation nicht pausiert ist
        if(!makePause) {
			mySimAcv.nextTimeStep();
		}
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
		
    @Override
	/*Verarbeitet die Tastatureingaben und passt die Anzeigeparameter entsprechend an*/
    public void keyPressed(KeyEvent e) {
		char c=e.getKeyChar();
		if(c=='s') makeScreenshot=!makeScreenshot;
		if(c=='l') logarithmic=!logarithmic;
		if(c=='k') showCoord=!showCoord;
		if(c==' ') makePause=!makePause;
    }
	@Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
	
	/*Schreibt die schaltbaren Anzeigeparameter in die Konsole*/
	public static void printHelp() {
		System.out.println("\n--- verwendbare Befehle im Simulationsfenster ---");
		System.out.println("l.....logarithmische Skalierung");
		System.out.println("k.....Koordinatenachsen an/aus");
		System.out.println("s.....Screenshot erstellen");
		System.out.println("space.Pause");
		System.out.println("");
	}
}


