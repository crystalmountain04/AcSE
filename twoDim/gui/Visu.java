package acse.twoDim.gui;

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
//import com.sun.opengl.util.GLUT;

import acse.twoDim.util.*;
import acse.twoDim.algorithms.*;

import acse.twoDim.interfaces.*;
import acse.twoDim.potentials.*;
import acse.twoDim.solutions.*;

public class Visu implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

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

    private GL                   gl                 = null;
    private GLU                  glu                = null;

    private FloatBuffer          vaVertices         = null;
    private IntBuffer            vaIndicesFaces     = null;
    private IntBuffer            vaIndicesLines     = null;

    private float                rotY               = 10f;
	private	float				 rotX				= -124f;
	private int mouseX = 0;
	private int mouseY = 0;
	private int 				 zoom				= 110;
	
    private boolean rotLeft=false;
	private boolean rotRight=false;
	private boolean rotUp=false;
	private boolean rotDown=false;

    private static int xMax;
    private static int yMax;
	private static int height;
	private static int width;
	private static double dx;
	private static double a;
	private static double b;
	private static double c;
	private static double d;
    private static boolean isReady=false;
    private boolean pot=true;
    private static boolean ana=false;
    private boolean showAcv=true;
    private boolean showEqAcv=false;
	private boolean makeScreenshot=false;
	private boolean farbVerlauf=false;
	private boolean logarithmic=false;
	private static String sep = System.getProperty("file.separator");
    private static String potential;
    private static String initial;
    private static boolean isSolution=false;
    private static boolean Acv=true;
	private static int makeScreens;
	private static boolean makePause=false;
	private static boolean showCoord=false;
	private static double oldTime=0.0;
	private static AskarCakmakVisscher2D mySimAcv;
	
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
    public void init(final GLAutoDrawable drawable) {

        gl = drawable.getGL();
        glu = new GLU();
		
		gl.glClearColor(0.95f, 0.95f, 0.95f, 0.0f);
		
		//gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		//gl.glColor3f(1.0f, 0.0f, 0.0f);
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
    public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, int height) {
        if (height <= 0) height = 1;
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) width / (float) height, 1.0, 500.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
	
	public void drawCoordSystem(GL gl) {
		/*"Koordinatensystem" zeichnen*/
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		gl.glVertex3f(50,0,-50);
		gl.glVertex3f(50,0,50);	
		
		gl.glVertex3f(-50,0,50);
		gl.glVertex3f(50,0,50);
		
		gl.glVertex3f(-50,0,-50);
		gl.glVertex3f(50,0,-50);
		
		gl.glVertex3f(-50,0,-50);
		gl.glVertex3f(-50,0,50);	

		//z-Achse bei (x,y)=(50,50)
		gl.glVertex3f(0,0,0);
		gl.glVertex3f(0,80,0);
		
		//Markierungen
		if(logarithmic) {
			gl.glVertex3f(3,(float)(0.3*Math.log(201)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(201)/Math.log(10)*80),0);		
			gl.glVertex3f(3,(float)(0.3*Math.log(401)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(401)/Math.log(10)*80),0);		
			gl.glVertex3f(3,(float)(0.3*Math.log(601)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(601)/Math.log(10)*80),0);		
			gl.glVertex3f(3,(float)(0.3*Math.log(801)/Math.log(10)*80),0);	gl.glVertex3f(-3,(float)(0.3*Math.log(801)/Math.log(10)*80),0);		
		}
		else {
			gl.glVertex3f(3,(float)(0.2*80),0);	gl.glVertex3f(-3,(float)(0.2*80),0);		
			gl.glVertex3f(3,(float)(0.4*80),0);	gl.glVertex3f(-3,(float)(0.4*80),0);		
			gl.glVertex3f(3,(float)(0.6*80),0);	gl.glVertex3f(-3,(float)(0.6*80),0);					
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
	
    public void display(GLAutoDrawable drawable) {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

		glu.gluLookAt(0, 70, zoom, 0, 18, 0, 0, 1, 0);
		
		//gl.glTranslatef(0.0f,0.0f,zoom);

		gl.glRotatef(0.4f*rotY, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(0.4f*rotX, 0.0f, 1.0f, 0.0f);

        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vaVertices);
		
		gl.glLineWidth(2.0f);
        gl.glBegin(GL.GL_LINES);
		
		if(showCoord) drawCoordSystem(gl);
	
        int i,j;
		double x=a;
		double xx=0;
		double yy=0;
		double y=c;
        double tmpPot1=0.0;
		double tmpPot2=0.0;
		double tmpPot3=0.0;
        double tmp2=0.0;
        double tmp3=0.0;
		double tmp4=0.0;
		int lattice=1;
		int shift=50;
        for (i=-shift;i<shift-1; i=i+lattice) {
			x=i*3/20.0;
			xx=x+lattice*3/20.0;
            for(j=-shift;j<shift-1;j=j+lattice) {
				y=j*3/20.0;
				yy=y+lattice*3/20.0;
                			
				if(pot) {
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
				if(ana) {
					tmp2=mySimAcv.getAnalyticState(x,y);
					tmp3=mySimAcv.getAnalyticState(xx,y);
					tmp4=mySimAcv.getAnalyticState(x,yy);
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
				
				tmp2=mySimAcv.getState(x,y);
				tmp3=mySimAcv.getState(xx,y);
				tmp4=mySimAcv.getState(x,yy);
				if(logarithmic) {
					tmp2=0.3*Math.log(tmp2*1000+1)/Math.log(10);
					tmp3=0.3*Math.log(tmp3*1000+1)/Math.log(10);
					tmp4=0.3*Math.log(tmp4*1000+1)/Math.log(10);
				}
				
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
				/*
				Screenshot.writeToTargaFile (new File(path), viewport [0],
                                     viewport [1], viewport [2],
                                     viewport [3], false);
				*/
				Screenshot.writeToFile(new File(path),viewport[2],viewport[3]);
				System.out.println("Screenshot in "+path+" gespeichert!");
			} catch(Exception e) {
				System.out.println("Fehler beim Screenshot!");
				System.out.println(e);
			}
			makeScreenshot=false;
		}
		
		if(!makePause) {
			//oldTime=System.currentTimeMillis();
			mySimAcv.nextTimeStep();
			//System.out.println("Acv: "+(System.currentTimeMillis()-oldTime)+" ms");
		}
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
	
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
		if(c=='a' && mySimAcv.getHasSolution()) ana=!ana;
        if(c=='p') pot=!pot;
		if(c=='-') zoom=zoom+5;
		if(c=='+') zoom=zoom-5;
		if(c=='s') makeScreenshot=!makeScreenshot;
		if(c==' ') makePause=!makePause;
		if(c=='f') farbVerlauf=!farbVerlauf;
		if(c=='l') logarithmic=!logarithmic;
		if(c=='k') showCoord=!showCoord;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		zoom = zoom + notches*5;
	}
	
	public static void printHelp() {
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

