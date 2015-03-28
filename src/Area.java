public class Area {
  public int x;
  public int y;
  public int hash;

  public Area(int x, int y) {
    this.x = x;
    this.y = y;
    
    /* An Elegant Pairing Function by Matthew Szudzik, Wolfram Research, Inc. */
    this.hash = x >= y ? x * x + x + y : x + y * y;
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
}
