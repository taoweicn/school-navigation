import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Queue;
import java.util.*;

class Navigator {
  private Map<String, Building> buildings = new HashMap<>();

  Navigator() {
    this.readBuildingsInfo();
  }

  /** 获取文件信息 */
  private void readBuildingsInfo() {
    JsonParser parser = new JsonParser();
    try {
      JsonArray jsonArray =
          (JsonArray) parser.parse(new FileReader("./src/main/resources/buildings.json"));
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Type.class, new EnumSerializer());
      Gson gson = gsonBuilder.create();
      TypeToken typeToken = new TypeToken<List<Building>>() {};
      List<Building> buildings = gson.fromJson(jsonArray, typeToken.getType());
      for (Building building : buildings) {
        this.buildings.put(building.getName(), building);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * 通过建筑物名称来获取java对象
   *
   * @param name 名称
   * @return Building对象
   */
  Building getBuildingByName(String name) {
    return this.buildings.get(name);
  }

  Map<String, Building> getAllBuildings() {
    return this.buildings;
  }

  String[] getAllBuildingsName() {
    return this.buildings.keySet().toArray(String[]::new);
  }

  private Building[] getBuildingsByType(Type type) {
    return this.buildings.values().stream()
        .filter(building -> building.getType() == type)
        .toArray(Building[]::new);
  }

  private double calcDistance(String s1, String s2) {
    return this.buildings.get(s1).getDistance(this.buildings.get(s2));
  }

  Building getTheClosestBuildingByPoint(Point point) {
    double minDistance = Double.POSITIVE_INFINITY;
    Building result = null;
    for (Building building : this.buildings.values()) {
      double distance =
          building.getDistance(new Location((float) point.getX(), (float) point.getY()));
      if (distance <= 50 && distance < minDistance) {
        minDistance = distance;
        result = building;
      }
    }
    return result;
  }

  /**
   * 从起点开始，不重复地遍历所有人文景点
   *
   * @param origin 起点
   */
  String[] travelAllCulturalAttractions(String origin) {
    List<String[]> results = new ArrayList<>();
    List<String> route = new ArrayList<>();
    route.add(origin);
    this.travel(origin, origin, route, results);
    if (results.size() == 0) {
      System.out.println("Path does not exist!");
      return null;
    }
    double minDistance = Double.POSITIVE_INFINITY;
    String[] result = null;
    for (String[] r : results) {
      double distance = this.calcRouteDistance(r);
      if (distance < minDistance) {
        minDistance = distance;
        result = r;
      }
    }
    return result;
  }

  /**
   * 递归遍历所有可达到的人文景点
   *
   * @param origin 起点
   * @param current 当前所处位置
   * @param route 走过的路径
   * @param results 所有结果
   */
  private void travel(String origin, String current, List<String> route, List<String[]> results) {
    for (String availablePlaceName : this.getBuildingByName(current).getAvailablePlaces()) {
      // 到达终点且所有人文景点被遍历则保存路径
      if (availablePlaceName.equals(origin) && this.judgeWhetherAllTraveled(route)) {
        route.add(availablePlaceName);
        results.add(route.toArray(String[]::new));
        route.remove(route.size() - 1);
        return;
      }
      // 未被遍历过
      if (!route.contains(availablePlaceName)) {
        route.add(availablePlaceName);
        this.travel(origin, availablePlaceName, route, results);
        route.remove(availablePlaceName);
      }
    }
  }

  /**
   * 判断是否所有人文景点都被遍历过
   *
   * @param route 路径
   * @return 是否全被遍历
   */
  private boolean judgeWhetherAllTraveled(List<String> route) {
    return Arrays.stream(this.getBuildingsByType(Type.CULTURAL_ATTRACTION))
        .allMatch(building -> route.contains(building.getName()));
  }

  private double calcRouteDistance(String[] route) {
    double distance = 0;
    for (int i = 0; i < route.length - 1; i++) {
      distance += this.calcDistance(route[i], route[i + 1]);
    }
    return distance;
  }

  /**
   * Dijkstra 最短路径算法
   *
   * @param start 起点
   * @param end 终点
   * @return 最短路径数组
   */
  private String[] findTheShortestPath(String start, String end) {
    if (start.equals(end)) {
      return new String[] {start};
    }
    Map<String, Double> distances = new HashMap<>(this.buildings.size());
    Map<String, String> pathTo = new HashMap<>(this.buildings.size());
    for (String v : this.buildings.keySet()) {
      distances.put(v, Double.POSITIVE_INFINITY);
    }
    distances.put(start, 0.0);
    Queue<String> pq =
        new PriorityQueue<>(
            this.buildings.size(), ((s1, s2) -> (int) (distances.get(s1) - distances.get(s2))));
    pq.add(start);
    while (!pq.isEmpty()) {
      String v = pq.poll();
      for (String w : this.buildings.get(v).getAvailablePlaces()) {
        if (distances.get(w) > distances.get(v) + this.calcDistance(w, v)) {
          distances.put(w, distances.get(v) + this.calcDistance(w, v));
          pathTo.put(w, v);
          if (pq.contains(w)) {
            pq.remove(w);
          } else {
            pq.add(w);
          }
        }
      }
    }
    List<String> path = new LinkedList<>();
    while (end != null) {
      path.add(0, end);
      end = pathTo.get(end);
    }
    return path.toArray(String[]::new);
  }

  /**
   * 至少遍历一个人文景点
   *
   * @param start 起点
   * @param end 终点
   * @return 最短路径字符串数组
   */
  String[] travelAtLeastOneCulturalAttraction(String start, String end) {
    Building startBuilding = this.getBuildingByName(start);
    Building endBuilding = this.getBuildingByName(end);
    if (startBuilding.getType() == Type.CULTURAL_ATTRACTION
        || endBuilding.getType() == Type.CULTURAL_ATTRACTION) {
      return this.findTheShortestPath(start, end);
    } else {
      String[] minRoute = new String[0];
      double minDistance = Double.POSITIVE_INFINITY;
      for (Building culturalAttraction : this.getBuildingsByType(Type.CULTURAL_ATTRACTION)) {
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
