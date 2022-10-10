package Memory;

import java.util.Arrays;

/**
 * Memory Class
 */
public class Memory {
    private final int memSize = 2048;
    private final int[] memory = new int[memSize];

    /**
     * Convert From Hex (Memory Inst) to Base 10 (Integer) 
     * @param input
     * @return
     */
    public int convertHextoDec(String input) {
        try {
            return Integer.parseInt(input, 16);
        }
        catch (NumberFormatException ne) {
            System.out.println("[ERROR] [MEM] [HEX2DEC]: Invalid Hex Number");
            return -1;
        }
    }

    /**
     * Store Value into Memory Address
     * @param value
     * @param address
     */
    public void store(int value, int address) {
        if (address < 0 || address > memSize) {
            System.out.println("[Error] [MEM] [STORE] Invalid Memory Address");
            return;
        }
        this.memory[address] = value;
    }

    /**
     * Load Data from Memory address
     * @param address Base 10 Memory Address
     * @return
     */
    public int load(int address) {
        if (address < 0 || address > memSize) {
            System.out.println("[Error] [MEM] [LOAD] Invalid Memory Address");
            return 0;
        }
        return this.memory[address];
    }

    /**
     * getter for memory data
     * @return
     */
    public int[] getMemory() {
        return this.memory;
    }

    /**
     * Reset values in memory
     */
    public void reset() {
        Arrays.fill(this.memory, 0);
    }

    @Override
    public String toString() {
        return "Memory: " + Arrays.toString(this.memory);
    }
}