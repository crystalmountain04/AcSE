package acse.twoPart.gui;

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

public class Visu implements GLEventListener, MouseListener, KeyListener, MouseWheelListener {

    private static int xMax;
    private static int yMax;
	private static int height;
	private static int width;
	private static double dx;
	private static double a;
	private static double b;
	private static double c;
	private static double d;
	private static double oldTime=0.0;
    private static boolean isReady=false;
    private boolean pot=true;
    private static boolean ana=false;
    private boolean showAcv=true;
    private boolean showEqAcv=false;
	private boolean makeScreenshot=false;
	private static String sep = System.getProperty("file.separator");
    private static String potential;
    private static String initial;
    private static boolean isSolution=false;
    private static boolean Acv=true;
	private static boolean makePause=false;
	private static int makeScreens;
	private static boolean logarithmic=false;
	private static boolean showCoord=true;
    private static AskarCakmakVisscher2D mySimAcv;
    private static MainPanel myCo;

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
		
		printHelp();
		start();
	}
	
	public void start() {
		final Frame frame = new Frame("Visualisierung 2-Teilchen-Schr\u00f6dingergleichung in 1D");
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

    @Override
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(1);
        gl.glClearColor(0.95f, 0.95f, 0.95f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
    }

	@Override
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
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(201)/Math.log(10)*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(201)/Math.log(10)*yMax));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(401)/Math.log(10)*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(401)/Math.log(10)*yMax));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(601)/Math.log(10)*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(601)/Math.log(10)*yMax));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.3*Math.log(801)/Math.log(10)*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.3*Math.log(801)/Math.log(10)*yMax));
		}
		else {
			//Markierung bei y=0.2 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.2*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.2*yMax));	
			//Markierung bei y=0.4 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.4*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.4*yMax));
			//Markierung bei y=0.6 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.6*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.6*yMax));
			//Markierung bei y=0.8 zeichnen
			gl.glVertex2i(xMax/2-5,10+(int)(0.8*yMax));
			gl.glVertex2i(xMax/2+5,10+(int)(0.8*yMax));
		}
	}

    public void display(GLAutoDrawable drawable) {

		//if(mySimAcv.getSteps()%10==0) {
			/*Erstellen der Zeichenumgebung und des Koordinatensystems*/
			/*(0,0) unten links, (xMax, yMax) oben rechts)*/
			GL gl = drawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(-0.5, xMax-0.5, -0.5, yMax-0.5, -1, 1);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			gl.glLineWidth(2.0f);
			gl.glBegin(GL.GL_LINES);
			
			if(showCoord) drawCoordSys(gl);
		
			double x=a;
			int xPx=0;
			int xxPx=0;
		
			int i;
			double tmpAcv1=0.0;
			double tmpAcv2=0.0;
		
			for (i=0;i<xMax-1;i++) {
				xPx=(int)(x/dx+xMax/2);
				xxPx=(int)((x+dx)/dx+xMax/2);

				tmpAcv1=mySimAcv.getStateOne(x);
				tmpAcv2=mySimAcv.getStateOne(x+dx);
				if(logarithmic) {
					tmpAcv1=0.3*Math.log(tmpAcv1*1000+1)/Math.log(10);
					tmpAcv2=0.3*Math.log(tmpAcv2*1000+1)/Math.log(10);
				}
				//Teilchen 1 zeichnen
				gl.glColor3f(1.0f, 0.0f, 0.0f);
				gl.glVertex2i(xPx,(int)(tmpAcv1*yMax)+10);
				gl.glVertex2i(xxPx,(int)(tmpAcv2*yMax)+10);
				
				tmpAcv1=mySimAcv.getStateTwo(x);
				tmpAcv2=mySimAcv.getStateTwo(x+dx);
				if(logarithmic) {
					tmpAcv1=0.3*Math.log(tmpAcv1*1000+1)/Math.log(10);
					tmpAcv2=0.3*Math.log(tmpAcv2*1000+1)/Math.log(10);
				}
				//Teilchen 2 zeichnen
				gl.glColor3f(0.3f, 0.5f, 0.3f);
				gl.glVertex2i(xPx,(int)(tmpAcv1*yMax)+10);
				gl.glVertex2i(xxPx,(int)(tmpAcv2*yMax)+10);

				x+=dx;
			}
			
			gl.glEnd();
			
			//Screenshot machen
			if(makeScreenshot || (makeScreens!=0 && mySimAcv.getSteps()%makeScreens==0)) {
				try {
					String sep = System.getProperty("file.separator");
					String path = System.getProperty("user.dir");
					path=path+sep+"acse"+sep+"screens"+sep+mySimAcv.getPotential()+"_"+mySimAcv.getInitial()+"_"+mySimAcv.getSteps()+".png";
					Screenshot.writeToFile(new File(path),width-20,yMax-50);
					System.out.println("Screenshot in "+path+" gespeichert!");
				} catch(Exception e) {
					System.out.println("Fehler beim Screenshot!");
					System.out.println(e);
				}
				makeScreenshot=false;
			}
			
			gl.glFlush();
		//}
		
        if(!makePause) {
			//oldTime=System.currentTimeMillis();
			mySimAcv.nextTimeStep();
			//System.out.println("Zeit für Zeitschritt: "+(System.currentTimeMillis()-oldTime)+" ms");
		}
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
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
    public void keyTyped(KeyEvent e) {
    }
	
    @Override
    public void keyPressed(KeyEvent e) {
		char c=e.getKeyChar();
		if(c=='s') makeScreenshot=!makeScreenshot;
		if(c=='l') logarithmic=!logarithmic;
		if(c=='k') showCoord=!showCoord;
		if(c==' ') makePause=!makePause;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}
	
	public static void printHelp() {
		System.out.println("\n--- verwendbare Befehle im Simulationsfenster ---");
		System.out.println("l.....logarithmische Skalierung");
		System.out.println("k.....Koordinatenachsen an/aus");
		System.out.println("s.....Screenshot erstellen");
		System.out.println("space.Pause");
		System.out.println("");
	}
}


