package GUI;


import CPU.CPU;
import java.awt.*;
import java.io.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import Memory.Memory;

/**
 * GUI Class for the Simulator
 */
public class GUI {
    private final JFrame frame = new JFrame("CSCI6461_CPU_Simulator");
    private final JPanel simulatorPanel;
    private JButton ipl;
    

    private final Memory memory;
    private final CPU CPU;
    private final InputSwitches input;

    /**
     * Creates the simulator frame
     * Intializes Registers, Memory, CPU, ALU, and sets default values
     */
    public GUI() {
        this.frame.setDefaultCloseOperation(3);
        this.frame.setPreferredSize(new Dimension(1330, 800));
        this.frame.setLocationRelativeTo(null);
        this.frame.getContentPane().setBackground(new Color(173, 216, 230));
        this.input = new InputSwitches();
        this.simulatorPanel = new JPanel(new GridBagLayout());
        this.memory = new Memory();
        this.CPU = new CPU(this.simulatorPanel, this.memory, this.input);
        this.addIPL();
        this.resetSimulator();
        this.frame.setLayout(new BoxLayout(this.frame.getContentPane(), 1));
        this.frame.add(this.input.getPanel());
        this.frame.add(this.simulatorPanel);
        this.frame.pack();
    }

    /** 
     * Load Default Boot Program on Start
     */
    public void startMachine() {
        this.frame.setVisible(true);
        this.CPU.loadBootInstructions();
    }

    /** 
     * Simple Reset Functionality for Simulator
     */
    private void resetSimulator() {
        this.CPU.reset();
        this.input.reset();
    }

    /**
     * Creates IPL Button with functionality from IPL Function
     */
    private void addIPL() {
        this.ipl = new JButton("IPL");
        this.ipl.setForeground(Color.red);
        this.ipl.setBackground(Color.black);
        this.ipl.addActionListener(ae -> this.initialProgramLoad());
        addComponent(this.ipl, this.simulatorPanel, 0, 11, 1);
    }
   
    /**
     * IPL - Loading the Initial Program
     */
    private void initialProgramLoad() {
        try {
            String programFile = JOptionPane.showInputDialog(null, "Please Enter Program Name: ");
            if (programFile == null) {
                programFile = "boot.txt";
            }
            InputStream instream = getClass().getResourceAsStream(programFile);

            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
			String line;

			while ( (line = br.readLine()) != null) {
				String[] instr = line.split("\\s+");
				int index = Integer.parseInt(instr[0],16);
				int new_value = Integer.parseInt(instr[1],16);
				CPU.printToConsole(new_value + " inserted into memory location " + index);
				memory.store(new_value, index);
			}

        }
        catch (Exception e) {
            String error = "There was an error loading the IPL file";
            JOptionPane.showMessageDialog(this.frame, e.toString(), "Error", 1);
        }
    }

    /**
     * BUTTONS
     * @param comp
     * @param panel
     * @param x
     * @param y
     * @param width
     * @param anchor
     */
    public static void addComponent(Component comp, JPanel panel, int x, int y, int width, int anchor) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.anchor = anchor;
        c.insets = new Insets(10, 0, 0, 10);
        panel.add(comp, c);
    }

    /**
     * LABELS
     * @param comp
     * @param panel
     * @param x
     * @param y
     * @param width
     */
    public static void addComponent(Component comp, JPanel panel, int x, int y, int width) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.anchor = 10;
        c.insets = new Insets(10, 0, 0, 10);
        panel.add(comp, c);
    }

    /**
     * IO DEVICES
     * @param comp
     * @param panel
     * @param x
     * @param y
     * @param width
     * @param height
     * @param anchor
     */
    public static void addComponent(Component comp, JPanel panel, int x, int y, int width, int height, int anchor) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.anchor = anchor;
        c.insets = new Insets(10, 0, 0, 10);
        panel.add(comp, c);
    }

}