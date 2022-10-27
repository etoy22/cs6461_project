package CPU;

import GUI.DEVID;

public class InstructionSet {

    /**
     * Total Instruction List
     */
    private final String[] instructionList = {
            "HALT",
            "LDR",
            "STR",
            "LDA",
            "AMR",
            "SMR",
            "AIR",
            "SIR",
            "JZ",
            "JNE",
            "JCC",
            "JMA",
            "JSR",
            "RFS",
            "SOB",
            "JGE",
            "MLT",
            "DVD",
            "TRR",
            "AND",
            "ORR",
            "NOT",
            "N/A", "N/A", "N/A",
            "SRC",
            "RRC",
            "N/A", "N/A", "N/A", "N/A", "N/A", "N/A",
            "LDX",
            "STX",
            "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A",
            "IN",
            "OUT",

    };

    /**
     * Gets the string of the instruction from the op code
     * @param opcode
     * @return
     */
    public String getInstructionName(int opcode) {
        if (opcode >= 0 && opcode < instructionList.length)
            return instructionList[opcode];
        else
            return "Invalid";
    }

    /**
     * Decodes the op code and decodes the instruction as needed
     * @param opcode
     * @param cpu
     */
    public void decodeInstruction(int opcode, CPU cpu) {

        System.out.print("Decode Instruction :: " + opcode + " :: ");
        System.out.println(this.instructionList[opcode]);

        switch (opcode) {
            case 0:  decodeHalt(cpu); break;
            case 1:  decodeLDR(cpu);  break;
            case 2:  decodeSTR(cpu);  break;
            case 3:  decodeLDA(cpu);  break;
            case 4:  decodeAMR(cpu);  break;
            case 5:  decodeSMR(cpu);  break;
            case 6:  decodeAIR(cpu);  break;
            case 7:  decodeSIR(cpu);  break;
            case 8:  decodeJZ(cpu);   break;
            case 9:  decodeJNE(cpu);  break;
            case 10: decodeJCC(cpu);  break;
            case 11: decodeJMA(cpu);  break;
            case 12: decodeJSR(cpu);  break;
            case 13: decodeRFS(cpu);  break;
            case 14: decodeSOB(cpu);  break;
            case 15: decodeJGE(cpu);  break;
            case 16: decodeMLT(cpu);  break;
            case 17: decodeDVD(cpu);  break;
            case 18: decodeTRR(cpu);  break;
            case 19: decodeAND(cpu);  break;
            case 20: decodeORR(cpu);  break;
            case 21: decodeNOT(cpu);  break;
            case 25: decodeSRC(cpu);  break;
            case 26: decodeRRC(cpu);  break;
            case 33: decodeLDX(cpu);  break;
            case 34: decodeSTX(cpu);  break;
            case 49: decodeIN(cpu);   break;
            case 50: decodeOUT(cpu);  break;
            default:
                System.out.println("[ERROR] [INSTRUCTION] INVALID OP CODE :: " + opcode);
                break;
        }
        return;
    }

    /**
     * Executes the operation from the given op code
     * @param opcode
     * @param cpu
     */
    public void executeInstruction(int opcode, CPU cpu) {

        System.out.print("Executing Instruction :: " + opcode + " :: ");
        System.out.println(this.instructionList[opcode]);

        switch (opcode) {
            case 0:  executeHalt(cpu); break;
            case 1:  executeLDR(cpu);  break;
            case 2:  executeSTR(cpu);  break;
            case 3:  executeLDA(cpu);  break;
            case 4:  executeAMR(cpu);  break;
            case 5:  executeSMR(cpu);  break;
            case 6:  executeAIR(cpu);  break;
            case 7:  executeSIR(cpu);  break;
            case 8:  executeJZ(cpu);   break;
            case 9:  executeJNE(cpu);  break;
            case 10: executeJCC(cpu); break;
            case 11: executeJMA(cpu); break;
            case 12: executeJSR(cpu); break;
            case 13: executeRFS(cpu); break;
            case 14: executeSOB(cpu); break;
            case 15: executeJGE(cpu); break;
            case 16: executeMLT(cpu); break;
            case 17: executeDVD(cpu); break;
            case 18: executeTRR(cpu); break;
            case 19: executeAND(cpu); break;
            case 20: executeORR(cpu); break;
            case 21: executeNOT(cpu); break;
            case 25: executeSRC(cpu); break;
            case 26: executeRRC(cpu); break;
            case 33: executeLDX(cpu); break;
            case 34: executeSTX(cpu); break;
            case 49: executeIN(cpu);  break;
            case 50: executeOUT(cpu); break;
            default:
                System.out.println("[ERROR] [INSTRUCTION] INVALID OP CODE :: " + opcode);
            }

        }

