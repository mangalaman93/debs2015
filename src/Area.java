public class Area {
  public int x;
  public int y;

  public Area(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Area))
      return false;

    if(obj == this)
      return true;

    Area a = (Area) obj;
    if(a.x == this.x && a.y == this.y)
      return true;

    return false;
  }

  @Override
  public int hashCode() {
  	return (this.x << 16) + this.y;
  }
}
