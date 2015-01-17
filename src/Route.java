public class Route {
  public Area fromArea;
  public Area toArea;
  
  public Route(int beginCellX, int beginCellY,
      int endCellX, int endCellY) {
    this.fromArea = new Area(beginCellX, beginCellY);
    this.toArea   = new Area(endCellX, endCellY);
  }
  
  public Route(Area from, Area to) {
    this.fromArea = from;
    this.toArea   = to;
  }
}