    // 00
    /**
     * HALT OPERATION
     * @param cpu
     */
    private void decodeHalt(CPU cpu) {
        return;
    }

    private void executeHalt(CPU cpu) {
        cpu.setHalted(true);
    }

    // 01
    /**
     * LDR OPERATION
     * @param cpu
     */
    private void decodeLDR(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
    }

    private void executeLDR(CPU cpu) {
        cpu.getIrr().setValue(cpu.getMbr().getValue());
    }

    // 02
    /**
     * STR OPERATION
     * @param cpu
     */
    private void decodeSTR(CPU cpu) {
        cpu.setDeviceOutput(DEVID.MEMORY);
        cpu.setRegisterOutput("GPR");
    }

    private void executeSTR(CPU cpu) {
        int val = cpu.selectGPR(cpu.getRs1().getValue()).getValue();
        cpu.getIrr().setValue(val);
    }

    // 03
    /**
     * LDA OPERATION
     * @param cpu
     */
    private void decodeLDA(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
    }

    private void executeLDA(CPU cpu) {
        cpu.getIrr().setValue(cpu.getIar().getValue());
    }

    // 04
    /**
     * AMR OPERATION
     * @param cpu
     */
    private void decodeAMR(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
    }

    private void executeAMR(CPU cpu) {
        Register r = cpu.selectGPR(cpu.getRs1().getValue());
        int valToAdd = cpu.getMemory().load(cpu.getIar().getValue());
        cpu.getALU().add(cpu.getIrr(), r, valToAdd);
    }

    // 05
    /**
     * SMR OPERATION
     * @param cpu
     */
    private void decodeSMR(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
    }

    private void executeSMR(CPU cpu) {
        Register r = cpu.selectGPR(cpu.getRs1().getValue());
        int valToAdd = cpu.getMemory().load(cpu.getIar().getValue());
        cpu.getALU().sub(cpu.getIrr(), r, valToAdd);
    }

    // 06
    /**
     * AIR OPERATION
     * @param cpu
     */
    private void decodeAIR(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
        cpu.setIxiflag(false);
    }

    private void executeAIR(CPU cpu) {
        Register r = cpu.selectGPR(cpu.getRs1().getValue());
        cpu.getALU().add(cpu.getIrr(), r, cpu.getIar().getValue());
    }

    // 07
    /**
     * SIR OPERATION
     * @param cpu
     */
    private void decodeSIR(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
        cpu.setIxiflag(false);
    }

    private void executeSIR(CPU cpu) {
        Register r = cpu.selectGPR(cpu.getRs1().getValue());
        cpu.getALU().sub(cpu.getIrr(), r, cpu.getIar().getValue());
    }

    // 08
    /**
     * JZ OPERATION
     * @param cpu
     */
    private void decodeJZ(CPU cpu) {
        return;
    }

    private void executeJZ(CPU cpu) {
        int val = cpu.selectGPR(cpu.getRs1().getValue()).getValue();
        if (val == 0) {
            cpu.setNextPc(cpu.getIar().getValue());
        }
    }

    // 09
    /**
     * JNE OPERATION
     * @param cpu
     */
    private void decodeJNE(CPU cpu) {
        return;
    }

    private void executeJNE(CPU cpu) {
        int val = cpu.selectGPR(cpu.getRs1().getValue()).getValue();
        if (val != 0) {
            cpu.setNextPc(cpu.getIar().getValue());
        }
    }

    // 10
    /**
     * JCC OPERATION
     * @param cpu
     */
    private void decodeJCC(CPU cpu) {
        return;
    }

    private void executeJCC(CPU cpu) {
        if (cpu.getALU().getCc(cpu.getRs1().getValue()) == 1) {
            cpu.setNextPc(cpu.getIar().getValue());
        }
    }

    // 11
    /**
     * JMA OPERATION
     * @param cpu
     */
    private void decodeJMA(CPU cpu) {
        return;
    }

