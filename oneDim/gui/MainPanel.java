package acse.oneDim.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;

import acse.oneDim.algorithms.*;
import acse.oneDim.util.*;

public class MainPanel extends JPanel implements ItemListener, ActionListener {
	private JFrame potFrame;
	private JFrame initFrame;
	private PotPanel potPanel;
	private InitPanel initPanel;
    private boolean isReady;
    private JComboBox combo1;
    private JComboBox combo2;
    private JCheckBox check;
	private JCheckBox plot;
    private JCheckBox Cn;
    private JCheckBox Acv;
    private JButton button;
    private JButton potButton;
    private JButton initButton;
	private JTextField aField;
	private JTextField bField;
	private JTextField nField;
	private JTextField dtField;
	private JTextField screenField;
    private String potential;
    private String initial;
	private boolean makePlot;
    private boolean isSolution;
    private boolean calcCn;
    private boolean calcAcv;

	public MainPanel() {
	    potPanel = new PotPanel();
		initPanel = new InitPanel();
		setLayout(new GridLayout(0,2,5,5));
		makePlot=false;
        isSolution=false;
        isReady=false;
		aField = new JTextField("-7.5",5);
		bField = new JTextField("7.5",5);
		nField = new JTextField("750",5);
		dtField = new JTextField("0.0001",5);
		screenField = new JTextField("0");
        combo1 = new JComboBox();
        combo2 = new JComboBox();
		plot = new JCheckBox("Plot der Normierung erstellen?");
        check = new JCheckBox("Gibt es analytische L\u00f6sung?");
        Cn = new JCheckBox("Crank-Nicholson");
        Acv = new JCheckBox("Askar-Cakmak-Visscher");
        button = new JButton(">>> Simulation starten <<<");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });
        potButton = new JButton("Potential erstellen");
        potButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPotActionPerformed(evt);
            }
        });
        initButton = new JButton("Initialisierung erstellen");
        initButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInitActionPerformed(evt);
            }
        });

        /*------------ Auswahldialog für das Potential -----------------*/
        combo1.addItemListener(this);
        combo1.addItem("-- Potential w\u00e4hlen --");

		String sep = System.getProperty("file.separator");
        String path = System.getProperty("user.dir");
        File f = new File(path+sep+"acse"+sep+"oneDim"+sep+"potentials"+sep);
        String[] filenames = f.list(new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.endsWith(".class");
            }
        });

        for(int i = 0; i  < filenames.length; i++){
            combo1.addItem(filenames[i].replaceAll(".class", ""));
        }

        /*------------- Auswahldialog für den Anfangszustand -------------*/
        combo2.addItemListener(this);
        combo2.addItem("-- Anfangszustand w\u00e4hlen ---");

        File f2 = new File(path+sep+"acse"+sep+"oneDim"+sep+"solutions"+sep);
        String[] filenames2 = f2.list(new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.endsWith(".class");
            }
        });

        for(int i = 0; i  < filenames2.length; i++){
            combo2.addItem(filenames2[i].replaceAll(".class", ""));
        }

        /*Auswahldialog ob Anfangszustand als analytische Lösung funktioniert*/
        check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkActionPerformed(evt);
            }
        });
		
        /*Auswahldialog Normierungsplot erstellt werden soll*/
        plot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotActionPerformed(evt);
            }
        });

        /*Auswahldialog Cn verwendet werden soll*/
        Cn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CnActionPerformed(evt);
            }
        });

        /*Auswahldialog Acv verwendet werden soll*/
        Acv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcvActionPerformed(evt);
            }
        });
		
		JLabel algo = new JLabel("Wahl der Algorithmen:");
		algo.setFont(algo.getFont().deriveFont(Font.BOLD));
		JLabel param = new JLabel("Wahl der Parameter:");
		param.setFont(param.getFont().deriveFont(Font.BOLD));
		JLabel szen = new JLabel("Wahl des Simulationsszenarios:");
		szen.setFont(szen.getFont().deriveFont(Font.BOLD));
		JLabel opt = new JLabel("Wahl der Zusatzoptionen:");
		opt.setFont(opt.getFont().deriveFont(Font.BOLD));
        
		add(param);
		add(new JLabel());
        add(new JLabel("x von"));
		add(aField);
		add(new JLabel("x bis"));
		add(bField);
		add(new JLabel("Anzahl Gitterpunkte"));
		add(nField);
		add(new JLabel("zeitliche Schrittweite"));
		add(dtField);
		add(new JLabel());
		add(new JLabel());
		add(algo);
		add(new JLabel());
        add(Acv);
        add(Cn);
		add(new JLabel());
		add(new JLabel());
		add(szen);
		add(new JLabel());
        add(combo1);
        add(potButton);
        add(combo2);
        add(initButton);
		add(new JLabel());
		add(new JLabel());
		add(opt);
		add(new JLabel());
        add(check);
		add(new JLabel());
		add(plot);
		add(new JLabel());
		add(new JLabel("Automatische Screenshots"));
		add(screenField);
		add(new JLabel());
		add(new JLabel());
        add(button);    
	}

    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {
		if(this.potential.equals("-- Potential w\u00e4hlen ---") ||
		   this.initial.equals("-- Anfangszustand w\u00e4hlen ---") ||
		   (!calcCn && !calcAcv)) {
			System.out.println("Ungueltiges Simulationsszenario oder kein Algorithmus gewaehlt!");
		}
		else {
			int xMax=Integer.valueOf(nField.getText()).intValue();
			int yMax=700;
			double a=Double.parseDouble(aField.getText());
			double b=Double.parseDouble(bField.getText());
			double dx=(b-a)/xMax;
			double dt=Double.parseDouble(dtField.getText());
			int makeScreens=Integer.valueOf(screenField.getText()).intValue();
			AskarCakmakVisscher1D mySimAcv = new AskarCakmakVisscher1D(xMax,yMax,a,b,dx,dt,this.potential,this.initial,this.isSolution);
			CrankNicholson1D mySimCn = new CrankNicholson1D(xMax, yMax,a,b,dx,dt,this.potential,this.initial,this.isSolution);
			Visu simuWindow = new Visu(mySimAcv,mySimCn,xMax,yMax,a,b,dx,calcCn,calcAcv,makePlot,makeScreens);
		}
    }

    private void buttonPotActionPerformed(java.awt.event.ActionEvent evt) {
		potPanel.button.addActionListener(this);
		potFrame = new JFrame("Potential erstellen");
		potFrame.add(potPanel);
        potFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        potFrame.setLocation(500, 300);
		potFrame.setSize(400, 250);
        potFrame.setVisible(true);
    }

    private void buttonInitActionPerformed(java.awt.event.ActionEvent evt) {
		initPanel.button.addActionListener(this);
		initFrame = new JFrame("Initialisierung erstellen");
		initFrame.add(initPanel);
        initFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initFrame.setLocation(500, 300);
		initFrame.setSize(400, 250);
        initFrame.setVisible(true);
    }

    private void checkActionPerformed(java.awt.event.ActionEvent evt) {
        isSolution = !isSolution;
    }
	
    private void plotActionPerformed(java.awt.event.ActionEvent evt) {
        makePlot = !makePlot;
    }

    private void CnActionPerformed(java.awt.event.ActionEvent evt) {
        calcCn = !calcCn;
    }

    private void AcvActionPerformed(java.awt.event.ActionEvent evt) {
        calcAcv = !calcAcv;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange()==ItemEvent.SELECTED) {
            this.potential=(String)combo1.getSelectedItem();
            this.initial=(String)combo2.getSelectedItem();
        }
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int i;
		boolean alreadyInside=false;
		if (e.getSource()==potPanel.button) {
			for(i=0;i<combo1.getItemCount();i++) {
				if(potPanel.getName().equals((String)combo1.getItemAt(i))) alreadyInside=true;
			}
			if(!alreadyInside) combo1.addItem(potPanel.getName());
			potFrame.dispose();
		}  
		if (e.getSource()==initPanel.button) {
			alreadyInside=false;
			for(i=0;i<combo2.getItemCount();i++) {
				if(initPanel.getName().equals((String)combo2.getItemAt(i))) alreadyInside=true;
			}
			if(!alreadyInside) combo2.addItem(initPanel.getName());
			initFrame.dispose();
		}
	}
}
