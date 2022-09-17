package utils;

import java.util.HashMap;
import java.util.Map;

public class CollectionHelper {

    public static <K, V> Map<K, V> initializeMap(K[] keys, V[] values) {
        if (ObjectHelper.areNull(keys, values)) return null; // TODO: fix, throw Exception
        if (!sizeMatches(keys, values)) return null; // TODO: fix, throw Exception

        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            K key = keys[i];
            V value = values[i];
            map.put(key, value);
        }
        return map;
    }

    public static <A, B> boolean sizeMatches(A[] array1, B[] array2) {
        return ObjectHelper.areNotNull(array1, array2) && array1.length == array2.length;
    }

}
