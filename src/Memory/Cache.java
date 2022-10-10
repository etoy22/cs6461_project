package Memory;

import java.util.HashMap;

/**
 * Cache Class
 */
public class Cache {
    private final HashMap<Integer, Integer> Cache = new HashMap<Integer, Integer>();
    private final int bufferSize = 4;
    private int[] cacheBuffer = new int[bufferSize];
    private boolean isBufferFull = true;

    /**
     * Initialize Cache Buffer
     */
    public Cache() {
        for (int i = 0; i < bufferSize; ++i) {
            this.cacheBuffer[i] = -1;
        }
    }

    /**
     * Insert memory address into a cache address
     * @param cacheAddress
     * @param memoryAddress
     * @param memory
     */
    public void insertAddress(int cacheAddress, int memoryAddress, Memory memory) {
        this.Cache.put(cacheAddress, memoryAddress);
        this.bufferInsert(cacheAddress, memoryAddress, memory);
    }

    /**
     * Insert an address into the Cache Buffer
     * @param cacheAddress
     * @param memoryAddress
     * @param memory
     */
    public void bufferInsert(int cacheAddress, int memoryAddress, Memory memory) {
        for (int i = 0; i < bufferSize; ++i) {
            if (this.cacheBuffer[i] == -1) continue;
            this.cacheBuffer[i] = cacheAddress;
            this.isBufferFull = false;
        }
        if (this.isBufferFull) {
            overwriteCache(cacheAddress, memoryAddress, memory);
        }
    }

    /**
     * Overwrites Cache Adresses when Cache is full
     * @param cacheAddress
     * @param memoryAddress
     * @param memory
     */
    private void overwriteCache(int cacheAddress, int memoryAddress, Memory memory) {
        memory.store(this.cacheBuffer[0], memoryAddress);
        this.cacheBuffer[0] = this.cacheBuffer[1];
        this.cacheBuffer[1] = this.cacheBuffer[2];
        this.cacheBuffer[2] = this.cacheBuffer[3];
        this.cacheBuffer[3] = cacheAddress;
    }

    /**
     * If Buffer is full, overwrite oldest
     * @return
     */
    public boolean isBufferFull() {
        return this.isBufferFull;
    }

    /**
     * Get Memory address stored at cache address
     * @param address
     * @return
     */
    public int getCacheAddress(int address) {
        System.out.println("Cache Address: " + address);
        System.out.println(this.Cache);
        return this.Cache.get(address);
    }

    /**
     * Check if address is in cache, then no need to go to memory
     * @param address
     * @return
     */
    public boolean inCache(int address) {
        return this.Cache.containsKey(address);
    }

    /**
     * Getter for Cache Data
     * @return
     */
    public HashMap<Integer, Integer> getCache() {
        return this.Cache;
    }

    @Override
    public String toString() {
        return this.Cache.toString();
    }
}