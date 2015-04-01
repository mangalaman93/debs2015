package utils;

public class Geo {
  float topLeftX;
  float topLeftY;
  int cellSizeX;
  int cellSizeY;
  int cellRangeX;
  int cellRangeY;

  public Geo(float topLeftX, float topLeftY,
      int cellSizeX, int cellSizeY,
      int cellRangeX, int cellRangeY) {
    this.topLeftX = topLeftX;
    this.topLeftY = topLeftY;
    this.cellSizeX = cellSizeX;
    this.cellSizeY = cellSizeY;
    this.cellRangeX = cellRangeX;
    this.cellRangeY = cellRangeY;
  }

  /*
   * The approximate conversions are:
   *   Latitude: 1 deg = 110.54 km = 110.54*1000m
   *   Longitude: 1 deg = 111.320*cos(latitude) km = 84.38 km = 84.38*1000 m
   *   http://stackoverflow.com/questions/1253499/
   */
  public Area translate(float locX, float locY) {
    float distX = (float) ((locX-topLeftX)*84.38*1000) + 0.5f*cellSizeX;
    int gridX = (int) (distX / cellSizeX);
    if(gridX >= cellRangeX || gridX < 0) {
      return null;
    }

    float distY = (float) ((topLeftY-locY)*110.54*1000) + 0.5f*cellSizeY;
    int gridY = (int) (distY / cellSizeY);
    if(gridY >= cellRangeY || gridY < 0) {
      return null;
    }

    return new Area(gridX, gridY);
  }
}
