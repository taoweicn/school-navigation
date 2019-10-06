import java.util.Arrays;

class Building extends Location {
  private String name;
  private Type type;
  private String[] availablePlaces;

  Building(String name, String type, float longitude, float latitude, String[] availablePlaces) {
    super(longitude, latitude);
    this.name = name;
    this.type = Type.mapStringToType(type);
    this.availablePlaces = availablePlaces;
  }

  String getName() {
    return this.name;
  }

  String[] getAvailablePlaces() {
    return this.availablePlaces;
  }

  Type getType() {
    return type;
  }

  double getDistance(Building building) {
    if (this.whetherReachBuilding(building.getName())) {
      return super.getDistance(building);
    } else {
      throw new RuntimeException("can not reach the building directly");
    }
  }

  private boolean whetherReachBuilding(String name) {
    return Arrays.asList(this.availablePlaces).contains(name);
  }

  @Override
  public String toString() {
    return "Building{"
        + "name='"
        + name
        + '\''
        + ", type="
        + type
        + ", availablePlaces="
        + Arrays.toString(availablePlaces)
        + '}';
  }
}
