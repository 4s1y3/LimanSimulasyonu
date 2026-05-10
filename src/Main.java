
import logic.LimanYonetici;
import gui.AnaPencere;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        LimanYonetici yonetici = new LimanYonetici();
        yonetici.yukle();
        
        SwingUtilities.invokeLater(() -> {
            new AnaPencere(yonetici).setVisible(true);
        });
