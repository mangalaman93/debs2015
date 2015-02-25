import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CHashMap<K, V> extends HashMap<K, V> {
  private static final long serialVersionUID = 1L;
  private Method getNodeMethod;

  static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;

    Node(int hash, K key, V value, Node<K,V> next) {
      this.hash = hash;
      this.key = key;
      this.value = value;
      this.next = next;
    }

    public final K getKey()        { return key; }
    public final V getValue()      { return value; }
    public final String toString() { return key + "=" + value; }

    public final int hashCode() {
      return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    public final V setValue(V newValue) {
      V oldValue = value;
      value = newValue;
      return oldValue;
    }

    public final boolean equals(Object o) {
      if (o == this)
        return true;
      if (o instanceof Map.Entry) {
        Map.Entry<?,?> e = (Map.Entry<?,?>)o;
        if (Objects.equals(key, e.getKey()) &&
            Objects.equals(value, e.getValue()))
          return true;
      }
      return false;
    }
  }

  public CHashMap() throws NoSuchMethodException, SecurityException {
    getNodeMethod = HashMap.class.getDeclaredMethod("getNode");
    getNodeMethod.setAccessible(true);
  }

  @SuppressWarnings("unchecked")
  public V get(int hashvalue, Object key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Node<K,V> e;
    return (e = (Node<K, V>) getNodeMethod.invoke(this, hashvalue, key)) == null ? null : e.value;
  }
}
