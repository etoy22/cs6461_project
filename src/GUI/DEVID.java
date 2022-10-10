package GUI;

/**
 * Device IDs as specified by Instructions
 * Added Register as 3 and Memory as 4
 */
public enum DEVID {
    KEYBOARD(0),
    PRINTER(1),
    CARD_READER(2),
    REGISTER(3),
    MEMORY(4);

    private int id;

    private DEVID(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}