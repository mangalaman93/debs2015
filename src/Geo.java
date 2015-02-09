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
    this.cellSizeY = cellSizeY;
    this.cellRangeX = cellRangeX;
    this.cellRangeY = cellRangeY;
  }
  
  /*
  The approximate conversions are:
  Latitude: 1 deg = 110.54 km = 110.54*1000 m
  Longitude: 1 deg = 111.320*cos(latitude) km = 84.38 km = 84.38*1000 m
  Source : http://stackoverflow.com/questions/1253499/simple-calculations-for-working-with-lat-lon-km-distance
  */

  public Area translate(float locX, float locY) {
    int gridX;
    int gridY;
    float distX = (float) ((locX-topLeftX)*84.38*1000) + 0.5f;
    
    if(distX > cellRangeX) {
      return new Area(-1, -1);
    }
    else {
      gridX = (int) (distX / cellSizeX) + 1;
    }

    float distY = (float) ((topLeftY-locY)*110.54*1000) + 0.5f;
    
    if(distY > cellRangeY) {
      return new Area(-1, -1);
    }
    else {
      gridY = (int) (distY / cellSizeY) + 1;
    }

    return new Area(gridX, gridY);
  }
}
