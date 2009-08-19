package acse.twoPart.gui;

/*
Program: AcSE - AcSE calculates the Schr�dinger Equation
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
    private JTextField nameField;		/*Textfeld f�r den Namen*/
    private JTextArea initialArea;		/*Textfeld f�r die Implementierung des AZ*/

    JButton button;						/*Button zum Speichern*/
    private String name;				/*Enth�lt Namen*/
    private String initial;				/*Enth�lt Implementierung*/

	public InitPanel() {
		/*Erstellen der Textfelder, Eintragen sinnvoller Implementierungsbeispiele*/
        nameField = new JTextField("Name",30);
        initialArea = new JTextArea("/*Implementierung des Wellenpakets*/\ndouble k1=10.0;\ndouble k2=10.0;\ndouble tmp=0.8*Math.exp(-(x+2)*(x+2)-(y-2)*(y-2));\nreal=tmp*Math.cos(k1*x-k2*y);\nimag=tmp*Math.sin(k1*x-k2*y);",8,30);

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

		/*Elemente der Fensteroberfl�che hinzuf�gen*/
        add(nameField);
        add(initialArea);
        add(button);
	}

	/*Wird nach Klick auf den Button ausgef�hrt*/
    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {
		 /*Eingaben aus den Textfeldern lesen*/
         this.name = nameField.getText();
         this.initial = initialArea.getText();
		 
		 /*Pfad der zu schreibenden AZ-Klasse generieren*/
		 String sep = System.getProperty("file.separator");
         String path = System.getProperty("user.dir");
         String filename = path+sep+"acse"+sep+"twoPart"+sep+"solutions"+sep+name+".java";

		 /*Schreiben der AZ-Klasse*/
         try {
             Writer p = new FileWriter(filename);
        p.write(
			"package acse.twoPart.solutions;\n\n"+

			"import acse.twoPart.util.*;\n"+
			"import acse.twoPart.interfaces.Solution;\n\n"+

			"public class "+name+" implements Solution {\n"+
			"private Constants myConst;\n"+
			"private double dt;\n"+
			"private int n;\n\n"+

			"public "+name+"(Constants myConst, double dt) {\n"+
			"this.myConst = myConst;\n"+
			"this.n=0;\n"+
			"this.dt=dt;\n"+
			"}\n\n"+

			"public double getState(double x, double y) {\n"+
            "double out=0.0;\n"+
            "out=Math.pow(getStatePsi(x,y).getReal(),2)+Math.pow(getStatePsi(x,y).getImag(),2);\n"+
            "\nreturn out;\n"+
			"}\n\n"+

			"public void nextTimeStep() {\n"+
            "n+=1;\n"+
			"}\n\n"+

			"public Complex getStatePsi(double x, double y) {\n"+
            "double real=0.0;\n"+
            "double imag=0.0;\n"+
			"double t=n*dt;\n"+
			"/*Beginn der Implementierung*/\n"+
            initial+
			"\n/*Ende der Implementierung*/\n"+
            "Complex out = new Complex("+
            "real,imag" +
            ");\n"+
            "return out;\n"+
			"}\n"+
			"}"
			);
             p.close();

			 /*
			 Kompilieren der soeben erstellten AZ-Klasse;
			 ben�tigt das Paket Tools.jar, ansonsten NullPointerException
			 */
             JavaCompiler tool = ToolProvider.getSystemJavaCompiler();
             StandardJavaFileManager manager = tool.getStandardFileManager( null, null, null );
             List<File> fileList = Arrays.asList( new File(filename) );
             Iterable<? extends JavaFileObject> units;
             units = manager.getJavaFileObjectsFromFiles( fileList );
             CompilationTask task = tool.getTask( null, manager, null, null, null, units );
             task.call();
             manager.close();
			 
			 /*Ausgabe, falls die Klasse kompiliert werden konnte*/
             System.out.println("Initialisierungsklasse erfolgreich erstellt!");
         } catch(Exception e) {
			 /*Ausgabe, falls die Klasse nicht kompilierbar war*/
             System.out.println("Fehler beim Erstellen der Initialisierungsklasse");
             System.out.println(e);
         }
    }
	
	/*Liefert den eingegeben Namen des AZ zur�ck*/
	public String getName() {
		return this.nameField.getText();
	}
}

