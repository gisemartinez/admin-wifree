package utils;

public class ObjectHelper {

    public static boolean areNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) return true;
        }
        return false;
    }

    public static boolean areNotNull(Object... objects) {
        return !areNull(objects);
    }

}
