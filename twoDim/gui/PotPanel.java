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
Potentials dar
*/
public class PotPanel extends JPanel {
    private JTextField nameField;		/*Textfeld für den Namen des Potentials*/
    private JTextArea potentialArea;	/*Textfeld für die Implementierung des Potentials*/

    JButton button;						/*Button zum Speichern*/
    private String name;				/*Enthält Potential-Namen*/
    private String potential;			/*Enthält Potential-Implementierung*/
	
	public PotPanel() {
		/*Erstellen der Textfelder, Schreiben sinnvoller Implementierungsbeispiele*/
        nameField = new JTextField("Name",30);
        potentialArea = new JTextArea("/*Implementierung des Potentials*/\nout=x*x+y*y;",8,30);

		/*Erstellen des Speichern-Buttons, Klick => buttonActionPerformed(evt)*/
        button = new JButton("Potentialklasse erstellen");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

		/*Hinzufügen der Komponenten zur Fenster-Oberfläche*/
        add(nameField);
        add(potentialArea);
        add(button);
	}

	/*Wird beim Klick auf den Button ausgeführt*/
    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {
		 /*Lesen der Eingaben in den Textfeldern*/
         this.name = nameField.getText();
         this.potential = potentialArea.getText();
		 
		 /*Pfad der zu erstellenden Potential-Klasse generieren*/
		 String sep = System.getProperty("file.separator");
		 String path = System.getProperty("user.dir");
         String filename = path+sep+"acse"+sep+"twoDim"+sep+"potentials"+sep+name+".java";
         
		 /*Schreiben der Potential-Klasse*/
         try {
             Writer p = new FileWriter(filename);
             p.write(
			"package acse.twoDim.potentials;\n\n"+

			"import acse.twoDim.util.*;\n"+
			"import acse.twoDim.interfaces.Potential;\n\n"+

			"public class "+name+" implements Potential {\n"+
			"private Constants myConst;\n"+
			"private double dt;\n"+
			"private int n;\n\n"+

			"public "+name+"(Constants myConst, double dt) {\n"+
			"this.myConst = myConst;\n"+
			"this.dt = dt;\n"+
			"this.n=0;\n"+
			"}\n\n"+

			"public double getValue(double x, double y) {\n"+
			"double out=0.0;\n"+
			"double t = n*dt;\n"+
			"/*Beginn der Implementierung*/\n"+
			potential+
			"\n/*Ende der Implementierung*/\n"+
			"return out;\n"+
			"}\n\n"+

			"public void nextTimeStep() {\n"+
			"n=n+1;\n"+
			"}\n"+
			"}\n"
			);
             p.close();
			 
			 /*Kompilieren der soeben erstellten Potentialklasse*/
             JavaCompiler tool = ToolProvider.getSystemJavaCompiler();
             StandardJavaFileManager manager = tool.getStandardFileManager( null, null, null );
             List<File> fileList = Arrays.asList( new File(filename) );
             Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles( fileList );
             tool.getTask( null, manager, null, null, null, units ).call();
             manager.close();
			 
			 /*Ausgabe bei fehlerfreier Kompilierung*/
             System.out.println("Potentialklasse erfolgreich erstellt!");
         } catch(Exception e) {
			 /*Ausgabe, falls Klasse nicht kompilierbar war*/
             System.out.println("Fehler beim Erstellen der Potentialklasse");
             System.out.println(e);
         }
    }

	/*Liefert den eingegebenen Namen des Potentials zurück*/
	public String getName() {
		return this.nameField.getText();
	}
}
