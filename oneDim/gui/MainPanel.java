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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;

import acse.oneDim.algorithms.*;
import acse.oneDim.util.*;

/*
Diese Klasse liefert das Design der 1D, 1 Teilchen Konfigurationsoberfläche
*/
public class MainPanel extends JPanel implements ItemListener, ActionListener {
	private JFrame potFrame;		/*Fenster zur Potentialerstellung*/
	private JFrame initFrame;		/*Fenster zur Anfangszustandserstellung*/
	private PotPanel potPanel;		/*Inhalt des Potential-Erstell-Fensters*/
	private InitPanel initPanel;	/*Inhalt des Anfangszustand-Erstell-Fensters*/	
	
	/*Pull-Down-Menüs*/
    private JComboBox potCombo;
    private JComboBox initCombo;
	
	/*Check-Boxen für die Zusatzoptionen*/
    private JCheckBox analytic;
	private JCheckBox plot;
    private JCheckBox Cn;
    private JCheckBox Acv;
	
	/*Buttons*/
    private JButton startButton;
    private JButton potButton;
    private JButton initButton;
	
	/*Textfelder*/
	private JTextField aField;
	private JTextField bField;
	private JTextField nField;
	private JTextField dtField;
	private JTextField screenField;

    private String potential;		/*Enthält Namen des ausgewählten Potentials*/
    private String initial;			/*Enthält Namen des ausgewählten Anfangszustands*/
	private boolean makePlot;		/*Normierungsplot?*/
    private boolean isSolution;		/*analytische Lösung?*/
    private boolean calcCn;			/*CN benutzen?*/
    private boolean calcAcv;		/*ACV benutzen?*/

