import java.awt.*;
import java.util.Arrays;

public class Painter extends DrawComponent {
  private Navigator navigator;

  public Painter() {
    this.navigator = new Navigator();
  }

  @Override
  public void paintComponent(Graphics g) {
    this.drawAllBuildingsAndRoads(g, this.navigator.getAllBuildings());
    this.drawPath((Graphics2D) g, this.navigator.travelAtLeastOneCulturalAttraction("F", "C"));
  }

  public void drawBuilding(Graphics g, Building building) {
    String path = "";
    String name = "";
    int x = (int) building.getLongitude();
    int y = (int) building.getLatitude();
    switch (building.getType()) {
      case CULTURAL_ATTRACTION: {
        path = "./src/main/resources/icons/culturalAttraction.png";
        name = "人文景点";
        break;
      }
      case CLASSROOM: {
        path = "./src/main/resources/icons/classroom.png";
        name = "教室";
        break;
      }
      case DORMITORY: {
        path = "./src/main/resources/icons/dormitory.png";
        name = "宿舍";
        break;
      }
      default:
    }
    super.drawImage(g, path, x, y - 15, 36, 36);
    g.drawString(name + building.getName(), x - 20, y + 18);
  }

  public void drawAllBuildingsAndRoads(Graphics g, Building[] buildings) {
    for(Building building: buildings) {
      this.drawBuilding(g, building);
      for(String availablePlaceName: building.getAvailablePlaces()) {
        Building availablePlace = this.navigator.getBuildingByName(availablePlaceName);
        float x1 = building.getLongitude();
        float y1 = building.getLatitude();
        float x2 = availablePlace.getLongitude();
        float y2 = availablePlace.getLatitude();
        this.drawLine((Graphics2D) g, x1, y1, x2, y2, new Color(120, 165, 240));
        g.setColor(new Color(61, 77, 102));
        g.drawString(Long.toString(Math.round(building.getDistance(availablePlace))), (int) (x1 + x2) / 2, (int) (y1 + y2) / 2);
      }
    }
  }

  public void drawPath(Graphics2D g2, String[] path) {
    System.out.println(Arrays.toString(path));
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