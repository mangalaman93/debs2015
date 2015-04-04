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

  public boolean equals(Area a) {
    if(a.x == this.x && a.y == this.y)
      return true;

    return false;
  }

  @Override
  public int hashCode() {
  	return hash;
  }

  @Override
  public boolean equals(Object obj) {
    throw new UnsupportedOperationException();
  }
}
