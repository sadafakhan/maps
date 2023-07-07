package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    private int size;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;

    // You may add extra fields or helper methods though!

    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        SimpleEntry<K, V> spare = null;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                spare = entries[i];
            }
        }
        if (spare != null) {
            return spare.getValue();
        } else {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        SimpleEntry<K, V> spare = null;
        boolean inOrNot = false;
        if (size == entries.length) {
            SimpleEntry<K, V>[] oldEntry = entries;
            entries = createArrayOfEntries(size * 2);
            for (int i = 0; i < size; i++) {
                entries[i] = oldEntry[i];
                if (Objects.equals(entries[i].getKey(), key)) {
                    inOrNot = true;
                    spare = entries[i];
                    entries[i] = new SimpleEntry<>(key, value);
                }
            }
            if (!inOrNot) {
                entries[size] = new SimpleEntry<>(key, value);
                size++;
            }
        }
        else {
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (Objects.equals(entries[i].getKey(), key)) {
                        inOrNot = true;
                        spare = entries[i];
                        entries[i] = new SimpleEntry<>(key, value);
                    }
                }
                if (!inOrNot) {
                    entries[size] = new SimpleEntry<>(key, value);
                    size++;
                }
            } else {
                entries[size] = new SimpleEntry<>(key, value);
                size++;
            }
        }
        if (spare != null) {
            return spare.getValue();
        }
        else {
            return null;
        }
    }

    @Override
    public V remove(Object key) {
        SimpleEntry<K, V> spare = null;
        if (entries[0] != null) {
            if (Objects.equals(entries[0].getKey(), key)) {
                SimpleEntry<K, V> replace = entries[size - 1];
                spare = entries[0];
                entries[0] = replace;
                entries[size - 1] = null;
                size--;
            } else {
                for (int i = 0; i < size; i++) {
                    SimpleEntry<K, V> replace = entries[size - 1];
                    if (Objects.equals(entries[i].getKey(), key)) {
                        spare = entries[i];
                        entries[i] = replace;
                        entries[size - 1] = null;
                        size--;
                    }
                }
            }
        }
        if (spare != null) {
            return spare.getValue();
        }
        else {
            return null;
        }
    }

    @Override
    public void clear() {
        entries = createArrayOfEntries(DEFAULT_INITIAL_CAPACITY);
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < size; i++) {
            SimpleEntry<K, V> value1 = entries[i];
            K newValue = value1.getKey();
            if (Objects.equals(newValue, key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries, 0);
    }


    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int index;
        private int size;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries, int index) {
            this.entries = entries;
            this.index = index;
            for (SimpleEntry<K, V> entry : entries) {
                if (entry != null) {
                    this.size++;
                }
            }
        }

        @Override
        public boolean hasNext() {
            if (entries[0] == null) {
                return false;
            }
            return (index < size);
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index++;
            return entries[index - 1];
        }
    }
}
