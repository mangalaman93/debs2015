public class AVLTree<T> {
  Node root;
  int size;

  public AVLTree() {
    root = null;
    size = 0;
  }

  public Node insert(T elem) {
    // TODO
    return null;
  }

  public void modify(Node elem) {
    // TODO
  }

  public void delete(Node elem) {
    // TODO
  }

  public class Node {
    public T val;
    public  Node parent;
    public Node left;
    public Node right;

    public Node(T v, Node p, Node l, Node r) {
      val    = v;
      parent = p;
      left   = l;
      right  = r;
    }
  }
}
