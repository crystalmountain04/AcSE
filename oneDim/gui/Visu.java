package acse.oneDim.gui;

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

public class Visu implements GLEventListener, KeyListener {

    private static int xMax;
    private static int yMax;
	private static int width;
	private static double dx;
	private static double a;
	private static double b;
    private static boolean isReady=false;
    private boolean pot=true;
    private boolean ana=true;
    private boolean showCn=true;
    private boolean showAcv=true;
    private boolean showDiff=false;
    private boolean showEqAcv=false;
    private boolean showEqCn=false;
	private boolean makeScreenshot=false;
	private static String sep = System.getProperty("file.separator");
    private static String potential;
    private static String initial;
    private static String printFile;
    private static boolean isSolution=false;
	private static boolean makePlot=false;
    private static boolean Cn=true;
    private static boolean Acv=true;
	private static int makeScreens;
	private static boolean makePause=false;
	private static boolean logarithmic=false;
	private static boolean showCoord=true;
    private static File outFile;
    private static PrintWriter pWriter;
	private static double oldTime=0.0;
	private AskarCakmakVisscher1D mySimAcv;
	private CrankNicholson1D mySimCn;
	
	public Visu(AskarCakmakVisscher1D myAcv, CrankNicholson1D myCn,
				int xMax, int yMax, double a, double b, double dx,
				boolean Cn, boolean Acv, boolean makePlot, int makeScreens) {
		this.Cn=Cn;
        this.Acv=Acv;
		this.xMax=xMax;
		this.yMax=yMax;
		this.width=750;
		this.a=a;
		this.b=b;
		this.dx=(b-a)/(double)xMax;
		this.makePlot=makePlot;
		this.makeScreens=makeScreens;
		this.mySimAcv=myAcv;
		this.mySimCn=myCn;
		
		String path = System.getProperty("user.dir");
        printFile=path+sep+"oneDim"+sep+"plotBoth.g";

		printHelp();
		try {
			outFile = new java.io.File( "data.dat" );
			pWriter = new PrintWriter(new java.io.FileOutputStream(outFile), true);
		} catch(FileNotFoundException e) {
			outFile = null;
			pWriter = null;
		}
        
		start();
	}
	
