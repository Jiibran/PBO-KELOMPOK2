package perpus;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

public class SistemPerpustakaan {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
    }
}
    