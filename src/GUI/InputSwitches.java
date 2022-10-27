package GUI;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.*;

/**
 * User Input Class - Switches in Simulator
 */
public class InputSwitches {
    private final JPanel bitPanel = new JPanel();
    private JToggleButton[] switches;
    private String switchValue;

    /**
     * Initialize Switches and Labels for the panel
     */
    public InputSwitches() {
        this.bitPanel.setPreferredSize(new Dimension(1200, 100));
        this.bitPanel.setLayout(new GridBagLayout());
        this.addSwitches();
    }

    /**
     * Reset the switch values and untoggle all buttons
     */
    public void reset() {
        this.switchValue = "0".repeat(16);
        for (JToggleButton button : this.switches) {
            button.setSelected(false);
        }
    }

    /*
     * Adds to switches to GUI
     */
    private void addSwitches() {
        this.switchValue = "0".repeat(16);
        this.switches = new JToggleButton[16];
        for (int i = 0; i < this.switches.length; ++i) {
            String text = "" + (this.switches.length - i - 1);
            JToggleButton button = new JToggleButton(text);
            button.addItemListener(e -> {
                int state = e.getStateChange();
                int index = this.switches.length - Integer.parseInt(button.getText()) - 1;
                this.switchValue = state == 1 ? this.switchValue.substring(0, index) + "1" + this.switchValue.substring(index + 1) : this.switchValue.substring(0, index) + "0" + this.switchValue.substring(index + 1);
            });
            this.switches[i] = button;
            GUI.addComponent(button, this.bitPanel, i,0, 1);
        }
        setuplabelBitPanel();
    }

    /**
     * Creates Labels for Switches
     */
    private void setuplabelBitPanel(){
        
        int y = 1;
		GUI.addComponent(new JLabel("Operation"), this.bitPanel, 0, y, 5);
        GUI.addComponent(new JLabel("GPR"), this.bitPanel, 6, y, 2);
        GUI.addComponent(new JLabel("IXR"), this.bitPanel, 8, y, 2);
        GUI.addComponent(new JLabel("I"), this.bitPanel, 10, y, 1);
        GUI.addComponent(new JLabel("Address"), this.bitPanel, 11, y, 5);
	}

    /**
     * Set the values of the switches
     * @param value
     */
    public void setSwitchValue(String value) {
        if (value.length() != this.switches.length) {
            return;
        }
        for (int i = 0; i < value.length(); ++i) {
            this.switches[i].setSelected(value.charAt(i) == '1');
        }
        this.switchValue = value;
    }

    /**
     * Get the Value of the current positions of the switches
     * @return
     */
    public String getValue() {
        return this.switchValue;
    }

    public JPanel getPanel() {
        return this.bitPanel;
    }

}