package acse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import acse.oneDim.gui.*;

public class Acse extends JPanel {
    public Acse() {
        super(new GridLayout(1, 1));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
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
               
        //Add the tabbed pane to this panel.
        add(tabbedPane);
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
       
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("AcSE calculates the Schroedinger Equation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add content to the window.
        frame.add(new Acse(), BorderLayout.CENTER);
        
        //Display the window.
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

