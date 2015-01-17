public class Geo {
  // constructor
  public Geo(float topLeftX, float topLeftY,
      int cellSizeX, int cellSizeY,
      int cellRangeX, int cellRangeY) {
    
  }

  // converts geo-location coordinates to cell ids
  // reference http://www.movable-type.co.uk/scripts/latlong.html
  public Area translate(float locX, float locY) {
    return new Area(0, 0);
  }
}
