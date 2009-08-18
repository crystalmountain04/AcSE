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
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/*
Klasse stellt den Inhalt des Fensters zur Erstellung eines eigenen
Anfangszustands (AZ) dar
*/
public class InitPanel extends JPanel {
    private JTextField nameField;		/*Textfeld mit dem Namen des AZ*/
    private JTextArea initialArea;		/*Textfeld mit der Implementiertung des AZ*/

    JButton button;						/*Button zum Speichern der Angaben*/
    private String name;				/*Enthält den eingegebenen Namen für den AZ*/
    private String initial;				/*Enthält die Implementierung des AZ*/

	public InitPanel() {
		/*Erstellen der Textfelder, Eintragen sinnvoller Implementierungsbeispiele*/
        nameField = new JTextField("Name",30);
        initialArea = new JTextArea("/*Implementierung des Wellenpakets*/\nreal=0.8*Math.exp(-x*x);\nimag=0;",8,30);

		/*
		Erstellen und Beschriften des Speichern-Buttons;
		Klick ruft Methode buttonActionPerformed(evt) auf
		*/
        button = new JButton("Initialisierungsklasse erstellen");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

		/*Elemente der Fensteroberfläche hinzufügen*/
        add(nameField);
        add(initialArea);
        add(button);
	}

	/*Wird nach Klick auf den Button ausgeführt*/
    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {
		 /*Eingaben aus den Textfeldern lesen*/
         this.name = nameField.getText();
         this.initial = initialArea.getText();
		 
		 /*Pfad der zu schreibenden AZ-Klasse generieren*/
		 String sep = System.getProperty("file.separator");
         String path = System.getProperty("user.dir");
         String filename = path+sep+"acse"+sep+"oneDim"+sep+"solutions"+sep+name+".java";

		 /*Schreiben der AZ-Klasse*/
         try {
             Writer p = new FileWriter(filename);
        p.write(
			"package acse.oneDim.solutions;\n\n"+

			"import acse.oneDim.util.*;\n"+
			"import acse.oneDim.interfaces.Solution;\n\n"+

			"public class "+name+" implements Solution {\n"+
			"private Constants myConst;\n"+
			"private double dt;\n"+
			"private int n;\n\n"+

			"public "+name+"(Constants myConst, double dt) {\n"+
			"this.myConst = myConst;\n"+
			"this.n=0;\n"+
			"this.dt=dt;\n"+
			"}\n\n"+

			"public double getState(double x) {\n"+
            "double out=0.0;\n"+
            "out=Math.pow(getStatePsi(x).getReal(),2)+Math.pow(getStatePsi(x).getImag(),2);\n"+
            "\nreturn out;\n"+
			"}\n\n"+

			"public void nextTimeStep() {\n"+
            "n+=1;\n"+
			"}\n\n"+

			"public Complex getStatePsi(double x) {\n"+
			"double t = n*dt;\n"+
            "double real=0.0;\n"+
            "double imag=0.0;\n"+
			"/*Beginn der Implementierung*/\n"+
            initial+
			"\n/*Ende der Implementierung*/\n"+
            "\nComplex out = new Complex("+
            "real,imag" +
            ");\n"+
            "return out;\n"+
			"}\n"+
			"}"
			);
             p.close();

			 /*
			 Kompilieren der soeben erstellten AZ-Klasse;
			 benötigt das Paket Tools.jar, ansonsten NullPointerException
			 */
             JavaCompiler tool = ToolProvider.getSystemJavaCompiler();
             StandardJavaFileManager manager = tool.getStandardFileManager( null, null, null );
             List<File> fileList = Arrays.asList( new File(filename) );
             Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles( fileList );
             tool.getTask( null, manager, null, null, null, units ).call();
             manager.close();
			 
			 /*Ausgabe, falls die Klasse kompiliert werden konnte*/
             System.out.println("Initialisierungsklasse erfolgreich erstellt!");
         } catch(Exception e) {
			 /*Ausgabe, falls die Klasse nicht kompilierbar war*/
             System.out.println("Fehler beim Erstellen der Initialisierungsklasse (Syntaxfehler)?");
             System.out.println(e);
         }
    }
	
	/*Liefert den eingegeben Namen des AZ zurück*/
	public String getName() {
		return this.nameField.getText();
	}
}