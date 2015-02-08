import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

class Profitability {
  // TODO
}

class Triplet {
  Profitability ptb;
  Mc profit;
  int numEmptyTaxis;
}

public class TenMaxProfitability extends TenMax<Area> {
  private Map<Area, Triplet> data;

  public TenMaxProfitability() {
    // TODO
  }

  @Override
  protected Number getValue(Area a) {
    // TODO
    return 0;
  }

  public void insert() {
    // TODO
    System.out.println("removed");
  }

  public void remove() {
    //TODO
    System.out.println("removed");
  }

  public Boolean update(Q2Elem event, Timestamp ts, String type){
    //temporary
    System.out.println("updated");
    return false;
  }

  public Vector<KeyVal> getMaxTen() {
    //temporary
    Vector<KeyVal> max_ten;
    max_ten = new Vector<KeyVal>(10);
    return max_ten;
  }
}