	public MainPanel() {
		/*Erstellen des Inhalts der Potential/AZ-Erstell-Fenster*/
	    potPanel = new PotPanel();
		initPanel = new InitPanel();
		
		/*Initialisierung*/
		makePlot=false;
        isSolution=false;
		
		/*Tabellen-Layout*/
		setLayout(new GridLayout(0,2,5,5));
		
		/*Initialisieren der Formular-Komponenten (mit sinnvollen Anfangswerten)*/
		aField = new JTextField("-7.5",5);
		bField = new JTextField("7.5",5);
		nField = new JTextField("750",5);
		dtField = new JTextField("0.0001",5);
		screenField = new JTextField("0");
        potCombo = new JComboBox();
        initCombo = new JComboBox();
		plot = new JCheckBox("Plot der Normierung erstellen?");
        analytic = new JCheckBox("Gibt es analytische L\u00f6sung?");
        Cn = new JCheckBox("Crank-Nicholson");
        Acv = new JCheckBox("Askar-Cakmak-Visscher");
        startButton = new JButton(">>> Simulation starten <<<");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
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

        /*Auswahldialog für das Potential mit Namen füllen*/
        potCombo.addItemListener(this);
        potCombo.addItem("-- Potential w\u00e4hlen --");
		String sep = System.getProperty("file.separator");
        String path = System.getProperty("user.dir");
        File f = new File(path+sep+"acse"+sep+"oneDim"+sep+"potentials"+sep);
        String[] filenames = f.list(new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.endsWith(".class");
            }
        });
        for(int i = 0; i  < filenames.length; i++){
            potCombo.addItem(filenames[i].replaceAll(".class", ""));
        }

        /*Auswahldialog für den Anfangszustand mit Namen füllen*/
        initCombo.addItemListener(this);
        initCombo.addItem("-- Anfangszustand w\u00e4hlen ---");

        File f2 = new File(path+sep+"acse"+sep+"oneDim"+sep+"solutions"+sep);
        String[] filenames2 = f2.list(new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.endsWith(".class");
            }
        });
        for(int i = 0; i  < filenames2.length; i++){
            initCombo.addItem(filenames2[i].replaceAll(".class", ""));
        }

        /*Actionlistener für Auswahldialog ob Anfangszustand als analytische Lösung funktioniert*/
        analytic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyticActionPerformed(evt);
            }
        });
		
        /*Actionlistener für Auswahldialog ob Normierungsplot erstellt werden soll*/
        plot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotActionPerformed(evt);
            }
        });

        /*Actionlistener für Auswahldialog ob Cn verwendet werden soll*/
        Cn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CnActionPerformed(evt);
            }
        });

        /*Actionlistener für Auswahldialog ob Acv verwendet werden soll*/
        Acv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcvActionPerformed(evt);
            }
        });
		
		/*Erstellen der fetten Beschriftungen*/
		JLabel algo = new JLabel("Wahl der Algorithmen:");
		algo.setFont(algo.getFont().deriveFont(Font.BOLD));
		JLabel param = new JLabel("Wahl der Parameter:");
		param.setFont(param.getFont().deriveFont(Font.BOLD));
		JLabel szen = new JLabel("Wahl des Simulationsszenarios:");
		szen.setFont(szen.getFont().deriveFont(Font.BOLD));
		JLabel opt = new JLabel("Wahl der Zusatzoptionen:");
		opt.setFont(opt.getFont().deriveFont(Font.BOLD));
        
		/*Einfügen der Layoutelemente gemäß dem Tabellenlayout*/
		add(param);									add(new JLabel());
        add(new JLabel("x von"));					add(aField);
		add(new JLabel("x bis"));					add(bField);
		add(new JLabel("Anzahl Gitterpunkte"));		add(nField);
		add(new JLabel("zeitliche Schrittweite"));	add(dtField);
		add(new JLabel());							add(new JLabel());
		add(algo);									add(new JLabel());
        add(Acv);							        add(Cn);
		add(new JLabel());							add(new JLabel());
		add(szen);									add(new JLabel());
        add(potCombo);						        add(potButton);
        add(initCombo);						        add(initButton);
		add(new JLabel());							add(new JLabel());
		add(opt);									add(new JLabel());
        add(analytic);								add(new JLabel());
		add(plot);									add(new JLabel());
		add(new JLabel("Automatische Screenshots"));add(screenField);
		add(new JLabel());							add(new JLabel());
        add(startButton);    
	}

	/*Ausgeführt beim Klick auf den Startbutton*/
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if(this.potential.equals("-- Potential w\u00e4hlen ---") ||
		   this.initial.equals("-- Anfangszustand w\u00e4hlen ---") ||
		   (!calcCn && !calcAcv)) {
		   /*Fehler ausgeben, falls keine Anfangsbedingungen/Potential/Algorithmen gewählt sind*/
			System.out.println("Ungueltiges Simulationsszenario oder kein Algorithmus gewaehlt!");
		}
		else {
			/*Erstellen einer Simulation mit den gewünschten Optionen*/
			int xMax=Integer.valueOf(nField.getText()).intValue();
			int yMax=700;
			double a=Double.parseDouble(aField.getText());
			double b=Double.parseDouble(bField.getText());
			double dx=(b-a)/xMax;
			double dt=Double.parseDouble(dtField.getText());
			int makeScreens=Integer.valueOf(screenField.getText()).intValue();
			
			/*Erstellen eines Simulationsobjektes vom ACV-Algorithmus*/
			AskarCakmakVisscher1D mySimAcv = new AskarCakmakVisscher1D(xMax,a,b,dx,dt,this.potential,this.initial,this.isSolution);
			
			/*Erstellen eines Simulationsobjektes vom CN-Algorithmus*/
			CrankNicholson1D mySimCn = new CrankNicholson1D(xMax,a,b,dx,dt,this.potential,this.initial,this.isSolution);
			
			/*Erstellen eines Visualisierungsfensters*/
			Visu simuWindow = new Visu(mySimAcv,mySimCn,xMax,yMax,a,b,dx,calcCn,calcAcv,makePlot,makeScreens);
		}
    }

	/*Ausgeführt bei Klick auf 'Potential erstellen'*/
    private void buttonPotActionPerformed(java.awt.event.ActionEvent evt) {
		/*Fenster zum Erstellen eines Potentials anzeigen*/
		potPanel.button.addActionListener(this);
		potFrame = new JFrame("Potential erstellen");
		potFrame.add(potPanel);
        potFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        potFrame.setLocation(500, 300);
		potFrame.setSize(400, 250);
        potFrame.setVisible(true);
    }

	/*Ausgeführt bei Klick auf 'Initialisierung erstellen'*/
    private void buttonInitActionPerformed(java.awt.event.ActionEvent evt) {
		/*Fenster zum Erstellen eines Anfangszustands anzeigen*/
		initPanel.button.addActionListener(this);
		initFrame = new JFrame("Initialisierung erstellen");
		initFrame.add(initPanel);
        initFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initFrame.setLocation(500, 300);
		initFrame.setSize(400, 250);
        initFrame.setVisible(true);
    }

	/*Methoden um die Klicks auf die Options-Checkboxen umzusetzen*/
    private void analyticActionPerformed(java.awt.event.ActionEvent evt) {
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
	/*Speichert den Namen des ausgewählten Potentials/Anfangszustands als String*/
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange()==ItemEvent.SELECTED) {
            this.potential=(String)potCombo.getSelectedItem();
            this.initial=(String)initCombo.getSelectedItem();
        }
    }
	
	@Override
	/*Fügt die Namen von erstellten Potentialen/Anfangszuständen den Listen hinzu*/
	public void actionPerformed(ActionEvent e) {
		int i;
		boolean alreadyInside=false;
		if (e.getSource()==potPanel.button) {
			for(i=0;i<potCombo.getItemCount();i++) {
				if(potPanel.getName().equals((String)potCombo.getItemAt(i))) alreadyInside=true;
			}
			if(!alreadyInside) potCombo.addItem(potPanel.getName());
			potFrame.dispose();
		}  
		if (e.getSource()==initPanel.button) {
			alreadyInside=false;
			for(i=0;i<initCombo.getItemCount();i++) {
				if(initPanel.getName().equals((String)initCombo.getItemAt(i))) alreadyInside=true;
			}
			if(!alreadyInside) initCombo.addItem(initPanel.getName());
			initFrame.dispose();
		}
	}
}