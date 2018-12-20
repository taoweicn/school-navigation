import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.*;

class DrawComponent extends JComponent {
  public void drawCircle(Graphics2D g2, double centerX, double centerY, double radius) {
    Ellipse2D circle = new Ellipse2D.Double();
    circle.setFrameFromCenter(centerX, centerY, radius + radius, radius + radius);
    g2.draw(circle);
  }

  public void drawLine(Graphics2D g2, double startX, double startY, double endX, double endY, Color color) {
    g2.setPaint(color);
    g2.setStroke(new BasicStroke(3));
    g2.draw(new Line2D.Double(startX, startY, endX, endY));
  }

  public void drawImage(Graphics g, String path, int centerX, int centerY, int width, int height) {
    Image image = new ImageIcon(path).getImage();
    g.drawImage(image, centerX - width / 2, centerY - height / 2, width, height, null);
  }
}
