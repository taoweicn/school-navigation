import java.awt.*;
import java.util.Map;

public class Painter extends DrawComponent {
  static final int TRAVEL_MODE = 1;
  static final int NAVIGATOR_MODE = 2;
  private Navigator navigator;
  private String start;
  private String end;
  private int mode;

  Painter(Navigator navigator) {
    this.navigator = navigator;
    this.mode = 0;
  }

  @Override
  public void paintComponent(Graphics g) {
    this.drawAllBuildingsAndRoads(g, this.navigator.getAllBuildings());
    if (this.mode == TRAVEL_MODE) {
      this.drawPath((Graphics2D) g, this.navigator.travelAllCulturalAttractions(this.start));
    } else if (this.mode == NAVIGATOR_MODE) {
      this.drawPath(
          (Graphics2D) g, this.navigator.travelAtLeastOneCulturalAttraction(this.start, this.end));
    }
  }

  void setStart(String point) {
    this.start = point;
  }

  void setEnd(String point) {
    this.end = point;
  }

  void setMode(int mode) {
    this.mode = mode;
    this.repaint();
  }

  private void drawBuilding(Graphics g, Building building) {
    String path = "";
    String name = "";
    int x = (int) building.getLongitude();
    int y = (int) building.getLatitude();
    switch (building.getType()) {
      case CULTURAL_ATTRACTION:
        {
          path = "./src/main/resources/icons/culturalAttraction.png";
          name = "人文景点";
          break;
        }
      case CLASSROOM:
        {
          path = "./src/main/resources/icons/classroom.png";
          name = "教室";
          break;
        }
      case DORMITORY:
        {
          path = "./src/main/resources/icons/dormitory.png";
          name = "宿舍";
          break;
        }
      default:
    }
    super.drawImage(g, path, x, y - 15, 36, 36);
    g.drawString(name + building.getName(), x - 20, y + 18);
  }

  private void drawAllBuildingsAndRoads(Graphics g, Map<String, Building> buildings) {
    for (Building building : buildings.values()) {
      this.drawBuilding(g, building);
      for (String availablePlaceName : building.getAvailablePlaces()) {
        Building availablePlace = this.navigator.getBuildingByName(availablePlaceName);
        if (availablePlace == null) {
          continue;
        }
        float x1 = building.getLongitude();
        float y1 = building.getLatitude();
        float x2 = availablePlace.getLongitude();
        float y2 = availablePlace.getLatitude();
        this.drawLine((Graphics2D) g, x1, y1, x2, y2, new Color(120, 165, 240));
        g.setColor(new Color(61, 77, 102));
        g.drawString(
            Long.toString(Math.round(building.getDistance(availablePlace))),
            (int) (x1 + x2) / 2 + 10,
            (int) (y1 + y2) / 2 - 10);
      }
    }
  }

  private void drawPath(Graphics2D g2, String[] path) {
    for (int i = 0; i < path.length - 1; i++) {
      Building building = this.navigator.getBuildingByName(path[i]);
      Building nextBuilding = this.navigator.getBuildingByName(path[i + 1]);
      float x1 = building.getLongitude();
      float y1 = building.getLatitude();
      float x2 = nextBuilding.getLongitude();
      float y2 = nextBuilding.getLatitude();
      this.drawLine(g2, x1, y1, x2, y2, new Color(251, 114, 83));
    }
  }
}
