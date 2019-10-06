class Location {
  /** 经度 */
  private float longitude;
  /** 纬度 */
  private float latitude;

  Location(float longitude, float latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }

  float getLongitude() {
    return this.longitude;
  }

  float getLatitude() {
    return this.latitude;
  }

  double getDistance(Location location) {
    return Math.sqrt(
        Math.pow(this.latitude - location.latitude, 2)
            + Math.pow(this.longitude - location.longitude, 2));
  }
}
