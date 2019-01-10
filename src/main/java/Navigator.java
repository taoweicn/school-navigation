import java.io.*;
import java.util.*;
import com.google.gson.*;
import java.awt.Point;

public class Navigator {
  private Building[] buildings;

  public Navigator() {
    this.readBuildingsInfo();
  }

  /**
   * 获取文件信息
   */
  public void readBuildingsInfo() {
    JsonParser parser = new JsonParser();
    try {
      JsonArray buildings = (JsonArray) parser.parse(new FileReader("./src/main/resources/buildings.json"));
      this.buildings = this.parseJsonArrayBuildings(buildings);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * 将json格式转为为java对象
   * @param jsonArray json格式的地点数据
   * @return java对象
   */
  public Building[] parseJsonArrayBuildings(JsonArray jsonArray) {
    Building[] buildings = new Building[jsonArray.size()];
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
      JsonArray availablePlacesJson = jsonObject.get("availablePlaces").getAsJsonArray();
      String type = jsonObject.get("type").getAsString();
      String[] availablePlaces = new String[availablePlacesJson.size()];
      for (int j = 0; j < availablePlacesJson.size(); j++) {
        availablePlaces[j] = availablePlacesJson.get(j).getAsString();
      }
      buildings[i] = new Building(
        jsonObject.get("name").getAsString(),
        Type.mapStringToType(type),
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
    for (Building building: this.buildings) {
      if (building.getName().equals(name)) {
        return building;
      }
    }
    return null;
  }

  public Building[] getAllBuildings() {
    return this.buildings;
  }

  public String[] getAllBuildingsName() {
    String [] buildingsName = new String[this.buildings.length];
    for (int i = 0; i < this.buildings.length; i++) {
      buildingsName[i] = this.buildings[i].getName();
    }
    return buildingsName;
  }

  public Building[] getBuildingsByType(Type type) {
    ArrayList result = new ArrayList();
    for (Building building: this.buildings) {
      if (building.getType() == type) {
        result.add(building);
      }
    }
    return (Building[]) result.toArray(new Building[0]);
  }

  public Building[] getBuildingsPath(String [] path) {
    Building[] buildings = new Building[path.length];
    for(int i = 0; i < path.length; i++) {
      buildings[i] = this.getBuildingByName(path[i]);
    }
    return buildings;
  }

  public Building getTheClosestBuildingByPoint(Point point) {
    double minDistance = Double.MAX_VALUE;
    Building result = null;
    for (Building building: this.buildings) {
      double distance = building.getDistance(new Location((float) point.getX(), (float) point.getY()));
      if (distance <= 50 && distance < minDistance) {
        minDistance = distance;
        result = building;
      }
    }
    return result;
  }

  /**
   * 从起点开始，不重复地遍历所有人文景点
   * @param origin 起点
   */
  public ArrayList<String[]> travelAllCulturalAttractions(String origin) {
    ArrayList<String[]> results = new ArrayList<>(0);
    this.travel(origin, this.getBuildingByName(origin), new String[] { origin }, results);
    double minDistance = Double.MAX_VALUE;
    for (int i = results.size() - 1; i >= 0; i--) {
      String[] res = results.get(i);
      double distance = this.calcRouteDistance(res);
      // 如果这里是 <= 就可以去掉往返的重复路径，但也存在舍入误差
      if (minDistance < distance) {
        results.remove(res);
      } else {
        minDistance = distance;
      }
    }
    if (results.size() == 0) {
      System.out.println("Path does not exist!");
    }
    return results;
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
      // 未被遍历过
      if(!Arrays.asList(route).contains(availablePlaceName)) {
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
    for (Building culturalAttractions: this.getBuildingsByType(Type.CULTURAL_ATTRACTION)) {
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

  /**
   * Dijkstra最短路径算法
   * @param start 起点
   * @param end 终点
   * @return 最短路径数组
   */
  public String[] findTheShortestPath(String start, String end) {
    if (start.equals(end)) {
      return new String[] {start};
    }
    HashMap<String, Double> distances = new HashMap<>(this.buildings.length);
    distances.put(start, 0.0);
    ArrayList<String[]> results = new ArrayList<>(0);
    this.findPath(this.getBuildingByName(start), end, distances, new String[] { start }, results);
    return results.get(results.size() - 1);
  }

  public void findPath(Building start, String end, HashMap distances, String[] route, ArrayList<String[]> res) {
    double currentDistance = (double) distances.get(start.getName());
    for (String buildingName: start.getAvailablePlaces()) {
      if (Arrays.asList(route).contains(buildingName)) {
        continue;
      }
      Building building = this.getBuildingByName(buildingName);
      double nextDistance = currentDistance + start.getDistance(building);
      double distance = (double) distances.getOrDefault(buildingName, Double.MAX_VALUE);
      if(nextDistance < distance) {
        String[] newRoute = Arrays.copyOf(route, route.length + 1);
        newRoute[route.length] = buildingName;
        distances.put(buildingName, nextDistance);
        // 到终点即终止递归
        if(buildingName.equals(end)) {
          res.add(newRoute);
        } else {
          this.findPath(building, end, distances, newRoute, res);
        }
      }
    }
  }

  /**
   * 至少遍历一个人文景点
   * @param start 起点
   * @param end 终点
   * @return 最短路径字符串数组
   */
  public String[] travelAtLeastOneCulturalAttraction(String start, String end) {
    Building startBuilding = this.getBuildingByName(start);
    Building endBuilding = this.getBuildingByName(end);
    if (startBuilding.getType() == Type.CULTURAL_ATTRACTION || endBuilding.getType() == Type.CULTURAL_ATTRACTION) {
      return this.findTheShortestPath(start, end);
    } else {
      String[] minRoute = new String[0];
      double minDistance = Double.MAX_VALUE;
      for (Building culturalAttraction: this.getBuildingsByType(Type.CULTURAL_ATTRACTION)) {
        String[] path1 = this.findTheShortestPath(start, culturalAttraction.getName());
        String[] path2 = this.findTheShortestPath(culturalAttraction.getName(), end);
        String[] route = new String[path1.length + path2.length - 1];
        System.arraycopy(path1, 0, route, 0, path1.length);
        System.arraycopy(path2, 1, route, path1.length, path2.length - 1);
        double distance = this.calcRouteDistance(route);
        if (distance < minDistance) {
          minRoute = route;
          minDistance = distance;
        }
      }
      System.out.println(Arrays.toString(minRoute));
      return minRoute;
    }
  }
}
