import javax.swing.SwingUtilities;
import GUI.GUI;

public class Simulator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI simulator = new GUI();
            simulator.startMachine();
        });
    }
}