    private void executeJMA(CPU cpu) {
        cpu.setNextPc(cpu.getIar().getValue());
    }

    // 12
    /**
     * JSR OPERATION
     * @param cpu
     */
    private void decodeJSR(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
    }

    private void executeJSR(CPU cpu) {
        cpu.getRs1().setValue(3);
        cpu.getIrr().setValue(cpu.getPC().getValue() + 1);
        cpu.setNextPc(cpu.getIar().getValue());
    }

    // 13
    /**
     * RFS OPERATION
     * @param cpu
     */
    private void decodeRFS(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
        cpu.setIxiflag(false);
    }

    private void executeRFS(CPU cpu) {
        cpu.setIxiflag(false);
        cpu.getRs1().setValue(0);
        cpu.getIrr().setValue(cpu.getIar().getValue());
        cpu.setNextPc(cpu.getGPR3().getValue());
    }

    // 14
    /**
     * SOB OPERATION
     * @param cpu
     */
    private void decodeSOB(CPU cpu) {
    }

    private void executeSOB(CPU cpu) {
        Register r = cpu.selectGPR(cpu.getRs1().getValue());
        r.setValue(r.getValue() - 1);
        if (r.getValue() > 0) {
            cpu.setNextPc(cpu.getIar().getValue());
        }
    }

    // 15
    /**
     * JGE OPERATION
     * @param cpu
     */
    private void decodeJGE(CPU cpu) {
        return;
    }

    private void executeJGE(CPU cpu) {
        int val = cpu.selectGPR(cpu.getRs1().getValue()).getValue();
        if (val >= 0) {
            cpu.setNextPc(cpu.getIar().getValue());
        }
    }

    // 16
    /**
     * MLT COMMAND
     * @param cpu
     */
    private void decodeMLT(CPU cpu) {
        return;
    }

    private void executeMLT(CPU cpu) {
        Register rx = cpu.getRX();
        Register ry = cpu.getRY();
        int rxNum = cpu.getCurGPR();
        int ryNum = cpu.getCurIXR();
        if (rxNum != 0 && rxNum != 2) {
            System.out.println("[ERROR] [INST] Invalid rx register for MLT. Expected 0, 2 but got " + rxNum);
            return;
        }
        if (ryNum != 0 && ryNum != 2) {
            System.out.println("[ERROR] [INST] Invalid ry register for MLT. Expected 0, 2 but got " + ryNum);
            return;
        }
        cpu.getALU().multiply(rx, ry, cpu.selectGPR(rxNum + 1));
    }

    // 17
    /**
     * DVD OPERATION
     * @param cpu
     */
    private void decodeDVD(CPU cpu) {
        return;
    }

    private void executeDVD(CPU cpu) {
        Register rx = cpu.getRX();
        Register ry = cpu.getRY();
        int rxNum = cpu.getCurGPR();
        int ryNum = cpu.getCurIXR();
        if (rxNum != 0 && rxNum != 2) {
            System.out.println("[ERROR] [INST] Invalid rx register for DVD. Expected 0, 2 but got " + rxNum);
            return;
        }
        if (ryNum != 0 && ryNum != 2) {
            System.out.println("[ERROR] [INST] Invalid ry register for DVD. Expected 0, 2 but got " + ryNum);
            return;
        }
        cpu.getALU().divide(rx, ry, cpu.selectGPR(rxNum + 1));
    }

    // 18
    /**
     * TRR OPERATION
     * @param cpu
     */
    private void decodeTRR(CPU cpu) {
        return;
    }

    private void executeTRR(CPU cpu) {
        Register rx = cpu.getRX();
        Register ry = cpu.getRY();
        cpu.getALU().testEquality(rx, ry);
    }


    // 19
    /**
     * AND OPERATION
     * @param cpu
     */
    private void decodeAND(CPU cpu) {
        return;
    }

    private void executeAND(CPU cpu) {
        Register rx = cpu.getRX();
        Register ry = cpu.getRY();
        rx.setValue(cpu.getALU().logicalAnd(rx, ry));
    }

    // 20
    /**
     * ORR OPERATION
     * @param cpu
     */
    private void decodeORR(CPU cpu) {
        return;
    }

    private void executeORR(CPU cpu) {
        Register rx = cpu.getRX();
        Register ry = cpu.getRY();
        rx.setValue(cpu.getALU().logicalOr(rx, ry));
    }

