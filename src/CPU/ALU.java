package CPU;

import java.awt.Component;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ALU {
    private final int[] condCode = new int[4];
    private final JLabel[] condCodeLabels = new JLabel[4];
    private final JLabel[] condCodeValues = new JLabel[4];

    public void add(Register out, Register in, int value) {
        out.setValue(in.getValue() + value);
        if (out.getValue() < 0) {
            if (in.getValue() > 0 && value > 0) {
                this.condCode[0] = 1;
            }
        } else if (in.getValue() < 0 && value < 0) {
            this.condCode[1] = 1;
        }
        System.out.println("Result of add: " + out.getValue());
    }

    public void sub(Register out, Register in, int value) {
        out.setValue(in.getValue() - value);
        if (out.getValue() < 0) {
            if (in.getValue() > 0 && value < 0) {
                this.condCode[0] = 1;
            }
        } else if (in.getValue() < 0 && value > 0) {
            this.condCode[1] = 1;
        }
        System.out.println("Result of add: " + out.getValue());
    }

    public void multiply(Register rx, Register ry, Register rxPlusOne) {
        int result = rx.getValue() * ry.getValue();
        Register r = new Register("r", 16, null, true);
        r.setValue(result);
        if (r.getValue() < 0) {
            if (rx.getValue() > 0 && ry.getValue() > 0) {
                this.condCode[0] = 1;
            } else if (rx.getValue() < 0 && ry.getValue() < 0) {
                this.condCode[0] = 1;
            }
        } else if (rx.getValue() < 0 && ry.getValue() > 0) {
            this.condCode[1] = 1;
        } else if (rx.getValue() > 0 && ry.getValue() < 0) {
            this.condCode[1] = 1;
        }
        String binary = r.getBinaryStringValue();
        System.out.println("Result of mult: " + r.getValue() + ". Binary string: " + binary);
        String highOrderBits = binary.substring(0, 8);
        String lowOrderBits = binary.substring(8);
        System.out.println("high order bits: " + highOrderBits + ", low order bits: " + lowOrderBits);
        int rxVal = Integer.parseInt(highOrderBits, 2);
        System.out.println("setting " + rx.getName() + " to a value of " + rxVal);
        rx.setValue(rxVal);
        int rx1Val = Integer.parseInt(lowOrderBits, 2);
        System.out.println("setting " + rxPlusOne.getName() + " to a value of " + rx1Val);
        rxPlusOne.setValue(rx1Val);
    }

    public void divide(Register rx, Register ry, Register rxPlusOne) {
        if (ry.getValue() == 0) {
            this.condCode[2] = 1;
            System.out.println("[ERROR] [ALU] Divide by 0");
            return;
        }
        int remainder = rx.getValue() % ry.getValue();
        int quotient = rx.getValue() / ry.getValue();
        System.out.println("Quotient of divide: " + quotient);
        System.out.println("Remainder of divide: " + remainder);
        rx.setValue(quotient);
        rxPlusOne.setValue(remainder);
    }

    public void testEquality(Register rx, Register ry) {
        this.condCode[3] = rx.getValue() == ry.getValue() ? 1 : 0;
        System.out.println("Equality test: condCode[3]: " + this.condCode[3]);
    }

    public int logicalAnd(Register rx, Register ry) {
        String rxBinary = rx.getBinaryStringValue();
        String ryBinary = ry.getBinaryStringValue();
        String res = "";
        for (int i = 0; i < rxBinary.length(); ++i) {
            char rxChar = rxBinary.charAt(i);
            char ryChar = ryBinary.charAt(i);
            if (rxChar == '1' && ryChar == '0') {
                res += '0';
                continue;
            }
            if (rxChar == '0' && ryChar == '1') {
                res += '0';
                continue;
            }
            if (rxChar == '0' && ryChar == '0') {
                res += '0';
                continue;
            }
            res += '1';
        }
        int result = getIntValue(res);
        System.out.println("Logical and result: " + result);
        return result;
    }

    public int logicalOr(Register rx, Register ry) {
        String rxBinary = rx.getBinaryStringValue();
        String ryBinary = ry.getBinaryStringValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rxBinary.length(); ++i) {
            char rxChar = rxBinary.charAt(i);
            char ryChar = ryBinary.charAt(i);
            if (rxChar == '1' && ryChar == '0') {
                sb.append('1');
                continue;
            }
            if (rxChar == '0' && ryChar == '1') {
                sb.append('1');
                continue;
            }
            if (rxChar == '0' && ryChar == '0') {
                sb.append('0');
                continue;
            }
            sb.append('0');
        }
        int result = getIntValue(sb.toString());
        System.out.println("Logical Or result: " + result);
        return result;
    }

    public int logicalNot(Register rx) {
        int count = 0;
        String rBinary = rx.getBinaryStringValue();
        StringBuilder sb = new StringBuilder();
        while (count != rBinary.length()) {
            char x = rBinary.charAt(count);
            if (x == '1') {
                x = '0';
                sb.append(x);
                ++count;
                continue;
            }
            x = '1';
            sb.append(x);
            ++count;
        }
        int result = getIntValue(sb.toString());
        System.out.println("Logical Not result: " + result);
        return result;
    }

    public int shift(Register r, int count, int LR, int AL) {
        String result;
        if (count == 0) {
            return r.getValue();
        }
        String registerBinary = r.getBinaryStringValue();
        if (AL == 1) {
            String extraZeroes = "0".repeat(count);
            if (LR == 1) {
                String shifted = registerBinary.substring(count);
                result = shifted + extraZeroes;
            } else {
                String shifted = registerBinary.substring(0, r.getLength() - count);
                result = extraZeroes + shifted;
            }
        } else if (LR == 1) {
            String extraZeroes = "0".repeat(count);
            String shifted = registerBinary.substring(count);
            result = shifted + extraZeroes;
            if (Integer.parseInt(registerBinary.substring(0, count), 2) > 0) {
                this.condCode[0] = 1;
            }
        } else {
            String extendedSignBits = Character.toString(registerBinary.charAt(0)).repeat(count);
            String shifted = registerBinary.substring(0, r.getLength() - count);
            result = extendedSignBits + shifted;
            if (Integer.parseInt(registerBinary.substring(r.getLength() - count), 2) > 0) {
                this.condCode[1] = 1;
            }
        }
        System.out.println("Shift result: " + result);
        return getIntValue(result);
    }

    public int getCc(int index) {
        if (index < 0 || index > 3) {
            return 0;
        }
        return this.condCode[index];
    }

    public void setCc(int index, int val) {
        if (index < 0 || index > 3 && val != 0 && val != 1) {
            return;
        }
        this.condCode[index] = val;
    }

    public void addConditionCodeBits(JPanel mainPanel) {
        this.condCodeLabels[0] = new JLabel("CC :: 0 :: (OVERFLOW)");
        this.condCodeLabels[1] = new JLabel("CC :: 1 :: (UNDERFLOW)");
        this.condCodeLabels[2] = new JLabel("CC :: 2 :: (DIVZERO)");
        this.condCodeLabels[3] = new JLabel("CC :: 3 :: (EQUALORNOT)");
        for (int i = 0; i < this.condCodeValues.length; ++i) {
            this.condCodeValues[i] = new JLabel("" + this.condCode[i]);
            GUI.GUI.addComponent((Component)this.condCodeLabels[i], mainPanel, 10, 4 + i, 1, 21);
            GUI.GUI.addComponent(this.condCodeValues[i], mainPanel, 11, 4 + i, 1);
        }
    }

    public void updateCcDisplays() {
        for (int i = 0; i < this.condCodeValues.length; ++i) {
            this.condCodeValues[i].setText("" + this.condCode[i]);
        }
    }

    public void reset() {
        Arrays.fill(this.condCode, 0);
    }

    public static String getSignedValue(int value, int bits) {
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

    public static int getIntValue(String signedString) {
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