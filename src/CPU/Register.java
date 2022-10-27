package CPU;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import GUI.InputSwitches;

/**
 * Register Class for structure of all registers
 */
public class Register {
    // Standard for all registers
    private final String name;
    private final int length;
    private int value;
    // Certain registers support negative numbers
    private final boolean signed;
    // For displaying characters
    private boolean isChar;
    // Standard for all registers
    private final JLabel label;
    private final JTextField textField;
    private final JButton loadButton;
    private final InputSwitches switches;

    /**
     * Constructor - Sets name, value, length, etc.
     * @param name Name of the register
     * @param length Length of the bits the register can hold
     * @param switches Switches for input into a register
     * @param signedRegister Indicates whether the register created is signed or not
     */
    public Register(String name, int length, InputSwitches switches, boolean signedRegister) {
        this.name = name;        this.length = length;        this.switches = switches;        this.signed = signedRegister;
        this.value = 0;        this.isChar = false;
        this.textField = new JTextField(length);  this.label = new JLabel(name);
        this.textField.setName(name);    this.textField.setText("0".repeat(length));
        this.loadButton = new JButton("Load");
        this.textField.setEditable(false);
        this.addListener();
    }

    /**
     * Standard adding listener for the register
     */
    private void addListener() {
        this.loadButton.addActionListener(ae -> {
            String valueToSet = this.switches.getValue().substring(this.switches.getValue().length() - this.length);
            this.textField.setText(valueToSet);
            setValue(Integer.parseInt(valueToSet, 2));
            System.out.println("value: " + this.value);
        });
    }


    /* 
     * GETTERS AND SETTERS SECTION
     */

    /**
     * Return name of Register
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the value of the register
     * @return
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Return the value of the register as a character
     * @return
     */
    public char getChar() {
        return (char) this.value;
    }

    /**
     * Sets the value of the register 
     * @param value
     */
    public void setValue(int value) {
        this.isChar = false;
        String binary = intToSignedBinary(value, this.length);
        this.value = this.signed ? signedBinaryToInt(binary) : Integer.parseInt(binary, 2);
        this.textField.setText(binary);
    }

    /**
     * Sets the value of a register to a character
     * @param value
     */
    public void setValue(char value) {
        this.isChar = true;
        this.value = value;
    }

    /**
     * Gets the binary string of the register value
     * @return
     */
    public String getBinaryStringValue() {
        return this.textField.getText();
    }

    /**
     * Returns if the register holds character data or not
     * @return
     */
    public boolean isChar() {
        return this.isChar;
    }

    /**
     * Gets the label element of the register
     * @return
     */
    public JLabel getLabel() {
        return this.label;
    }

    /**
     * Gets the text field element of the register
     * @return
     */
    public JTextField getTextField() {
        return this.textField;
    }

    /**
     * Gets the load button connected to the register
     */
    public JButton getLoadButton() {
        return this.loadButton;
    }

    /**
     * Gets the length of the register
     * @return
     */
    public int getLength() {
        return this.length;
    }

    @Override
    public String toString() {
        return "Register :: " + this.name + " :: Size: " + this.length + " :: Value: " + this.value + " ::";
    }

    /**
     * UTILITY SECTION FOR REGISTER VALUES
     * Convert UNSIGNED and SIGNED VALUES
     */

    /**
     * @param value
     * @param bits
     * @return
     */
    public static String intToSignedBinary(int value, int bits) {
        String binary = Integer.toBinaryString(value);
        if ((binary).length() > bits) {
            binary = (binary).substring((binary).length() - bits);
        } else {
            int missingBits = bits - (binary).length();
            String extendedSignBits = value < 0 ? "1".repeat(missingBits) : "0".repeat(missingBits);
            binary = extendedSignBits + binary;
        }
        return binary;
    }

    /**
     * @param signedString
     * @return
     */
    public static int signedBinaryToInt(String signedString) {
        if (signedString.length() == 1) {
            return Integer.parseInt(signedString);
        }
        String withoutSignedBit = signedString.substring(1);
        int val = Integer.parseInt(withoutSignedBit, 2);
        if (signedString.charAt(0) == '0') {
            return val;
        }
        String unsignedString = "";
        for (int i = 0; i < withoutSignedBit.length(); ++i) {
            if (withoutSignedBit.charAt(i) == '0') {
                unsignedString += '1';
                continue;
            }
            unsignedString += '0';
        }
        int unsignedVal = Integer.parseInt(unsignedString, 2);
        return -(unsignedVal + 1);
    }
}