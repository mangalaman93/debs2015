public class Geo {
  // constructor
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
    this.cellRangeY = cellSizeY;
    this.cellRangeX = cellRangeX;
    this.cellRangeY = cellRangeY;
  }

  // converts geo-location coordinates to cell ids
  // reference http://www.movable-type.co.uk/scripts/latlong.html

  /*
  The approximate conversions are:
  Latitude: 1 deg = 110.54 km
  Longitude: 1 deg = 111.320*cos(latitude) km = 84.38 km
  Source : http://stackoverflow.com/questions/1253499/simple-calculations-for-working-with-lat-lon-km-distance
  */

  public Area translate(float locX, float locY) {
    int gridX;
    int gridY;
    float distX = (locX-topLeftX)*110.54;

    if(distX > cellRangeX) {
      return new Area(-1, -1);
    }
    else {
      gridX = distX / cellRangeX;
    }

    float distY = (locY-topLeftY)*84.38;

    if(distY > cellRangeY) {
      return new Area(-1, -1);
    }
    else {
      gridY = distY / cellRangeY;
    }

    return new Area(gridX, gridY);
  }
}
