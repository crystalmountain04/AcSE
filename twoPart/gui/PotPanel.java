package acse.twoPart.gui;

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

public class PotPanel extends JPanel implements ItemListener {
    private JTextField nameField;
    private JTextArea potentialArea;

    JButton button;
    private String name;
    private String potential;

	public PotPanel() {
        nameField = new JTextField("Name",30);
        potentialArea = new JTextArea("/*Implementierung des Potentials*/\nout=100*(x-y)*(x-y);",8,30);

        button = new JButton("Potentialklasse erstellen");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        add(nameField);
        add(potentialArea);
        add(button);
	}

    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {
         this.name = nameField.getText();
         this.potential = potentialArea.getText();
		 String sep = System.getProperty("file.separator");
		 String path = System.getProperty("user.dir");
         String filename = path+sep+"acse"+sep+"twoPart"+sep+"potentials"+sep+name+".java";
         
         try {
             Writer p = new FileWriter(filename);
             p.write(
			"package acse.twoPart.potentials;\n\n"+

			"import acse.twoPart.util.*;\n"+
			"import acse.twoPart.interfaces.Potential;\n\n"+

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
			potential+
			"return out;\n"+
			"}\n\n"+

			"public void nextTimeStep() {\n"+
			"n=n+1;\n"+
			"}\n"+
			"}\n"
			);
             p.close();
			 
             JavaCompiler tool = ToolProvider.getSystemJavaCompiler();
             StandardJavaFileManager manager = tool.getStandardFileManager( null, null, null );
			 
             List<File> fileList = Arrays.asList( new File(filename) );
             Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles( fileList );
             tool.getTask( null, manager, null, null, null, units ).call();
             manager.close();
			 
             System.out.println("Potentialklasse erfolgreich erstellt!");
         } catch(Exception e) {
             System.out.println("Fehler beim Erstellen der Potentialklasse");
             System.out.println(e);
         }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange()==ItemEvent.SELECTED) {

        }
    }

    public String getName() {
		return this.nameField.getText();
	}
}
