package acse;

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
import java.awt.event.KeyEvent;

import acse.oneDim.gui.*;

public class Acse extends JPanel {
    public Acse() {
		/*Erzeugt tabelleförmiges Layout*/
        super(new GridLayout(1, 1));
        
		/*Hauptfenster*/
        JTabbedPane tabbedPane = new JTabbedPane();
        
		/*Erzeugt die Panels für die einzelnen Simulationsszenarien*/
		JComponent panelOneDim = new acse.oneDim.gui.MainPanel();
		panelOneDim.setPreferredSize(new Dimension(400, 450));
        tabbedPane.addTab("1D, 1 Teilchen", null, panelOneDim,"eindimensionale SGL");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        JComponent panelTwoDim = new acse.twoDim.gui.MainPanel();
        tabbedPane.addTab("2D, 1 Teilchen", null, panelTwoDim,"zweidimensionale SGL");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        JComponent panelTwoPart = new acse.twoPart.gui.MainPanel();
        tabbedPane.addTab("1D, 2 Teilchen", null, panelTwoPart,"zwei Teilchen SGL");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
               
        /*Fügt die Reiter-Struktur dem Hauptfenster hinzu*/
        add(tabbedPane);
        
        /*Reiter-Scrolling wird ermöglicht*/
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
       
    private static void createAndShowGUI() {
        /*Erstellt das eigentliche Fenster*/
        JFrame frame = new JFrame("AcSE calculates the Schroedinger Equation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /*fügt dem Fenster eine Instanz dieser Klasse hinzu (Reiter-Struktur)*/
        frame.add(new Acse(), BorderLayout.CENTER);
        
        /*Fenster wird angezeigt im linken oberen Bildschirm-Eck*/
        frame.pack();
		frame.setLocation(0, 0);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
            }
        });
    }
}

