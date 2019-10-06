import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Application extends JFrame {
  private Navigator navigator = new Navigator();
  private Painter painter;
  private JComboBox startComboBox;
  private JComboBox endComboBox;

  private Application() {
    this.painter = new Painter(this.navigator);
    this.painter.addMouseListener(new MouseHandle());
    this.add(this.painter);
    this.drawButton(this.navigator.getAllBuildingsName());
    this.pack();
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          JFrame frame = new Application();
          Image icon = new ImageIcon("./src/main/resources/icons/navigator.png").getImage();
          frame.setIconImage(icon);
          frame.setSize(800, 600);
          frame.setTitle("校园导航系统");
          frame.setLocationRelativeTo(null);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setVisible(true);
        });
  }

  private JComboBox setComboBox(JPanel panel, String[] options, String label) {
    JLabel endLabel = new JLabel(label);
    panel.add(endLabel);
    JComboBox<String> comboBox = new JComboBox<>();
    for (String option : options) {
      comboBox.addItem(option);
    }
    panel.add(comboBox);
    return comboBox;
  }

  private void drawButton(String[] buildingsName) {
    JPanel contentPane = new JPanel();
    this.startComboBox = this.setComboBox(contentPane, buildingsName, "选择起点：");
    contentPane.add(this.startComboBox);
    JButton travelButton = new JButton("游览人文景点");
    travelButton.addActionListener(
        event -> {
          this.painter.setStart(
              (String) this.startComboBox.getItemAt(this.startComboBox.getSelectedIndex()));
          this.painter.setMode(Painter.TRAVEL_MODE);
        });
    contentPane.add(travelButton);
    this.endComboBox = this.setComboBox(contentPane, buildingsName, "选择终点：");
    contentPane.add(this.endComboBox);
    JButton navigateButton = new JButton("开始导航");
    navigateButton.addActionListener(
        event -> {
          this.painter.setStart(
              (String) this.startComboBox.getItemAt(this.startComboBox.getSelectedIndex()));
          this.painter.setEnd(
              (String) this.endComboBox.getItemAt(this.endComboBox.getSelectedIndex()));
          this.painter.setMode(Painter.NAVIGATOR_MODE);
        });
    contentPane.add(navigateButton);
    this.add(contentPane, BorderLayout.NORTH);
  }

  private class MouseHandle extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent event) {
      Building building = navigator.getTheClosestBuildingByPoint(event.getPoint());
      if (building == null) {
        return;
      }
      String buildingName = building.getName();
      switch (event.getButton()) {
        case MouseEvent.BUTTON1:
          painter.setStart(buildingName);
          startComboBox.setSelectedItem(buildingName);
          break;
        case MouseEvent.BUTTON3:
          painter.setEnd(buildingName);
          endComboBox.setSelectedItem(buildingName);
          break;
        default:
      }
    }
  }
}
