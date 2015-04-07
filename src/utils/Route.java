package utils;
public class Route implements Comparable<Route> {
  public Area fromArea;
  public Area toArea;
  public int hash;

  public Route(int begin_cellX, int begin_cellY,
      int end_cellX, int end_cellY) {
    this.fromArea = new Area(begin_cellX, begin_cellY);
    this.toArea   = new Area(end_cellX, end_cellY);
    this.computeHash();
  }

  public Route(Area from, Area to) {
    this.fromArea = from;
    this.toArea   = to;
    this.computeHash();
  }

  private void computeHash() {
    Integer temp = (((((this.fromArea.x << 8) | this.fromArea.y) << 8)
        | this.toArea.x) << 8) | this.fromArea.y;
    this.hash = temp.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Area))
      return false;

    if(obj == this)
      return true;

    Route r = (Route) obj;
    if(r.fromArea.equals(this.fromArea) && r.toArea.equals(this.toArea))
      return true;

    return false;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  public int compareTo(Route route) {
    if(route.fromArea.x < this.fromArea.x){
      return 1;
    } else if(route.fromArea.x > this.fromArea.x){
      return -1;
    } else if(route.fromArea.y < this.fromArea.y){
      return 1;
    } else if(route.fromArea.y > this.fromArea.y){
      return -1;
    } else if(route.toArea.x < this.toArea.x){
      return 1;
    } else if(route.toArea.x > this.toArea.x){
      return -1;
    } else if(route.toArea.y < this.toArea.y){
      return 1;
    } else if(route.toArea.y > this.toArea.y){
      return -1;
    } else {
      return 0;
    }
  }
}
