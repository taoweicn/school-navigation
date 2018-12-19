import java.io.*;
import java.util.*;
import com.google.gson.*;

public class Navigator {
  private Building[] culturalAttraction;
  private Building[] classroom;
  private Building[] dormitory;

  public static void main(String[] args) {
    Navigator n = new Navigator();
    n.travelAllCulturalAttractions("F");
  }

  public Navigator() {
    this.readBuildingsInfo();
  }

  /**
   * 获取文件信息
   */
  public void readBuildingsInfo() {
    JsonParser parser = new JsonParser();
    try {
      JsonObject buildings = (JsonObject) parser.parse(new FileReader("./src/main/resources/buildings.json"));

      this.culturalAttraction = this.parseJsonArrayBuildings(
        Type.CULTURAL_ATTRACTION,
        buildings.get("culturalAttraction").getAsJsonArray()
      );
      this.classroom = this.parseJsonArrayBuildings(
        Type.CLASSROOM,
        buildings.get("classroom").getAsJsonArray()
      );
      this.dormitory = this.parseJsonArrayBuildings(
        Type.DORMITORY,
        buildings.get("dormitory").getAsJsonArray()
      );
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * 将json格式转为为java对象
   * @param type 地点类型
   * @param jsonArray json格式的地点数据
   * @return java对象
   */
  public Building[] parseJsonArrayBuildings(Type type, JsonArray jsonArray) {
    Building[] buildings = new Building[jsonArray.size()];
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
      JsonArray availablePlacesJson = jsonObject.get("availablePlaces").getAsJsonArray();
      String[] availablePlaces = new String[availablePlacesJson.size()];
      for (int j = 0; j < availablePlacesJson.size(); j++) {
        availablePlaces[j] = availablePlacesJson.get(j).getAsString();
      }
      buildings[i] = new Building(
        jsonObject.get("name").getAsString(),
        type,
        jsonObject.get("longitude").getAsFloat(),
        jsonObject.get("latitude").getAsFloat(),
        availablePlaces
      );
    }
    return buildings;
  }

  /**
   * 通过建筑物名称来获取java对象
   * @param name 名称
   * @return java对象
   */
  public Building getBuildingByName(String name) {
    for (Building culturalAttractions: this.culturalAttraction) {
      if (culturalAttractions.getName().equals(name)) {
        return culturalAttractions;
      }
    }
    for (Building classroom: this.classroom) {
      if (classroom.getName().equals(name)) {
        return classroom;
      }
    }
    for (Building dormitory: this.dormitory) {
      if (dormitory.getName().equals(name)) {
        return dormitory;
      }
    }
    return null;
  }

  /**
   * 从起点开始，不重复地遍历所有人文景点
   * @param origin 起点
   */
  public void travelAllCulturalAttractions(String origin) {
    ArrayList<String[]> results = new ArrayList<>(0);
    this.travel(origin, this.getBuildingByName(origin), new String[] { origin }, results);
    double minDistance = Double.MAX_VALUE;
    for (int i = results.size() - 1; i >= 0; i--) {
      String[] res = results.get(i);
      double distance = this.calcRouteDistance(res);
      System.out.println(Arrays.toString(res));
      // 如果这里是 <= 就可以去掉往返的重复路径，但也存在舍入误差
      if (minDistance < distance) {
        results.remove(res);
      } else {
        minDistance = distance;
      }
    }
    System.out.println(minDistance);
  }

  /**
   * 递归遍历所有可达到的人文景点
   * @param origin 起点
   * @param building 当前所处位置
   * @param route 走过的路径
   * @param res 所有结果
   */
  public void travel(String origin, Building building, String[] route, ArrayList<String[]> res) {
    for (String availablePlaceName: building.getAvailablePlaces()) {
      Building availablePlace = this.getBuildingByName(availablePlaceName);
      boolean flag = false;
      // 表明是人文景点且未被遍历过
      if(!Arrays.asList(route).contains(availablePlaceName) && availablePlace.getType() == Type.CULTURAL_ATTRACTION) {
        String[] newRoute = Arrays.copyOf(route, route.length + 1);
        newRoute[route.length] = availablePlaceName;
        this.travel(origin, availablePlace, newRoute, res);
        flag = true;
      }
      // 到达终点且所有人文景点被遍历则保存路径
      if (!flag && availablePlaceName.equals(origin) && this.judgeWhetherAllTraveled(route)) {
        String[] newRoute = Arrays.copyOf(route, route.length + 1);
        newRoute[route.length] = origin;
        res.add(newRoute);
      }
    }
  }

  /**
   * 判断是否所有人文景点都被遍历过
   * @param route 路径
   * @return 是否全被遍历
   */
  public boolean judgeWhetherAllTraveled(String[] route){
    List routeList = Arrays.asList(route);
    for (Building culturalAttractions: this.culturalAttraction) {
      if (!routeList.contains(culturalAttractions.getName())) {
        return false;
      }
    }
    return true;
  }

  public double calcRouteDistance(String[] route) {
    double distance = 0;
    for(int i = 0; i < route.length - 1; i++) {
      distance += this.getBuildingByName(route[i]).getDistance(this.getBuildingByName(route[i + 1]));
    }
    return distance;
  }
}
