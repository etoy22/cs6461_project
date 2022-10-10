package CPU;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import GUI.InputSwitches;

/**
 * 
 */
public class Register {
    private final String name;
    private final int length;
    private int value;
    private final boolean signed;
    private boolean isChar;
    private final JLabel label;
    private final JTextField textField;
    private final JButton loadButton;
    private final InputSwitches switches;

    /**
     * 
     * @param name
     * @param length
     * @param switches
     * @param signedRegister
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
     * 
     */
    private void addListener() {
        this.loadButton.addActionListener(ae -> {
            String valueToSet = this.switches.getValue().substring(this.switches.getValue().length() - this.length);
            this.textField.setText(valueToSet);
            setValue(Integer.parseInt(valueToSet, 2));
            System.out.println("value: " + this.value);
        });
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @return
     */
    public int getValue() {
        return this.value;
    }

    /**
     * 
     * @return
     */
    public char getChar() {
        return (char) this.value;
    }

    /**
     * 
     * @param value
     */
    public void setValue(int value) {
        this.isChar = false;
        String binary = intToSignedBinary(value, this.length);
        this.value = this.signed ? signedBinaryToInt(binary) : Integer.parseInt(binary, 2);
        this.textField.setText(binary);
    }

    /**
     * 
     * @param value
     */
    public void setValue(char value) {
        this.isChar = true;
        this.value = value;
    }

    public String getBinaryStringValue() {
        return this.textField.getText();
    }

    public boolean isChar() {
        return this.isChar;
    }

    public JLabel getLabel() {
        return this.label;
    }

    public JTextField getTextField() {
        return this.textField;
    }

    /**
     * 
     */
    public JButton getLoadButton() {
        return this.loadButton;
    }

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