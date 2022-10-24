package CPU;

import GUI.DEVID;
import GUI.InputSwitches;
import Memory.Memory;
import Memory.Cache;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

/**
 * 
 */
public class CPU {
    private final JPanel Frame;
    private final Memory memory;
    private final Cache cache;
    private final ALU ALU;
    private final InputSwitches inputSwitches;

    private boolean runningBoot = false;
    private JTextArea printer;
    private JTextField keyboard;
    private Thread runThread;

    private int curGPR;
    private Register GPR0;  private Register GPR1;  private Register GPR2;  private Register GPR3;
    private int curIXR;
    private Register IXR1;   private Register IXR2;   private Register IXR3;   private boolean useIxi = true;
    private Register RX;    private Register RY;
    private Register PC;    private int nextPc;
    private Register MAR;   private Register MARMemory;
    private Register MBR;   private Register MFR;
    private Register IR;
    private Register IAR;   private JLabel IARVal;
    private Register IRR;   private JLabel IRRVal;
    private Register RS1;   private JLabel RS1Val;
    
    private JButton RunButton;    private JButton stepButton; private JToggleButton haltButton;
    private JButton storeButton; private JButton loadButton;
    private boolean isSimPaused = false;

    private DEVID outputDevice; private String outputRegister = "";
    private InstructionSet instructionSet; private JLabel currentInstructionDisplay; private int opCode;
    private int indirectBit;
    private int memoryLocation;
    private int ArithShift;
    private int LogRotate;
    private int Count;

    /**
     * Constructor for the CPU functionality. Initializes all registers and functionality
     * @param Frame The GUI Frame to display all information
     * @param memory An instance of memory class to contain data in memory
     * @param input An instance of the UserInput class to handle user input in switches
     */
    public CPU(JPanel Frame, Memory memory, InputSwitches input) {
        this.Frame = Frame;     this.memory = memory;    this.inputSwitches = input;
        this.ALU = new ALU();   this.cache = new Cache();
        this.addGeneralPurposeRegisters();
        this.addIndexRegisters();
        this.addPC();
        this.addMAR();
        this.addMBR();
        this.addIR();
        this.addMFR();
        this.addInternalRegisters();
        this.addRxRY();
        this.addRunButtons();
        this.addIODevices();
        this.addCurrentInstructionDisplay();
        this.ALU.addConditionCodeBits(Frame);
        this.addListeners();
        this.createRunThread();
        this.instructionSet = new InstructionSet();
    }

    /**
     * Loads the required Boot Steps to start the CPU
     */
    public void loadBootInstructions() {
        // TODO:
        loadBootProgram();
    }

    /**
     * Conducts 1 Instruction Cycle
     * Fetch Instruction
     * Decode Instruction
     * Fetch Operand
     * Execute Instruction
     * Store Results
     */
    public void singleInstructionCycle() {
        this.fetchInstruction();
        this.decodeInstruction();
        this.fetchOperand();
        this.execute();
        if (!this.haltButton.isSelected()) {
            this.writeResults();
            this.nextInstruction();
            this.updateInternalDisplays();
        }
    }

    /**
     * Step 1 of Instruction Cycle
     * Uses PC to get instruction from Memory
     * Sets value into MBR
     * Increases PC value
     */
    private void fetchInstruction() {
        this.MAR.setValue(this.PC.getValue());
        if (this.cache.inCache(this.PC.getValue())) {
            this.MBR.setValue(this.cache.getCacheAddress(this.PC.getValue()));
        } else {
            System.out.println("[INFO] [CPU] [FETCH] ADDRESS NOT IN CACHE");
            int data = this.memory.load(this.PC.getValue());
            this.MBR.setValue(data);
        }
        this.nextPc = this.PC.getValue() + 1;
    }

    /**
     * Step 2 of Instrcution Cycle
     * Decodes Instruction and updates required fields for Instruction to execute
     */
    private void decodeInstruction() {
        this.IR.setValue(this.MBR.getValue());
        this.getEffectiveAddress();
        this.RS1.setValue(this.curGPR);

        this.updateCurrentInstructionDisplay();
        this.instructionSet.decodeInstruction(opCode, this);
    }

