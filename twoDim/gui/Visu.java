package acse.twoDim.gui;

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

import acse.twoDim.util.*;
import acse.twoDim.algorithms.*;

import acse.twoDim.interfaces.*;
import acse.twoDim.potentials.*;
import acse.twoDim.solutions.*;

/*
Stellt die eigentliche Visualisierung dar; Erzeugt dazu ein
OpenGL-Fenster
*/
public class Visu implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

	/*Parameter zur 3D-Visualisierung*/
    private static final float[] staticVertices   = new float[] {
        0.0f,  0.0f,  0.0f,
        0.0f, 50.0f,  0.0f,
       25.0f, 50.0f,  0.0f,
       25.0f, 25.0f,  0.0f,
       50.0f, 25.0f,  0.0f,
       50.0f,  0.0f,  0.0f,
        0.0f,  0.0f, 50.0f,
        0.0f, 50.0f, 50.0f,
       25.0f, 50.0f, 50.0f,
       25.0f, 25.0f, 50.0f,
       50.0f, 25.0f, 50.0f,
       50.0f,  0.0f, 50.0f
    };
    private static final int[] staticIndicesFaces = new int[] {
        0, 1, 2, 3
    };
    private static final int[] staticIndicesLines = new int[] {
        0, 1,   1, 2,   2, 3,   3,  4,    4,  5,    5,  0,
        6, 7,   7, 8,   8, 9,   9, 10,   10, 11,   11,  6,
        0, 6,   1, 7,   2, 8,   3,  9,    4, 10,    5, 11
    };

    private GL gl = null;
    private GLU glu = null;

    private FloatBuffer vaVertices = null;
    private IntBuffer vaIndicesFaces = null;
    private IntBuffer vaIndicesLines = null;

    private float rotY = 10f;		/*Rotationsempfindlichkeit in y-Richtung*/
	private	float rotX = -124f;		/*Rotationsempfindlichkeit in x-Richtung*/
	private int mouseX = 0;			/*Mousekoordinate in x-Richtung*/
	private int mouseY = 0;			/*Mousekoordinate in y-Richtung*/
	private int zoom = 110;			/*wie nah ist herangezoomt?*/

    private static int xMax;		/*Anzahl Gitterpunkte x-Richtung*/
    private static int yMax;		/*Anzahl Gitterpunkte y-Richtung*/
	private static int height;		/*Höhe des Simulationsfensters*/
	private static int width;		/*Breite des Simulationsfensters*/
	private static double dx;		/*Abstand Gitterpunkte*/
	private static double a;		/*linker Rand der Simulationsbox x-Richtung*/
	private static double b;		/*rechter Rand der Simulationsbox x-Richtung*/
	private static double c;		/*linker Rand der Simulationsbox y-Richtung*/
	private static double d;		/*rechter Rand der Simulationsbox y-Richtung*/
	
	/*Anzeige-Option, können live umgeschaltet werden*/
	private boolean showAcv=true;
    private boolean showEqAcv=false;
	private boolean showPot=true;
	private boolean makeScreenshot=false;	/*einzelner Screenshot*/
	private boolean farbVerlauf=false;
	private boolean logarithmic=false;
	private boolean showCoord=false;
	private boolean makePause=false;
    private boolean showAna=false;
	
	/*vorher eingestellte Simulations-Optionen*/
    private static boolean isSolution=false;	/*Ist analytische Lösung angegeben?*/
	private static int makeScreens;				/*Abstand zwischen automatischen Screenshots, 0 für nicht*/
	
	/*Die übergebenen Algorithmus-Objekte*/
	private AskarCakmakVisscher2D mySimAcv;
	
	/*Hilfsvariablen*/
	private static String sep = System.getProperty("file.separator");	/*Ordner-Trennzeichen des OS*/
	private static double oldTime=0.0;
	
	public Visu(AskarCakmakVisscher2D myAcv,
				int xMax, int yMax, double a, double b, double dx,
				int makeScreens) {
		this.mySimAcv = myAcv;
		this.xMax=xMax;
		this.yMax=yMax;
		this.height=700;
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
        final Frame frame = new Frame("Visualisierung Schr\u00f6dingergleichung in 2D");
        GLCanvas canvas = new GLCanvas();
        final Animator animator = new Animator(canvas);
		canvas.addKeyListener(this);
        canvas.addGLEventListener(this);
        canvas.addMouseListener(this);
		canvas.addMouseWheelListener(this);
		canvas.addMouseMotionListener(this);
        frame.add(canvas);
        frame.setSize(width, height);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
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
    public void init(final GLAutoDrawable drawable) {
        gl = drawable.getGL();
        glu = new GLU();
		gl.glClearColor(0.95f, 0.95f, 0.95f, 0.0f);		/*Hintergrundfarbe*/
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glClearDepth(1.0);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glDisable(GL.GL_BLEND);
	    gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_LIGHTING);
        vaVertices = BufferUtil.newFloatBuffer(staticVertices.length);
        vaVertices.put(staticVertices, 0, staticVertices.length);
        vaVertices.rewind();
        vaIndicesFaces = BufferUtil.newIntBuffer(staticIndicesFaces.length);
        vaIndicesFaces.put(staticIndicesFaces, 0, staticIndicesFaces.length);
        vaIndicesFaces.rewind();
        vaIndicesLines = BufferUtil.newIntBuffer(staticIndicesLines.length);
        vaIndicesLines.put(staticIndicesLines, 0, staticIndicesLines.length);
        vaIndicesLines.rewind();
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
    }

    @Override
	/*Verarbeitet Größenänderungen des Simulationsfensters*/
    public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, int height) {
        if (height <= 0) height = 1;
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) width / (float) height, 1.0, 500.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
	
	/*Zeichnet ein rudimentäres Koordinatensystem*/
	public void drawCoordSystem(GL gl) {
		/*Rahmen um die Simulationsbox*/
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		gl.glVertex3f(50,0,-50);		gl.glVertex3f(50,0,50);	
		gl.glVertex3f(-50,0,50);		gl.glVertex3f(50,0,50);
		gl.glVertex3f(-50,0,-50);		gl.glVertex3f(50,0,-50);
		gl.glVertex3f(-50,0,-50);		gl.glVertex3f(-50,0,50);	

		//z-Achse bei (x,y)=(50,50)
		gl.glVertex3f(0,0,0);
		gl.glVertex3f(0,80,0);
		
		//Markierungen
		if(logarithmic) {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex3f(3,(float)(0.3*Math.log(201)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(201)/Math.log(10)*80),0);		
			//Markierung bei y=0.4 zeichnen
			gl.glVertex3f(3,(float)(0.3*Math.log(401)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(401)/Math.log(10)*80),0);		
			//Markierung bei y=0.6 zeichnen
			gl.glVertex3f(3,(float)(0.3*Math.log(601)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(601)/Math.log(10)*80),0);		
			//Markierung bei y=0.8 zeichnen
			gl.glVertex3f(3,(float)(0.3*Math.log(801)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(801)/Math.log(10)*80),0);		
		}
		else {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex3f(3,(float)(0.2*80),0);	gl.glVertex3f(-3,(float)(0.2*80),0);	
			//Markierung bei y=0.4 zeichnen			
			gl.glVertex3f(3,(float)(0.4*80),0);	gl.glVertex3f(-3,(float)(0.4*80),0);
			//Markierung bei y=0.6 zeichnen
			gl.glVertex3f(3,(float)(0.6*80),0);	gl.glVertex3f(-3,(float)(0.6*80),0);
			//Markierung bei y=0.8 zeichnen
			gl.glVertex3f(3,(float)(0.8*80),0);	gl.glVertex3f(-3,(float)(0.8*80),0);		
		}

		//x-Achse
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glVertex3f(-50,0,0);
		gl.glVertex3f(50,0,0);
		
		//y-Achse
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glVertex3f(0,0,-50);
		gl.glVertex3f(0,0,50);
	}
	
	/*Wird nach jedem Zeitschritt aufgerufen und zeichnet alle sichtbaren Elemente*/
    public void display(GLAutoDrawable drawable) {
        /*
		Erstellen der Zeichenumgebung und des Koordinatensystems
		*/
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
		glu.gluLookAt(0, 70, zoom, 0, 18, 0, 0, 1, 0);
		gl.glRotatef(0.4f*rotY, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(0.4f*rotX, 0.0f, 1.0f, 0.0f);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vaVertices);
		gl.glLineWidth(2.0f);	/*Liniendicke*/
        gl.glBegin(GL.GL_LINES);
		
		/*Koordinatensystem zeichnen*/
		if(showCoord) drawCoordSystem(gl);
	
        int i,j;			/*räumliche Indizes x=a+i*dx, y=c+j*dx*/
		double x=a;			/*x beginnt am linken Rand der Simulationsbox*/
		double xx=0;		/*x-Wert einen Gitterpunkt weiter*/
		double yy=0;		/*y-Wert einen Gitterpunkt weiter*/
		double y=c;			/*y beginnt am linken Rand der Simulationsbox*/
        double tmpPot1=0.0;	/*Enthält Potentialwert*/
		double tmpPot2=0.0;	/*Enthält Potentialwert vom Nachbarn in x-Richtung*/
		double tmpPot3=0.0;	/*Enthält Potentialwert vom Nachbarn in y-Richtung*/
        double tmp2=0.0;	/*Wellenfunktion*/
        double tmp3=0.0;	/*Nachbar in x-Richtung*/
		double tmp4=0.0;	/*Nachbar in y-Richtung*/
		int lattice=1;		/*Gitterabstand*/
		int shift=50;		/*Verschieben der Simulationsbox in die Mitte des Visualisierungsfensters*/
		
		/*Durchläuft x-Raum*/
        for (i=-shift;i<shift-1; i=i+lattice) {
			/*Berechnen der Pixelkoordinaten*/
			x=i*3/20.0;
			xx=x+lattice*3/20.0;
			
			/*Durchläuft y-Raum*/
            for(j=-shift;j<shift-1;j=j+lattice) {
				/*Berechnen der Pixelkoordinaten*/
				y=j*3/20.0;
				yy=y+lattice*3/20.0;
                		
				//Potential zeichhnen (grün)				
				if(showPot) {
					tmpPot1=mySimAcv.getPotential(x,y);
					tmpPot2=mySimAcv.getPotential(xx,y);
					tmpPot3=mySimAcv.getPotential(x,yy);
					
					if(farbVerlauf) gl.glColor3f(0.3f, (float)(tmpPot2*tmpPot3/80000.0), 0.3f);
					else gl.glColor3f(0.3f, 0.5f, 0.3f);
					gl.glVertex3f(i,(float)(tmpPot1/300.0)*80,j);
					gl.glVertex3f(i+lattice,(float)(tmpPot2/300.0)*80,j);
					gl.glVertex3f(i,(float)(tmpPot1/300.0)*80,j);
					gl.glVertex3f(i,(float)(tmpPot3/300.0)*80,j+lattice);
				}
				
				//Analytische Lösung zeichnen
				if(showAna) {
					tmp2=mySimAcv.getAnalyticState(x,y);
					tmp3=mySimAcv.getAnalyticState(xx,y);
					tmp4=mySimAcv.getAnalyticState(x,yy);
					/*unter Umständen: logarithmische Skalierung*/
					if(logarithmic) {
						tmp2=0.3*Math.log(tmp2*1000+1)/Math.log(10);
						tmp3=0.3*Math.log(tmp3*1000+1)/Math.log(10);
						tmp4=0.3*Math.log(tmp4*1000+1)/Math.log(10);
					}
					gl.glColor3f((float)(2*tmp2), 0.0f, (float)(1-2*tmp2));
					gl.glVertex3f(i,(float)(tmp2)*80,j);
					gl.glVertex3f(i+lattice,(float)(tmp3)*80,j);
					gl.glVertex3f(i,(float)(tmp2)*80,j);
					gl.glVertex3f(i,(float)(tmp4)*80,j+lattice);
				}
				
				/*Einlesen der Wellenfunktionswerte*/
				tmp2=mySimAcv.getState(x,y);
				tmp3=mySimAcv.getState(xx,y);
				tmp4=mySimAcv.getState(x,yy);
				/*unter Umständen: logarithmische Skalierung*/
				if(logarithmic) {
					tmp2=0.3*Math.log(tmp2*1000+1)/Math.log(10);
					tmp3=0.3*Math.log(tmp3*1000+1)/Math.log(10);
					tmp4=0.3*Math.log(tmp4*1000+1)/Math.log(10);
				}
				
				//Wellenfunktion zeichnen (falls relevant != 0)
				if(tmp2>0.001) {
					gl.glColor3f((float)(2*tmp2), 0.0f, (float)(1-2*tmp2));
					gl.glVertex3f(i,(float)(tmp2)*80,j);
					gl.glVertex3f(i+lattice,(float)(tmp3)*80,j);
					gl.glVertex3f(i,(float)(tmp2)*80,j);
					gl.glVertex3f(i,(float)(tmp4)*80,j+lattice);
				}
            }
        }
        gl.glEnd();
		
		//Screenshot machen
		if(makeScreenshot || (makeScreens!=0 && mySimAcv.getSteps()%makeScreens==0)) {
			try {
				String sep = System.getProperty("file.separator");
				String path = System.getProperty("user.dir");
				path=path+sep+"acse"+sep+"screens"+sep+mySimAcv.getPotential()+"_"+mySimAcv.getInitial()+"_"+mySimAcv.getSteps()+".jpg";
				int [] viewport = { 0, 0, 0, 0 };
				gl.glGetIntegerv (GL.GL_VIEWPORT, viewport, 0);
				Screenshot.writeToFile(new File(path),viewport[2],viewport[3]);
				System.out.println("Screenshot in "+path+" gespeichert!");
			} catch(Exception e) {
				System.out.println("Fehler beim Screenshot!");
				System.out.println(e);
			}
			makeScreenshot=false;
		}
		
		//nächster Zeitschritt, falls Simulation nicht pausiert ist
		if(!makePause) {
			mySimAcv.nextTimeStep();
		}
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
	
	/*Methoden um die Skalier- und Zoombarkeit per mouse zu implementieren*/
    public void mouseMoved(MouseEvent e) {
		mouseX=e.getX();
		mouseY=e.getY();
    }
    public void mouseDragged(MouseEvent e) {
       rotX+=e.getX()-mouseX;
	   rotY+=e.getY()-mouseY;
	   mouseX=e.getX();
	   mouseY=e.getY();
    }
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		zoom = zoom + notches*5;
	}
    @Override
    public void mouseClicked(final MouseEvent e) {
    }
    @Override
    public void mouseEntered(final MouseEvent e) {
    }
    @Override
    public void mouseExited(final MouseEvent e) {
    }
    @Override
    public void mousePressed(final MouseEvent e) {
    }
    @Override
    public void mouseReleased(final MouseEvent e) {
    }
		
    @Override
	/*Verarbeitet die Tastatureingaben und passt die Anzeigeparameter entsprechend an*/
    public void keyPressed(KeyEvent e) {
        char c=e.getKeyChar();
		if(c=='a' && mySimAcv.getHasSolution()) showAna=!showAna;
        if(c=='p') showPot=!showPot;
		if(c=='-') zoom=zoom+5;
		if(c=='+') zoom=zoom-5;
		if(c=='s') makeScreenshot=!makeScreenshot;
		if(c==' ') makePause=!makePause;
		if(c=='f') farbVerlauf=!farbVerlauf;
		if(c=='l') logarithmic=!logarithmic;
		if(c=='k') showCoord=!showCoord;
    }
	@Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

	/*Schreibt die schaltbaren Anzeigeparameter in die Konsole*/
	public void printHelp() {
		System.out.println("\n--- verwendbare Befehle im Simulationsfenster ---");
		System.out.println("p.....Potential an/aus");
		if(mySimAcv.getHasSolution()) System.out.println("a.....analytische Loesung an/aus");
		System.out.println("f.....Farbverlauf im Potential an/aus");
		System.out.println("l.....logarithmische Skalierung");
		System.out.println("k.....Koordinatenachsen an/aus");
		System.out.println("s.....Screenshot erstellen");
		System.out.println("space.Simulation pausieren");
		System.out.println("+/-...Zoom");
		System.out.println("");
	}
}

