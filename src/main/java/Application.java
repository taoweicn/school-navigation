import java.awt.*;
import javax.swing.*;

public class Application extends JFrame {
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      JFrame frame = new Application();
      frame.setSize(800, 600);
      frame.setTitle("校园导航系统");
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
    });
  }

  public Application() {
    this.add(new Painter());
    this.pack();
  }
}