    /**
     * 
     */
    private void fetchOperand() {
        this.IAR.setValue(this.memoryLocation);
        if (this.useIxi) {
            if (!this.outputRegister.equals("IXR") && this.curIXR != 0) {
                this.IAR.setValue(this.IAR.getValue() + this.selectIXR(this.curIXR).getValue());
                System.out.println("Iar after indexing: " + this.IAR.getValue());
            }
            if (this.indirectBit == 1) {
                System.out.println("Indirect addressing");
                this.IAR.setValue(this.memory.load(this.IAR.getValue()));
            }
        }
        this.MAR.setValue(this.IAR.getValue());
        this.MBR.setValue(this.memory.load(this.MAR.getValue()));
    }

    /**
     * 
     */
    private void execute() {
        this.instructionSet.executeInstruction(opCode, this);
    }

    /**
     * 
     */
    private void writeResults() {
        if (this.outputDevice.getId() == DEVID.REGISTER.getId()) {
            System.out.println("Loading value " + this.IRR.getValue() + " into " + this.outputRegister + this.RS1.getValue());
            if (this.outputRegister.equals("GPR")) {
                Register r = this.selectGPR(this.RS1.getValue());
                r.setValue(this.IRR.getValue());
                System.out.println("Storing value " + this.IRR.getValue() + " into register " + r.getName());
            } else if (this.outputRegister.equals("IXR")) {
                Register r = this.selectIXR(this.RS1.getValue());
                r.setValue(this.IRR.getValue());
                System.out.println("Storing value " + this.IRR.getValue() + " into register " + r.getName());
            }
        } else if (this.outputDevice.getId() == DEVID.MEMORY.getId()) {
            this.MBR.setValue(this.IRR.getValue());
            System.out.println("Storing value " + this.MBR.getValue() + " from register into memory location " + this.MAR.getValue());
            if (this.opCode != 2 && this.opCode != 34) {
                this.cache.insertAddress(this.MBR.getValue(), this.MAR.getValue(), this.memory);
            } else {
                this.memory.store(this.MBR.getValue(), this.MAR.getValue());
            }
        }
    }

    /**
     * 
     */
    private void nextInstruction() {
        this.PC.setValue(this.nextPc);
        System.out.println("PC: " + this.PC.getValue());
    }

    /**
     * 
     */
    private void updateInternalDisplays() {
        this.MARMemory.setValue(this.memory.load(this.MAR.getValue()));
        this.IARVal.setText("" + this.IAR.getValue());
        this.IRRVal.setText("" + this.IRR.getValue());
        this.RS1Val.setText("" + this.RS1.getValue());
        this.ALU.updateCcDisplays();
    }

    /**
     * 
     */
    public void resetCPUandMemory() {
        this.reset();
        this.memory.reset();
    }

    /**
     * 
     */
    public void reset() {
        resetGPR();
        resetIXR();
        resetRXRY();
        resetPC();
        resetMemory();
        this.IR.setValue(0);
        this.setHalted(false);
        resetALUVals();
        this.updateCurrentInstructionDisplay();
        this.opCode = 0;
        this.curIXR = 0;
        this.indirectBit = 0;
    }

    /**
     * 
     */
    private void resetALUVals() {
        this.ALU.reset();
        this.IAR.setValue(0);
        this.IARVal.setText("0");
        this.IRR.setValue(0);
        this.IRRVal.setText("0");
        this.RS1.setValue(0);
        this.RS1Val.setText("0");
    }

    /**
     * 
     */
    private void resetMemory() {
        this.MAR.setValue(0);
        this.MARMemory.setValue(this.memory.load(this.MAR.getValue()));
        this.MBR.setValue(0);
        this.MFR.setValue(0);
        this.memoryLocation = 0;
    }

    /**
     * 
     */
    private void resetPC() {
        this.PC.setValue(0);
        this.nextPc = 0;
    }

    /**
     * 
     */
    private void resetRXRY() {
        this.RX.setValue(0);
        this.RY.setValue(0);
    }

    /**
     * 
     */
    private void resetIXR() {
        this.IXR1.setValue(0);
        this.IXR2.setValue(0);
        this.IXR3.setValue(0);
    }

    /**
     * 
     */
    private void resetGPR() {
        this.GPR0.setValue(0);
        this.GPR1.setValue(0);
        this.GPR2.setValue(0);
        this.GPR3.setValue(0);
        this.curGPR = 0;

    }

    /**
     * 
     * @param gpr
     * @return
     */
    public Register selectGPR(int gpr) {
        if (gpr == 1) {
            return this.GPR1;
        }
        if (gpr == 2) {
            return this.GPR2;
        }
        if (gpr == 3) {
            return this.GPR3;
        }
        return this.GPR0;
    }

