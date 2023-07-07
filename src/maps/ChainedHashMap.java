package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 0.75;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 5;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 5;
    private static double RLFT = DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD;
    private static int ICCOUNT = DEFAULT_INITIAL_CHAIN_COUNT;
    private static int ICCAPACITY = DEFAULT_INITIAL_CHAIN_CAPACITY;
    private int size = 0;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        RLFT = resizingLoadFactorThreshold;
        ICCOUNT = initialChainCount;
        ICCAPACITY = chainInitialCapacity;
        chains = new AbstractIterableMap[ICCOUNT];
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        int i = 0;
        if (key != null) {
            i = Math.abs(key.hashCode() % ICCOUNT);
        }
        V got = null;
        if (chains[i] != null) {
            got = chains[i].get(key);
        }
        return got;
    }

    @Override
    public V put(K key, V value) {
        int i = 0;
        if (key != null) {
            i = Math.abs(key.hashCode() % ICCOUNT);
        }
        V old = null;
        if (chains[i] == null) {
            ArrayMap<K, V> chain = new ArrayMap<>(ICCAPACITY);
            chain.put(key, value);
            chains[i] = chain;
        } else {
            old = chains[i].put(key, value);
        }
        if (old == null) {
            size++;
        }
        return old;
    }

    @Override
    public V remove(Object key) {
        int i = 0;
        if (key != null) {
            i = Math.abs(key.hashCode() % ICCOUNT);
        }
        V removed = null;
        if (chains[i] != null) {
            removed = chains[i].remove(key);
            if (chains[i].size() == 0) {
                chains[i] = null;
            }

        }
        if (removed != null) {
            size--;
        }
        return removed;
    }

    @Override
    public void clear() {
        chains = new AbstractIterableMap[ICCOUNT];
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int i = 0;
        if (key != null) {
            i = Math.abs(key.hashCode() % ICCOUNT);
        }
        return (chains[i] != null && chains[i].containsKey(key));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }


    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private int index;
        private Iterator<Entry<K, V>> it;
        private int explorer = 0;
        // You may add more fields and constructor parameters

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            this.index = 0;
        }

        private int findNextEntry(int i) {
            while (chains[i] == null) {
                if (i == chains.length - 1) {
                    return -1;
                }
                i++;
            }
            return i;
        }

        @Override
        public boolean hasNext() {
            // in a null entry -> iterate till the next chained one, or, if at the end, return false
            if (explorer == chains.length - 1 | explorer == -1) {
                return false;
            }
            if (chains[explorer] == null) {
                explorer = findNextEntry(explorer);
                if (explorer == -1) {
                    return false;
                }
            }


            // we're now guaranteed in a chained entry

            // first time in new chain
            if (this.it == null) {
                this.it = chains[explorer].iterator();
            }

            // at the end of a current chained entry
            if (!it.hasNext()) {
                explorer++;
                if (explorer == chains.length - 1) {
                    return false;
                }
                if (chains[explorer] == null) {
                    explorer = findNextEntry(explorer);
                    if (explorer == -1) {
                        return false;
                    }
                }
                it = chains[explorer].iterator();
            }
            return it.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                index = explorer;
                Entry<K, V> answer = it.next();
                return answer;
            }
        }
    }
}