    // 21
    /**
     * NOT OPERATION
     * @param cpu
     */
    private void decodeNOT(CPU cpu) {
        return;
    }

    private void executeNOT(CPU cpu) {
        Register rx = cpu.getRX();
        rx.setValue(cpu.getALU().logicalNot(rx));
    }

    // 24 TRAP (P3)

    // 25
    /**
     * SRC OPERATION
     * @param cpu
     */
    private void decodeSRC(CPU cpu) {
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("GPR");
    }

    private void executeSRC(CPU cpu) {
        Register r = cpu.selectGPR(cpu.getRs1().getValue());
        int count = cpu.getCount();
        int LR = cpu.getRotate();
        int AL = cpu.getShift();
        int result = cpu.getALU().shift(r, count, LR, AL);
        cpu.getIrr().setValue(result);
    }

    // 26
    /**
     * RRC OPERATION
     * @param cpu
     */
    private void decodeRRC(CPU cpu) {
        return;
    }

    private void executeRRC(CPU cpu) {
        int Count = cpu.getCount();
        int AL = cpu.getShift();
        int LR = cpu.getRotate();
        String rV = Integer.toBinaryString(cpu.selectGPR(cpu.getRs1().getValue()).getValue());
        String rValue = "0".repeat(16 - rV.length()) + rV;
        Object result = "";
        if (Count == 0) {
            return;
        }
        if (AL == 1) {
            if (LR == 1) {
                String saveRotated = rValue.substring(0, Count);
                String saveParsed = rValue.substring(Count);
                result = saveParsed + saveRotated;
                cpu.selectGPR(cpu.getRs1().getValue()).setValue(Integer.parseInt((String) result, 2));
            } else if (LR == 0) {
                String saveRotated = rValue.substring(Count);
                String saveParsed = rValue.substring(0, 16 - Count);
                result = saveRotated + saveParsed;
                cpu.selectGPR(cpu.getRs1().getValue()).setValue(Integer.parseInt((String) result, 2));
            }
        }
    }

    // 33
    /**
     * LDX OPERATION
     * @param cpu
     */
    private void decodeLDX(CPU cpu) {
        int ixVal = cpu.getCurIXR();
        if (ixVal == 0) {
            System.out.println("[ERROR] [INST] [LDX] [FAULT] Fault: LDX Missing IXR");
        }
        cpu.getRs1().setValue(ixVal);
        cpu.setDeviceOutput(DEVID.REGISTER);
        cpu.setRegisterOutput("IXR");
    }

    private void executeLDX(CPU cpu) {
        cpu.getIrr().setValue(cpu.getMbr().getValue());
    }

    // 34
    /**
     * STX OPERATION
     * @param cpu
     */
    private void decodeSTX(CPU cpu) {
        int ixVal = cpu.getCurIXR();
        if (ixVal == 0) {
            System.out.println("Fault: no index register specified in LDX");
        }
        cpu.getRs1().setValue(ixVal);
        cpu.setDeviceOutput(DEVID.MEMORY);
        cpu.setRegisterOutput("IXR");
    }

    private void executeSTX(CPU cpu) {
        int val = cpu.selectIXR(cpu.getRs1().getValue()).getValue();
        cpu.getIrr().setValue(val);
    }

    // 49
    /**
     * IN OPERATION
     * @param cpu
     */
    private void decodeIN(CPU cpu) {
        cpu.setIxiflag(false);
    }

    private void executeIN(CPU cpu) {
        int devId = cpu.getIar().getValue();
        if (devId == DEVID.KEYBOARD.getId()) {
            cpu.getKeyboardInput();
        } else if (devId != DEVID.CARD_READER.getId()) {
            System.out.println("[ERROR] [INST] Invalid devid for IN instruction");
        }
    }

    // 50
    /**
     * OUT OPERATION
     * @param cpu
     */
    private void decodeOUT(CPU cpu) {
        cpu.setIxiflag(false);
    }

    private void executeOUT(CPU cpu) {
        int devId = cpu.getIar().getValue();
        if (devId == DEVID.PRINTER.getId()) {
            cpu.printToConsole(cpu.selectGPR(cpu.getRs1().getValue()));
        } else {
            System.out.println("[ERROR] [INST] Invalid devid for IN instruction");
        }
    }

    // 51 CHK (P3)
}