import java.awt.*;
import javax.swing.*;

public class Application extends JFrame {
  private Painter painter;

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
    Navigator navigator = new Navigator();
    this.painter = new Painter(navigator);
    this.add(this.painter);
    this.drawButton(navigator.getAllBuildingsName());
    this.pack();
  }

  public JComboBox setComboBox(JPanel panel, String[] options, String label) {
    JLabel endLabel = new JLabel(label);
    panel.add(endLabel);
    JComboBox comboBox = new JComboBox();
    for(String option: options) {
      comboBox.addItem(option);
    }
    panel.add(comboBox);
    return comboBox;
  }

  public void drawButton(String[] buildingsName) {
    JPanel contentPane = new JPanel();
    JComboBox startComboBox = this.setComboBox(contentPane, buildingsName, "选择起点：");
    contentPane.add(startComboBox);
    JButton travelButton = new JButton("游览人文景点");
    travelButton.addActionListener(event -> {
      this.painter.setStart((String) startComboBox.getItemAt(startComboBox.getSelectedIndex()));
      this.painter.setMode(1);
    });
    contentPane.add(travelButton);
    JComboBox endComboBox = this.setComboBox(contentPane, buildingsName, "选择终点：");
    contentPane.add(endComboBox);
    JButton navigateButton = new JButton("开始导航");
    navigateButton.addActionListener(event -> {
      this.painter.setStart((String) startComboBox.getItemAt(startComboBox.getSelectedIndex()));
      this.painter.setEnd((String) endComboBox.getItemAt(endComboBox.getSelectedIndex()));
      this.painter.setMode(2);
    });
    contentPane.add(navigateButton);
    this.add(contentPane, BorderLayout.NORTH);
  }
}
