class Location {
  /**
   * 经度
   */
  private float longitude;
  /**
   * 纬度
   */
  private float latitude;

  public Location(float longitude, float latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }

  public float getLongitude() {
    return this.longitude;
  }

  public float getLatitude() {
    return this.latitude;
  }

  public double getDistance(Location location) {
    return Math.sqrt(
      Math.pow(this.latitude - location.latitude, 2) + Math.pow(this.longitude - location.longitude, 2)
    );
  }
}
