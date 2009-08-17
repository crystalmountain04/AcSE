package acse.oneDim.gui;

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

public class InitPanel extends JPanel implements ItemListener {
    private JTextField nameField;
    private JTextArea initialArea;

    JButton button;
    private String name;
    private String initial;

	public InitPanel() {
        nameField = new JTextField("Name",30);
        initialArea = new JTextArea("/*Implementierung des Wellenpakets*/\nreal=0.8*Math.exp(-x*x);\nimag=0;",8,30);

        button = new JButton("Initialisierungsklasse erstellen");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        add(nameField);
        add(initialArea);
        add(button);
	}

    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {
         this.name = nameField.getText();
         this.initial = initialArea.getText();
		 String sep = System.getProperty("file.separator");
         String path = System.getProperty("user.dir");
         String filename = path+sep+"acse"+sep+"oneDim"+sep+"solutions"+sep+name+".java";

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
            initial+
            "Complex out = new Complex("+
            "real,imag" +
            ");\n"+
            "return out;\n"+
			"}\n"+
			"}"
			);
             p.close();

             JavaCompiler tool = ToolProvider.getSystemJavaCompiler();
             StandardJavaFileManager manager = tool.getStandardFileManager( null, null, null );

             List<File> fileList = Arrays.asList( new File(filename) );
             Iterable<? extends JavaFileObject> units;
             units = manager.getJavaFileObjectsFromFiles( fileList );

             CompilationTask task = tool.getTask( null, manager, null, null, null, units );
             task.call();

             manager.close();
             System.out.println("Initialisierungsklasse erfolgreich erstellt!");
         } catch(Exception e) {
             System.out.println("Fehler beim Erstellen der Initialisierungsklasse");
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