    /**
     * 
     * @param ixr
     * @return
     */
    public Register selectIXR(int ixr) {
        if (ixr == 2) {
            return this.IXR2;
        }
        if (ixr == 3) {
            return this.IXR3;
        }
        return this.IXR1;
    }

    /**
     * 
     */
    private void getEffectiveAddress() {
        String binary = this.IR.getBinaryStringValue();
        this.inputSwitches.setSwitchValue(binary);
        this.opCode = Integer.parseInt(binary.substring(0, 6), 2);
        this.curGPR = Integer.parseInt(binary.substring(6, 8), 2);
        this.curIXR = Integer.parseInt(binary.substring(8, 10), 2);
        this.indirectBit = Integer.parseInt(binary.substring(10, 11), 2);
        this.memoryLocation = Integer.parseInt(binary.substring(11), 2);
        this.ArithShift = Integer.parseInt(binary.substring(8, 9), 2);
        this.LogRotate = Integer.parseInt(binary.substring(9, 10), 2);
        this.Count = Integer.parseInt(binary.substring(12), 2);
        this.RX = this.selectGPR(this.curGPR);
        this.RY = this.selectGPR(this.curIXR);
    }

    /**
     * 
     */
    private void loadBootProgram() {
        System.out.println("[INFO] [CPU] [BOOT] Loading Boot Program.");

        try {
            String line;
            InputStream instream = this.getClass().getResourceAsStream("boot.txt");
            if (instream == null) {
                System.out.println("[ERROR] [CPU] [BOOT] Unable to get File boot.txt");
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
            while ((line = br.readLine()) != null) {
                String[] instr = line.split("\\s+");
                int new_location = this.memory.convertHextoDec(instr[0]);
                int new_value = this.memory.convertHextoDec(instr[1]);
                System.out.println(new_value + " :: Stored :: " + new_location);
                this.memory.store(new_value, new_location);
            }
            System.out.println("[INFO] [CPU] [BOOT] Boot Program loaded.");
        }
        catch (Exception e) {
            System.out.println("[ERROR] [CPU] [BOOT] Unable to load boot program :: " + e.getMessage());
        }
    }

    /**
     * 
     */
    public void printToConsole(Register r) {
        if (this.runningBoot || r.isChar()) {
            this.printer.append(Character.toString(r.getChar()));
        } else {
            this.printer.append(r.getValue() + " \n");
        }
    }

    /**
     * 
     */
    public void getKeyboardInput() {
        Register r = this.selectGPR(this.RS1.getValue());
        while (true) {
            boolean isInputLarge = false;
            String input = JOptionPane.showInputDialog(this.Frame, (Object)"Enter a number: ");
            if (input == null) {
                String quit = "Input must be a valid number.\nWould you like to QUIT instead?";
                int quitStatus = JOptionPane.showConfirmDialog(this.Frame, quit);
                if (quitStatus != 0) continue;
                System.exit(0);
            }
            try {
                int inputNumber = Integer.parseInt(input);
                if (!((double)inputNumber > Math.pow(2.0, r.getLength()))) {
                    r.setValue(inputNumber);
                    this.keyboard.setText("" + inputNumber);
                    System.out.println("[INFO] [CPU] [INPUT] Register " + r.getName() + " loaded with value " + inputNumber);
                    return;
                }
                JOptionPane.showMessageDialog(this.Frame, "Hold on there, that number is too large for the Simulator!");
                System.out.println("[ERROR] [CPU] [INPUT] User entered a number that was too large for the simulator");
                isInputLarge = true;
            }
            catch (NumberFormatException e) {
                System.out.println("[ERROR] [CPU] [INPUT] Exception reading User Input: " + e.getMessage());
            }
            if (isInputLarge) continue;
            if (input.length() == 1) {
                char c = input.charAt(0);
                r.setValue(c);
                this.keyboard.setText("" + c);
                System.out.println("[INFO] [CPU] [INPUT] " + r.getName() + " Loaded with  " + c);
                return;
            }
            JOptionPane.showMessageDialog(this.Frame, "Input must be a valid number");
        }
    }

    /**
     * 
     */
    public void setHalted(boolean isHalted) {
        this.haltButton.setSelected(isHalted);
        this.RunButton.setEnabled(!isHalted);
        this.stepButton.setEnabled(!isHalted);
    }

    /**
     * 
     */
    private void createRunThread() {
        this.runThread = new Thread("runThread"){

            @Override
            public void run() {
                while (true) {
                    if (!CPU.this.isSimPaused) {
                        CPU.this.singleInstructionCycle();
                        if (CPU.this.PC.getValue() + 1 == 2048) break;
                    }
                    try {
                        Thread.sleep(50L);
                    }
                    catch (InterruptedException ex) {
                        System.out.println("Critical error when machine is halted: " + ex.getMessage());
                        break;
                    }
                }
            }
        };
    }

    /**
     * 
     */
    private void addGeneralPurposeRegisters() {
        this.GPR0 = new Register("GPR0", 16, this.inputSwitches, true);
        this.GPR1 = new Register("GPR1", 16, this.inputSwitches, true);
        this.GPR2 = new Register("GPR2", 16, this.inputSwitches, true);
        this.GPR3 = new Register("GPR3", 16, this.inputSwitches, true);
        GUI.GUI.addComponent(this.GPR0.getLabel(), this.Frame, 0, 0, 1);
        GUI.GUI.addComponent(this.GPR0.getTextField(), this.Frame, 1, 0, 1);
        GUI.GUI.addComponent(this.GPR0.getLoadButton(), this.Frame, 2, 0, 1);
        GUI.GUI.addComponent(this.GPR1.getLabel(), this.Frame, 0, 1, 1);
        GUI.GUI.addComponent(this.GPR1.getTextField(), this.Frame, 1, 1, 1);
        GUI.GUI.addComponent(this.GPR1.getLoadButton(), this.Frame, 2, 1, 1);
        GUI.GUI.addComponent(this.GPR2.getLabel(), this.Frame, 0, 2, 1);
        GUI.GUI.addComponent(this.GPR2.getTextField(), this.Frame, 1, 2, 1);
        GUI.GUI.addComponent(this.GPR2.getLoadButton(), this.Frame, 2, 2, 1);
        GUI.GUI.addComponent(this.GPR3.getLabel(), this.Frame, 0, 3, 1);
        GUI.GUI.addComponent(this.GPR3.getTextField(), this.Frame, 1, 3, 1);
        GUI.GUI.addComponent(this.GPR3.getLoadButton(), this.Frame, 2, 3, 1);
    }

    /**
     * 
     */
    private void addIndexRegisters() {
        this.IXR1 = new Register("IXR1", 16, this.inputSwitches, true);
        this.IXR2 = new Register("IXR2", 16, this.inputSwitches, true);
        this.IXR3 = new Register("IXR3", 16, this.inputSwitches, true);
        GUI.GUI.addComponent(this.IXR1.getLabel(), this.Frame, 0, 6, 1);
        GUI.GUI.addComponent(this.IXR1.getTextField(), this.Frame, 1, 6, 1);
        GUI.GUI.addComponent(this.IXR1.getLoadButton(), this.Frame, 2, 6, 1);
        GUI.GUI.addComponent(this.IXR2.getLabel(), this.Frame, 0, 7, 1);
        GUI.GUI.addComponent(this.IXR2.getTextField(), this.Frame, 1, 7, 1);
        GUI.GUI.addComponent(this.IXR2.getLoadButton(), this.Frame, 2, 7, 1);
        GUI.GUI.addComponent(this.IXR3.getLabel(), this.Frame, 0, 8, 1);
        GUI.GUI.addComponent(this.IXR3.getTextField(), this.Frame, 1, 8, 1);
        GUI.GUI.addComponent(this.IXR3.getLoadButton(), this.Frame, 2, 8, 1);
    }

    /**
     * 
     */
    private void addPC() {
        this.PC = new Register("PC", 12, this.inputSwitches, false);
        this.nextPc = 0;
        GUI.GUI.addComponent(this.PC.getLabel(), this.Frame, 6, 0, 1);
        GUI.GUI.addComponent(this.PC.getTextField(), this.Frame, 7, 0, 1, 22);
        GUI.GUI.addComponent(this.PC.getLoadButton(), this.Frame, 8, 0, 1);
    }

    /**
     * 
     */
    private void addMAR() {
        this.MAR = new Register("MAR", 12, this.inputSwitches, false);
        this.MARMemory = new Register("Mem @ MAR", 16, this.inputSwitches, false);
        GUI.GUI.addComponent(this.MAR.getLabel(), this.Frame, 6, 1, 1);
        GUI.GUI.addComponent(this.MAR.getTextField(), this.Frame, 7, 1, 1, 22);
        GUI.GUI.addComponent(this.MAR.getLoadButton(), this.Frame, 8, 1, 1);
        GUI.GUI.addComponent(this.MARMemory.getLabel(), this.Frame, 9, 1, 1);
        GUI.GUI.addComponent(this.MARMemory.getTextField(), this.Frame, 10, 1, 1, 21);
    }

    /**
     * 
     */
    private void addMBR() {
        this.MBR = new Register("MBR", 16, this.inputSwitches, true);
        GUI.GUI.addComponent(this.MBR.getLabel(), this.Frame, 6, 2, 1);
        GUI.GUI.addComponent(this.MBR.getTextField(), this.Frame, 7, 2, 1, 22);
        GUI.GUI.addComponent(this.MBR.getLoadButton(), this.Frame, 8, 2, 1);
    }

    /**
     * 
     */
    private void addIR() {
        this.IR = new Register("IR", 16, this.inputSwitches, false);
        GUI.GUI.addComponent(this.IR.getLabel(), this.Frame, 6, 3, 1);
        GUI.GUI.addComponent(this.IR.getTextField(), this.Frame, 7, 3, 1, 22);
    }

    /**
     * 
     */
    private void addMFR() {
        this.MFR = new Register("MFR", 4, this.inputSwitches, false);
        GUI.GUI.addComponent(this.MFR.getLabel(), this.Frame, 6, 4, 1);
        GUI.GUI.addComponent(this.MFR.getTextField(), this.Frame, 7, 4, 1, 22);
    }

    /**
     * 
     */
    private void addRunButtons() {
        this.stepButton = new JButton("Single Step");
        this.RunButton = new JButton("Run");
        this.haltButton = new JToggleButton("Halt");
        this.storeButton= new JButton("Store");
        this.loadButton=new JButton("Load");
        GUI.GUI.addComponent(this.stepButton, this.Frame, 1, 11, 1);
        GUI.GUI.addComponent(this.RunButton, this.Frame, 2, 11, 1);
        GUI.GUI.addComponent(this.haltButton, this.Frame, 3, 11, 1);
        GUI.GUI.addComponent(this.storeButton, this.Frame, 4, 11, 1);
        GUI.GUI.addComponent(this.loadButton, this.Frame, 5, 11, 1);

    }

    /**
     * 
     */
    private void addInternalRegisters() {
        this.IAR = new Register("IAR", 16, this.inputSwitches, true);
        this.IARVal = new JLabel("" + this.IAR.getValue());
        this.IRR = new Register("IRR", 16, this.inputSwitches, true);
        this.IRRVal = new JLabel("" + this.IRR.getValue());
        this.RS1 = new Register("RS1", 2, this.inputSwitches, false);
        this.RS1Val = new JLabel("" + this.RS1.getValue());
        GUI.GUI.addComponent(this.IAR.getLabel(), this.Frame, 9, 2, 1);
        GUI.GUI.addComponent(this.IAR.getTextField(), this.Frame, 10, 2, 1);
        GUI.GUI.addComponent(this.IARVal, this.Frame, 11, 2, 1);
        GUI.GUI.addComponent(this.IRR.getLabel(), this.Frame, 9, 3, 1);
        GUI.GUI.addComponent(this.IRR.getTextField(), this.Frame, 10, 3, 1);
        GUI.GUI.addComponent(this.IRRVal, this.Frame, 11, 3, 1);
        GUI.GUI.addComponent(this.RS1.getLabel(), this.Frame, 9, 0, 1);
        GUI.GUI.addComponent(this.RS1.getTextField(), this.Frame, 10, 0, 1, 22);
        GUI.GUI.addComponent(this.RS1Val, this.Frame, 11, 0, 1);
    }

    /**
     * Arithmetic Registers
     */
    private void addRxRY() {
        this.RX = new Register("RX", 16, this.inputSwitches, true);
        this.RY = new Register("RY", 16, this.inputSwitches, true);
    }

    /**
     * 
     */
    private void addIODevices() {
        JLabel printerLabel = new JLabel("DEV CONSOLE");
        this.printer = new JTextArea(20, 20);
        JScrollPane scroll = new JScrollPane(this.printer);
        this.printer.setEditable(false);
        this.printer.setLineWrap(true);
        GUI.GUI.addComponent(printerLabel, this.Frame, 6, 6, 3, 10);
        GUI.GUI.addComponent(scroll, this.Frame, 6, 7, 3, 5, 10);
        JLabel keyboardLabel = new JLabel("Last Input");
        this.keyboard = new JTextField("", 10);
        keyboardLabel.setHorizontalAlignment(4);
        this.keyboard.setEditable(false);
        GUI.GUI.addComponent(keyboardLabel, this.Frame, 10, 9, 2, 10);
        GUI.GUI.addComponent(this.keyboard, this.Frame, 10, 10, 3, 10);
    }

    /**
     * 
     */
    public void addCurrentInstructionDisplay() {
        this.currentInstructionDisplay = new JLabel("Current Instr: N/A");
        GUI.GUI.addComponent(this.currentInstructionDisplay, this.Frame, 10, 11, 2, 21);
    }

    /**
     * 
     */
    private void addListeners() {
        this.RunButton.addActionListener(ae -> {
            if (!this.runThread.isAlive()) {
                this.runThread.start();
            }
            this.isSimPaused = false;
        });
        this.haltButton.addItemListener(e -> {
            if (e.getStateChange() == 1) {
                this.setHalted(true);
                this.isSimPaused = true;
            } else {
                this.setHalted(false);
            }
        });
        this.stepButton.addActionListener(e -> this.singleInstructionCycle());
        this.storeButton.addActionListener
        (new ActionListener() 
        {
			public void actionPerformed(ActionEvent e) 
				{
				int Value=MBR.getValue();
				int Index=MAR.getValue();
	            memory.store(Value, Index);
	            }
		}
        );
    
        this.loadButton.addActionListener
        (new ActionListener() 
        {
			public void actionPerformed(ActionEvent e) 
			{
				int index_marmemory=MAR.getValue();
				System.out.println(index_marmemory);
					int n5 = memory.load(index_marmemory);
		            
		            
		            MBR.setValue(n5);
				memory.load(index_marmemory);
			}
		});
        
        
        
        
        
    }

    /**
     * 
     * @param deviceOutput
     */
    public void setDeviceOutput(DEVID deviceOutput) {
        this.outputDevice = deviceOutput;
    }

    /**
     * 
     * @param registerOutput
     */
    public void setRegisterOutput(String registerOutput) {
        this.outputRegister = registerOutput;
    }

    /**
     * 
     */
    public void updateCurrentInstructionDisplay() {
        this.currentInstructionDisplay.setText("Current Inst.: " + this.instructionSet.getInstructionName(opCode));
    }

    /**
     * 
     * @return
     */
    public Register getMbr() {
        return this.MBR;
    }

    /**
     * 
     * @return
     */
    public Register getRs1() {
        return this.RS1;
    }

    /**
     * 
     * @return
     */
    public Register getIar() {
        return this.IAR;
    }

    /**
     * 
     * @return
     */
    public Register getIrr() {
        return this.IRR;
    }

    /**
     * 
     * @return
     */
    public int getShift() {
        return this.ArithShift;
    }

    /**
     * 
     * @return
     */
    public int getRotate() {
        return this.LogRotate;
    }

    /**
     * 
     * @return
     */
    public int getCount() {
        return this.Count;
    }

    /**
     * 
     * @return
     */
    public Memory getMemory() {
        return this.memory;
    }

    /**
     * 
     * @return
     */
    public ALU getALU() {
        return this.ALU;
    }

    /**
     * 
     * @param PCValue
     */
    public void setNextPc(int PCValue) {
        this.nextPc = PCValue;
    }

    /**
     * 
     * @return
     */
    public Register getPC() {
        return this.PC;
    }

    /**
     * 
     * @return
     */
    public Register getRX() {
        return this.RX;
    }

    /**
     * 
     * @return
     */
    public Register getRY() {
        return this.RY;
    }

    /**
     * 
     * @return
     */
    public int getCurGPR() {
        return this.curGPR;
    }

    /**
     * 
     * @return
     */
    public Register getGPR0() {
        return this.GPR0;
    }

    /**
     * 
     * @return
     */
    public Register getGPR1() {
        return this.GPR1;
    }

    /**
     * 
     * @return
     */
    public Register getGPR2() {
        return this.GPR2;
    }

    /**
     * 
     * @return
     */
    public Register getGPR3() {
        return this.GPR3;
    }

    /**
     * 
     * @return
     */
    public void setIxiflag(boolean flag) {
        this.useIxi = flag;
    }

    /**
     * 
     * @return
     */
    public int getCurIXR() {
        return this.curIXR;
    }

    /**
     * 
     * @return
     */
    public Register getIX1() {
        return this.IXR1;
    }

    /**
     * 
     * @return
     */
    public Register getIX2() {
        return this.IXR2;
    }

    /**
     * 
     * @return
     */
    public Register getIX3() {
        return this.IXR3;
    }

}