	public void start() {
		final Frame frame = new Frame("Visualisierung Schr\u00f6dingergleichung in 1D");
        GLCanvas canvas = new GLCanvas();
		
        canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
        frame.add(canvas);
        frame.setSize(width, yMax);
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
        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
	}

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(1);
        gl.glClearColor(0.95f, 0.95f, 0.95f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
        
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
	
	public void drawCoordSys(GL gl) {
		//y-Achse bei x=0 zeichnen
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		gl.glVertex2i(xMax/2,0);
		gl.glVertex2i(xMax/2,yMax);
		
		//x-Achse bei y=0 zeichnen
		gl.glVertex2i(0,10);
		gl.glVertex2i(width,10);
		
		/*Markierungen an der y-Achse zeichnen*/
		if(logarithmic) {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(201)/Math.log(10)*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(201)/Math.log(10)*yMax));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(401)/Math.log(10)*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(401)/Math.log(10)*yMax));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(601)/Math.log(10)*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(601)/Math.log(10)*yMax));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.3*Math.log(801)/Math.log(10)*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.3*Math.log(801)/Math.log(10)*yMax));
		}
		else {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.2*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.2*yMax));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.4*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.4*yMax));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.6*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.6*yMax));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(width/2-10,10+(int)(0.8*yMax));
			gl.glVertex2i(width/2+10,10+(int)(0.8*yMax));
		}
	}

    public void display(GLAutoDrawable drawable) {
        /*Erstellen der Zeichenumgebung und des Koordinatensystems*/
        /*(0,0) unten links, (xMax, yMax) oben rechts)*/
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-0.5, xMax-0.5, -0.5, yMax-0.5, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
		
		double x=a;
		int xPx=0;
		int xxPx=0;
	
	    gl.glLineWidth(2.0f);
        gl.glBegin(GL.GL_LINES);
		
		if(showCoord) drawCoordSys(gl);
		
        int i;
        double tmpAcv1=0.0;
        double tmpAcv2=0.0;
        double tmpCn1=0.0;
        double tmpCn2=0.0;
        double tmpDiff1=0.0;
        double tmpDiff2=0.0;
		double tmpAna1=0.0;
		double tmpAna2=0.0;

        for (i=0;i<xMax-1;i++) {
			xPx=(int)(x/dx+xMax/2);
			xxPx=(int)((x+dx)/dx+xMax/2);
					
            //Potential zeichhnen (grün)
            if(pot){
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
				if(logarithmic) {
					tmpAna1=0.3*Math.log(tmpAna1*1000+1)/Math.log(10);
					tmpAna2=0.3*Math.log(tmpAna2*1000+1)/Math.log(10);
				}
				if(ana) {
					gl.glColor3f(0.0f, 0.0f, 1.0f);
					gl.glVertex2i(xPx,(int)(tmpAna1*yMax)+10);
					gl.glVertex2i(xxPx,(int)(tmpAna2*yMax)+10);
				}
            }
			
			if(x==0) System.out.println(xPx);

            //AskarCakmakVisscher-Lösung zeichnen
            if(Acv) {
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
					gl.glVertex2i(xPx,(int)(tmpAcv1*yMax)+10);
					gl.glVertex2i(xxPx,(int)(tmpAcv2*yMax)+10);
				}
            }

            //CrankNicolson-Lösung zeichnen
            if(Cn) {
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
					gl.glVertex2i(xPx,(int)(tmpCn1*yMax)+10);
					gl.glVertex2i(xxPx,(int)(tmpCn2*yMax)+10);
				}
            }

            //Differenz der beiden Algorithmen zeichnen
            if(showDiff) {
                gl.glColor3f(0.0f, 0.0f, 0.0f);
                gl.glVertex2i(xPx,(int)(Math.sqrt((tmpCn1-tmpAcv1)*(tmpCn1-tmpAcv1))*yMax+10));
                gl.glVertex2i(xxPx,(int)(Math.sqrt((tmpCn2-tmpAcv2)*(tmpCn2-tmpAcv2))*yMax+10));
            }

            //Differenz zur analytischen Lösung (Acv)
            if(showEqAcv) {
                tmpDiff1 = Math.sqrt(Math.pow(tmpAcv1-tmpAna1,2))*yMax;
                tmpDiff2 = Math.sqrt(Math.pow(tmpAcv2-tmpAna2,2))*yMax;
                gl.glColor3f(1.0f, 0.0f, 0.0f);
                gl.glVertex2i(xPx,(int)(tmpDiff1)+10);
                gl.glVertex2i(xxPx,(int)(tmpDiff2)+10);
            }

            //Differenz zur analytischen Lösung (Cn)
            if(showEqCn) {
                tmpDiff1 = Math.sqrt(Math.pow(tmpCn1-tmpAna1,2))*yMax;
                tmpDiff2 = Math.sqrt(Math.pow(tmpCn2-tmpAna2,2))*yMax;
                gl.glColor3f(1.0f, 0.0f, 1.0f);
                gl.glVertex2i(xPx,(int)(tmpDiff1)+10);
                gl.glVertex2i(xxPx,(int)(tmpDiff2)+10);
            }
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
				Screenshot.writeToFile(new File(path),xMax-20,yMax-50);
				System.out.println("Screenshot in "+path+" gespeichert!");
			} catch(Exception e) {
				System.out.println("Fehler beim Screenshot!");
				System.out.println(e);
			}
			makeScreenshot=false;
		}

        /*Schreiben der Normierung in die Plot-Datei*/
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
		
		//evtl. Simulation pausieren
		if(!makePause) {
			//oldTime=System.currentTimeMillis();
			if(Acv) mySimAcv.nextTimeStep();
			//System.out.println("Acv: "+(System.currentTimeMillis()-oldTime)+" ms");
			//oldTime=System.currentTimeMillis();
			if(Cn) mySimCn.nextTimeStep();
			//System.out.println("Cn : "+(System.currentTimeMillis()-oldTime)+" ms");
		}
		
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
	
    @Override
    public void keyTyped(KeyEvent e) {
    }
	
    @Override
    public void keyPressed(KeyEvent e) {
        char c=e.getKeyChar();
        if(c=='p') pot=!pot;
        if(c=='a' && mySimAcv.getHasSolution()) ana=!ana;
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