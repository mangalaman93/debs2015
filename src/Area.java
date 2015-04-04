public class Area {
  public int x;
  public int y;
  public int hash;

  public Area(int x, int y) {
    this.x = x;
    this.y = y;
    Integer temp = (this.x << 16) | this.y;
    this.hash = temp.hashCode();
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
  	return hash;
  }
}
